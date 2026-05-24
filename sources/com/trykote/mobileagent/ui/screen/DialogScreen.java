package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.ChatState;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.key.UIKeys;
import com.trykote.mobileagent.map.MapController;
import com.trykote.mobileagent.map.MapPoint;
import com.trykote.mobileagent.model.ChatRoom;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.net.InlineImageCache;
import com.trykote.mobileagent.net.SocketWrapper;
import com.trykote.mobileagent.net.TrafficAccounting;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.mrim.RegistrationService;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.GraphicsContext;
import com.trykote.mobileagent.ui.ListView;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.Screen;
import com.trykote.mobileagent.ui.ScreenBuilder;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.ui.Screens;
import com.trykote.mobileagent.ui.TextInputHandler;
import com.trykote.mobileagent.ui.TextInputHelper;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.IOUtils;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.SoftFloat;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.lcdui.Image;
import java.util.Vector;

public final class DialogScreen extends ScreenView {

    private static final int MMP_EMOTICON_COUNT = 37;
    private static final int XMPP_EMOTICON_COUNT = 50;
    private static final int XMPP_EXCLUDED_INDEX_1 = 21;
    private static final int XMPP_EXCLUDED_INDEX_2 = 27;
    private static final int XMPP_EMOTICON_ICON_BASE = 161;
    private static final int XMPP_EMOTICON_LABEL_BASE = 155;
    private static final int XMPP_EMOTICON_CMD_OFFSET = 4;
    private static final int MMP_EMOTICON_ICON_BASE = 268;
    private static final int MMP_EMOTICON_LABEL_BASE = 118;
    private static final int PICKER_MMP_COUNT = 43;
    private static final int PICKER_XMPP_COUNT = 37;
    private static final int PICKER_MRIM_START = 10;
    private static final int PICKER_MRIM_END = 74;
    private static final int PICKER_MRIM_ICON_BASE = 36;
    private static final int PICKER_MRIM_EXTRA_START = 0;
    private static final int PICKER_MRIM_EXTRA_END = 10;
    private static final int PICKER_XMPP_ICON_BASE = 318;
    private static final int PICKER_MMP_ICON_BASE = 110;

    public DialogScreen(int screenId) {
        super(ScreenManager.TYPE_FULLSCREEN, screenId);
    }

    public void showSelf() {
        if (screenId == ScreenId.PRESENCE_ACTION) {
            return;
        }
        super.showSelf();
    }

    public void buildContent() {
        switch (screenId) {
            case ScreenId.STATUS_DIALOG: buildStatusDialog(); break;
            case ScreenId.BLOCK_CONFIRM: NotificationHelper.showAlertById(10, 710); break;
            case ScreenId.UNBLOCK_CONFIRM: NotificationHelper.showAlertBuffer(11, ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_BLOCK_CONFIRM)).append(AppState.getCurrentContact().displayName).append(ObjectPool.unpackChars(16167))); break;
            case ScreenId.CONFIRM_EXIT: NotificationHelper.showConfirmDialog(13, 505); break;
            case ScreenId.EMOTICON_DIALOG: buildEmoticonDialog(); break;
            case ScreenId.DELETE_CONFIRM: NotificationHelper.showConfirmDialog(55, 761); break;
            case ScreenId.INPUT_DIALOG: Screens.inputDialog().show(); break;
            case ScreenId.STATUS_INPUT: buildStatusInput(); break;
            case ScreenId.SOFTKEY_MENU: Screens.softkeyMenu().show(); break;
            case ScreenId.EMOTICON_PICKER: buildEmoticonPicker(); break;
            case ScreenId.CAPTCHA: buildCaptcha(); break;
            case ScreenId.CLEAR_NOTIFICATIONS: NotificationHelper.clearNotifications(); break;
            case ScreenId.PRIVACY_MODE: buildPrivacyMode(); break;
            case ScreenId.STATUS_PREVIEW: buildStatusPreview(); break;
            case ScreenId.PRESENCE_ACTION: buildPresenceAction(); break;
            case ScreenId.BLOG_POST: buildBlogPost(); break;
            case ScreenId.CLEAR_SEARCH: ContactListManager.openContactMessages(); break;
            case ScreenId.ASYNC_CONFIRM: buildAsyncConfirm(); break;
            case ScreenId.UPDATE_ALERT: NotificationHelper.showAlertById(171, 787); SessionState.setUpdateAvailable(1); break;
            case ScreenId.INVITE_ALERT: NotificationHelper.showAlertById(173, 416); break;
            case ScreenId.TRAFFIC_STATS: TrafficAccounting.showTrafficStats(); break;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        switch (screenId) {
            case ScreenId.STATUS_DIALOG: return AccountManager.handleStatusChange(action);
            case ScreenId.BLOCK_CONFIRM: return ScreenId.DELETE_CONFIRM;
            case ScreenId.UNBLOCK_CONFIRM: return handleLeftKey();
            case ScreenId.CONFIRM_EXIT: case ScreenId.DELETE_CONFIRM: case ScreenId.ASYNC_CONFIRM: return -1;
            case ScreenId.EMOTICON_DIALOG: return handleAccountOption(action);
            case ScreenId.INPUT_DIALOG: return ScreenManager.processInputText(title);
            case ScreenId.STATUS_INPUT:
                AppController.needsLayoutUpdate = true;
                AppState.setScreen(AppState.getCanvas().updateCommands());
                ScreenBuilder.onScreenClosed();
                return ScreenId.STATUS_PREVIEW;
            case ScreenId.SOFTKEY_MENU: return handleSoftKeyAction(title);
            case ScreenId.EMOTICON_PICKER: return ScreenManager.handleSoundOption(action);
            case ScreenId.CLEAR_NOTIFICATIONS: return -1;
            case ScreenId.PRIVACY_MODE: return handleHashKey();
            case ScreenId.STATUS_PREVIEW: return handleMessageInputAction(title, action);
            case ScreenId.PRESENCE_ACTION: return AccountManager.handlePresenceAction();
            case ScreenId.BLOG_POST: return handleBlogPost();
            case ScreenId.UPDATE_ALERT: return handleRightKey();
            case ScreenId.INVITE_ALERT: return MapScreen.handleGeoSearch();
            case ScreenId.TRAFFIC_STATS: return handleTrafficReset();
            default: return 0;
        }
    }

    public int onSelect(MenuItem item, String title, int selectedOption,
                        Object data, Object headerData) {
        switch (screenId) {
            case ScreenId.STATUS_DIALOG: return AccountManager.handleStatusChange(selectedOption);
            case ScreenId.BLOCK_CONFIRM: return ScreenId.DELETE_CONFIRM;
            case ScreenId.UNBLOCK_CONFIRM: return handleLeftKey();
            case ScreenId.CONFIRM_EXIT: case ScreenId.DELETE_CONFIRM: case ScreenId.ASYNC_CONFIRM: return -1;
            case ScreenId.EMOTICON_DIALOG: return handleAccountOption(selectedOption);
            case ScreenId.INPUT_DIALOG: return ScreenManager.processInputText(title);
            case ScreenId.SOFTKEY_MENU: return handleSoftKeyAction(title);
            case ScreenId.EMOTICON_PICKER: return ScreenManager.handleSoundOption(selectedOption);
            case ScreenId.PRIVACY_MODE: return handleHashKey();
            case ScreenId.STATUS_PREVIEW: return handleMessageInputAction(title, selectedOption);
            case ScreenId.PRESENCE_ACTION: return AccountManager.handlePresenceAction();
            case ScreenId.CLEAR_SEARCH: return handleClearSearchSelect(headerData);
            case ScreenId.UPDATE_ALERT: return handleRightKey();
            case ScreenId.INVITE_ALERT: return MapScreen.handleGeoSearch();
            default: return 0;
        }
    }

    public int onAction(MenuItem item, Object data) {
        switch (screenId) {
            case ScreenId.DELETE_CONFIRM: return -1;
            case ScreenId.STATUS_INPUT:
                int sendResult;
                String inputText = TextInputHelper.getTextInputValue();
                if (!StringUtils.isEmpty(inputText) && (sendResult = AppState.getCurrentContact().sendMessage(inputText)) != 0) {
                    ScreenBuilder.onScreenClosed();
                    EventDispatcher.postNotification(AppState.getString(sendResult));
                }
                UIState.setStatusTextSet(0);
                UIState.clearStatusText();
                AppController.needsLayoutUpdate = true;
                AppState.setScreen(AppState.getCanvas());
                ScreenBuilder.onScreenClosed();
                return ScreenId.CLEAR_SEARCH;
            case ScreenId.PRIVACY_MODE: return handleHashKey();
            case ScreenId.STATUS_PREVIEW: ScreenBuilder.onScreenClosed(); return 0;
            case ScreenId.UPDATE_ALERT:
                SessionState.setAppStarting(0);
                MapController.toggleScrollMode();
                return ScreenId.MAP;
            case ScreenId.INVITE_ALERT:
                AppState.setAccount((Object) null);
                return ScreenId.CLOSE;
            default: return 0;
        }
    }

    public int onIdle(MenuItem item, Object data, String title) {
        switch (screenId) {
            case ScreenId.CONFIRM_EXIT: return processConfirmExitIdle();
            case ScreenId.DELETE_CONFIRM: return processDeleteConfirmIdle();
            case ScreenId.STATUS_INPUT: return updateMessageInput();
            case ScreenId.CLEAR_SEARCH: return processMessageScreenIdle();
            case ScreenId.ASYNC_CONFIRM: return processAsyncConfirmIdle();
            default: return 0;
        }
    }

    public void onClosed() {
        switch (screenId) {
            case ScreenId.SOFTKEY_MENU: IOUtils.setSelectedItems((Object) null); break;
            case ScreenId.CLEAR_SEARCH: RuntimeState.clearCurrentMsgText(); UIState.clearStatusText(); break;
            case ScreenId.PRESENCE_ACTION: ObjectPool.releaseVector(SessionState.getAccountSelection()); SessionState.clearAccountSelection(); break;
            case ScreenId.BLOG_POST: UIState.clearScreenProperties(); break;
        }
    }

    public void onMenuItemChanged(MenuItem item) {
        if (item.id == 2 && screenId == ScreenId.BLOG_POST
                && SessionState.setCaptchaShown(((Boolean) item.data).booleanValue())) {
            ScreenManager.processScreenForm();
            UIState.setScreenDescriptionFromPool(UIKeys.SLOT_SCREEN_VALUE);
            AppController.clearInitParamsAndReport();
        }
    }

    // --- Build helpers ---

    private static void buildStatusDialog() {
        ListView dialogScreen = ScreenManager.createDialogScreen(3);
        Account account = AppState.getAccount();
        switch (account.getType()) {
            case 0:
                dialogScreen.addIcon(156, AppState.getString(642), 0).addIcon(159, AppState.getString(643), 1).addIcon(157, AppState.getString(644), 2).addIcon(160, AppState.getString(645), 3).addIcon(158, AppState.getString(646), 4).addIcon(155, AppState.getString(647), 5).addAction(-1, AppState.getString(718), 6);
                break;
            case 1:
                int iconResId = account.getIconResourceId();
                dialogScreen.addIcon(iconResId, AppState.getString(642), 0).addIcon(iconResId | 16384000, AppState.getString(643), 1).addIcon(iconResId | 16449536, AppState.getString(645), 3).addIcon(iconResId | 16318464, AppState.getString(644), 4).addIcon(iconResId | 16580608, AppState.getString(648), 5).addIcon(iconResId | 16646144, AppState.getString(654), 6).addIcon(iconResId | 17039360, AppState.getString(649), 7).addIcon(iconResId | 16973824, AppState.getString(650), 8).addIcon(iconResId | 16908288, AppState.getString(651), 9).addIcon(iconResId | 16842752, AppState.getString(652), 10).addIcon(iconResId | 17104896, AppState.getString(653), 11).addIcon(iconResId | 16515072, AppState.getString(646), 2).addIcon(255, AppState.getString(647), 12).addAction(account.getExtType(), AppState.getString(655), 14).addAction(-1, AppState.getString(718), 13);
                break;
            default:
                if (account.isMailRuVariant()) {
                    dialogScreen.addIcon(387, AppState.getString(642), 1).addIcon(385, AppState.getString(647), 0);
                } else {
                    dialogScreen.addIcon(383, AppState.getString(642), 1).addIcon(16384383, AppState.getString(643), 4).addIcon(16318847, AppState.getString(644), 2).addIcon(16449919, AppState.getString(645), 5).addIcon(16580991, AppState.getString(648), 6).addIcon(16515455, AppState.getString(646), 3).addIcon(381, AppState.getString(647), 0);
                }
                break;
        }
        ScreenManager.showScreen(dialogScreen.setSoftKeys(StringPool.get(StringResKeys.STR_SOFTKEY_YES), StringPool.get(StringResKeys.STR_SOFTKEY_NO), 199, 12, 199));
    }

    private static void buildEmoticonDialog() {
        ListView dialogScreen = ScreenManager.createDialogScreen(17);
        if (AppState.getAccount().getType() == Account.TYPE_MMP) {
            for (int i = 0; i < MMP_EMOTICON_COUNT; i++) {
                dialogScreen.addIcon(i + MMP_EMOTICON_ICON_BASE, AppState.getString(i + MMP_EMOTICON_LABEL_BASE), i);
            }
        } else {
            for (int i = 0; i < XMPP_EMOTICON_COUNT; i++) {
                if (i != XMPP_EXCLUDED_INDEX_1 && i != XMPP_EXCLUDED_INDEX_2) {
                    dialogScreen.addIcon(i + XMPP_EMOTICON_ICON_BASE, AppState.getString(i + XMPP_EMOTICON_LABEL_BASE), i + XMPP_EMOTICON_CMD_OFFSET);
                }
            }
        }
        ScreenManager.showScreen(dialogScreen.setSoftKeys(StringPool.get(StringResKeys.STR_SOFTKEY_YES), StringPool.get(StringResKeys.STR_SOFTKEY_NO), 199, 12, 199));
    }

    private static void buildStatusInput() {
        TextInputHelper.showTextInputDialog(AppState.getCurrentContact().displayName, UIState.getStatusText(), 1000, StringUtils.isKnownDevice2 ? 2097152 : 0, StringPool.get(StringResKeys.STR_INPUT_MODE_DEFAULT), 1059, 1055, new TextInputHandler());
        RuntimeState.resetPollingState();
        ScreenManager.showScreen(new ListView());
    }

    private static void buildEmoticonPicker() {
        Screen screen = Screens.emoticonPicker();
        int contactProtocol = AppState.getCurrentContact().account.getType();
        if (contactProtocol == Account.TYPE_MMP) {
            for (int i = 0; i < PICKER_MMP_COUNT; i++) {
                if (StringPool.get(StringResKeys.MMP_EMOTICONS_BASE + i) != null) {
                    screen.addIconTextItem(i + PICKER_MMP_ICON_BASE, StringUtils.intern(Integer.toString(i)), i);
                }
            }
        } else if (AppState.getCurrentContact().account.isXmppType()) {
            for (int i = 0; i < PICKER_XMPP_COUNT; i++) {
                if (StringPool.get(StringResKeys.XMPP_EMOTICONS_BASE + i) != null) {
                    screen.addIconTextItem(i + PICKER_XMPP_ICON_BASE, StringUtils.intern(Integer.toString(i)), i);
                }
            }
        } else {
            for (int i = PICKER_MRIM_START; i < PICKER_MRIM_END; i++) {
                screen.addIconTextItem(i + PICKER_MRIM_ICON_BASE, StringUtils.intern(Integer.toString(i)), i);
            }
            for (int i = PICKER_MRIM_EXTRA_START; i < PICKER_MRIM_EXTRA_END; i++) {
                screen.addIconTextItem(i + PICKER_MRIM_ICON_BASE, StringUtils.intern(Integer.toString(i)), i);
            }
            screen.addIconTextItem(142, StringUtils.intern(Integer.toString(74)), 74);
            screen.addIconTextItem(137, StringUtils.intern(Integer.toString(75)), 75);
            screen.addIconTextItem(210, StringUtils.intern(Integer.toString(76)), 76);
            screen.addIconTextItem(205, StringUtils.intern(Integer.toString(77)), 77);
        }
        ScreenManager.showScreen(screen);
    }

    private static void buildCaptcha() {
        Screen screen = Screens.captcha();
        Object obj = RegistrationState.getRegistrationData()[2];
        if (obj instanceof javax.microedition.lcdui.Image) {
            screen.addItem(MenuItem.createGraphics(new GraphicsContext((javax.microedition.lcdui.Image) obj)));
        } else {
            screen.addLabel(AppState.getString(((Integer) obj).intValue()));
        }
        ScreenManager.showScreen(screen);
        RegistrationState.clearRegistrationData();
    }

    private static void buildPrivacyMode() {
        NotificationHelper.playNotificationSound(NotificationHelper.SOUND_MESSAGE_SENT);
        UIState.setNotificationScreenId(ScreenId.PRIVACY_MODE);
        UIState.setNotificationTitleFromPool(StringResKeys.STR_PRIVACY_MODE_BASE);
        NotificationHelper.clearNotifications();
    }

    private static void buildStatusPreview() {
        String inputText = TextInputHelper.getTextInputValue();
        UIState.setStatusText((Object) inputText);
        UIState.setStatusTextSet(!StringUtils.isEmpty(inputText));
        Screens.statusPreview().show();
    }

    private static void buildPresenceAction() {
        Vector pendingAccounts = SessionState.getAccountSelection();
        if (pendingAccounts != null) {
            for (int i = pendingAccounts.size() - 1; i >= 0; i--) {
                ((Account) pendingAccounts.elementAt(i)).connect(0);
            }
            ObjectPool.releaseVector(pendingAccounts);
            SessionState.clearAccountSelection();
        }
    }

    private static void buildBlogPost() {
        String[] langOptions = ScreenManager.getLanguageOptions();
        if (langOptions != null) {
            UIState.setScreenSubtitle((Object) langOptions[0]);
            UIState.setScreenTitleFromBuffer(ObjectPool.newStringBuffer().append(StringPool.get(StringResKeys.STR_LANGUAGE_PREFIX)).append(langOptions[1]));
            if (RegistrationState.getSearchResultCount() != 0) {
                UIState.clearLangOption1();
                UIState.clearLangOption2();
            }
            UIState.setScreenValueFromPool(UIKeys.SLOT_LANG_OPTION_1);
            if (UIState.getScreenDescription() != null) {
                UIState.setScreenValueFromPool(UIKeys.SLOT_SCREEN_DESCRIPTION);
                UIState.clearScreenDescription();
            }
            Screens.blogPost().show();
        }
    }

    private static void buildAsyncConfirm() {
        NotificationHelper.showConfirmDialog(165, 505);
        RegistrationState.setRegistrationData(RegistrationService.newRequest());
    }

    // --- Selection handlers ---

    private static int handleBlogPost() {
        ScreenManager.processScreenForm();
        String messageText = Utils.defaultStr(UIState.getScreenValue());
        if (StringUtils.isEmpty(messageText)) {
            return NotificationHelper.showError(523);
        }
        Account smsAccount = AppState.getAccount();
        new AsyncTask(AsyncTaskId.SEND_SMS_REQUEST, new ByteBuffer().writeCharBytes("http://mobile.mail.ru/").writeCharBytes("data/agentupload").writeUInt(4022591).writeRawString(smsAccount.login).writeUInt(4022822).writeRawString(smsAccount.password).writeCharBytes("&z=java").writeCharBytes("&c=2&e=").writeRawString(Conversation.urlEncodeCyrillic((Object) messageText)).writeRawString(Utils.defaultStr(SessionState.isCaptchaShown() ? UIState.getScreenSubtitle() : null)).getStringAndClear());
        RegistrationState.addSearchResultCount(1);
        return 0;
    }

    private static int handleTrafficReset() {
        int intVal = RuntimeState.getPeriodIndex();
        Account account = AppState.getAccount();
        if (account != null) {
            account.syncArray[intVal + intVal + 1] = 0;
            account.syncArray[intVal + intVal] = 0;
        } else {
            for (int i = 0; i < 4; i++) {
                TrafficAccounting.addTrafficCount(i, intVal, 0);
                TrafficAccounting.addTrafficCount(i, intVal, 1);
            }
        }
        return 0;
    }

    private static int handleClearSearchSelect(Object headerData) {
        if (headerData != null) {
            Object[] objArr = (Object[]) headerData;
            int marker = ((Integer) objArr[0]).intValue();
            if (marker == 2) {
                return handleImageClick((String) objArr[1]);
            }
            if (marker == 0) {
                MapPoint mapPoint = new MapPoint((String) objArr[1]);
                mapPoint.height = 2;
                MapController.navigateToPoint(mapPoint, false);
                MapState.setMapOverlayActive(true);
                return ScreenId.MAP;
            }
            String str = (String) objArr[1];
            String str2 = (String) objArr[2];
            long timestamp = ((Long) objArr[3]).longValue();
            UIState.clearScreenProperties();
            ContactState.setGroupOperationResult(0);
            RegistrationState.setDeviceId(str);
            UIState.setScreenTitleFromBuffer(ObjectPool.newStringBuffer().append(str2).append(':'));
            RuntimeState.setSelectedMsgTimestamp(timestamp);
            return ScreenId.MESSAGE_INPUT;
        }
        UIState.clearStatusText();
        Contact currentContact = AppState.getCurrentContact();
        return !currentContact.account.isConnected() ? NotificationHelper.showError(299) : currentContact.isOffline() ? ContactListManager.clearSmsFields() : 63;
    }

    private static int handleImageClick(String url) {
        Image cached = InlineImageCache.getImage(url);
        if (cached != null) {
            showImagePopup(cached);
            return -1;
        }
        if (!InlineImageCache.isDownloading(url)) {
            InlineImageCache.markDownloading(url);
            new AsyncTask(AsyncTaskId.DOWNLOAD_INLINE_IMAGE, url);
        }
        return 0;
    }

    static void showImagePopup(Image image) {
        Screen popup = new Screen(ScreenManager.TYPE_TOAST, ScreenId.NONE);
        popup.configureSoftKeys(null, 0, "Назад", 12, 0);
        popup.addItem(MenuItem.createInlineImage(image));
        popup.show();
        // force GC so the peak does not stay near the OOM ceiling.
        System.gc();
    }

    // --- Public static ---

    public static int handleMessageInputAction(String str, int i) {
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

    public static int updateMessageInput() {
        try {
            if (TextInputHelper.getTextInputValue().length() != 0) {
                TextInputHelper.setTextInputScreen(1055, 1060);
            } else {
                TextInputHelper.setTextInputScreen(1060, 1055);
            }
            if (SettingsState.isExtendedPresence()) {
                int timestamp = RuntimeState.getCurrentTimestamp();
                if (Utils.abs(timestamp - RuntimeState.getLastCheckTimestamp()) > 5000) {
                    RuntimeState.setLastCheckTimestamp(timestamp);
                    int length = TextInputHelper.getTextInputValue().length();
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
        Account account = AppState.getAccount();
        ChatRoom chatRoom = account.findChatRoomById(chatRoomId);
        IOUtils.setSelectedItems(chatRoom.readMessages);
        if (StringUtils.matchesKey(852, label)) { chatRoom.readMessages.removeAllElements(); return 0; }
        if (StringUtils.matchesKey(853, label)) { ChatState.setChatViewMode(2); return 0; }
        if (StringUtils.matchesKey(854, label)) { ChatState.setChatViewMode(1); return 0; }
        if (StringUtils.matchesKey(845, label)) { ChatState.setActiveChatRoomId(account.getDefaultChatRoomId()); return 0; }
        return 0;
    }

    public static int handleLeftKey() {
        int errorCode = AppState.getCurrentContact().validateDelete();
        return errorCode != 0 ? NotificationHelper.showError(errorCode) : ScreenId.CONTACT_LIST;
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
        if (account.getType() != Account.TYPE_MMP) {
            return openSettingsScreen((optionId + XMPP_EMOTICON_ICON_BASE) - XMPP_EMOTICON_CMD_OFFSET, (optionId + XMPP_EMOTICON_LABEL_BASE) - XMPP_EMOTICON_CMD_OFFSET, XMPP_EMOTICON_CMD_OFFSET);
        }
        account.setEmoticonSelection(optionId);
        if (optionId == 0) {
            return ScreenId.STATUS_DIALOG;
        }
        return openSettingsScreen(optionId + MMP_EMOTICON_ICON_BASE, optionId + MMP_EMOTICON_LABEL_BASE, 3);
    }

    // --- Idle processing ---

    private static int processConfirmExitIdle() {
        Object[] objArr = RegistrationState.getRegistrationData();
        Object obj = objArr[0];
        if (obj == null) {
            String str = (String) objArr[20];
            if ((str != null && Utils.parseInt((Object) str) == 0) || objArr[3] != null) {
                return RegistrationService.handleRegSubmit(objArr);
            }
        } else {
            NotificationHelper.showNotification(StringUtils.concatKeyObj(506, obj));
        }
        return 0;
    }

    private static int processDeleteConfirmIdle() {
        SocketWrapper.closeAll();
        AppController.clearImageCache();
        SoftFloat.clearMathTables();
        System.gc();
        try { Thread.sleep(50); } catch (Throwable unused) {}
        SessionState.addErrors(1);
        AppController.saveOnExit = true;
        AppController.isBackgrounded = true;
        return 0;
    }

    private static int processAsyncConfirmIdle() {
        Object[] stateArr = RegistrationState.getRegistrationData();
        Object obj = stateArr[0];
        if (obj != null) {
            NotificationHelper.showNotification(StringUtils.concatKeyObj(506, obj));
            return 0;
        }
        return stateArr[3] == null ? 0 : RegistrationService.handleRegSubmit(stateArr);
    }

    private static int processMessageScreenIdle() {
        Image pending = InlineImageCache.takePendingImage();
        if (pending != null) {
            showImagePopup(pending);
            return 0;
        }
        updateImageProgress();
        Contact contact = AppState.getCurrentContact();
        return (contact.flags != 0) || contact.dirty ? ScreenId.CLEAR_SEARCH : 0;
    }

    private static final int GFX_EXPANDABLE = 5;
    private static final int IMAGE_DATA_MIN_LEN = 4;
    private static final int IMAGE_DATA_MARKER = 2;

    private static void updateImageProgress() {
        if (!InlineImageCache.consumeStateChange()) return;
        ListView screen = ScreenManager.getCurrentScreen();
        if (screen == null) return;
        Vector items = screen.menuItems;
        int size = items.size();
        for (int i = 0; i < size; i++) {
            MenuItem item = (MenuItem) items.elementAt(i);
            if (!(item.data instanceof Object[])) continue;
            Object[] arr = (Object[]) item.data;
            if (arr.length < IMAGE_DATA_MIN_LEN) continue;
            if (!(arr[0] instanceof Integer)) continue;
            if (((Integer) arr[0]).intValue() != IMAGE_DATA_MARKER) continue;
            String url = (String) arr[1];
            int colorStyle = ((Integer) arr[2]).intValue();
            String oldLabel = (String) arr[3];
            String newLabel = InlineImageCache.resolveImageLabel(url);
            if (!newLabel.equals(oldLabel)) {
                arr[3] = newLabel;
                item.clear();
                item.addText(newLabel, GFX_EXPANDABLE, colorStyle);
                AppController.needsRepaint = true;
            }
        }
    }

    // --- Private ---

    private static int openSettingsScreen(int themeId, int valueId, int actionId) {
        SettingsState.setEmoticonConfigId(themeId);
        SettingsState.setEmoticonConfigValue1(themeId);
        SettingsState.setEmoticonConfigValue2(valueId);
        SettingsState.setEmoticonConfigAction(actionId);
        return ScreenId.CHAT_ROOM_CONFIG;
    }
}
