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

public final class ProfileHandler extends BaseScreenHandler {

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.SEARCH_RESULTS:
                AppController.resetSearchResults();
                ScreenManager.showScreen(ScreenManager.createScreen(4769));
                return;
            case ScreenId.SEARCH_RESULT_LIST:
                int errorMsgId = AppState.getInt(StateKeys.INT_ERROR_MSG_INDEX);
                if (0 != errorMsgId) {
                    AppController.clearSearchResults2();
                    NotificationHelper.showMessageById(errorMsgId);
                    return;
                }
                Vector searchResults = AppState.getVector(StateKeys.SLOT_REG_PARAM_4);
                if (0 != searchResults.size()) {
                    ScreenManager.showScreen(ContactListManager.addContactItems(ScreenManager.createScreen(3868), searchResults));
                    return;
                } else {
                    AppController.clearSearchResults2();
                    NotificationHelper.showMessageById(736);
                    return;
                }
            case ScreenId.USER_PROFILE:
                if (AppController.pendingAccount == null && AppController.pendingUrl == null) {
                    Contact contact = AppState.getCurrentContact();
                    String statusText = AppController.getStatusText();
                    NotificationHelper.showErrorOrConfirm(102, 728, statusText != null ? contact.account.getResourceId((Object) statusText) : ResourceManager.loadUserProfile(contact.getIdentifier(), contact.account));
                    return;
                } else {
                    NotificationHelper.showErrorOrConfirm(102, 728, 0);
                    ResourceManager.loadUserProfile(AppController.pendingUrl, AppController.pendingAccount);
                    AppController.clearMapPoints();
                    return;
                }
            case ScreenId.PROFILE_LOAD:
                ContactInfo contactInfo = (ContactInfo) AppState.pool[StateKeys.SLOT_CONTACT_INFO];
                NotificationHelper.showErrorOrConfirm(107, 728, contactInfo.getAccount().getResourceId((Object) contactInfo.getEmailOrMmpId()));
                return;
            case ScreenId.VCARD_ACTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(4892));
                return;
            case ScreenId.PEOPLE_SEARCH:
                ScreenManager.showScreen(ScreenManager.createScreen(2043));
                return;
            case ScreenId.PROFILE_EDIT:
                StringBuffer stringBuffer = new StringBuffer(AppState.getString(StateKeys.STR_REGISTRATION_TEXT));
                stringBuffer.append(AppState.getString(((MrimAccount) AppState.getAccount()).accountProfile.gender + 780));
                AppState.setFromBuffer(StateKeys.OBJ_PHOTO_CACHE_2, stringBuffer);
                ResourceManager.lastTileLoadTime = System.currentTimeMillis();
                ScreenManager.showScreen(ScreenManager.createScreen(4258));
                return;
            case ScreenId.SEARCH_ENTRY:
                AppState.setCurrentEntity((Object) null);
                ScreenManager.showScreen(ScreenManager.createScreen(2247));
                return;
        }
        AppController.finishScreenBuild();
    }

    public int onMenuItemSelected(Screen screen, MenuItem item, String title, int action, Object data) {
        switch (screen.screenId) {
            case ScreenId.SEARCH_RESULTS:
                return AppController.handleEnterKey();
            case ScreenId.SEARCH_RESULT_LIST:
                AppState.pool[StateKeys.SLOT_CONTACT_INFO] = data;
                return 0;
            case ScreenId.USER_PROFILE:
                return -1;
            case ScreenId.PROFILE_LOAD:
                return -1;
            case ScreenId.VCARD_ACTIONS:
                return AppController.handleScreenAction(action);
            case ScreenId.PEOPLE_SEARCH:
                return AppController.processSearchQuery(title);
            case ScreenId.PROFILE_EDIT:
                return ResourceManager.syncAndReturn();
            case ScreenId.SEARCH_ENTRY:
                return ResourceManager.handleSearchResultAction(action);
        }
        return 0;
    }

    public int onMenuItemAction(Screen screen, MenuItem item, Object data) {
        switch (screen.screenId) {
            case ScreenId.SEARCH_RESULTS:
                return AppController.handleBackKey();
            case ScreenId.SEARCH_RESULT_LIST:
                return 0;
            case ScreenId.USER_PROFILE:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                return 0;
            case ScreenId.PROFILE_LOAD:
                AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
                return 0;
            case ScreenId.VCARD_ACTIONS:
                return 0;
            case ScreenId.PEOPLE_SEARCH:
                return 0;
            case ScreenId.PROFILE_EDIT:
                ScreenBuilder.onScreenClosed();
                return 0;
            case ScreenId.SEARCH_ENTRY:
                return 0;
        }
        return 0;
    }

    public void onScreenClosed(Screen screen) {
        switch (screen.screenId) {
            case ScreenId.SEARCH_RESULTS:
                AppController.resetSearchResults();
                break;
            case ScreenId.SEARCH_RESULT_LIST:
                AppController.clearSearchResults2();
                break;
        }
    }

    public int onItemSelected(Screen screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.SEARCH_RESULTS:
                return 0;
            case ScreenId.SEARCH_RESULT_LIST:
                AppState.pool[StateKeys.SLOT_CONTACT_INFO] = data;
                return 0;
            case ScreenId.USER_PROFILE:
                return -1;
            case ScreenId.PROFILE_LOAD:
                return -1;
            case ScreenId.VCARD_ACTIONS:
                return AppController.handleScreenAction(selectedOption);
            case ScreenId.PEOPLE_SEARCH:
                return AppController.processSearchQuery(title);
            case ScreenId.PROFILE_EDIT:
                return ResourceManager.syncAndReturn();
            case ScreenId.SEARCH_ENTRY:
                return ResourceManager.handleSearchResultAction(selectedOption);
        }
        return 0;
    }

    public int onIdleProcess(Screen screen, MenuItem item, Object data, String title) {
        switch (screen.screenId) {
            case ScreenId.SEARCH_RESULTS:
                return 0;
            case ScreenId.SEARCH_RESULT_LIST:
                return 0;
            case ScreenId.USER_PROFILE:
                return AppState.getObjectArray(StateKeys.OBJ_REGISTRATION_DATA)[2] == null ? 0 : ScreenId.CAPTCHA;
            case ScreenId.PROFILE_LOAD:
                return AppState.getObjectArray(StateKeys.OBJ_REGISTRATION_DATA)[2] == null ? 0 : ScreenId.CAPTCHA;
            case ScreenId.VCARD_ACTIONS:
                return 0;
            case ScreenId.PEOPLE_SEARCH:
                return 0;
            case ScreenId.PROFILE_EDIT:
                return System.currentTimeMillis() - ResourceManager.lastTileLoadTime > 5000 ? ResourceManager.syncAndReturn() : 0;
            case ScreenId.SEARCH_ENTRY:
                return 0;
        }
        return 0;
    }
}
