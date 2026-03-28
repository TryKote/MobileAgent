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

public final class MiscHandler extends BaseScreenHandler {

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.FIRST_RUN:
                NotificationHelper.showConfirmDialog(57, 730);
                AppState.setLong(SessionKeys.TIMESTAMP_FIRST_RUN, System.currentTimeMillis());
                Object[] objArr2 = new Object[1];
                AppState.pool[RegistrationKeys.OBJ_REGISTRATION_DATA] = objArr2;
                new AsyncTask(AsyncTaskId.FETCH_TILE_BUFFER, objArr2);
                DiagnosticReporter.checkCrashReport();
                return;
            case ScreenId.VERSION_CHECK:
                ResourceManager.processUpdateResult();
                return;
            case ScreenId.TOS_SCREEN:
                ResourceManager.showTosScreen();
                return;
            case ScreenId.EVENT_QUEUE:
                MrimChatRoomManager.showChatRoomSelector();
                return;
            case ScreenId.PHONE_INPUT: {
                ListView screen6 = ScreenManager.createScreen(ScreenDef.PHONE_INPUT);
                for (int i14 = 0; i14 < 15; i14++) {
                    screen6.addTextItem(AppState.getString(i14 + 48));
                }
                ScreenManager.showScreen(screen6);
                return;
            }
            case ScreenId.SERVER_ADDRESS: {
                ListView screen7 = ScreenManager.createScreen(ScreenDef.SERVER_ADDRESS);
                for (int i15 = 0; i15 < 15; i15++) {
                    screen7.addTextItem(AppState.getString(i15 + 48));
                }
                ScreenManager.showScreen(screen7);
                return;
            }
            case ScreenId.REGION_SELECTOR: {
                Vector regions = AppState.getVector(MapKeys.VEC_MAP_POINTS);
                int size9 = regions.size();
                if (size9 == 0) {
                    NotificationHelper.showMessageById(397);
                    return;
                }
                ListView screen8 = ScreenManager.createScreen(ScreenDef.REGION_SELECTOR);
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
                ListView screen9 = ScreenManager.createScreen(ScreenDef.PHONE_INPUT_ALT);
                for (int i17 = 0; i17 < 15; i17++) {
                    screen9.addTextItem(AppState.getString(i17 + 48));
                }
                ScreenManager.showScreen(screen9);
                return;
            }
            case ScreenId.URL_OPEN: {
                ListView screen10 = ScreenManager.createScreen(ScreenDef.URL_OPEN);
                for (int i18 = 0; i18 < 15; i18++) {
                    screen10.addTextItem(AppState.getString(i18 + 48));
                }
                ScreenManager.showScreen(screen10);
                return;
            }
            case ScreenId.CONVERSATION:
                return;
            case ScreenId.VERSION_SELECT:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.VERSION_SELECT).selectByTitle(AppState.getString(StringResKeys.STR_PRIVACY_MODE_BASE + ((MmpProtocol) AppState.getAccount()).getPendingVersion())));
                return;
            case ScreenId.EMPTY_SCREEN:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.EMPTY_SCREEN));
                return;
            case ScreenId.WIFI_NETWORKS:
                ResourceManager.showWiFiNetworks();
                return;
            case ScreenId.REG_FORM:
                RegistrationService.processRegForm();
                return;
            case ScreenId.INVITE_TOS:
                ResourceManager.showTosScreen();
                return;
            case ScreenId.FORM_LIST: {
                ListView screen18 = ScreenManager.createScreen(ScreenDef.FORM_LIST);
                Vector vector2 = ((Conversation) AppState.pool[UIKeys.SLOT_TEMP_OBJECT_1]).items;
                int size14 = vector2.size();
                while (true) {
                    size14--;
                    if (size14 < 0) {
                        ScreenManager.showScreen(screen18);
                        AppState.clearIndex(UIKeys.SLOT_TEMP_OBJECT_1);
                        return;
                    } else {
                        ListItem listItem = (ListItem) vector2.elementAt(size14);
                        screen18.addIconItemWithData(-1, listItem.getText(), 0, listItem);
                    }
                }
            }
            case ScreenId.PHONE_CONTACTS:
                return;
            case ScreenId.EDIT_SCREEN:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.EDIT_SCREEN));
                return;
            case ScreenId.ASYNC_TASK:
                break;
            case ScreenId.MAIN_SCREEN:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.MAIN_SCREEN));
                if (AppState.getBool(SessionKeys.FLAG_HAS_XMPP_ACCOUNT)) {
                    TimerManager.disableBacklight();
                    return;
                }
                return;
            case ScreenId.SHARE_MEDIA:
                NotificationHelper.showConfirmDialog(78, 861);
                Vector params8 = ObjectPool.newVector();
                params8.addElement(AppState.getVector(UIKeys.SLOT_MEDIA_STREAM));
                JsonParser.addIntToVector(params8, AppState.getBool(UIKeys.FLAG_SPECIAL_KEY_MODE) ? 1 : 0);
                MrimChatRoomManager.sendChatRoomRequest(ApiClient.createUploadRequest(AppState.getString(StringResKeys.STR_RES_API_URL_5), ApiClient.appendAuthParams(ObjectPool.newStringBuffer().append(AppState.getString(StringResKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StringResKeys.STR_RES_LONG_API_URL_3)), Conversation.urlEncode((Object) JsonParser.toJson(params8)))));
                return;
            case ScreenId.SHARE_ALERT:
                NotificationHelper.showAlertById(79, 863);
                return;
            case ScreenId.PHOTO_SELECTOR:
                MrimProfileManager.showPhotoSelector();
                return;
            case ScreenId.ACCOUNT_SETUP:
                ScreenManager.pushScreen(ScreenManager.createScreen(ScreenDef.ACCOUNT_SETUP));
                return;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onMenuItemSelected(ListView screen, MenuItem item, String title, int action, Object data) {
        switch (screen.screenId) {
            case ScreenId.FIRST_RUN:
                return -1;
            case ScreenId.VERSION_CHECK:
                return ResourceManager.applyVersionLabel();
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
                return ResourceManager.setSelectedObject(data);
            case ScreenId.REG_FORM: {
                ScreenManager.processScreenForm();
                String loginLower = XmppMailRuProtocol.getLoginLowerCase();
                String fullLogin = loginLower;
                if (!XmppMailRuProtocol.isMailRuDomain(loginLower)) {
                    fullLogin = StringUtils.concat(fullLogin, Utils.splitAndGet(694, AppState.getInt(SessionKeys.INT_SERVER_INDEX)));
                }
                if (XmppMailRuProtocol.isValidUsername(fullLogin)) {
                    String str2 = fullLogin;
                    String password = Utils.defaultStr(AppState.getString(RegistrationKeys.SLOT_PASSWORD));
                    String firstName = Utils.defaultStr(AppState.getString(UIKeys.SLOT_SCREEN_TITLE));
                    int intVal3 = AppState.getInt(SettingsKeys.INT_SETTINGS_THEME);
                    AppState.pool[RegistrationKeys.OBJ_REGISTRATION_DATA] = RegistrationService.createRegRequest(str2, 0, password, firstName, 0 == intVal3 ? Utils.defaultStr(AppState.getString(RegistrationKeys.SLOT_DEVICE_ID)) : (String) Utils.splitNonEmpty(AppState.getString(StringResKeys.STR_MENU_REG_SMS), (char) 0).elementAt(intVal3), Utils.defaultStr(AppState.getString(UIKeys.SLOT_APP_VERSION_STRING)), Utils.defaultStr(AppState.getString(RegistrationKeys.SLOT_FIRST_NAME)), Utils.defaultStr(AppState.getString(RegistrationKeys.SLOT_LAST_NAME)), AppState.getInt(RegistrationKeys.INT_SEARCH_GENDER), AppState.getInt(RegistrationKeys.INT_SEARCH_AGE), AppState.getInt(RegistrationKeys.INT_REG_DOMAIN_INDEX), AppState.getInt(RegistrationKeys.INT_COUNTRY_CODE), AppState.getInt(RegistrationKeys.INT_REGION_CODE), AppState.getString(MapKeys.STR_MAP_LOCATION_NAME), AppState.getString(MapKeys.STR_MAP_LOCATION_URL));
                    return 13;
                } else {
                    return NotificationHelper.showError(559);
                }
            }
            case ScreenId.INVITE_TOS:
                return ResourceManager.collectInvitees(screen);
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
                    AppState.setInt(SessionKeys.INT_PROTOCOL_TYPE, Account.TYPE_XMPP);
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
                AppState.setInt(ContactKeys.FLAG_CONTACTS_LOADED, 0);
                AppState.setInt(UIKeys.FLAG_NEW_MESSAGE, 0);
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
                AppState.clearIndex(UIKeys.SLOT_SCREEN_TITLE);
                AppState.clearIndex(UIKeys.SLOT_SCREEN_SUBTITLE);
                AppState.clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CONVERSATION:
                AppState.setInt(UIKeys.FLAG_NEW_MESSAGE, 0);
                AppState.setInt(UIKeys.FLAG_LOADING, 0);
                break;
            case ScreenId.SHARE_MEDIA:
                AppState.clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.PHOTO_SELECTOR:
                MrimProfileManager.photoUrlList = null;
                MrimProfileManager.contactIdList = null;
                break;
            case ScreenId.REG_FORM:
                AppState.clearIndex(ChatKeys.SLOT_CHAT_NAME);
                AppState.clearIndex(RegistrationKeys.SLOT_PASSWORD);
                AppState.clearIndex(UIKeys.SLOT_SCREEN_TITLE);
                AppState.setInt(SessionKeys.INT_SERVER_INDEX, 0);
                AppState.setInt(SettingsKeys.INT_SETTINGS_THEME, 0);
                AppState.clearRange(MapKeys.SLOT_MAP_SEARCH_QUERY, MapKeys.STR_MAP_LOCATION_URL);
                AppController.clearPreviewState();
                StringUtils.resetRegForm();
                break;
            case ScreenId.INVITE_TOS:
                ScreenManager.clearFormFields();
                break;
            case ScreenId.PHONE_CONTACTS:
                AppState.clearRange(UIKeys.RANGE_PHONE_CONTACT_START, RegistrationKeys.OBJ_SEARCH_RESULT);
                break;
            case ScreenId.ASYNC_TASK:
                AppState.clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                break;
        }
    }

    public int onItemSelected(ListView screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.FIRST_RUN:
                return -1;
            case ScreenId.VERSION_CHECK:
                return ResourceManager.applyVersionLabel();
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
                return ResourceManager.setSelectedObject(data);
            case ScreenId.REG_FORM:
                return 0;
            case ScreenId.INVITE_TOS:
                return 0;
            case ScreenId.FORM_LIST:
                return ScreenManager.handleFormSubmit(data);
            case ScreenId.PHONE_CONTACTS:
                return selectedOption == 1 ? AppController.dialPhoneContactPrev() : selectedOption == 2 ? AppController.dialPhoneContactNext() : handleStarAction(data);
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
                    AppState.setInt(SessionKeys.INT_PROTOCOL_TYPE, Account.TYPE_XMPP);
                }
                return 0;
        }
        return 0;
    }

    public int onIdleProcess(ListView screen, MenuItem item, Object data, String title) {
        switch (screen.screenId) {
            case ScreenId.FIRST_RUN:
                return AppState.getObjectArray(RegistrationKeys.OBJ_REGISTRATION_DATA)[0] == null ? 0 : ScreenId.VERSION_CHECK;
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
                return AppState.getString(UIKeys.SLOT_INIT_PARAMS) == null ? 0 : ScreenId.BLOG_POST;
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
                        int size6 = ((Vector) jsonArray).size();
                        while (true) {
                            size6--;
                            if (size6 >= 0) {
                                String jsonValue = JsonParser.getVectorString(jsonArray, size6);
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
                            } else {
                                break;
                            }
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

    public static int handleStarAction(Object searchResult) {
        AppState.pool[RegistrationKeys.OBJ_SEARCH_RESULT] = searchResult;
        return ScreenId.SEARCH_ENTRY;
    }

    public static int handleEventObject(Object chatRoomObj) {
        AppState.setInt(ChatKeys.INT_ACTIVE_CHATROOM_ID, ((ChatRoom) chatRoomObj).id);
        return 0;
    }

    public static int handleEditAction(int mode) {
        MapController.applyViewMode(mode == 0, mode != 0, true);
        AppState.setInt(ContactKeys.FLAG_REFRESH_CONTACTS, 1);
        return 0;
    }

    public static int openUrl(String url) {
        AppState.setObject(UIKeys.SLOT_STATUS_TEXT, new StringBuffer().append(Utils.getMessageBuffer()).append(url).toString());
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
        int itemIdx = items.size();
        while (true) {
            itemIdx--;
            if (itemIdx < 0) {
                if (item.title.equals(AppState.getString(StringResKeys.STR_MENU_OPTIONS))) {
                    String optionStr = selectedIndex == 0 ? Utils.defaultStr(AppState.getString(RegistrationKeys.SLOT_DEVICE_ID)) : options[selectedIndex];
                    Object[] phoneData = (Object[]) phoneItem.data;
                    phoneItem.clear().setAction(phoneData[4], optionStr, phoneData[1], phoneData[2], phoneData[3]);
                }
                screen.rebuildItems();
                break;
            } else {
                MenuItem entry = (MenuItem) items.elementAt(itemIdx);
                if (entry.id == 15 && entry.title.startsWith(AppState.getString(StringResKeys.STR_MENU_PHONE_PREFIX))) {
                    phoneItem = entry;
                }
            }
        }
    }
}
