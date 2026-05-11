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

public final class ProfileHandler extends BaseScreenHandler {

    // Packed string base offset for profile photo URL suffixes
    private static final int PACKED_STRING_PHOTO_URL_BASE = 467;

    public static long lastTileLoadTime;

    public void buildScreen(int screenId) {
        switch (screenId) {
            case ScreenId.SEARCH_RESULTS:
                resetSearchResults();
                Screens.searchResults(this).show();
                return;
            case ScreenId.SEARCH_RESULT_LIST:
                int errorMsgId = RuntimeState.getErrorMsgIndex();
                if (errorMsgId != 0) {
                    clearSearchResults2();
                    NotificationHelper.showMessageById(errorMsgId);
                    return;
                }
                Vector searchResults = (Vector) RegistrationState.getParam4();
                if (searchResults.size() != 0) {
                    ScreenManager.showScreen(ContactListManager.addContactItems(Screens.searchResultList(this), searchResults));
                    return;
                } else {
                    clearSearchResults2();
                    NotificationHelper.showMessageById(736);
                    return;
                }
            case ScreenId.USER_PROFILE:
                if (MrimProfileManager.pendingAccount == null && MrimProfileManager.pendingUrl == null) {
                    Contact contact = AppState.getCurrentContact();
                    String statusText = MrimProfileManager.getPendingDisplayText();
                    NotificationHelper.showErrorOrConfirm(102, 728, statusText != null ? contact.account.getResourceId((Object) statusText) : loadUserProfile(contact.getIdentifier(), contact.account));
                    return;
                } else {
                    NotificationHelper.showErrorOrConfirm(102, 728, 0);
                    loadUserProfile(MrimProfileManager.pendingUrl, MrimProfileManager.pendingAccount);
                    MrimProfileManager.clearPendingProfile();
                    return;
                }
            case ScreenId.PROFILE_LOAD:
                ContactInfo contactInfo = ContactState.getInfo();
                NotificationHelper.showErrorOrConfirm(107, 728, contactInfo.getAccount().getResourceId((Object) contactInfo.getEmailOrMmpId()));
                return;
            case ScreenId.VCARD_ACTIONS:
                Screens.vcardActions(this).show();
                return;
            case ScreenId.PEOPLE_SEARCH:
                Screens.peopleSearch(this).show();
                return;
            case ScreenId.PROFILE_EDIT:
                StringBuffer stringBuffer = new StringBuffer(ResourceAccessor.str(StringResKeys.STR_REGISTRATION_TEXT));
                stringBuffer.append(AppState.getString(((MrimAccount) AppState.getAccount()).profileManager.profile.gender + StringResKeys.STR_REGISTRATION_TEXT));
                UIState.setPhotoCache2FromBuffer(stringBuffer);
                lastTileLoadTime = System.currentTimeMillis();
                Screens.profileEdit(this).show();
                return;
            case ScreenId.SEARCH_ENTRY:
                AppState.setCurrentEntity((Object) null);
                Screens.searchEntry(this).show();
                return;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onMenuItemSelected(ListView screen, MenuItem item, String title, int action, Object data) {
        switch (screen.screenId) {
            case ScreenId.SEARCH_RESULTS:
                return handleEnterKey();
            case ScreenId.SEARCH_RESULT_LIST:
                ContactState.setInfo(data);
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
                RegistrationState.clearRegistrationData();
                return 0;
            case ScreenId.PROFILE_LOAD:
                RegistrationState.clearRegistrationData();
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
                ContactState.setInfo(data);
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
                return RegistrationState.getRegistrationData()[2] == null ? 0 : ScreenId.CAPTCHA;
            case ScreenId.PROFILE_LOAD:
                return RegistrationState.getRegistrationData()[2] == null ? 0 : ScreenId.CAPTCHA;
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

    public static final int syncAndReturn() {
        ((MrimAccount) AppState.getAccount()).profileManager.sync();
        if (SessionState.isUpdateAvailable()) {
            return SessionState.getConnectionState();
        }
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    public static final int loadUserProfile(String str, Account targetAccount) {
        ByteBuffer urlBuffer;
        int atIndex = str.indexOf('@');
        String domain = StringUtils.suffix(str, atIndex + 1);
        Object[] objArr = new Object[3];
        if (targetAccount instanceof MmpProtocol) {
            urlBuffer = new ByteBuffer().writeCompressed(PackedStringKeys.URL_ICQ_BUDDY_ICON).writeRawString(str);
        } else {
            ByteBuffer profileBuf2 = new ByteBuffer().writeCompressed(PackedStringKeys.URL_OBRAZ_FOTO);
            int dotIndex = domain.indexOf('.');
            urlBuffer = profileBuf2.writeRawString(dotIndex < 0 ? ObjectPool.unpackChars(6775139) : StringUtils.prefix(domain, dotIndex)).writeByte('/').writeRawString(atIndex < 0 ? str : StringUtils.prefix(str, atIndex)).writeCompressed(PACKED_STRING_PHOTO_URL_BASE + RuntimeState.getAsyncTaskId());
        }
        objArr[0] = urlBuffer.getStringAndClear();
        objArr[1] = targetAccount;
        objArr[2] = null;
        RegistrationState.setRegistrationData(objArr);
        new AsyncTask(AsyncTaskId.DOWNLOAD_PHOTO, objArr);
        return 0;
    }

    public static final int handleSearchResultAction(int i) {
        Vector onlineAccounts = AccountManager.getOnlineMrimAccounts();
        switch (i) {
            case 0:
                if (onlineAccounts.size() <= 0) {
                    return NotificationHelper.showError(422);
                }
                ((MrimAccount) onlineAccounts.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) RegistrationState.getSearchResult()).userId, 1));
                ScreenBuilder.onScreenClosed();
                return ScreenId.CONTACT_DELETE;
            case 1:
                if (onlineAccounts.size() <= 0) {
                    return NotificationHelper.showError(422);
                }
                ((MrimAccount) onlineAccounts.firstElement()).performUserSearch(new SearchEntry(((UserSearchResult) RegistrationState.getSearchResult()).userId, 2));
                return ScreenId.MAP;
            case 2:
                return ContactListManager.dialPhoneContactNext();
            case 3:
                return ContactListManager.dialPhoneContactPrev();
            default:
                RuntimeState.setAsyncTaskId(0);
                MrimProfileManager.openUserProfile((MrimAccount) null, ((UserSearchResult) RegistrationState.getSearchResult()).userId);
                ScreenBuilder.onScreenClosed();
                return 0;
        }
    }

    public static int handleScreenAction(int taskId) {
        ScreenBuilder.onScreenClosed();
        RuntimeState.setAsyncTaskId(taskId);
        return UIState.getCurrentScreenId();
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
        RuntimeState.clearMsgFields();
    }

    private static void restoreState() {
        ((MrimAccount) AppState.getAccount()).chatRoomManager.getLast().clear();
    }

    public static void clearSearchResults2() {
        AppState.clearRange(RegistrationKeys.SLOT_REG_PARAM_3, ContactKeys.SLOT_CONTACT_INFO);
    }
}
