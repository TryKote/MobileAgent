package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
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

public final class MiscHandler extends BaseScreenHandler {

    // Number of phone number format strings in the resource block
    private static final int PHONE_STRING_COUNT = 15;

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.FIRST_RUN:
                NotificationHelper.showConfirmDialog(57, 730);
                SessionState.setTimestampFirstRun(System.currentTimeMillis());
                Object[] objArr2 = new Object[1];
                RegistrationState.setRegistrationData(objArr2);
                new AsyncTask(AsyncTaskId.FETCH_TILE_BUFFER, objArr2);
                DiagnosticReporter.checkCrashReport();
                return;
            case ScreenId.VERSION_CHECK:
                RegistrationService.processUpdateResult();
                return;
            case ScreenId.TOS_SCREEN:
                showTosScreen();
                return;
            case ScreenId.EVENT_QUEUE:
                MrimChatRoomManager.showChatRoomSelector();
                return;
            case ScreenId.PHONE_INPUT: {
                Screen screen6 = Screens.phoneInput(this);
                for (int i14 = 0; i14 < PHONE_STRING_COUNT; i14++) {
                    screen6.addTextItem(ResourceAccessor.blockStr(StringResKeys.PHONE_STRINGS_BASE, i14));
                }
                ScreenManager.showScreen(screen6);
                return;
            }
            case ScreenId.SERVER_ADDRESS: {
                Screen screen7 = Screens.serverAddress(this);
                for (int i15 = 0; i15 < PHONE_STRING_COUNT; i15++) {
                    screen7.addTextItem(ResourceAccessor.blockStr(StringResKeys.PHONE_STRINGS_BASE, i15));
                }
                ScreenManager.showScreen(screen7);
                return;
            }
            case ScreenId.REGION_SELECTOR: {
                Vector regions = MapState.getMapPoints();
                int size9 = regions.size();
                if (size9 == 0) {
                    NotificationHelper.showMessageById(397);
                    return;
                }
                Screen screen8 = Screens.regionSelector(this);
                for (int i16 = 0; i16 < size9; i16++) {
                    GeoRegion region = (GeoRegion) regions.elementAt(i16);
                    screen8.addIconItemWithData(-1, region.name, 6, region);
                }
                GeoRegion currentRegion = StringUtils.getGeoRegion();
                screen8.addIconItemWithData(-1, currentRegion.name, 6, currentRegion);
                ScreenManager.showScreen(screen8);
                return;
            }
            case ScreenId.PHONE_INPUT_ALT: {
                Screen screen9 = Screens.phoneInputAlt(this);
                for (int i17 = 0; i17 < PHONE_STRING_COUNT; i17++) {
                    screen9.addTextItem(ResourceAccessor.blockStr(StringResKeys.PHONE_STRINGS_BASE, i17));
                }
                ScreenManager.showScreen(screen9);
                return;
            }
            case ScreenId.URL_OPEN: {
                Screen screen10 = Screens.urlOpen(this);
                for (int i18 = 0; i18 < PHONE_STRING_COUNT; i18++) {
                    screen10.addTextItem(ResourceAccessor.blockStr(StringResKeys.PHONE_STRINGS_BASE, i18));
                }
                ScreenManager.showScreen(screen10);
                return;
            }
            case ScreenId.CONVERSATION:
                return;
            case ScreenId.VERSION_SELECT:
                Screen versionScreen = Screens.versionSelect(this);
                versionScreen.selectByTitle(ResourceAccessor.str(StringResKeys.STR_PRIVACY_MODE_BASE + ((MmpProtocol) AppState.getAccount()).getPendingVersion()));
                versionScreen.show();
                return;
            case ScreenId.EMPTY_SCREEN:
                Screens.emptyScreen(this).show();
                return;
            case ScreenId.WIFI_NETWORKS:
                showWiFiNetworks();
                return;
            case ScreenId.REG_FORM:
                RegistrationService.processRegForm();
                return;
            case ScreenId.INVITE_TOS:
                showTosScreen();
                return;
            case ScreenId.FORM_LIST: {
                Screen screen18 = Screens.formList(this);
                Vector vector2 = ((Conversation) UIState.getTempObject1()).items;
                for (int idx = vector2.size() - 1; idx >= 0; idx--) {
                    ListItem listItem = (ListItem) vector2.elementAt(idx);
                    screen18.addIconItemWithData(-1, listItem.getText(), 0, listItem);
                }
                ScreenManager.showScreen(screen18);
                UIState.clearTempObject1();
                return;
            }
            case ScreenId.PHONE_CONTACTS:
                return;
            case ScreenId.EDIT_SCREEN:
                Screens.editScreen(this).show();
                return;
            case ScreenId.ASYNC_TASK:
                break;
            case ScreenId.MAIN_SCREEN:
                Screens.mainScreen(this).show();
                if (SessionState.hasXmppAccount()) {
                    TimerManager.disableBacklight();
                    return;
                }
                return;
            case ScreenId.SHARE_MEDIA:
                NotificationHelper.showConfirmDialog(78, 861);
                Vector params8 = ObjectPool.newVector();
                params8.addElement(UIState.getMediaStream());
                JsonParser.addIntToVector(params8, UIState.isSpecialKeyMode() ? 1 : 0);
                MrimChatRoomManager.sendChatRoomRequest(ApiClient.createUploadRequest(ResourceAccessor.str(PackedStringKeys.URL_PATH_AJAX_SPAMABUSE), ApiClient.appendAuthParams(ObjectPool.newStringBuffer().append(ResourceAccessor.str(PackedStringKeys.PARAM_AJAX_CALL)).append(ResourceAccessor.str(PackedStringKeys.FUNC_AJAX_SPAMABUSE)), Conversation.urlEncode((Object) JsonParser.toJson(params8)))));
                return;
            case ScreenId.SHARE_ALERT:
                NotificationHelper.showAlertById(79, 863);
                return;
            case ScreenId.PHOTO_SELECTOR:
                MrimProfileManager.showPhotoSelector();
                return;
            case ScreenId.ACCOUNT_SETUP:
                ScreenManager.pushScreen(Screens.accountSetup(this));
                return;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onMenuItemSelected(ListView screen, MenuItem item, String title, int action, Object data) {
        switch (screen.screenId) {
            case ScreenId.FIRST_RUN:
                return -1;
            case ScreenId.VERSION_CHECK:
                return RegistrationService.applyVersionLabel();
            case ScreenId.TOS_SCREEN:
                return -1;
            case ScreenId.EVENT_QUEUE:
                return handleEventObject(data);
            case ScreenId.PHONE_INPUT:
                return ScreenManager.processPhoneInput(title);
            case ScreenId.SERVER_ADDRESS:
                return ScreenManager.validateServerAddress(title);
            case ScreenId.REGION_SELECTOR:
                return MapController.handleSearchResultAction(data);
            case ScreenId.PHONE_INPUT_ALT:
                return ScreenManager.processPhoneInput(title);
            case ScreenId.URL_OPEN:
                return openUrl(title);
            case ScreenId.CONVERSATION:
                return MapController.handleMapPointAction(data);
            case ScreenId.VERSION_SELECT:
                return ((MmpProtocol) AppState.getAccount()).scheduleVersionUpdate(action);
            case ScreenId.EMPTY_SCREEN:
                return 0;
            case ScreenId.WIFI_NETWORKS:
                return setSelectedObject(data);
            case ScreenId.REG_FORM: {
                ScreenManager.processScreenForm();
                String loginLower = XmppMailRuProtocol.getLoginLowerCase();
                String fullLogin = loginLower;
                if (!XmppMailRuProtocol.isMailRuDomain(loginLower)) {
                    fullLogin = StringUtils.concat(fullLogin, Utils.splitAndGet(StringResKeys.STR_DOMAIN_LIST, SessionState.getServerIndex()));
                }
                if (XmppMailRuProtocol.isValidUsername(fullLogin)) {
                    String str2 = fullLogin;
                    String password = Utils.defaultStr(RegistrationState.getPassword());
                    String firstName = Utils.defaultStr(UIState.getScreenTitle());
                    int intVal3 = SettingsState.getSettingsTheme();
                    RegistrationState.setRegistrationData(RegistrationService.createRegRequest(str2, 0, password, firstName, intVal3 == 0 ? Utils.defaultStr(RegistrationState.getDeviceId()) : (String) Utils.splitNonEmpty(ResourceAccessor.str(StringResKeys.STR_MENU_REG_SMS), (char) 0).elementAt(intVal3), Utils.defaultStr(UIState.getAppVersionString()), Utils.defaultStr(RegistrationState.getFirstName()), Utils.defaultStr(RegistrationState.getLastName()), RegistrationState.getSearchGender(), RegistrationState.getSearchAge(), RegistrationState.getRegDomainIndex(), RegistrationState.getCountryCode(), RegistrationState.getRegionCode(), MapState.getLocationName(), MapState.getLocationUrl()));
                    return 13;
                } else {
                    return NotificationHelper.showError(559);
                }
            }
            case ScreenId.INVITE_TOS:
                return collectInvitees(screen);
            case ScreenId.FORM_LIST:
                return ScreenManager.handleFormSubmit(data);
            case ScreenId.PHONE_CONTACTS:
                return handleStarAction(data);
            case ScreenId.EDIT_SCREEN:
                return handleEditAction(action);
            case ScreenId.ASYNC_TASK:
                return -1;
            case ScreenId.MAIN_SCREEN:
                return -1;
            case ScreenId.SHARE_MEDIA:
                return -1;
            case ScreenId.SHARE_ALERT:
                ScreenBuilder.onScreenClosed();
                ScreenBuilder.onScreenClosed();
                return 0;
            case ScreenId.PHOTO_SELECTOR:
                return MrimProfileManager.applyPhotoSelection();
            case ScreenId.ACCOUNT_SETUP:
                if (action == ScreenId.XMPP_LOGIN) {
                    SessionState.setProtocolType(Account.TYPE_XMPP);
                }
                return 0;
        }
        return 0;
    }

    public int onMenuItemAction(ListView screen, MenuItem item, Object data) {
        switch (screen.screenId) {
            case ScreenId.FIRST_RUN:
                return 0;
            case ScreenId.VERSION_CHECK:
                return 0;
            case ScreenId.TOS_SCREEN:
                return 0;
            case ScreenId.EVENT_QUEUE:
                return 0;
            case ScreenId.PHONE_INPUT:
                return 0;
            case ScreenId.SERVER_ADDRESS:
                return 0;
            case ScreenId.REGION_SELECTOR:
                return 0;
            case ScreenId.PHONE_INPUT_ALT:
                return 0;
            case ScreenId.URL_OPEN:
                return 0;
            case ScreenId.CONVERSATION:
                ContactState.setLoaded(false);
                UIState.setNewMessage(0);
                return 0;
            case ScreenId.VERSION_SELECT:
                return 0;
            case ScreenId.EMPTY_SCREEN:
                return 0;
            case ScreenId.WIFI_NETWORKS:
                return 0;
            case ScreenId.REG_FORM:
                return 0;
            case ScreenId.INVITE_TOS:
                return 0;
            case ScreenId.FORM_LIST:
                return 0;
            case ScreenId.PHONE_CONTACTS:
                return ScreenId.CLOSE;
            case ScreenId.EDIT_SCREEN:
                return 0;
            case ScreenId.ASYNC_TASK:
                return 0;
            case ScreenId.MAIN_SCREEN:
                return ScreenId.CLOSE;
            case ScreenId.SHARE_MEDIA:
                return 0;
            case ScreenId.SHARE_ALERT:
                return 0;
            case ScreenId.PHOTO_SELECTOR:
                return 0;
            case ScreenId.ACCOUNT_SETUP:
                return 0;
        }
        return 0;
    }

    public void onScreenClosed(ListView screen) {
        switch (screen.screenId) {
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

    public int onItemSelected(ListView screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.FIRST_RUN:
                return -1;
            case ScreenId.VERSION_CHECK:
                return RegistrationService.applyVersionLabel();
            case ScreenId.TOS_SCREEN:
                return -1;
            case ScreenId.EVENT_QUEUE:
                return handleEventObject(data);
            case ScreenId.PHONE_INPUT:
                return ScreenManager.processPhoneInput(title);
            case ScreenId.SERVER_ADDRESS:
                return ScreenManager.validateServerAddress(title);
            case ScreenId.REGION_SELECTOR:
                return MapController.handleSearchResultAction(data);
            case ScreenId.PHONE_INPUT_ALT:
                return ScreenManager.processPhoneInput(title);
            case ScreenId.URL_OPEN:
                return openUrl(title);
            case ScreenId.CONVERSATION:
                return MapController.handleMapPointAction(data);
            case ScreenId.VERSION_SELECT:
                return ((MmpProtocol) AppState.getAccount()).scheduleVersionUpdate(selectedOption);
            case ScreenId.EMPTY_SCREEN:
                return 0;
            case ScreenId.WIFI_NETWORKS:
                return setSelectedObject(data);
            case ScreenId.REG_FORM:
                return 0;
            case ScreenId.INVITE_TOS:
                return 0;
            case ScreenId.FORM_LIST:
                return ScreenManager.handleFormSubmit(data);
            case ScreenId.PHONE_CONTACTS:
                return selectedOption == 1 ? ContactListManager.dialPhoneContactPrev() : selectedOption == 2 ? ContactListManager.dialPhoneContactNext() : handleStarAction(data);
            case ScreenId.EDIT_SCREEN:
                return handleEditAction(selectedOption);
            case ScreenId.ASYNC_TASK:
                return -1;
            case ScreenId.MAIN_SCREEN:
                return -1;
            case ScreenId.SHARE_MEDIA:
                return -1;
            case ScreenId.SHARE_ALERT:
                ScreenBuilder.onScreenClosed();
                ScreenBuilder.onScreenClosed();
                return 0;
            case ScreenId.PHOTO_SELECTOR:
                return MrimProfileManager.applyPhotoSelection();
            case ScreenId.ACCOUNT_SETUP:
                if (selectedOption == ScreenId.XMPP_LOGIN) {
                    SessionState.setProtocolType(Account.TYPE_XMPP);
                }
                return 0;
        }
        return 0;
    }

    public int onIdleProcess(ListView screen, MenuItem item, Object data, String title) {
        switch (screen.screenId) {
            case ScreenId.FIRST_RUN:
                return RegistrationState.getRegistrationData()[0] == null ? 0 : ScreenId.VERSION_CHECK;
            case ScreenId.VERSION_CHECK:
                return 0;
            case ScreenId.TOS_SCREEN:
                return 0;
            case ScreenId.EVENT_QUEUE:
                return 0;
            case ScreenId.PHONE_INPUT:
                return 0;
            case ScreenId.SERVER_ADDRESS:
                return 0;
            case ScreenId.REGION_SELECTOR:
                return 0;
            case ScreenId.PHONE_INPUT_ALT:
                return 0;
            case ScreenId.URL_OPEN:
                return 0;
            case ScreenId.CONVERSATION:
                return 0;
            case ScreenId.VERSION_SELECT:
                return 0;
            case ScreenId.EMPTY_SCREEN:
                return 0;
            case ScreenId.WIFI_NETWORKS:
                return 0;
            case ScreenId.REG_FORM:
                return 0;
            case ScreenId.INVITE_TOS:
                return 0;
            case ScreenId.FORM_LIST:
                return 0;
            case ScreenId.PHONE_CONTACTS:
                return 0;
            case ScreenId.EDIT_SCREEN:
                return 0;
            case ScreenId.ASYNC_TASK:
                return UIState.getInitParams() == null ? 0 : ScreenId.BLOG_POST;
            case ScreenId.MAIN_SCREEN:
                return 0;
            case ScreenId.SHARE_MEDIA: {
                Object[] asyncResult5 = ApiClient.getAsyncResult(ApiClient.pollAsyncResult());
                if (asyncResult5 != null) {
                    int responseCode5 = ApiClient.validateJsonResponse(asyncResult5);
                    if (responseCode5 != 0) {
                        return responseCode5;
                    }
                    Object payload4 = ApiClient.getJsonPayload();
                    Object jsonArray = JsonParser.getValueByInt(payload4, 329636);
                    if (JsonParser.getIntByInt(payload4, 198543) == 1) {
                        for (int idx = ((Vector) jsonArray).size() - 1; idx >= 0; idx--) {
                            String jsonValue = JsonParser.getVectorString(jsonArray, idx);
                            MrimAccount mrimAccount4 = (MrimAccount) AppState.getAccount();
                            ChatRoom selectedChatRoom3 = mrimAccount4.chatRoomManager.findByName(jsonValue);
                            Message message;
                            if (selectedChatRoom3 != null && (message = selectedChatRoom3.getMessage(jsonValue)) != null) {
                                if (message.hasFlag(4)) {
                                    selectedChatRoom3.decrementUnread();
                                }
                                selectedChatRoom3.decrementMembers();
                            }
                            mrimAccount4.chatRoomManager.removeUser(jsonValue);
                        }
                    }
                    return ScreenId.CHAT_ROOM_VIEW;
                }
                return 0;
            }
            case ScreenId.SHARE_ALERT:
                return 0;
            case ScreenId.PHOTO_SELECTOR:
                return 0;
            case ScreenId.ACCOUNT_SETUP:
                return 0;
        }
        return 0;
    }

    public static final void showTosScreen() {
        Screens.wifiNetworks(null).show();
        NotificationHelper.showMessageById(1027);
    }

    public static final int collectInvitees(ListView parentScreen) {
        ScreenManager.processScreenForm();
        String[] phoneNumbers = Utils.getPhoneNumbers(true);
        Vector invitees = ContactListManager.getCheckedItems(parentScreen, 1);
        for (int idx = phoneNumbers.length - 1; idx >= 0; idx--) {
            invitees.addElement(phoneNumbers[idx]);
        }
        if (invitees.size() == 0) {
            return NotificationHelper.showError(775);
        }
        invitees.addElement(((MrimAccount) AppState.getAccount()).login);
        UIState.setScreenTitle(invitees);
        return ScreenId.SEND_DATA;
    }

    public static final void showWiFiNetworks() {
        Vector networks = ServiceRegistry.getActiveContactIds();
        int size = networks == null ? 0 : networks.size();
        if (size == 0) {
            NotificationHelper.showMessageById(404);
            return;
        }
        Screen screen = Screens.savedLocations(null);
        for (int idx = size - 1; idx >= 0; idx--) {
            Object networkObj = networks.elementAt(idx);
            screen.addIconItemWithData(-1, ServiceRegistry.getPhotoHost(networkObj), 6, networkObj);
        }
        ScreenManager.showScreen(screen);
    }

    public static final int setSelectedObject(Object obj) {
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

    public void onMenuItemEvent(ListView screen, MenuItem item) {
        if (screen.screenId != ScreenId.REG_FORM) {
            return;
        }
        Object[] itemData = (Object[]) item.data;
        int selectedIndex = ((Integer) itemData[0]).intValue();
        String[] options = (String[]) itemData[1];
        MenuItem phoneItem = null;
        Vector items = screen.menuItems;
        for (int itemIdx = items.size() - 1; itemIdx >= 0; itemIdx--) {
            MenuItem entry = (MenuItem) items.elementAt(itemIdx);
            if (entry.id == 15 && entry.title.startsWith(ResourceAccessor.str(StringResKeys.STR_MENU_PHONE_PREFIX))) {
                phoneItem = entry;
            }
        }
        if (item.title.equals(ResourceAccessor.str(StringResKeys.STR_MENU_OPTIONS))) {
            String optionStr = selectedIndex == 0 ? Utils.defaultStr(RegistrationState.getDeviceId()) : options[selectedIndex];
            Object[] phoneData = (Object[]) phoneItem.data;
            phoneItem.clear().setAction(phoneData[4], optionStr, phoneData[1], phoneData[2], phoneData[3]);
        }
        screen.rebuildItems();
    }
}
