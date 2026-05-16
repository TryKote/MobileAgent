package com.trykote.mobileagent.ui;

import com.trykote.mobileagent.core.ScreenId;
import com.trykote.mobileagent.ui.screen.AboutScreen;
import com.trykote.mobileagent.ui.screen.AccountScreen;
import com.trykote.mobileagent.ui.screen.DialogScreen;
import com.trykote.mobileagent.ui.screen.ChatAsyncScreen;
import com.trykote.mobileagent.ui.screen.ChatScreen;
import com.trykote.mobileagent.ui.screen.ContactScreen;
import com.trykote.mobileagent.ui.screen.MapScreen;
import com.trykote.mobileagent.ui.screen.MessageScreen;
import com.trykote.mobileagent.ui.screen.MiscScreen;
import com.trykote.mobileagent.ui.screen.ProfileScreen;
import com.trykote.mobileagent.ui.screen.SettingsScreen;
import com.trykote.mobileagent.ui.screen.TrafficCostScreen;

/**
 * Base class for self-contained screens that own their logic.
 * Subclasses implement buildContent/onItemSelected/onAction instead of
 * relying on external handler classes.
 */
public abstract class ScreenView extends Screen {

    private static final ScreenView[] activeViews = new ScreenView[181];

    protected ScreenView(int type, int screenId) {
        super(type, screenId);
        if (screenId >= 0 && screenId < activeViews.length) {
            activeViews[screenId] = this;
        }
    }

    static ScreenView getActive(int screenId) {
        return (screenId >= 0 && screenId < activeViews.length) ? activeViews[screenId] : null;
    }

    static void clearActive(ListView closingScreen) {
        int id = closingScreen.screenId;
        if (id >= 0 && id < activeViews.length && activeViews[id] == closingScreen) {
            activeViews[id] = null;
        }
    }

    /**
     * Build the screen content (menu items, header, soft keys).
     * Called once when the screen is opened.
     */
    public abstract void buildContent();

    /**
     * Handle left soft key / OK / item selection.
     * @return ScreenId.NONE (no action), ScreenId.CLOSE (close), or screenId to open
     */
    public abstract int onItemSelected(MenuItem item, String title, int action, Object data);

    /**
     * Handle right soft key / center action.
     * @return ScreenId.NONE (no action), ScreenId.CLOSE (close), or screenId to open
     */
    public int onAction(MenuItem item, Object data) {
        return 0;
    }

    /**
     * Handle FIRE / select on a specific item (with header context).
     * @return ScreenId.NONE, ScreenId.CLOSE, screenId, or -1 to veto
     */
    public int onSelect(MenuItem item, String title, int selectedOption,
                        Object data, Object headerData) {
        return 0;
    }

    /**
     * Called when this screen is being closed (removed from stack).
     */
    public void onClosed() {
    }

    /**
     * Called when this screen is restored after a child screen closed.
     */
    public void onResumed(int closedScreenId) {
    }

    /**
     * Called on idle tick (event queue empty) while this screen is on top.
     * @return ScreenId.NONE, ScreenId.CLOSE, or screenId to open
     */
    public int onIdle(MenuItem item, Object data, String title) {
        return 0;
    }

    /**
     * Called when a MenuItem's data changes via user interaction.
     */
    public void onMenuItemChanged(MenuItem item) {
    }

    /**
     * Show this screen. Default uses showScreen; override for push behavior.
     */
    public void showSelf() {
        ScreenManager.showScreen(this);
    }

    // --- Factory ---

    /**
     * Create a ScreenView for the given screenId, or null if not registered.
     * Subclass registrations will be added here as screens are migrated.
     */
    public static ScreenView create(int screenId) {
        switch (screenId) {
            case ScreenId.ABOUT:
                return new AboutScreen();
            case ScreenId.TRAFFIC_COST:
                return new TrafficCostScreen();
            case ScreenId.SETTINGS:
            case ScreenId.SETTINGS_MENU:
            case ScreenId.GPS_SETTINGS:
            case ScreenId.THEME_SETTINGS:
            case ScreenId.NOTIFICATION_SETTINGS:
            case ScreenId.SOUND_SETTINGS:
            case ScreenId.PRIVACY_SETTINGS:
            case ScreenId.CONNECTION_SETTINGS:
            case ScreenId.NOTIFICATION_OPTIONS:
            case ScreenId.THEME_OPTIONS:
            case ScreenId.VIEW_MODE:
            case ScreenId.COLOR_PICKER:
            case ScreenId.KEY_MAPPING:
            case ScreenId.FORM_SETTINGS:
            case ScreenId.EXT_SETTINGS:
            case ScreenId.MAP_VIEW_SETTINGS:
            case ScreenId.NEARBY_SETTINGS:
                return new SettingsScreen(screenId);
            case ScreenId.CHAT_ROOMS:
            case ScreenId.CHAT_ROOM_MESSAGES:
            case ScreenId.CHAT_ROOM_INVITE:
                return new ChatAsyncScreen(screenId);
            case ScreenId.CHAT_ROOM_INIT:
            case ScreenId.CHAT_ROOM_VIEW:
            case ScreenId.CHAT_ROOM_CONFIG:
            case ScreenId.CHAT_VIEW_MODE:
            case ScreenId.CHAT_ROOM_CONTEXT:
            case ScreenId.CHAT_ROOM_ALERT:
            case ScreenId.CHAT_STATUS:
            case ScreenId.CHAT_ROOM_OPTIONS:
            case ScreenId.CHAT_LIST_OPTIONS:
            case ScreenId.CREATE_CHAT_ROOM:
            case ScreenId.CHAT_DETAIL:
            case ScreenId.CHAT_OPTIONS:
                return new ChatScreen(screenId);
            case ScreenId.CONTACT_LIST:
            case ScreenId.CONTACT_EDITOR:
            case ScreenId.ADD_CONTACT:
            case ScreenId.ADD_MRIM_CONTACT:
            case ScreenId.CONTACT_GROUP_MENU:
            case ScreenId.CONTACT_GROUPS:
            case ScreenId.CONTACT_SETTINGS:
            case ScreenId.GROUP_SELECTOR:
            case ScreenId.PHONE_GROUPS:
            case ScreenId.ADD_CONTACT_INFO:
            case ScreenId.CREATE_GROUP:
            case ScreenId.RENAME_GROUP:
            case ScreenId.DELETE_ENTITY:
            case ScreenId.BATCH_DELETE:
            case ScreenId.CONTACT_DELETE:
            case ScreenId.GROUP_MOVE:
            case ScreenId.CONTACT_MENU:
            case ScreenId.CONTACT_INFO_VIEW:
            case ScreenId.CONTACT_INFO_DETAIL:
            case ScreenId.CONTACT_LIST_KEY:
            case ScreenId.BLOCK_CONTACT_LIST:
            case ScreenId.UNBLOCK_CONTACT_LIST:
            case ScreenId.DELETE_CONTACT_LIST:
            case ScreenId.GROUP_MEMBERS:
            case ScreenId.EDIT_MEMBERS:
            case ScreenId.CONTACT_DELETE_MRIM:
            case ScreenId.CONTACT_MODIFY:
            case ScreenId.VISIBLE_CONTACTS:
            case ScreenId.GROUP_MANAGEMENT:
                return new ContactScreen(screenId);
            case ScreenId.MAP:
            case ScreenId.MAP_MENU:
            case ScreenId.MAP_POINTS:
            case ScreenId.MAP_TOOLTIP:
            case ScreenId.MAP_CONTEXT_MENU:
            case ScreenId.SAVE_LOCATION:
            case ScreenId.MAP_ROUTE:
            case ScreenId.MAP_STATUS:
            case ScreenId.MAP_ROUTE_SELECT:
            case ScreenId.MAP_SEARCH:
            case ScreenId.MAP_OPTIONS:
            case ScreenId.SAVED_LOCATIONS:
            case ScreenId.SHARE_LOCATION:
            case ScreenId.PEOPLE_NEARBY:
                return new MapScreen(screenId);
            case ScreenId.USER_PROFILE:
            case ScreenId.PROFILE_LOAD:
            case ScreenId.PROFILE_EDIT:
            case ScreenId.PEOPLE_SEARCH:
            case ScreenId.SEARCH_RESULTS:
            case ScreenId.SEARCH_RESULT_LIST:
            case ScreenId.SEARCH_ENTRY:
            case ScreenId.VCARD_ACTIONS:
                return new ProfileScreen(screenId);
            case ScreenId.MESSAGE_DETAIL:
            case ScreenId.MESSAGE_PREVIEW:
            case ScreenId.COMPOSE_RECIPIENTS:
            case ScreenId.COMPOSE_MESSAGE:
            case ScreenId.MESSAGE_INPUT:
            case ScreenId.MESSAGE_SUMMARY:
            case ScreenId.DELETE_MESSAGES:
            case ScreenId.NOTIFY_MESSAGE:
            case ScreenId.SEND_TO_CONTACT:
            case ScreenId.SEND_CONFIRM:
            case ScreenId.SEND_DATA:
            case ScreenId.MAIL_MENU:
            case ScreenId.SEND_MAIL:
            case ScreenId.REPLY_MAIL:
            case ScreenId.MAILBOX_OPTIONS:
                return new MessageScreen(screenId);
            case ScreenId.ACCOUNT_LIST:
            case ScreenId.ACCOUNTS_MENU:
            case ScreenId.ACCOUNT_SWITCHER:
            case ScreenId.REGISTRATION:
            case ScreenId.MULTI_ACCOUNT_LIST:
            case ScreenId.MULTI_ACCOUNT_SETTINGS:
            case ScreenId.MAIL_ACCOUNT_LIST:
            case ScreenId.ACCOUNT_CHECKBOX_LIST:
            case ScreenId.SUBMIT_REGISTRATION:
            case ScreenId.ACCOUNT_SWITCH_OPTIONS:
            case ScreenId.XMPP_LOGIN:
            case ScreenId.ACCOUNT_DELETE_CONFIRM:
            case ScreenId.XMPP_LOGIN_ALT:
            case ScreenId.MMP_ACCOUNT_SELECT:
            case ScreenId.MRIM_ACCOUNT_SELECT:
            case ScreenId.WIFI_ACCOUNT_LIST:
                return new AccountScreen(screenId);
            case ScreenId.FIRST_RUN:
            case ScreenId.VERSION_CHECK:
            case ScreenId.TOS_SCREEN:
            case ScreenId.EVENT_QUEUE:
            case ScreenId.PHONE_INPUT:
            case ScreenId.SERVER_ADDRESS:
            case ScreenId.REGION_SELECTOR:
            case ScreenId.PHONE_INPUT_ALT:
            case ScreenId.URL_OPEN:
            case ScreenId.CONVERSATION:
            case ScreenId.VERSION_SELECT:
            case ScreenId.EMPTY_SCREEN:
            case ScreenId.WIFI_NETWORKS:
            case ScreenId.REG_FORM:
            case ScreenId.INVITE_TOS:
            case ScreenId.FORM_LIST:
            case ScreenId.PHONE_CONTACTS:
            case ScreenId.EDIT_SCREEN:
            case ScreenId.ASYNC_TASK:
            case ScreenId.MAIN_SCREEN:
            case ScreenId.SHARE_MEDIA:
            case ScreenId.SHARE_ALERT:
            case ScreenId.PHOTO_SELECTOR:
            case ScreenId.ACCOUNT_SETUP:
                return new MiscScreen(screenId);
            case ScreenId.STATUS_DIALOG:
            case ScreenId.BLOCK_CONFIRM:
            case ScreenId.UNBLOCK_CONFIRM:
            case ScreenId.CONFIRM_EXIT:
            case ScreenId.EMOTICON_DIALOG:
            case ScreenId.DELETE_CONFIRM:
            case ScreenId.INPUT_DIALOG:
            case ScreenId.STATUS_INPUT:
            case ScreenId.SOFTKEY_MENU:
            case ScreenId.EMOTICON_PICKER:
            case ScreenId.CAPTCHA:
            case ScreenId.CLEAR_NOTIFICATIONS:
            case ScreenId.PRIVACY_MODE:
            case ScreenId.STATUS_PREVIEW:
            case ScreenId.PRESENCE_ACTION:
            case ScreenId.BLOG_POST:
            case ScreenId.CLEAR_SEARCH:
            case ScreenId.ASYNC_CONFIRM:
            case ScreenId.UPDATE_ALERT:
            case ScreenId.INVITE_ALERT:
            case ScreenId.TRAFFIC_STATS:
                return new DialogScreen(screenId);
        }
        return null;
    }
}
