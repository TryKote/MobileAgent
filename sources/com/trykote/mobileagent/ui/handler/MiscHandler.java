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
                AppState.setLong(StateKeys.TIMESTAMP_FIRST_RUN, System.currentTimeMillis());
                Object[] objArr2 = new Object[1];
                AppState.pool[StateKeys.OBJ_REGISTRATION_DATA] = objArr2;
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
                AppController.processEventQueue();
                return;
            case ScreenId.PHONE_INPUT: {
                Screen screen6 = ScreenManager.createScreen(2611);
                for (int i14 = 0; i14 < 15; i14++) {
                    screen6.addTextItem(AppState.getString(i14 + 48));
                }
                ScreenManager.showScreen(screen6);
                return;
            }
            case ScreenId.SERVER_ADDRESS: {
                Screen screen7 = ScreenManager.createScreen(2601);
                for (int i15 = 0; i15 < 15; i15++) {
                    screen7.addTextItem(AppState.getString(i15 + 48));
                }
                ScreenManager.showScreen(screen7);
                return;
            }
            case ScreenId.REGION_SELECTOR: {
                Vector regions = AppState.getVector(StateKeys.VEC_MAP_POINTS);
                int size9 = regions.size();
                if (size9 == 0) {
                    NotificationHelper.showMessageById(397);
                    return;
                }
                Screen screen8 = ScreenManager.createScreen(1691);
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
                Screen screen9 = ScreenManager.createScreen(4080);
                for (int i17 = 0; i17 < 15; i17++) {
                    screen9.addTextItem(AppState.getString(i17 + 48));
                }
                ScreenManager.showScreen(screen9);
                return;
            }
            case ScreenId.URL_OPEN: {
                Screen screen10 = ScreenManager.createScreen(4090);
                for (int i18 = 0; i18 < 15; i18++) {
                    screen10.addTextItem(AppState.getString(i18 + 48));
                }
                ScreenManager.showScreen(screen10);
                return;
            }
            case ScreenId.CONVERSATION:
                return;
            case ScreenId.VERSION_SELECT:
                ScreenManager.showScreen(ScreenManager.createScreen(4862).selectByTitle(AppState.getString(StateKeys.STR_PRIVACY_MODE_BASE + ((MmpProtocol) AppState.getAccount()).getPendingVersion())));
                return;
            case ScreenId.EMPTY_SCREEN:
                ScreenManager.showScreen(ScreenManager.createScreen(3479));
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
                Screen screen18 = ScreenManager.createScreen(2176);
                Vector vector2 = ((Conversation) AppState.pool[StateKeys.SLOT_TEMP_OBJECT_1]).items;
                int size14 = vector2.size();
                while (true) {
                    size14--;
                    if (size14 < 0) {
                        ScreenManager.showScreen(screen18);
                        AppState.clearIndex(StateKeys.SLOT_TEMP_OBJECT_1);
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
                ScreenManager.showScreen(ScreenManager.createScreen(2279));
                return;
            case ScreenId.ASYNC_TASK:
                break;
            case ScreenId.MAIN_SCREEN:
                ScreenManager.showScreen(ScreenManager.createScreen(4369));
                if (AppState.getBool(StateKeys.FLAG_HAS_XMPP_ACCOUNT)) {
                    AppController.processBackgroundTasks();
                    return;
                }
                return;
            case ScreenId.SHARE_MEDIA:
                NotificationHelper.showConfirmDialog(78, 861);
                Vector params8 = ObjectPool.newVector();
                params8.addElement(AppState.getVector(StateKeys.SLOT_MEDIA_STREAM));
                JsonParser.addIntToVector(params8, AppState.getBool(StateKeys.FLAG_SPECIAL_KEY_MODE) ? 1 : 0);
                IOUtils.sendChatRoomRequest(ApiClient.createUploadRequest(AppState.getString(StateKeys.STR_RES_API_URL_5), IOUtils.appendAuthParams(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_XML_TAG_1)).append(AppState.getString(StateKeys.STR_RES_LONG_API_URL_3)), Conversation.urlEncode((Object) JsonParser.toJson(params8)))));
                return;
            case ScreenId.SHARE_ALERT:
                NotificationHelper.showAlertById(79, 863);
                return;
            case ScreenId.PHOTO_SELECTOR:
                IOUtils.showPhotoSelector();
                return;
            case ScreenId.PHOTO_VIEW:
                ScreenManager.pushScreen(ScreenManager.createScreen(4381));
                return;
        }
        AppController.finishScreenBuild();
    }

    public int onMenuItemSelected(Screen screen, MenuItem item, String title, int action, Object data) {
        switch (screen.screenId) {
            case ScreenId.FIRST_RUN:
                return -1;
            case ScreenId.VERSION_CHECK:
                return ResourceManager.applyVersionLabel();
            case ScreenId.TOS_SCREEN:
                return -1;
            case ScreenId.EVENT_QUEUE:
                return AppController.handleEventObject(data);
            case ScreenId.PHONE_INPUT:
                return AppController.processPhoneInput(title);
            case ScreenId.SERVER_ADDRESS:
                return AppController.validateServerAddress(title);
            case ScreenId.REGION_SELECTOR:
                return AppController.handleSearchResultAction(data);
            case ScreenId.PHONE_INPUT_ALT:
                return AppController.processPhoneInput(title);
            case ScreenId.URL_OPEN:
                return AppController.openUrl(title);
            case ScreenId.CONVERSATION:
                return AppController.handleConversationAction(data);
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
                    fullLogin = StringUtils.concat(fullLogin, Utils.splitAndGet(694, AppState.getInt(StateKeys.INT_SERVER_INDEX)));
                }
                if (XmppMailRuProtocol.isValidUsername(fullLogin)) {
                    String str2 = fullLogin;
                    String password = Utils.defaultStr(AppState.getString(StateKeys.SLOT_PASSWORD));
                    String firstName = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SCREEN_TITLE));
                    int intVal3 = AppState.getInt(StateKeys.INT_SETTINGS_THEME);
                    AppState.pool[StateKeys.OBJ_REGISTRATION_DATA] = RegistrationService.createRegRequest(str2, 0, password, firstName, 0 == intVal3 ? Utils.defaultStr(AppState.getString(StateKeys.SLOT_DEVICE_ID)) : (String) Utils.splitNonEmpty(AppState.getString(StateKeys.STR_MENU_REG_SMS), (char) 0).elementAt(intVal3), Utils.defaultStr(AppState.getString(StateKeys.SLOT_APP_VERSION_STRING)), Utils.defaultStr(AppState.getString(StateKeys.SLOT_FIRST_NAME)), Utils.defaultStr(AppState.getString(StateKeys.SLOT_LAST_NAME)), AppState.getInt(StateKeys.INT_SEARCH_GENDER), AppState.getInt(StateKeys.INT_SEARCH_AGE), AppState.getInt(StateKeys.INT_REG_DOMAIN_INDEX), AppState.getInt(StateKeys.INT_COUNTRY_CODE), AppState.getInt(StateKeys.INT_REGION_CODE), AppState.getString(StateKeys.STR_MAP_LOCATION_NAME), AppState.getString(StateKeys.STR_MAP_LOCATION_URL));
                    return 13;
                } else {
                    return NotificationHelper.showError(559);
                }
            }
            case ScreenId.INVITE_TOS:
                return ResourceManager.collectInvitees(screen);
            case ScreenId.FORM_LIST:
                return AppController.handleFormSubmit(data);
            case ScreenId.PHONE_CONTACTS:
                return AppController.handleStarAction(data);
            case ScreenId.EDIT_SCREEN:
                return AppController.handleEditAction(action);
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
                return IOUtils.applyPhotoSelection();
            case ScreenId.PHOTO_VIEW:
                return 0;
        }
        return 0;
    }

    public int onMenuItemAction(Screen screen, MenuItem item, Object data) {
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
                AppState.setInt(StateKeys.FLAG_CONTACTS_LOADED, 0);
                AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 0);
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
            case ScreenId.PHOTO_VIEW:
                return 0;
        }
        return 0;
    }

    public void onScreenClosed(Screen screen) {
        switch (screen.screenId) {
            case ScreenId.VERSION_CHECK:
                AppState.clearIndex(StateKeys.SLOT_SCREEN_TITLE);
                AppState.clearIndex(StateKeys.SLOT_SCREEN_SUBTITLE);
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.CONVERSATION:
                AppState.setInt(StateKeys.FLAG_NEW_MESSAGE, 0);
                AppState.setInt(StateKeys.FLAG_LOADING, 0);
                break;
            case ScreenId.SHARE_MEDIA:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
            case ScreenId.PHOTO_SELECTOR:
                IOUtils.photoUrlList = null;
                IOUtils.contactIdList = null;
                break;
            case ScreenId.REG_FORM:
                AppState.clearIndex(StateKeys.SLOT_CHAT_NAME);
                AppState.clearIndex(StateKeys.SLOT_PASSWORD);
                AppState.clearIndex(StateKeys.SLOT_SCREEN_TITLE);
                AppState.setInt(StateKeys.INT_SERVER_INDEX, 0);
                AppState.setInt(StateKeys.INT_SETTINGS_THEME, 0);
                AppState.clearRange(StateKeys.SLOT_MAP_SEARCH_QUERY, StateKeys.STR_MAP_LOCATION_URL);
                AppController.clearPreviewState();
                StringUtils.resetRegForm();
                break;
            case ScreenId.INVITE_TOS:
                AppController.clearFormFields();
                break;
            case ScreenId.PHONE_CONTACTS:
                AppState.clearRange(StateKeys.RANGE_PHONE_CONTACT_START, StateKeys.OBJ_SEARCH_RESULT);
                break;
            case ScreenId.ASYNC_TASK:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                break;
        }
    }

    public int onItemSelected(Screen screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.FIRST_RUN:
                return -1;
            case ScreenId.VERSION_CHECK:
                return ResourceManager.applyVersionLabel();
            case ScreenId.TOS_SCREEN:
                return -1;
            case ScreenId.EVENT_QUEUE:
                return AppController.handleEventObject(data);
            case ScreenId.PHONE_INPUT:
                return AppController.processPhoneInput(title);
            case ScreenId.SERVER_ADDRESS:
                return AppController.validateServerAddress(title);
            case ScreenId.REGION_SELECTOR:
                return AppController.handleSearchResultAction(data);
            case ScreenId.PHONE_INPUT_ALT:
                return AppController.processPhoneInput(title);
            case ScreenId.URL_OPEN:
                return AppController.openUrl(title);
            case ScreenId.CONVERSATION:
                return AppController.handleConversationAction(data);
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
                return AppController.handleFormSubmit(data);
            case ScreenId.PHONE_CONTACTS:
                return selectedOption == 1 ? AppController.showPeopleSearch() : selectedOption == 2 ? AppController.showPeopleNearby() : AppController.handleStarAction(data);
            case ScreenId.EDIT_SCREEN:
                return AppController.handleEditAction(selectedOption);
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
                return IOUtils.applyPhotoSelection();
            case ScreenId.PHOTO_VIEW:
                return 0;
        }
        return 0;
    }

    public int onIdleProcess(Screen screen, MenuItem item, Object data, String title) {
        switch (screen.screenId) {
            case ScreenId.FIRST_RUN:
                return AppState.getObjectArray(StateKeys.OBJ_REGISTRATION_DATA)[0] == null ? 0 : ScreenId.VERSION_CHECK;
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
                return AppState.getString(StateKeys.SLOT_INIT_PARAMS) == null ? 0 : ScreenId.BLOG_POST;
            case ScreenId.MAIN_SCREEN:
                return 0;
            case ScreenId.SHARE_MEDIA: {
                Object[] asyncResult5 = ApiClient.getAsyncResult(IOUtils.pollAsyncResult());
                if (asyncResult5 != null) {
                    int responseCode5 = IOUtils.validateJsonResponse(asyncResult5);
                    if (responseCode5 != 0) {
                        return responseCode5;
                    }
                    Object payload4 = IOUtils.getJsonPayload();
                    Object jsonArray = JsonParser.getValueByInt(payload4, 329636);
                    if (JsonParser.getIntByInt(payload4, 198543) == 1) {
                        int size6 = ((Vector) jsonArray).size();
                        while (true) {
                            size6--;
                            if (size6 >= 0) {
                                String jsonValue = JsonParser.getVectorString(jsonArray, size6);
                                MrimAccount mrimAccount4 = (MrimAccount) AppState.getAccount();
                                ChatRoom selectedChatRoom3 = mrimAccount4.findChatRoomByName(jsonValue);
                                Message message;
                                if (selectedChatRoom3 != null && (message = selectedChatRoom3.getMessage(jsonValue)) != null) {
                                    if (message.hasFlag(4)) {
                                        selectedChatRoom3.decrementUnread();
                                    }
                                    selectedChatRoom3.decrementMembers();
                                }
                                mrimAccount4.removeUserFromChatRooms(jsonValue);
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
            case ScreenId.PHOTO_VIEW:
                return 0;
        }
        return 0;
    }
}
