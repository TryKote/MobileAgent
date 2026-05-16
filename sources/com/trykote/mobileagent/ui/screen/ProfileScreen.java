package com.trykote.mobileagent.ui.screen;

import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.ContactKeys;
import com.trykote.mobileagent.key.RegistrationKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.map.MapController;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.model.ContactInfo;
import com.trykote.mobileagent.model.SearchEntry;
import com.trykote.mobileagent.model.UserSearchResult;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.mrim.MrimAccount;
import com.trykote.mobileagent.protocol.mrim.MrimProfileManager;
import com.trykote.mobileagent.ui.ContactListManager;
import com.trykote.mobileagent.ui.ListView;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.NotificationHelper;
import com.trykote.mobileagent.ui.ScreenBuilder;
import com.trykote.mobileagent.ui.ScreenManager;
import com.trykote.mobileagent.ui.ScreenView;
import com.trykote.mobileagent.ui.Screens;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;

import java.util.Vector;

public final class ProfileScreen extends ScreenView {

    private static final int PACKED_STRING_PHOTO_URL_BASE = 467;

    public static long lastTileLoadTime;

    public ProfileScreen(int screenId) {
        super(ScreenManager.TYPE_FULLSCREEN, screenId);
    }

    public void buildContent() {
        switch (screenId) {
            case ScreenId.SEARCH_RESULTS:
                resetSearchResults();
                Screens.searchResults().show();
                break;
            case ScreenId.SEARCH_RESULT_LIST:
                buildSearchResultList();
                break;
            case ScreenId.USER_PROFILE:
                buildUserProfile();
                break;
            case ScreenId.PROFILE_LOAD:
                ContactInfo contactInfo = ContactState.getInfo();
                NotificationHelper.showErrorOrConfirm(107, 728, contactInfo.getAccount().getResourceId((Object) contactInfo.getEmailOrMmpId()));
                break;
            case ScreenId.VCARD_ACTIONS:
                Screens.vcardActions().show();
                break;
            case ScreenId.PEOPLE_SEARCH:
                Screens.peopleSearch().show();
                break;
            case ScreenId.PROFILE_EDIT:
                StringBuffer stringBuffer = new StringBuffer(StringPool.get(StringResKeys.STR_REGISTRATION_TEXT));
                stringBuffer.append(StringPool.get(AppState.getAccount().getProfileGender() + StringResKeys.STR_REGISTRATION_TEXT));
                UIState.setPhotoCache2FromBuffer(stringBuffer);
                lastTileLoadTime = System.currentTimeMillis();
                Screens.profileEdit().show();
                break;
            case ScreenId.SEARCH_ENTRY:
                AppState.setCurrentEntity((Object) null);
                Screens.searchEntry().show();
                break;
        }
        AppController.clearInitParamsAndReport();
    }

    public int onItemSelected(MenuItem item, String title, int action, Object data) {
        switch (screenId) {
            case ScreenId.SEARCH_RESULTS:
                return handleEnterKey();
            case ScreenId.SEARCH_RESULT_LIST:
                ContactState.setInfo(data);
                return 0;
            case ScreenId.USER_PROFILE:
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
            default:
                return 0;
        }
    }

    public int onSelect(MenuItem item, String title, int selectedOption,
                        Object data, Object headerData) {
        switch (screenId) {
            case ScreenId.SEARCH_RESULT_LIST:
                ContactState.setInfo(data);
                return 0;
            case ScreenId.USER_PROFILE:
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
            default:
                return 0;
        }
    }

    public int onAction(MenuItem item, Object data) {
        switch (screenId) {
            case ScreenId.SEARCH_RESULTS:
                return handleBackKey();
            case ScreenId.USER_PROFILE:
            case ScreenId.PROFILE_LOAD:
                RegistrationState.clearRegistrationData();
                return 0;
            case ScreenId.PROFILE_EDIT:
                ScreenBuilder.onScreenClosed();
                return 0;
            default:
                return 0;
        }
    }

    public int onIdle(MenuItem item, Object data, String title) {
        switch (screenId) {
            case ScreenId.USER_PROFILE:
            case ScreenId.PROFILE_LOAD:
                return RegistrationState.getRegistrationData()[2] == null ? 0 : ScreenId.CAPTCHA;
            case ScreenId.PROFILE_EDIT:
                return System.currentTimeMillis() - lastTileLoadTime > 5000 ? syncAndReturn() : 0;
            default:
                return 0;
        }
    }

    public void onClosed() {
        switch (screenId) {
            case ScreenId.SEARCH_RESULTS:
                resetSearchResults();
                break;
            case ScreenId.SEARCH_RESULT_LIST:
                clearSearchResults2();
                break;
        }
    }

    // --- Build helpers ---

    private void buildSearchResultList() {
        int errorMsgId = RuntimeState.getErrorMsgIndex();
        if (errorMsgId != 0) {
            clearSearchResults2();
            NotificationHelper.showMessageById(errorMsgId);
            return;
        }
        Vector searchResults = (Vector) RegistrationState.getParam4();
        if (searchResults.size() != 0) {
            ScreenManager.showScreen(ContactListManager.addContactItems(Screens.searchResultList(), searchResults));
        } else {
            clearSearchResults2();
            NotificationHelper.showMessageById(736);
        }
    }

    private static void buildUserProfile() {
        if (MrimProfileManager.pendingAccount == null && MrimProfileManager.pendingUrl == null) {
            Contact contact = AppState.getCurrentContact();
            String statusText = MrimProfileManager.getPendingDisplayText();
            NotificationHelper.showErrorOrConfirm(102, 728, statusText != null ? contact.account.getResourceId((Object) statusText) : loadUserProfile(contact.getIdentifier(), contact.account));
        } else {
            NotificationHelper.showErrorOrConfirm(102, 728, 0);
            loadUserProfile(MrimProfileManager.pendingUrl, MrimProfileManager.pendingAccount);
            MrimProfileManager.clearPendingProfile();
        }
    }

    // --- Public static (used externally) ---

    public static int syncAndReturn() {
        AppState.getAccount().syncProfile();
        if (SessionState.isUpdateAvailable()) {
            return SessionState.getConnectionState();
        }
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    public static int loadUserProfile(String str, Account targetAccount) {
        ByteBuffer urlBuffer;
        int atIndex = str.indexOf('@');
        String domain = StringUtils.suffix(str, atIndex + 1);
        Object[] objArr = new Object[3];
        if (targetAccount.getType() == Account.TYPE_MMP) {
            urlBuffer = new ByteBuffer().writeCharBytes("http://api.icq.net/expressions/get?f=native&type=buddyIcon&t=").writeRawString(str);
        } else {
            ByteBuffer profileBuf = new ByteBuffer().writeCharBytes("http://obraz.foto.mail.ru/");
            int dotIndex = domain.indexOf('.');
            urlBuffer = profileBuf.writeRawString(dotIndex < 0 ? ObjectPool.unpackChars(6775139) : StringUtils.prefix(domain, dotIndex)).writeByte('/').writeRawString(atIndex < 0 ? str : StringUtils.prefix(str, atIndex)).writeCharBytes(StringPool.get(PACKED_STRING_PHOTO_URL_BASE + RuntimeState.getAsyncTaskId()));
        }
        objArr[0] = urlBuffer.getStringAndClear();
        objArr[1] = targetAccount;
        objArr[2] = null;
        RegistrationState.setRegistrationData(objArr);
        new AsyncTask(AsyncTaskId.DOWNLOAD_PHOTO, objArr);
        return 0;
    }

    public static int handleSearchResultAction(int i) {
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

    public static void clearSearchResults2() {
        AppState.clearRange(RegistrationKeys.SLOT_REG_PARAM_3, ContactKeys.SLOT_CONTACT_INFO);
    }

    private static void restoreState() {
        AppState.getAccount().clearLastChatRoom();
    }
}
