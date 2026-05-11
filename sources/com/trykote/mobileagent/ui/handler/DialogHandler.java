package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import java.util.Vector;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.util.*;

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
                        if (((XmppProtocol) account).isMailRuVariant()) {
                            dialogScreen.addIconById(387, 642, 1).addIconById(385, 647, 0);
                        } else {
                            dialogScreen.addIconById(383, 642, 1).addIconById(16384383, 643, 4).addIconById(16318847, 644, 2).addIconById(16449919, 645, 5).addIconById(16580991, 648, 6).addIconById(16515455, 646, 3).addIconById(381, 647, 0);
                        }
                        break;
                }
                ScreenManager.showScreen(dialogScreen.setSoftKeys(ResourceAccessor.str(StringResKeys.STR_SOFTKEY_YES), ResourceAccessor.str(StringResKeys.STR_SOFTKEY_NO), 199, 12, 199));
                return;
            case ScreenId.ABOUT:
                UIState.setShowNotification(1);
                UIState.setScreenTitle((Object) StringUtils.intern(Long.toString(Runtime.getRuntime().totalMemory())));
                UIState.setScreenSubtitle((Object) Utils.getFreeMemoryString());
                UIState.setAppVersionStringFromBuffer(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_APP_NAME)).append(ResourceAccessor.str(StringResKeys.STR_APP_BUILD_SUFFIX)));
                RegistrationState.setDeviceId(new ByteBuffer().writeLongBytes(ABOUT_SCREEN_KEY).writeByte(',').writeRawString(AppState.getAppProperty(StringResKeys.STR_APP_PROPERTY_NAME)).getStringAndClear());
                Screens.about(this).show();
                return;
            case ScreenId.BLOCK_CONFIRM:
                NotificationHelper.showAlertById(10, 710);
                return;
            case ScreenId.UNBLOCK_CONFIRM:
                NotificationHelper.showAlertBuffer(11, ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_BLOCK_CONFIRM)).append(AppState.getCurrentContact().displayName).append(ObjectPool.unpackChars(16167)));
                return;
            case ScreenId.CONFIRM_EXIT:
                NotificationHelper.showConfirmDialog(13, 505);
                return;
            case ScreenId.TRAFFIC_COST:
                StringBuffer sb = ObjectPool.newStringBuffer();
                int intVal = SettingsState.getTrafficCost();
                UIState.setScreenValueFromBuffer(sb.append(intVal / 100).append('.').append(Utils.zeroPad(intVal % 100)));
                Screens.trafficCost(this).show();
                return;
            case ScreenId.EMOTICON_DIALOG:
                ListView dialogScreen2 = ScreenManager.createDialogScreen(17);
                if (AppState.getAccount() instanceof MmpProtocol) {
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
                ScreenManager.showScreen(dialogScreen2.setSoftKeys(ResourceAccessor.str(StringResKeys.STR_SOFTKEY_YES), ResourceAccessor.str(StringResKeys.STR_SOFTKEY_NO), 199, 12, 199));
                return;
            case ScreenId.DELETE_CONFIRM:
                NotificationHelper.showConfirmDialog(55, 761);
                return;
            case ScreenId.INPUT_DIALOG:
                Screens.inputDialog(this).show();
                return;
            case ScreenId.STATUS_INPUT:
                XmppContactGroup.showTextInputDialog(AppState.getCurrentContact().displayName, UIState.getStatusText(), 1000, StringUtils.isKnownDevice2 ? 2097152 : 0, ResourceAccessor.str(StringResKeys.STR_INPUT_MODE_DEFAULT), 1059, 1055, new TextInputHandler());
                RuntimeState.resetPollingState();
                ScreenManager.showScreen(new ListView());
                return;
            case ScreenId.SOFTKEY_MENU:
                Screens.softkeyMenu(this).show();
                return;
            case ScreenId.EMOTICON_PICKER:
                Screen screen5 = Screens.emoticonPicker(this);
                if (AppState.getCurrentContact() instanceof MmpContact) {
                    for (int i10 = 0; i10 < PICKER_MMP_COUNT; i10++) {
                        if (ResourceAccessor.blockStr(StringResKeys.MMP_EMOTICONS_BASE, i10) != null) {
                            screen5.addIconTextItem(i10 + PICKER_MMP_ICON_BASE, StringUtils.intern(Integer.toString(i10)), i10);
                        }
                    }
                } else if (AppState.getCurrentContact() instanceof XmppContact) {
                    for (int i11 = 0; i11 < PICKER_XMPP_COUNT; i11++) {
                        if (ResourceAccessor.blockStr(StringResKeys.XMPP_EMOTICONS_BASE, i11) != null) {
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
                Screen screen12 = Screens.captcha(this);
                Object obj4 = RegistrationState.getRegistrationData()[2];
                if (obj4 instanceof javax.microedition.lcdui.Image) {
                    screen12.addItem(MenuItem.createGraphics(new GraphicsContext((javax.microedition.lcdui.Image) obj4)));
                } else {
                    screen12.addLabelById(((Integer) obj4).intValue());
                }
                ScreenManager.showScreen(screen12);
                RegistrationState.clearRegistrationData();
                return;
            case ScreenId.CLEAR_NOTIFICATIONS:
                NotificationHelper.clearNotifications();
                return;
            case ScreenId.PRIVACY_MODE:
                NotificationHelper.playNotificationSound(NotificationHelper.SOUND_MESSAGE_SENT);
                UIState.setNotificationScreenId(ScreenId.PRIVACY_MODE);
                UIState.setNotificationTitleFromPool(StringResKeys.STR_PRIVACY_MODE_BASE);
                NotificationHelper.clearNotifications();
                return;
            case ScreenId.STATUS_PREVIEW:
                String inputText = XmppContactGroup.getTextInputValue();
                UIState.setStatusText((Object) inputText);
                UIState.setStatusTextSet(!StringUtils.isEmpty(inputText));
                Screens.statusPreview(this).show();
                return;
            case ScreenId.PRESENCE_ACTION: {
                RemoteLogger.log("UI", "PRESENCE_ACTION: connecting accounts directly");
                Vector pendingAccounts = SessionState.getAccountSelection();
                if (pendingAccounts != null) {
                    for (int i = pendingAccounts.size() - 1; i >= 0; i--) {
                        Account acc = (Account) pendingAccounts.elementAt(i);
                        RemoteLogger.log("UI", "  connecting: " + acc.login);
                        acc.connect(0);
                    }
                    ObjectPool.releaseVector(pendingAccounts);
                    SessionState.clearAccountSelection();
                }
                return;
            }
            case ScreenId.BLOG_POST:
                String[] langOptions = ScreenManager.getLanguageOptions();
                if (langOptions != null) {
                    UIState.setScreenSubtitle((Object) langOptions[0]);
                    UIState.setScreenTitleFromBuffer(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_LANGUAGE_PREFIX)).append(langOptions[1]));
                    if (RegistrationState.getSearchResultCount() != 0) {
                        UIState.clearLangOption1();
                        UIState.clearLangOption2();
                    }
                    UIState.setScreenValueFromPool(UIKeys.SLOT_LANG_OPTION_1);
                    if (UIState.getScreenDescription() != null) {
                        UIState.setScreenValueFromPool(UIKeys.SLOT_SCREEN_DESCRIPTION);
                        UIState.clearScreenDescription();
                    }
                    Screens.blogPost(this).show();
                    return;
                }
                break;
            case ScreenId.CLEAR_SEARCH:
                ContactListManager.openContactMessages();
                return;
            case ScreenId.ASYNC_CONFIRM:
                NotificationHelper.showConfirmDialog(165, 505);
                RegistrationState.setRegistrationData(RegistrationService.newRequest());
                return;
            case ScreenId.UPDATE_ALERT:
                NotificationHelper.showAlertById(171, 787);
                SessionState.setUpdateAvailable(1);
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
                AppState.setScreen(AppState.getCanvas().updateCommands());
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
                String messageText6 = Utils.defaultStr(UIState.getScreenValue());
                if (StringUtils.isEmpty(messageText6)) {
                    errorCode2 = NotificationHelper.showError(523);
                } else {
                    MrimAccount mrimAccount5 = (MrimAccount) AppState.getAccount();
                    new AsyncTask(AsyncTaskId.SEND_SMS_REQUEST, new ByteBuffer().writeCompressed(PackedStringKeys.URL_MOBILE_MAIL_RU).writeCompressed(PackedStringKeys.API_AGENTUPLOAD).writeUInt(4022591).writeRawString(mrimAccount5.login).writeUInt(4022822).writeRawString(mrimAccount5.password).writeCompressed(PackedStringKeys.PARAM_Z_JAVA).writeCompressed(PackedStringKeys.PARAM_CE).writeRawString(Conversation.urlEncodeCyrillic((Object) messageText6)).writeRawString(Utils.defaultStr(SessionState.isCaptchaShown() ? UIState.getScreenSubtitle() : null)).getStringAndClear());
                    RegistrationState.addSearchResultCount(1);
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
                int intVal = RuntimeState.getPeriodIndex();
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
                if (!StringUtils.isEmpty(inputText) && (sendMsgResult = AppState.getCurrentContact().sendMessage(inputText)) != 0) {
                    ScreenBuilder.onScreenClosed();
                    EventDispatcher.postNotification(AppState.getString(sendMsgResult));
                }
                UIState.setStatusTextSet(0);
                UIState.clearStatusText();
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
                SessionState.setAppStarting(0);
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

    public void onScreenClosed(ListView screen) {
        switch (screen.screenId) {
            case ScreenId.ABOUT:
                UIState.clearScreenProperties();
                break;
            case ScreenId.TRAFFIC_COST:
                UIState.clearScreenValue();
                break;
            case ScreenId.SOFTKEY_MENU:
                IOUtils.setSelectedItems((Object) null);
                break;
            case ScreenId.CLEAR_SEARCH:
                RuntimeState.clearCurrentMsgText();
                UIState.clearStatusText();
                break;
            case ScreenId.PRESENCE_ACTION:
                ObjectPool.releaseVector(SessionState.getAccountSelection());
                SessionState.clearAccountSelection();
                break;
            case ScreenId.BLOG_POST:
                UIState.clearScreenProperties();
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
                        MapState.setMapOverlayActive(true);
                        nextState = ScreenId.MAP;
                    } else {
                        String str = (String) objArr[1];
                        String str2 = (String) objArr[2];
                        long jLongValue = ((Long) objArr[3]).longValue();
                        UIState.clearScreenProperties();
                        ContactState.setGroupOperationResult(0);
                        RegistrationState.setDeviceId(str);
                        UIState.setScreenTitleFromBuffer(ObjectPool.newStringBuffer().append(str2).append(':'));
                        RuntimeState.setSelectedMsgTimestamp(jLongValue);
                        nextState = ScreenId.MESSAGE_INPUT;
                    }
                } else {
                    UIState.clearStatusText();
                    Contact currentContact = AppState.getCurrentContact();
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
                Object[] objArr = RegistrationState.getRegistrationData();
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
                SessionState.addErrors(1);
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
                Contact currentContact = AppState.getCurrentContact();
                return (currentContact.flags != 0) || currentContact.dirty ? ScreenId.CLEAR_SEARCH : 0;
            case ScreenId.ASYNC_CONFIRM:
                Object[] stateArr = RegistrationState.getRegistrationData();
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
                && SessionState.setCaptchaShown(((Boolean) item.data).booleanValue())) {
            ScreenManager.processScreenForm();
            UIState.setScreenDescriptionFromPool(UIKeys.SLOT_SCREEN_VALUE);
            AppController.clearInitParamsAndReport();
        }
    }

    public static final int handleMessageInputAction(String str, int i) {
        String messageText = Utils.defaultStr(UIState.getStatusText());
        if (StringUtils.matchesKey(1060, str)) {
            int sendResult = AppState.getCurrentContact().sendMessage(messageText);
            if (sendResult != 0) {
                ScreenBuilder.onScreenClosed();
                return NotificationHelper.showError(sendResult);
            }
            UIState.setStatusTextSet(0);
            UIState.clearStatusText();
        } else if (StringUtils.matchesKey(473, str)) {
            UIState.setStatusTextFromBuffer(Utils.getMessageBuffer().append(UIState.getNotificationText()));
        } else if (StringUtils.matchesKey(474, str)) {
            UIState.setNotificationText((Object) messageText);
            UIState.setResourceLoading(true);
        } else if (StringUtils.matchesKey(478, str)) {
            UIState.setStatusText((Object) StringUtils.transliterate(messageText));
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
            if (SettingsState.isExtendedPresence()) {
                int timestamp = RuntimeState.getCurrentTimestamp();
                if (Utils.abs(timestamp - RuntimeState.getLastCheckTimestamp()) > 5000) {
                    RuntimeState.setLastCheckTimestamp(timestamp);
                    int length = XmppContactGroup.getTextInputValue().length();
                    if (length != RuntimeState.getLastListSize() && Utils.abs(timestamp - RuntimeState.getLastPollTimestamp()) > 10000) {
                        Contact currentContact = AppState.getCurrentContact();
                        if (!currentContact.isOnline() && !currentContact.hasUnread() && !currentContact.isOffline()) {
                            currentContact.account.validateContactResend(currentContact);
                        }
                        RuntimeState.setLastPollTimestamp(timestamp);
                        RuntimeState.setLastListSize(length);
                    }
                }
            }
            return 0;
        } catch (Throwable unused) {
            return 0;
        }
    }

    public static int handleSoftKeyAction(String label) {
        int chatRoomId = ChatState.getChatRoomId();
        MrimAccount account = (MrimAccount) AppState.getAccount();
        ChatRoom chatRoom = account.chatRoomManager.findById(chatRoomId);
        IOUtils.setSelectedItems(chatRoom.readMessages);
        if (StringUtils.matchesKey(852, label)) {
            chatRoom.readMessages.removeAllElements();
            return 0;
        }
        if (StringUtils.matchesKey(853, label)) {
            ChatState.setChatViewMode(2);
            return 0;
        }
        if (StringUtils.matchesKey(854, label)) {
            ChatState.setChatViewMode(1);
            return 0;
        }
        if (!StringUtils.matchesKey(845, label)) {
            return 0;
        }
        ChatState.setActiveChatRoomId(account.chatRoomManager.findDefault().id);
        return 0;
    }

    public static int handleLeftKey() {
        int errorCode = AppState.getCurrentContact().validateDelete();
        if (errorCode != 0) {
            return NotificationHelper.showError(errorCode);
        }
        return ScreenId.CONTACT_LIST;
    }

    public static int handleRightKey() {
        SessionState.setAppStarting(1);
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
        Account account = AppState.getAccount();
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
        SettingsState.setEmoticonConfigId(themeId);
        SettingsState.setEmoticonConfigValue1(themeId);
        SettingsState.setEmoticonConfigValue2(valueId);
        SettingsState.setEmoticonConfigAction(actionId);
        return ScreenId.CHAT_ROOM_CONFIG;
    }
}
