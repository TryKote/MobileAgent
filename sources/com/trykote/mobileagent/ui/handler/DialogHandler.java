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

    // About screen encryption key for device ID
    private static final long ABOUT_SCREEN_KEY = 7234309766870429269L;

    // Emoticon counts per protocol
    private static final int MMP_EMOTICON_COUNT = 37;          // 0..36 inclusive
    private static final int XMPP_EMOTICON_COUNT = 50;         // 0..49 inclusive
    private static final int XMPP_EXCLUDED_INDEX_1 = 21;
    private static final int XMPP_EXCLUDED_INDEX_2 = 27;
    private static final int XMPP_EMOTICON_ICON_BASE = 161;
    private static final int XMPP_EMOTICON_LABEL_BASE = 155;
    private static final int XMPP_EMOTICON_CMD_OFFSET = 4;
    private static final int MMP_EMOTICON_ICON_BASE = 268;
    private static final int MMP_EMOTICON_LABEL_BASE = 118;

    // Emoticon picker counts
    private static final int PICKER_MMP_COUNT = 43;
    private static final int PICKER_XMPP_COUNT = 37;
    private static final int PICKER_MRIM_START = 10;
    private static final int PICKER_MRIM_END = 74;
    private static final int PICKER_MRIM_ICON_BASE = 36;
    private static final int PICKER_MRIM_EXTRA_START = 0;
    private static final int PICKER_MRIM_EXTRA_END = 10;
    private static final int PICKER_XMPP_ICON_BASE = 318;
    private static final int PICKER_MMP_ICON_BASE = 110;

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.STATUS_DIALOG:
                ListView dialogScreen = ScreenManager.createDialogScreen(3);
                Account account = Storage.state().getAccount();
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
                        if (((XmppProtocol) account).isMailRuVariant()) {
                            dialogScreen.addIconById(387, 642, 1).addIconById(385, 647, 0);
                        } else {
                            dialogScreen.addIconById(383, 642, 1).addIconById(16384383, 643, 4).addIconById(16318847, 644, 2).addIconById(16449919, 645, 5).addIconById(16580991, 648, 6).addIconById(16515455, 646, 3).addIconById(381, 647, 0);
                        }
                        break;
                }
                ScreenManager.showScreen(dialogScreen.setSoftKeys(Storage.resources().getString(StringResKeys.STR_SOFTKEY_YES), Storage.resources().getString(StringResKeys.STR_SOFTKEY_NO), 199, 12, 199));
                return;
            case ScreenId.ABOUT:
                Storage.state().setInt(UIKeys.FLAG_SHOW_NOTIFICATION, 1);
                Storage.state().setObject(UIKeys.SLOT_SCREEN_TITLE, (Object) StringUtils.intern(Long.toString(Runtime.getRuntime().totalMemory())));
                Storage.state().setObject(UIKeys.SLOT_SCREEN_SUBTITLE, (Object) AppController.getFreeMemoryString());
                Storage.state().setFromBuffer(UIKeys.SLOT_APP_VERSION_STRING, ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_APP_NAME)).append(Storage.resources().getString(StringResKeys.STR_APP_BUILD_SUFFIX)));
                Storage.state().setObject(RegistrationKeys.SLOT_DEVICE_ID, (Object) new ByteBuffer().writeLongBytes(ABOUT_SCREEN_KEY).writeByte(',').writeRawString(Storage.state().getAppProperty(StringResKeys.STR_APP_PROPERTY_NAME)).getStringAndClear());
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.ABOUT));
                return;
            case ScreenId.BLOCK_CONFIRM:
                NotificationHelper.showAlertById(10, 710);
                return;
            case ScreenId.UNBLOCK_CONFIRM:
                NotificationHelper.showAlertBuffer(11, ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_BLOCK_CONFIRM)).append(Storage.state().getCurrentContact().displayName).append(ObjectPool.unpackChars(16167)));
                return;
            case ScreenId.CONFIRM_EXIT:
                NotificationHelper.showConfirmDialog(13, 505);
                return;
            case ScreenId.TRAFFIC_COST:
                StringBuffer sb = ObjectPool.newStringBuffer();
                int intVal = Storage.state().getInt(SettingsKeys.SETTING_TRAFFIC_COST);
                Storage.state().setFromBuffer(UIKeys.SLOT_SCREEN_VALUE, sb.append(intVal / 100).append('.').append(Utils.zeroPad(intVal % 100)));
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.TRAFFIC_COST));
                return;
            case ScreenId.EMOTICON_DIALOG:
                ListView dialogScreen2 = ScreenManager.createDialogScreen(17);
                if (Storage.state().getAccount() instanceof MmpProtocol) {
                    for (int i3 = 0; i3 < MMP_EMOTICON_COUNT; i3++) {
                        dialogScreen2.addIconById(i3 + MMP_EMOTICON_ICON_BASE, i3 + MMP_EMOTICON_LABEL_BASE, i3);
                    }
                } else {
                    for (int i4 = 0; i4 < XMPP_EMOTICON_COUNT; i4++) {
                        if (i4 != XMPP_EXCLUDED_INDEX_1 && i4 != XMPP_EXCLUDED_INDEX_2) {
                            dialogScreen2.addIconById(i4 + XMPP_EMOTICON_ICON_BASE, i4 + XMPP_EMOTICON_LABEL_BASE, i4 + XMPP_EMOTICON_CMD_OFFSET);
                        }
                    }
                }
                ScreenManager.showScreen(dialogScreen2.setSoftKeys(Storage.resources().getString(StringResKeys.STR_SOFTKEY_YES), Storage.resources().getString(StringResKeys.STR_SOFTKEY_NO), 199, 12, 199));
                return;
            case ScreenId.DELETE_CONFIRM:
                NotificationHelper.showConfirmDialog(55, 761);
                return;
            case ScreenId.INPUT_DIALOG:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.INPUT_DIALOG));
                return;
            case ScreenId.STATUS_INPUT:
                XmppContactGroup.showTextInputDialog(Storage.state().getCurrentContact().displayName, Storage.state().getString(UIKeys.SLOT_STATUS_TEXT), 1000, StringUtils.isKnownDevice2 ? 2097152 : 0, Storage.resources().getString(StringResKeys.STR_INPUT_MODE_DEFAULT), 1059, 1055, new TextInputHandler());
                Storage.state().setInt(RuntimeKeys.INT_LAST_POLL_TIMESTAMP, 0);
                Storage.state().setInt(RuntimeKeys.INT_LAST_CHECK_TIMESTAMP, 0);
                Storage.state().setInt(RuntimeKeys.INT_LAST_LIST_SIZE, 0);
                ScreenManager.showScreen(new ListView());
                return;
            case ScreenId.SOFTKEY_MENU:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.SOFTKEY_MENU));
                return;
            case ScreenId.EMOTICON_PICKER:
                ListView screen5 = ScreenManager.createScreen(ScreenDef.EMOTICON_PICKER);
                if (Storage.state().getCurrentContact() instanceof MmpContact) {
                    for (int i10 = 0; i10 < PICKER_MMP_COUNT; i10++) {
                        if (Storage.resources().getBlockString(StringResKeys.MMP_EMOTICONS_BASE, i10) != null) {
                            screen5.addIconTextItem(i10 + PICKER_MMP_ICON_BASE, StringUtils.intern(Integer.toString(i10)), i10);
                        }
                    }
                } else if (Storage.state().getCurrentContact() instanceof XmppContact) {
                    for (int i11 = 0; i11 < PICKER_XMPP_COUNT; i11++) {
                        if (Storage.resources().getBlockString(StringResKeys.XMPP_EMOTICONS_BASE, i11) != null) {
                            screen5.addIconTextItem(i11 + PICKER_XMPP_ICON_BASE, StringUtils.intern(Integer.toString(i11)), i11);
                        }
                    }
                } else {
                    for (int i12 = PICKER_MRIM_START; i12 < PICKER_MRIM_END; i12++) {
                        screen5.addIconTextItem(i12 + PICKER_MRIM_ICON_BASE, StringUtils.intern(Integer.toString(i12)), i12);
                    }
                    for (int i13 = PICKER_MRIM_EXTRA_START; i13 < PICKER_MRIM_EXTRA_END; i13++) {
                        screen5.addIconTextItem(i13 + PICKER_MRIM_ICON_BASE, StringUtils.intern(Integer.toString(i13)), i13);
                    }
                    screen5.addIconTextItem(142, StringUtils.intern(Integer.toString(74)), 74);
                    screen5.addIconTextItem(137, StringUtils.intern(Integer.toString(75)), 75);
                    screen5.addIconTextItem(210, StringUtils.intern(Integer.toString(76)), 76);
                    screen5.addIconTextItem(205, StringUtils.intern(Integer.toString(77)), 77);
                }
                ScreenManager.showScreen(screen5);
                return;
            case ScreenId.CAPTCHA:
                ListView screen12 = ScreenManager.createScreen(ScreenDef.CAPTCHA);
                Object obj4 = ((Object[]) Storage.state().getObject(RegistrationKeys.OBJ_REGISTRATION_DATA))[2];
                if (obj4 instanceof javax.microedition.lcdui.Image) {
                    screen12.addItem(MenuItem.createGraphics(new GraphicsContext((javax.microedition.lcdui.Image) obj4)));
                } else {
                    screen12.addLabelById(((Integer) obj4).intValue());
                }
                ScreenManager.showScreen(screen12);
                Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                return;
            case ScreenId.CLEAR_NOTIFICATIONS:
                NotificationHelper.clearNotifications();
                return;
            case ScreenId.PRIVACY_MODE:
                NotificationHelper.playNotificationSound(NotificationHelper.SOUND_MESSAGE_SENT);
                Storage.state().setInt(UIKeys.INT_NOTIFICATION_SCREEN_ID, ScreenId.PRIVACY_MODE);
                Storage.state().setFromPool(UIKeys.SLOT_NOTIFICATION_TITLE, StringResKeys.STR_PRIVACY_MODE_BASE);
                NotificationHelper.clearNotifications();
                return;
            case ScreenId.STATUS_PREVIEW:
                String inputText = XmppContactGroup.getTextInputValue();
                Storage.state().setObject(UIKeys.SLOT_STATUS_TEXT, (Object) inputText);
                Storage.state().setBool(UIKeys.FLAG_STATUS_TEXT_SET, !StringUtils.isEmpty(inputText));
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.STATUS_PREVIEW));
                return;
            case ScreenId.PRESENCE_ACTION:
                NotificationHelper.showAlertById(122, 535);
                return;
            case ScreenId.BLOG_POST:
                String[] langOptions = ScreenManager.getLanguageOptions();
                if (langOptions != null) {
                    Storage.state().setObject(UIKeys.SLOT_SCREEN_SUBTITLE, (Object) langOptions[0]);
                    Storage.state().setFromBuffer(UIKeys.SLOT_SCREEN_TITLE, ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_LANGUAGE_PREFIX)).append(langOptions[1]));
                    if (Storage.state().getInt(RegistrationKeys.COUNTER_SEARCH_RESULTS) != 0) {
                        Storage.state().clearIndex(UIKeys.SLOT_LANG_OPTION_1);
                        Storage.state().clearIndex(UIKeys.SLOT_LANG_OPTION_2);
                    }
                    Storage.state().setFromPool(UIKeys.SLOT_SCREEN_VALUE, UIKeys.SLOT_LANG_OPTION_1);
                    if (Storage.state().getString(UIKeys.SLOT_SCREEN_DESCRIPTION) != null) {
                        Storage.state().setFromPool(UIKeys.SLOT_SCREEN_VALUE, UIKeys.SLOT_SCREEN_DESCRIPTION);
                        Storage.state().clearIndex(UIKeys.SLOT_SCREEN_DESCRIPTION);
                    }
                    ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.BLOG_POST));
                    return;
                }
                break;
            case ScreenId.CLEAR_SEARCH:
                ContactListManager.openContactMessages();
                return;
            case ScreenId.ASYNC_CONFIRM:
                NotificationHelper.showConfirmDialog(165, 505);
                Storage.state().setObject(RegistrationKeys.OBJ_REGISTRATION_DATA, RegistrationService.newRequest());
                return;
            case ScreenId.UPDATE_ALERT:
                NotificationHelper.showAlertById(171, 787);
                Storage.state().setInt(SessionKeys.FLAG_UPDATE_AVAILABLE, 1);
                return;
            case ScreenId.INVITE_ALERT:
                NotificationHelper.showAlertById(173, 416);
                return;
            case ScreenId.TRAFFIC_STATS:
                TrafficAccounting.showTrafficStats();
                return;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onMenuItemSelected(ListView screen, MenuItem item, String title, int action, Object data) {
        switch (screen.screenId) {
            case ScreenId.STATUS_DIALOG:
                return AccountManager.handleStatusChange(action);
            case ScreenId.ABOUT:
                return 0;
            case ScreenId.BLOCK_CONFIRM:
                return ScreenId.DELETE_CONFIRM;
            case ScreenId.UNBLOCK_CONFIRM:
                return handleLeftKey();
            case ScreenId.CONFIRM_EXIT:
                return -1;
            case ScreenId.TRAFFIC_COST:
                return TrafficAccounting.parseBalance();
            case ScreenId.EMOTICON_DIALOG:
                return handleAccountOption(action);
            case ScreenId.DELETE_CONFIRM:
                return -1;
            case ScreenId.INPUT_DIALOG:
                return ScreenManager.processInputText(title);
            case ScreenId.STATUS_INPUT:
                AppController.needsLayoutUpdate = true;
                Storage.state().setScreen(Storage.state().getCanvas().updateCommands());
                ScreenBuilder.onScreenClosed();
                return ScreenId.STATUS_PREVIEW;
            case ScreenId.SOFTKEY_MENU:
                return handleSoftKeyAction(title);
            case ScreenId.EMOTICON_PICKER:
                return ScreenManager.handleSoundOption(action);
            case ScreenId.CAPTCHA:
                return 0;
            case ScreenId.CLEAR_NOTIFICATIONS:
                return -1;
            case ScreenId.PRIVACY_MODE:
                return handleHashKey();
            case ScreenId.STATUS_PREVIEW:
                return handleMessageInputAction(title, action);
            case ScreenId.PRESENCE_ACTION:
                return AccountManager.handlePresenceAction();
            case ScreenId.BLOG_POST:
                int errorCode2;
                ScreenManager.processScreenForm();
                String messageText6 = Utils.defaultStr(Storage.state().getString(UIKeys.SLOT_SCREEN_VALUE));
                if (StringUtils.isEmpty(messageText6)) {
                    errorCode2 = NotificationHelper.showError(523);
                } else {
                    MrimAccount mrimAccount5 = (MrimAccount) Storage.state().getAccount();
                    new AsyncTask(AsyncTaskId.SEND_SMS_REQUEST, new ByteBuffer().writeCompressed(PackedStringKeys.URL_MOBILE_MAIL_RU).writeCompressed(PackedStringKeys.API_AGENTUPLOAD).writeUInt(4022591).writeRawString(mrimAccount5.login).writeUInt(4022822).writeRawString(mrimAccount5.password).writeCompressed(PackedStringKeys.PARAM_Z_JAVA).writeCompressed(PackedStringKeys.PARAM_CE).writeRawString(Conversation.urlEncodeCyrillic((Object) messageText6)).writeRawString(Utils.defaultStr(Storage.state().getBool(SessionKeys.FLAG_CAPTCHA_SHOWN) ? Storage.state().getString(UIKeys.SLOT_SCREEN_SUBTITLE) : null)).getStringAndClear());
                    Storage.state().addInt(RegistrationKeys.COUNTER_SEARCH_RESULTS, 1);
                    errorCode2 = 0;
                }
                return errorCode2;
            case ScreenId.CLEAR_SEARCH:
                return 0;
            case ScreenId.ASYNC_CONFIRM:
                return -1;
            case ScreenId.UPDATE_ALERT:
                return handleRightKey();
            case ScreenId.INVITE_ALERT:
                return MapHandler.handleGeoSearch();
            case ScreenId.TRAFFIC_STATS:
                int intVal = Storage.state().getInt(RuntimeKeys.INT_PERIOD_INDEX);
                Account account2 = Storage.state().getAccount();
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

    public int onMenuItemAction(ListView screen, MenuItem item, Object data) {
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
                if (!StringUtils.isEmpty(inputText) && (sendMsgResult = Storage.state().getCurrentContact().sendMessage(inputText)) != 0) {
                    ScreenBuilder.onScreenClosed();
                    EventDispatcher.postNotification(Storage.state().getString(sendMsgResult));
                }
                Storage.state().setInt(UIKeys.FLAG_STATUS_TEXT_SET, 0);
                Storage.state().clearIndex(UIKeys.SLOT_STATUS_TEXT);
                AppController.needsLayoutUpdate = true;
                Storage.state().setScreen(Storage.state().getCanvas());
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
                return handleHashKey();
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
                Storage.state().setInt(SessionKeys.FLAG_APP_STARTING, 0);
                MapController.toggleScrollMode();
                return ScreenId.MAP;
            case ScreenId.INVITE_ALERT:
                Storage.state().setAccount((Object) null);
                return ScreenId.CLOSE;
            case ScreenId.TRAFFIC_STATS:
                return 0;
        }
        return 0;
    }

    public void onScreenClosed(ListView screen) {
        switch (screen.screenId) {
            case ScreenId.ABOUT:
                AppController.clearPreviewState();
                break;
            case ScreenId.TRAFFIC_COST:
                Storage.state().clearIndex(UIKeys.SLOT_SCREEN_VALUE);
                break;
            case ScreenId.SOFTKEY_MENU:
                IOUtils.setSelectedItems((Object) null);
                break;
            case ScreenId.CLEAR_SEARCH:
                Storage.state().clearIndex(RuntimeKeys.SLOT_CURRENT_MSG_TEXT);
                Storage.state().clearIndex(UIKeys.SLOT_STATUS_TEXT);
                break;
            case ScreenId.PRESENCE_ACTION:
                ObjectPool.releaseVector(Storage.state().getVector(SessionKeys.VEC_ACCOUNT_SELECTION));
                Storage.state().clearIndex(SessionKeys.VEC_ACCOUNT_SELECTION);
                break;
            case ScreenId.BLOG_POST:
                AppController.clearPreviewState();
                break;
        }
    }

    public int onItemSelected(ListView screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.STATUS_DIALOG:
                return AccountManager.handleStatusChange(selectedOption);
            case ScreenId.ABOUT:
                return 0;
            case ScreenId.BLOCK_CONFIRM:
                return ScreenId.DELETE_CONFIRM;
            case ScreenId.UNBLOCK_CONFIRM:
                return handleLeftKey();
            case ScreenId.CONFIRM_EXIT:
                return -1;
            case ScreenId.TRAFFIC_COST:
                return 0;
            case ScreenId.EMOTICON_DIALOG:
                return handleAccountOption(selectedOption);
            case ScreenId.DELETE_CONFIRM:
                return -1;
            case ScreenId.INPUT_DIALOG:
                return ScreenManager.processInputText(title);
            case ScreenId.STATUS_INPUT:
                return 0;
            case ScreenId.SOFTKEY_MENU:
                return handleSoftKeyAction(title);
            case ScreenId.EMOTICON_PICKER:
                return ScreenManager.handleSoundOption(selectedOption);
            case ScreenId.CAPTCHA:
                return 0;
            case ScreenId.CLEAR_NOTIFICATIONS:
                return 0;
            case ScreenId.PRIVACY_MODE:
                return handleHashKey();
            case ScreenId.STATUS_PREVIEW:
                return handleMessageInputAction(title, selectedOption);
            case ScreenId.PRESENCE_ACTION:
                return AccountManager.handlePresenceAction();
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
                        Storage.state().setInt(MapKeys.FLAG_MAP_OVERLAY_ACTIVE, 1);
                        nextState = ScreenId.MAP;
                    } else {
                        String str = (String) objArr[1];
                        String str2 = (String) objArr[2];
                        long jLongValue = ((Long) objArr[3]).longValue();
                        AppController.clearPreviewState();
                        Storage.state().setInt(ContactKeys.INT_GROUP_OPERATION_RESULT, 0);
                        Storage.state().setObject(RegistrationKeys.SLOT_DEVICE_ID, (Object) str);
                        Storage.state().setFromBuffer(UIKeys.SLOT_SCREEN_TITLE, ObjectPool.newStringBuffer().append(str2).append(':'));
                        Storage.state().setLong(RuntimeKeys.TIMESTAMP_SELECTED_MSG, jLongValue);
                        nextState = ScreenId.MESSAGE_INPUT;
                    }
                } else {
                    Storage.state().clearIndex(UIKeys.SLOT_STATUS_TEXT);
                    Contact currentContact = Storage.state().getCurrentContact();
                    nextState = !currentContact.account.isConnected() ? NotificationHelper.showError(299) : currentContact.isOffline() ? ContactListManager.clearSmsFields() : 63;
                }
                return nextState;
            case ScreenId.ASYNC_CONFIRM:
                return -1;
            case ScreenId.UPDATE_ALERT:
                return handleRightKey();
            case ScreenId.INVITE_ALERT:
                return MapHandler.handleGeoSearch();
            case ScreenId.TRAFFIC_STATS:
                return 0;
        }
        return 0;
    }

    public int onIdleProcess(ListView screen, MenuItem item, Object data, String title) {
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
                Object[] objArr = (Object[]) Storage.state().getObject(RegistrationKeys.OBJ_REGISTRATION_DATA);
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
                AppController.clearImageCache();
                SoftFloat.clearMathTables();
                System.gc();
                try {
                    Thread.sleep(50);
                } catch (Throwable unused) {
                }
                Storage.state().addInt(SessionKeys.COUNTER_ERRORS, 1);
                AppController.saveOnExit = true;
                AppController.isBackgrounded = true;
                return 0;
            case ScreenId.INPUT_DIALOG:
                return 0;
            case ScreenId.STATUS_INPUT:
                return updateMessageInput();
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
                Contact currentContact = Storage.state().getCurrentContact();
                return (currentContact.flags != 0) || currentContact.dirty ? ScreenId.CLEAR_SEARCH : 0;
            case ScreenId.ASYNC_CONFIRM:
                Object[] stateArr = Storage.state().getObjectArray(RegistrationKeys.OBJ_REGISTRATION_DATA);
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

    public void onMenuItemEvent(ListView screen, MenuItem item) {
        if (item.id == 2 && screen.screenId == ScreenId.BLOG_POST
                && Storage.state().setBool(SessionKeys.FLAG_CAPTCHA_SHOWN, ((Boolean) item.data).booleanValue())) {
            ScreenManager.processScreenForm();
            Storage.state().setFromPool(UIKeys.SLOT_SCREEN_DESCRIPTION, UIKeys.SLOT_SCREEN_VALUE);
            AppController.clearInitParamsAndReport();
        }
    }

    public static final int handleMessageInputAction(String str, int i) {
        String messageText = Utils.defaultStr(Storage.state().getString(UIKeys.SLOT_STATUS_TEXT));
        if (StringUtils.matchesKey(1060, str)) {
            int sendResult = Storage.state().getCurrentContact().sendMessage(messageText);
            if (sendResult != 0) {
                ScreenBuilder.onScreenClosed();
                return NotificationHelper.showError(sendResult);
            }
            Storage.state().setInt(UIKeys.FLAG_STATUS_TEXT_SET, 0);
            Storage.state().clearIndex(UIKeys.SLOT_STATUS_TEXT);
        } else if (StringUtils.matchesKey(473, str)) {
            Storage.state().setFromBuffer(UIKeys.SLOT_STATUS_TEXT, Utils.getMessageBuffer().append(Storage.state().getString(UIKeys.SLOT_NOTIFICATION_TEXT)));
        } else if (StringUtils.matchesKey(474, str)) {
            Storage.state().setObject(UIKeys.SLOT_NOTIFICATION_TEXT, (Object) messageText);
            Storage.state().setBool(UIKeys.FLAG_RESOURCE_LOADING, true);
        } else if (StringUtils.matchesKey(478, str)) {
            Storage.state().setObject(UIKeys.SLOT_STATUS_TEXT, (Object) StringUtils.transliterate(messageText));
        }
        if (i == 93 || i == 123 || i == 95 || i == 94) {
            return 0;
        }
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    public static final int updateMessageInput() {
        try {
            if (XmppContactGroup.getTextInputValue().length() != 0) {
                XmppContactGroup.setTextInputScreen(1055, 1060);
            } else {
                XmppContactGroup.setTextInputScreen(1060, 1055);
            }
            if (Storage.state().getBool(SettingsKeys.SETTING_EXTENDED_PRESENCE)) {
                int timestamp = Storage.state().getInt(RuntimeKeys.INT_CURRENT_TIMESTAMP);
                if (Utils.abs(timestamp - Storage.state().getInt(RuntimeKeys.INT_LAST_CHECK_TIMESTAMP)) > 5000) {
                    Storage.state().setInt(RuntimeKeys.INT_LAST_CHECK_TIMESTAMP, timestamp);
                    int length = XmppContactGroup.getTextInputValue().length();
                    if (length != Storage.state().getInt(RuntimeKeys.INT_LAST_LIST_SIZE) && Utils.abs(timestamp - Storage.state().getInt(RuntimeKeys.INT_LAST_POLL_TIMESTAMP)) > 10000) {
                        Contact currentContact = Storage.state().getCurrentContact();
                        if (!currentContact.isOnline() && !currentContact.hasUnread() && !currentContact.isOffline()) {
                            currentContact.account.validateContactResend(currentContact);
                        }
                        Storage.state().setInt(RuntimeKeys.INT_LAST_POLL_TIMESTAMP, timestamp);
                        Storage.state().setInt(RuntimeKeys.INT_LAST_LIST_SIZE, length);
                    }
                }
            }
            return 0;
        } catch (Throwable unused) {
            return 0;
        }
    }

    public static int handleSoftKeyAction(String label) {
        int chatRoomId = Storage.state().getInt(ChatKeys.INT_CHATROOM_ID);
        MrimAccount account = (MrimAccount) Storage.state().getAccount();
        ChatRoom chatRoom = account.chatRoomManager.findById(chatRoomId);
        IOUtils.setSelectedItems(chatRoom.readMessages);
        if (StringUtils.matchesKey(852, label)) {
            chatRoom.readMessages.removeAllElements();
            return 0;
        }
        if (StringUtils.matchesKey(853, label)) {
            Storage.state().setInt(ChatKeys.INT_CHAT_VIEW_MODE, 2);
            return 0;
        }
        if (StringUtils.matchesKey(854, label)) {
            Storage.state().setInt(ChatKeys.INT_CHAT_VIEW_MODE, 1);
            return 0;
        }
        if (!StringUtils.matchesKey(845, label)) {
            return 0;
        }
        Storage.state().setInt(ChatKeys.INT_ACTIVE_CHATROOM_ID, account.chatRoomManager.findDefault().id);
        return 0;
    }

    public static int handleLeftKey() {
        int errorCode = Storage.state().getCurrentContact().validateDelete();
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return ScreenId.CONTACT_LIST;
    }

    public static int handleRightKey() {
        Storage.state().setInt(SessionKeys.FLAG_APP_STARTING, 1);
        MapController.toggleScrollMode();
        return ScreenId.MAP;
    }

    public static int handleHashKey() {
        if (ScreenManager.hasScreen(ScreenId.CHAT_ROOM_VIEW)) {
            return ScreenId.CHAT_ROOM_VIEW;
        }
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return -1;
    }

    public static int handleAccountOption(int optionId) {
        Account account = Storage.state().getAccount();
        if (!(account instanceof MmpProtocol)) {
            return openSettingsScreen((optionId + XMPP_EMOTICON_ICON_BASE) - XMPP_EMOTICON_CMD_OFFSET, (optionId + XMPP_EMOTICON_LABEL_BASE) - XMPP_EMOTICON_CMD_OFFSET, XMPP_EMOTICON_CMD_OFFSET);
        }
        ((MmpProtocol) account).reserved2 = optionId;
        if (optionId == 0) {
            return ScreenId.STATUS_DIALOG;
        }
        return openSettingsScreen(optionId + MMP_EMOTICON_ICON_BASE, optionId + MMP_EMOTICON_LABEL_BASE, 3);
    }

    private static int openSettingsScreen(int themeId, int valueId, int actionId) {
        Storage.state().setInt(SettingsKeys.INT_EMOTICON_CONFIG_ID, themeId);
        Storage.state().setInt(SettingsKeys.INT_EMOTICON_CONFIG_VALUE_1, themeId);
        Storage.state().setInt(SettingsKeys.INT_EMOTICON_CONFIG_VALUE_2, valueId);
        Storage.state().setInt(SettingsKeys.INT_EMOTICON_CONFIG_ACTION, actionId);
        return ScreenId.CHAT_ROOM_CONFIG;
    }
}
