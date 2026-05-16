package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.ChatState;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.MapKeys;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.RegistrationKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.key.UIKeys;
import com.trykote.mobileagent.map.GeoRegion;
import com.trykote.mobileagent.map.MapController;
import com.trykote.mobileagent.model.ChatRoom;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.Message;
import com.trykote.mobileagent.net.ApiClient;
import com.trykote.mobileagent.net.ServiceRegistry;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.mrim.MrimProfileManager;
import com.trykote.mobileagent.protocol.mrim.RegistrationService;
import com.trykote.mobileagent.protocol.xmpp.XmppMailRuProtocol;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.ListItem;
import com.trykote.mobileagent.ui.ListView;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.Screen;
import com.trykote.mobileagent.ui.ScreenBuilder;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.ui.Screens;
import com.trykote.mobileagent.util.DiagnosticReporter;
import com.trykote.mobileagent.util.JsonParser;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.RemoteLogger;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.TimerManager;
import com.trykote.mobileagent.util.Utils;

import java.util.Vector;

public final class MiscScreen extends ScreenView {

    private static final int PHONE_STRING_COUNT = 15;

    public MiscScreen(int screenId) {
        super(ScreenManager.TYPE_FULLSCREEN, screenId);
    }

    public void showSelf() {
        switch (screenId) {
            case ScreenId.CONVERSATION:
            case ScreenId.PHONE_CONTACTS:
            case ScreenId.ASYNC_TASK:
                break;
            case ScreenId.ACCOUNT_SETUP:
                ScreenManager.pushScreen(Screens.accountSetup());
                break;
            default:
                ScreenManager.showScreen(this);
        }
    }

    public void buildContent() {
        switch (screenId) {
            case ScreenId.FIRST_RUN:
                buildFirstRun();
                break;
            case ScreenId.VERSION_CHECK:
                RegistrationService.processUpdateResult();
                break;
            case ScreenId.TOS_SCREEN:
            case ScreenId.INVITE_TOS:
                showTosScreen();
                break;
            case ScreenId.EVENT_QUEUE:
                AppState.getAccount().showChatRoomSelector();
                break;
            case ScreenId.PHONE_INPUT:
                buildPhoneScreen(Screens.phoneInput());
                break;
            case ScreenId.SERVER_ADDRESS:
                buildPhoneScreen(Screens.serverAddress());
                break;
            case ScreenId.REGION_SELECTOR:
                buildRegionSelector();
                break;
            case ScreenId.PHONE_INPUT_ALT:
                buildPhoneScreen(Screens.phoneInputAlt());
                break;
            case ScreenId.URL_OPEN:
                buildPhoneScreen(Screens.urlOpen());
                break;
            case ScreenId.CONVERSATION:
            case ScreenId.PHONE_CONTACTS:
                break;
            case ScreenId.VERSION_SELECT:
                Screen versionScreen = Screens.versionSelect();
                versionScreen.selectByTitle(StringPool.get(StringResKeys.STR_PRIVACY_MODE_BASE + AppState.getAccount().getPendingVersion()));
                versionScreen.show();
                break;
            case ScreenId.EMPTY_SCREEN:
                Screens.emptyScreen().show();
                break;
            case ScreenId.WIFI_NETWORKS:
                showWiFiNetworks();
                break;
            case ScreenId.REG_FORM:
                RegistrationService.processRegForm();
                break;
            case ScreenId.FORM_LIST:
                buildFormList();
                break;
            case ScreenId.EDIT_SCREEN:
                Screens.editScreen().show();
                break;
            case ScreenId.ASYNC_TASK:
                break;
            case ScreenId.MAIN_SCREEN:
                Screens.mainScreen().show();
                if (SessionState.hasXmppAccount()) {
                    TimerManager.disableBacklight();
                }
                break;
            case ScreenId.SHARE_MEDIA:
                buildShareMedia();
                break;
            case ScreenId.SHARE_ALERT:
                NotificationHelper.showAlertById(79, 863);
                break;
            case ScreenId.PHOTO_SELECTOR:
                MrimProfileManager.showPhotoSelector();
                break;
            case ScreenId.ACCOUNT_SETUP:
                break;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        switch (screenId) {
            case ScreenId.FIRST_RUN:
            case ScreenId.TOS_SCREEN:
            case ScreenId.ASYNC_TASK:
            case ScreenId.MAIN_SCREEN:
            case ScreenId.SHARE_MEDIA:
                return -1;
            case ScreenId.VERSION_CHECK:
                return RegistrationService.applyVersionLabel();
            case ScreenId.EVENT_QUEUE:
                return handleEventObject(data);
            case ScreenId.PHONE_INPUT:
            case ScreenId.PHONE_INPUT_ALT:
                return ScreenManager.processPhoneInput(title);
            case ScreenId.SERVER_ADDRESS:
                return ScreenManager.validateServerAddress(title);
            case ScreenId.REGION_SELECTOR:
                return MapController.handleSearchResultAction(data);
            case ScreenId.URL_OPEN:
                return openUrl(title);
            case ScreenId.CONVERSATION:
                return MapController.handleMapPointAction(data);
            case ScreenId.VERSION_SELECT:
                return AppState.getAccount().scheduleVersionUpdate(action);
            case ScreenId.WIFI_NETWORKS:
                return setSelectedObject(data);
            case ScreenId.REG_FORM:
                return handleRegForm();
            case ScreenId.INVITE_TOS:
                return collectInvitees(ScreenManager.getCurrentScreen());
            case ScreenId.FORM_LIST:
                return ScreenManager.handleFormSubmit(data);
            case ScreenId.PHONE_CONTACTS:
                return handleStarAction(data);
            case ScreenId.EDIT_SCREEN:
                return handleEditAction(action);
            case ScreenId.SHARE_ALERT:
                ScreenBuilder.onScreenClosed();
                ScreenBuilder.onScreenClosed();
                return 0;
            case ScreenId.PHOTO_SELECTOR:
                return MrimProfileManager.applyPhotoSelection();
            case ScreenId.ACCOUNT_SETUP:
                if (action == ScreenId.XMPP_LOGIN) {
                    SessionState.setProtocolType(Account.TYPE_XMPP);
                    return ScreenId.XMPP_LOGIN_ALT;
                }
                return 0;
            default:
                return 0;
        }
    }

    public int onSelect(MenuItem item, String title, int selectedOption,
                        Object data, Object headerData) {
        switch (screenId) {
            case ScreenId.FIRST_RUN:
            case ScreenId.TOS_SCREEN:
            case ScreenId.ASYNC_TASK:
            case ScreenId.MAIN_SCREEN:
            case ScreenId.SHARE_MEDIA:
                return -1;
            case ScreenId.VERSION_CHECK:
                return RegistrationService.applyVersionLabel();
            case ScreenId.EVENT_QUEUE:
                return handleEventObject(data);
            case ScreenId.PHONE_INPUT:
            case ScreenId.PHONE_INPUT_ALT:
                return ScreenManager.processPhoneInput(title);
            case ScreenId.SERVER_ADDRESS:
                return ScreenManager.validateServerAddress(title);
            case ScreenId.REGION_SELECTOR:
                return MapController.handleSearchResultAction(data);
            case ScreenId.URL_OPEN:
                return openUrl(title);
            case ScreenId.CONVERSATION:
                return MapController.handleMapPointAction(data);
            case ScreenId.VERSION_SELECT:
                return AppState.getAccount().scheduleVersionUpdate(selectedOption);
            case ScreenId.WIFI_NETWORKS:
                return setSelectedObject(data);
            case ScreenId.FORM_LIST:
                return ScreenManager.handleFormSubmit(data);
            case ScreenId.PHONE_CONTACTS:
                return selectedOption == 1 ? ContactListManager.dialPhoneContactPrev() : selectedOption == 2 ? ContactListManager.dialPhoneContactNext() : handleStarAction(data);
            case ScreenId.EDIT_SCREEN:
                return handleEditAction(selectedOption);
            case ScreenId.SHARE_ALERT:
                ScreenBuilder.onScreenClosed();
                ScreenBuilder.onScreenClosed();
                return 0;
            case ScreenId.PHOTO_SELECTOR:
                return MrimProfileManager.applyPhotoSelection();
            case ScreenId.ACCOUNT_SETUP:
                if (selectedOption == ScreenId.XMPP_LOGIN) {
                    SessionState.setProtocolType(Account.TYPE_XMPP);
                    return ScreenId.XMPP_LOGIN_ALT;
                }
                return 0;
            default:
                return 0;
        }
    }

    public int onAction(MenuItem item, Object data) {
        switch (screenId) {
            case ScreenId.CONVERSATION:
                ContactState.setLoaded(false);
                UIState.setNewMessage(0);
                return 0;
            case ScreenId.PHONE_CONTACTS:
                return ScreenId.CLOSE;
            case ScreenId.MAIN_SCREEN:
                return ScreenId.CLOSE;
            default:
                return 0;
        }
    }

    public int onIdle(MenuItem item, Object data, String title) {
        switch (screenId) {
            case ScreenId.FIRST_RUN:
                return RegistrationState.getRegistrationData()[0] == null ? 0 : ScreenId.VERSION_CHECK;
            case ScreenId.ASYNC_TASK:
                return UIState.getInitParams() == null ? 0 : ScreenId.BLOG_POST;
            case ScreenId.SHARE_MEDIA:
                return processShareMediaIdle();
            default:
                return 0;
        }
    }

    public void onClosed() {
        switch (screenId) {
            case ScreenId.VERSION_CHECK:
                UIState.clearScreenTitle();
                UIState.clearScreenSubtitle();
                RegistrationState.clearRegistrationData();
                break;
            case ScreenId.CONVERSATION:
                UIState.setNewMessage(0);
                UIState.setLoading(0);
                break;
            case ScreenId.SHARE_MEDIA:
                RegistrationState.clearRegistrationData();
                break;
            case ScreenId.PHOTO_SELECTOR:
                MrimProfileManager.photoUrlList = null;
                MrimProfileManager.contactIdList = null;
                break;
            case ScreenId.REG_FORM:
                ChatState.clearChatName();
                RegistrationState.clearPassword();
                UIState.clearScreenTitle();
                SessionState.setServerIndex(0);
                SettingsState.setSettingsTheme(0);
                AppState.clearRange(MapKeys.SLOT_MAP_SEARCH_QUERY, MapKeys.STR_MAP_LOCATION_URL);
                UIState.clearScreenProperties();
                StringUtils.resetRegForm();
                break;
            case ScreenId.INVITE_TOS:
                ScreenManager.clearFormFields();
                break;
            case ScreenId.PHONE_CONTACTS:
                AppState.clearRange(UIKeys.RANGE_PHONE_CONTACT_START, RegistrationKeys.OBJ_SEARCH_RESULT);
                break;
            case ScreenId.ASYNC_TASK:
                RegistrationState.clearRegistrationData();
                break;
        }
    }

    public void onMenuItemChanged(MenuItem item) {
        if (screenId != ScreenId.REG_FORM) {
            return;
        }
        ListView screen = ScreenManager.getCurrentScreen();
        Object[] itemData = (Object[]) item.data;
        int selectedIndex = ((Integer) itemData[0]).intValue();
        String[] options = (String[]) itemData[1];
        MenuItem phoneItem = null;
        Vector items = screen.menuItems;
        for (int itemIdx = items.size() - 1; itemIdx >= 0; itemIdx--) {
            MenuItem entry = (MenuItem) items.elementAt(itemIdx);
            if (entry.id == 15 && entry.title.startsWith(StringPool.get(StringResKeys.STR_MENU_PHONE_PREFIX))) {
                phoneItem = entry;
            }
        }
        if (item.title.equals(StringPool.get(StringResKeys.STR_MENU_OPTIONS))) {
            String optionStr = selectedIndex == 0 ? Utils.defaultStr(RegistrationState.getDeviceId()) : options[selectedIndex];
            Object[] phoneData = (Object[]) phoneItem.data;
            phoneItem.clear().setAction(phoneData[4], optionStr, phoneData[1], phoneData[2], phoneData[3]);
        }
        screen.rebuildItems();
    }

    // --- Build helpers ---

    private static void buildFirstRun() {
        NotificationHelper.showConfirmDialog(57, 730);
        SessionState.setTimestampFirstRun(System.currentTimeMillis());
        Object[] objArr = new Object[1];
        RegistrationState.setRegistrationData(objArr);
        new AsyncTask(AsyncTaskId.FETCH_TILE_BUFFER, objArr);
        DiagnosticReporter.checkCrashReport();
    }

    private static void buildPhoneScreen(Screen screen) {
        for (int i = 0; i < PHONE_STRING_COUNT; i++) {
            screen.addTextItem(StringPool.get(StringResKeys.PHONE_STRINGS_BASE + i));
        }
        ScreenManager.showScreen(screen);
    }

    private static void buildRegionSelector() {
        Vector regions = MapState.getMapPoints();
        int size = regions.size();
        if (size == 0) {
            NotificationHelper.showMessageById(397);
            return;
        }
        Screen screen = Screens.regionSelector();
        for (int i = 0; i < size; i++) {
            GeoRegion region = (GeoRegion) regions.elementAt(i);
            screen.addIconItemWithData(-1, region.name, 6, region);
        }
        GeoRegion currentRegion = StringUtils.getGeoRegion();
        screen.addIconItemWithData(-1, currentRegion.name, 6, currentRegion);
        ScreenManager.showScreen(screen);
    }

    private static void buildFormList() {
        Screen screen = Screens.formList();
        Vector vector = ((Conversation) UIState.getTempObject1()).items;
        for (int idx = vector.size() - 1; idx >= 0; idx--) {
            ListItem listItem = (ListItem) vector.elementAt(idx);
            screen.addIconItemWithData(-1, listItem.getText(), 0, listItem);
        }
        ScreenManager.showScreen(screen);
        UIState.clearTempObject1();
    }

    private static void buildShareMedia() {
        NotificationHelper.showConfirmDialog(78, 861);
        Vector params = ObjectPool.newVector();
        params.addElement(UIState.getMediaStream());
        params.addElement(ObjectPool.integerOf(UIState.isSpecialKeyMode() ? 1 : 0));
        AppState.getAccount().sendChatRoomRequest(ApiClient.createUploadRequest(StringPool.get(PackedStringKeys.URL_PATH_AJAX_SPAMABUSE), ApiClient.appendAuthParams(ObjectPool.newStringBuffer().append(StringPool.get(PackedStringKeys.PARAM_AJAX_CALL)).append(StringPool.get(PackedStringKeys.FUNC_AJAX_SPAMABUSE)), Conversation.urlEncode((Object) JsonParser.toJson(params)))));
    }

    // --- Selection handlers ---

    private static int handleRegForm() {
        ScreenManager.processScreenForm();
        String loginLower = XmppMailRuProtocol.getLoginLowerCase();
        String fullLogin = loginLower;
        if (!XmppMailRuProtocol.isMailRuDomain(loginLower)) {
            fullLogin = StringUtils.concat(fullLogin, Utils.splitAndGet(StringResKeys.STR_DOMAIN_LIST, SessionState.getServerIndex()));
        }
        if (!XmppMailRuProtocol.isValidUsername(fullLogin)) {
            return NotificationHelper.showError(559);
        }
        String password = Utils.defaultStr(RegistrationState.getPassword());
        String firstName = Utils.defaultStr(UIState.getScreenTitle());
        int themeIdx = SettingsState.getSettingsTheme();
        RegistrationState.setRegistrationData(RegistrationService.createRegRequest(fullLogin, 0, password, firstName, themeIdx == 0 ? Utils.defaultStr(RegistrationState.getDeviceId()) : (String) Utils.splitNonEmpty(StringPool.get(StringResKeys.STR_MENU_REG_SMS), (char) 0).elementAt(themeIdx), Utils.defaultStr(UIState.getAppVersionString()), Utils.defaultStr(RegistrationState.getFirstName()), Utils.defaultStr(RegistrationState.getLastName()), RegistrationState.getSearchGender(), RegistrationState.getSearchAge(), RegistrationState.getRegDomainIndex(), RegistrationState.getCountryCode(), RegistrationState.getRegionCode(), MapState.getLocationName(), MapState.getLocationUrl()));
        return 13;
    }

    // --- Public static (used externally) ---

    public static void showTosScreen() {
        Screens.wifiNetworks().show();
        NotificationHelper.showMessageById(1027);
    }

    public static int collectInvitees(ListView parentScreen) {
        ScreenManager.processScreenForm();
        String[] phoneNumbers = Utils.getPhoneNumbers(true);
        Vector invitees = ContactListManager.getCheckedItems(parentScreen, 1);
        for (int idx = phoneNumbers.length - 1; idx >= 0; idx--) {
            invitees.addElement(phoneNumbers[idx]);
        }
        if (invitees.size() == 0) {
            return NotificationHelper.showError(775);
        }
        invitees.addElement(AppState.getAccount().login);
        UIState.setScreenTitle(invitees);
        return ScreenId.SEND_DATA;
    }

    public static void showWiFiNetworks() {
        Vector networks = ServiceRegistry.getActiveContactIds();
        int size = networks == null ? 0 : networks.size();
        if (size == 0) {
            NotificationHelper.showMessageById(404);
            return;
        }
        Screen screen = Screens.savedLocations();
        for (int idx = size - 1; idx >= 0; idx--) {
            Object networkObj = networks.elementAt(idx);
            screen.addIconItemWithData(-1, ServiceRegistry.getPhotoHost(networkObj), 6, networkObj);
        }
        ScreenManager.showScreen(screen);
    }

    public static int setSelectedObject(Object obj) {
        RuntimeState.setMsgId1(obj);
        return 0;
    }

    public static int handleStarAction(Object searchResult) {
        RegistrationState.setSearchResult(searchResult);
        return ScreenId.SEARCH_ENTRY;
    }

    public static int handleEventObject(Object chatRoomObj) {
        ChatState.setActiveChatRoomId(((ChatRoom) chatRoomObj).id);
        return 0;
    }

    public static int handleEditAction(int mode) {
        MapController.applyViewMode(mode == 0, mode != 0, true);
        ContactState.setRefreshNeeded(true);
        return 0;
    }

    public static int openUrl(String url) {
        UIState.setStatusText(new StringBuffer().append(Utils.getMessageBuffer()).append(url).toString());
        return 0;
    }

    // --- Idle processing ---

    private static int processShareMediaIdle() {
        Object[] asyncResult = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
        if (asyncResult == null) {
            return 0;
        }
        int responseCode = ApiClient.validateJsonResponse(asyncResult);
        if (responseCode != 0) {
            return responseCode;
        }
        Object payload = ApiClient.getJsonPayload();
        Object jsonArray = JsonParser.getValueByInt(payload, 329636);
        if (JsonParser.getIntByInt(payload, 198543) == 1) {
            for (int idx = ((Vector) jsonArray).size() - 1; idx >= 0; idx--) {
                String jsonValue = Utils.getVectorString((Vector) jsonArray, idx);
                Account chatAccount = AppState.getAccount();
                ChatRoom selectedChatRoom = chatAccount.findChatRoomByName(jsonValue);
                Message message;
                if (selectedChatRoom != null && (message = selectedChatRoom.getMessage(jsonValue)) != null) {
                    if (message.hasFlag(4)) {
                        selectedChatRoom.decrementUnread();
                    }
                    selectedChatRoom.decrementMembers();
                }
                chatAccount.removeChatRoomUser(jsonValue);
            }
        }
        return ScreenId.CHAT_ROOM_VIEW;
    }
}
