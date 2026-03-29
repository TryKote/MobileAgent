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

    /* renamed from: g */
    public static long lastTileLoadTime;

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.SEARCH_RESULTS:
                resetSearchResults();
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.SEARCH_RESULTS));
                return;
            case ScreenId.SEARCH_RESULT_LIST:
                int errorMsgId = Storage.state().getInt(RuntimeKeys.INT_ERROR_MSG_INDEX);
                if (0 != errorMsgId) {
                    clearSearchResults2();
                    NotificationHelper.showMessageById(errorMsgId);
                    return;
                }
                Vector searchResults = Storage.state().getVector(RegistrationKeys.SLOT_REG_PARAM_4);
                if (0 != searchResults.size()) {
                    ScreenManager.showScreen(ContactListManager.addContactItems(ScreenManager.createScreen(ScreenDef.SEARCH_RESULT_LIST), searchResults));
                    return;
                } else {
                    clearSearchResults2();
                    NotificationHelper.showMessageById(736);
                    return;
                }
            case ScreenId.USER_PROFILE:
                if (AppController.pendingAccount == null && AppController.pendingUrl == null) {
                    Contact contact = Storage.state().getCurrentContact();
                    String statusText = AppController.getPendingDisplayText();
                    NotificationHelper.showErrorOrConfirm(102, 728, statusText != null ? contact.account.getResourceId((Object) statusText) : loadUserProfile(contact.getIdentifier(), contact.account));
                    return;
                } else {
                    NotificationHelper.showErrorOrConfirm(102, 728, 0);
                    loadUserProfile(AppController.pendingUrl, AppController.pendingAccount);
                    AppController.clearPendingProfile();
                    return;
                }
            case ScreenId.PROFILE_LOAD:
                ContactInfo contactInfo = (ContactInfo) Storage.state().getObject(ContactKeys.SLOT_CONTACT_INFO);
                NotificationHelper.showErrorOrConfirm(107, 728, contactInfo.getAccount().getResourceId((Object) contactInfo.getEmailOrMmpId()));
                return;
            case ScreenId.VCARD_ACTIONS:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.VCARD_ACTIONS));
                return;
            case ScreenId.PEOPLE_SEARCH:
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.PEOPLE_SEARCH));
                return;
            case ScreenId.PROFILE_EDIT:
                StringBuffer stringBuffer = new StringBuffer(Storage.resources().getString(StringResKeys.STR_REGISTRATION_TEXT));
                stringBuffer.append(Storage.state().getString(((MrimAccount) Storage.state().getAccount()).profileManager.profile.gender + 780));
                Storage.state().setFromBuffer(UIKeys.OBJ_PHOTO_CACHE_2, stringBuffer);
                lastTileLoadTime = System.currentTimeMillis();
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.PROFILE_EDIT));
                return;
            case ScreenId.SEARCH_ENTRY:
                Storage.state().setCurrentEntity((Object) null);
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.SEARCH_ENTRY));
                return;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onMenuItemSelected(ListView screen, MenuItem item, String title, int action, Object data) {
        switch (screen.screenId) {
            case ScreenId.SEARCH_RESULTS:
                return handleEnterKey();
            case ScreenId.SEARCH_RESULT_LIST:
                Storage.state().setObject(ContactKeys.SLOT_CONTACT_INFO, data);
                return 0;
            case ScreenId.USER_PROFILE:
                return -1;
            case ScreenId.PROFILE_LOAD:
                return -1;
            case ScreenId.VCARD_ACTIONS:
                return handleScreenAction(action);
            case ScreenId.PEOPLE_SEARCH:
                return MapController.processSearchQuery(title);
            case ScreenId.PROFILE_EDIT:
                return syncAndReturn();
            case ScreenId.SEARCH_ENTRY:
                return handleSearchResultAction(action);
        }
        return 0;
    }

    public int onMenuItemAction(ListView screen, MenuItem item, Object data) {
        switch (screen.screenId) {
            case ScreenId.SEARCH_RESULTS:
                return handleBackKey();
            case ScreenId.SEARCH_RESULT_LIST:
                return 0;
            case ScreenId.USER_PROFILE:
                Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
                return 0;
            case ScreenId.PROFILE_LOAD:
                Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
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

    public void onScreenClosed(ListView screen) {
        switch (screen.screenId) {
            case ScreenId.SEARCH_RESULTS:
                resetSearchResults();
                break;
            case ScreenId.SEARCH_RESULT_LIST:
                clearSearchResults2();
                break;
        }
    }

    public int onItemSelected(ListView screen, MenuItem item, String title, int selectedOption,
                              Object data, Object headerData) {
        switch (screen.screenId) {
            case ScreenId.SEARCH_RESULTS:
                return 0;
            case ScreenId.SEARCH_RESULT_LIST:
                Storage.state().setObject(ContactKeys.SLOT_CONTACT_INFO, data);
                return 0;
            case ScreenId.USER_PROFILE:
                return -1;
            case ScreenId.PROFILE_LOAD:
                return -1;
            case ScreenId.VCARD_ACTIONS:
                return handleScreenAction(selectedOption);
            case ScreenId.PEOPLE_SEARCH:
                return MapController.processSearchQuery(title);
            case ScreenId.PROFILE_EDIT:
                return syncAndReturn();
            case ScreenId.SEARCH_ENTRY:
                return handleSearchResultAction(selectedOption);
        }
        return 0;
    }

    public int onIdleProcess(ListView screen, MenuItem item, Object data, String title) {
        switch (screen.screenId) {
            case ScreenId.SEARCH_RESULTS:
                return 0;
            case ScreenId.SEARCH_RESULT_LIST:
                return 0;
            case ScreenId.USER_PROFILE:
                return Storage.state().getObjectArray(RegistrationKeys.OBJ_REGISTRATION_DATA)[2] == null ? 0 : ScreenId.CAPTCHA;
            case ScreenId.PROFILE_LOAD:
                return Storage.state().getObjectArray(RegistrationKeys.OBJ_REGISTRATION_DATA)[2] == null ? 0 : ScreenId.CAPTCHA;
            case ScreenId.VCARD_ACTIONS:
                return 0;
            case ScreenId.PEOPLE_SEARCH:
                return 0;
            case ScreenId.PROFILE_EDIT:
                return System.currentTimeMillis() - lastTileLoadTime > 5000 ? syncAndReturn() : 0;
            case ScreenId.SEARCH_ENTRY:
                return 0;
        }
        return 0;
    }

    /* renamed from: h */
    public static final int syncAndReturn() {
        ((MrimAccount) Storage.state().getAccount()).profileManager.sync();
        if (Storage.state().getBool(SessionKeys.FLAG_UPDATE_AVAILABLE)) {
            return Storage.state().getInt(SessionKeys.INT_CONNECTION_STATE);
        }
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    /* renamed from: a */
    public static final int loadUserProfile(String str, Account targetAccount) {
        ByteBuffer urlBuffer;
        int atIndex = str.indexOf(64);
        String domain = StringUtils.suffix(str, atIndex + 1);
        Object[] objArr = new Object[3];
        if (targetAccount instanceof MmpProtocol) {
            urlBuffer = new ByteBuffer().writeCompressed(PackedStringKeys.URL_ICQ_BUDDY_ICON).writeRawString(str);
        } else {
            ByteBuffer profileBuf2 = new ByteBuffer().writeCompressed(PackedStringKeys.URL_OBRAZ_FOTO);
            int dotIndex = domain.indexOf(46);
            urlBuffer = profileBuf2.writeRawString(dotIndex < 0 ? ObjectPool.unpackChars(6775139) : StringUtils.prefix(domain, dotIndex)).writeByte(47).writeRawString(atIndex < 0 ? str : StringUtils.prefix(str, atIndex)).writeCompressed(467 + Storage.state().getInt(RuntimeKeys.INT_ASYNC_TASK_ID));
        }
        objArr[0] = urlBuffer.getStringAndClear();
        objArr[1] = targetAccount;
        objArr[2] = null;
        Storage.state().setObject(RegistrationKeys.OBJ_REGISTRATION_DATA, objArr);
        new AsyncTask(AsyncTaskId.DOWNLOAD_PHOTO, objArr);
        return 0;
    }

    /* renamed from: d */
    public static final int handleSearchResultAction(int i) {
        Vector onlineAccounts = AccountManager.getOnlineMrimAccounts();
        switch (i) {
            case 0:
                if (onlineAccounts.size() <= 0) {
                    return NotificationHelper.showError(422);
                }
                ((MrimAccount) onlineAccounts.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) Storage.state().getObject(RegistrationKeys.OBJ_SEARCH_RESULT)).userId, 1));
                ScreenBuilder.onScreenClosed();
                return ScreenId.CONTACT_DELETE;
            case 1:
                if (onlineAccounts.size() <= 0) {
                    return NotificationHelper.showError(422);
                }
                ((MrimAccount) onlineAccounts.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) Storage.state().getObject(RegistrationKeys.OBJ_SEARCH_RESULT)).userId, 2));
                return ScreenId.MAP;
            case 2:
                return AppController.dialPhoneContactNext();
            case 3:
                return AppController.dialPhoneContactPrev();
            default:
                Storage.state().setInt(RuntimeKeys.INT_ASYNC_TASK_ID, 0);
                AppController.openUserProfile((MrimAccount) null, ((UserSearchResult) Storage.state().getObject(RegistrationKeys.OBJ_SEARCH_RESULT)).userId);
                ScreenBuilder.onScreenClosed();
                return 0;
        }
    }

    public static int handleScreenAction(int taskId) {
        ScreenBuilder.onScreenClosed();
        Storage.state().setInt(RuntimeKeys.INT_ASYNC_TASK_ID, taskId);
        return Storage.state().getInt(UIKeys.INT_CURRENT_SCREEN_ID);
    }

    public static int handleEnterKey() {
        restoreState();
        return ScreenManager.processScreenForm();
    }

    public static int handleBackKey() {
        restoreState();
        return 0;
    }

    public static void resetSearchResults() {
        Storage.state().clearRange(RuntimeKeys.SLOT_MSG_SUBJECT, RuntimeKeys.SLOT_MSG_EXTRA_1);
    }

    private static void restoreState() {
        ((MrimAccount) Storage.state().getAccount()).chatRoomManager.getLast().clear();
    }

    public static void clearSearchResults2() {
        Storage.state().clearRange(RegistrationKeys.SLOT_REG_PARAM_3, ContactKeys.SLOT_CONTACT_INFO);
    }
}
