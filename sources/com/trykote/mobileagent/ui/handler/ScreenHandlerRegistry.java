package com.trykote.mobileagent.ui.handler;

import com.trykote.mobileagent.core.ScreenId;

public final class ScreenHandlerRegistry {

    private static final ScreenHandler[] handlers = new ScreenHandler[181];

    static {
        ScreenHandler h;

        h = new AccountHandler();
        register(h, new int[]{ScreenId.ACCOUNT_LIST, ScreenId.ACCOUNTS_MENU, ScreenId.ACCOUNT_SWITCHER,
            ScreenId.REGISTRATION, ScreenId.MULTI_ACCOUNT_LIST, ScreenId.MULTI_ACCOUNT_SETTINGS,
            ScreenId.MAIL_ACCOUNT_LIST, ScreenId.ACCOUNT_CHECKBOX_LIST,
            ScreenId.SUBMIT_REGISTRATION, ScreenId.ACCOUNT_SWITCH_OPTIONS,
            ScreenId.XMPP_LOGIN, ScreenId.ACCOUNT_DELETE_CONFIRM, ScreenId.XMPP_LOGIN_ALT,
            ScreenId.MMP_ACCOUNT_SELECT, ScreenId.MRIM_ACCOUNT_SELECT, ScreenId.WIFI_ACCOUNT_LIST});

        h = new SettingsHandler();
        register(h, new int[]{ScreenId.SETTINGS, ScreenId.SETTINGS_MENU, ScreenId.GPS_SETTINGS,
            ScreenId.THEME_SETTINGS, ScreenId.NOTIFICATION_SETTINGS, ScreenId.SOUND_SETTINGS,
            ScreenId.PRIVACY_SETTINGS, ScreenId.CONNECTION_SETTINGS, ScreenId.THEME_OPTIONS,
            ScreenId.VIEW_MODE, ScreenId.COLOR_PICKER, ScreenId.KEY_MAPPING,
            ScreenId.FORM_SETTINGS, ScreenId.EXT_SETTINGS, ScreenId.MAP_VIEW_SETTINGS,
            ScreenId.NEARBY_SETTINGS, ScreenId.NOTIFICATION_OPTIONS});

        h = new ContactHandler();
        register(h, new int[]{ScreenId.CONTACT_LIST, ScreenId.CONTACT_EDITOR, ScreenId.ADD_CONTACT,
            ScreenId.ADD_MRIM_CONTACT, ScreenId.CONTACT_GROUP_MENU, ScreenId.CONTACT_GROUPS,
            ScreenId.CONTACT_SETTINGS, ScreenId.GROUP_SELECTOR, ScreenId.ADD_CONTACT_INFO,
            ScreenId.PHONE_GROUPS, ScreenId.CREATE_GROUP, ScreenId.RENAME_GROUP,
            ScreenId.DELETE_ENTITY, ScreenId.BATCH_DELETE, ScreenId.CONTACT_DELETE,
            ScreenId.GROUP_MOVE, ScreenId.CONTACT_MENU, ScreenId.CONTACT_INFO_VIEW,
            ScreenId.CONTACT_INFO_DETAIL, ScreenId.CONTACT_LIST_KEY, ScreenId.BLOCK_CONTACT_LIST,
            ScreenId.UNBLOCK_CONTACT_LIST, ScreenId.DELETE_CONTACT_LIST,
            ScreenId.CONTACT_DELETE_MRIM, ScreenId.CONTACT_MODIFY, ScreenId.VISIBLE_CONTACTS,
            ScreenId.GROUP_MEMBERS, ScreenId.GROUP_MANAGEMENT, ScreenId.EDIT_MEMBERS});

        h = new ChatHandler();
        register(h, new int[]{ScreenId.CHAT_ROOMS, ScreenId.CHAT_ROOM_INIT,
            ScreenId.CHAT_ROOM_MESSAGES, ScreenId.CHAT_ROOM_INVITE,
            ScreenId.CHAT_ROOM_VIEW, ScreenId.CHAT_ROOM_CONFIG,
            ScreenId.CHAT_VIEW_MODE, ScreenId.CHAT_ROOM_CONTEXT,
            ScreenId.CHAT_ROOM_ALERT, ScreenId.CHAT_ROOM_OPTIONS,
            ScreenId.CHAT_LIST_OPTIONS, ScreenId.CREATE_CHAT_ROOM,
            ScreenId.CHAT_STATUS, ScreenId.CHAT_DETAIL, ScreenId.CHAT_OPTIONS});

        h = new MessageHandler();
        register(h, new int[]{ScreenId.MESSAGE_DETAIL, ScreenId.MESSAGE_PREVIEW,
            ScreenId.COMPOSE_RECIPIENTS, ScreenId.COMPOSE_MESSAGE,
            ScreenId.MESSAGE_INPUT, ScreenId.MESSAGE_SUMMARY,
            ScreenId.DELETE_MESSAGES, ScreenId.NOTIFY_MESSAGE,
            ScreenId.SEND_TO_CONTACT, ScreenId.SEND_CONFIRM, ScreenId.SEND_DATA,
            ScreenId.MAIL_MENU, ScreenId.SEND_MAIL, ScreenId.REPLY_MAIL,
            ScreenId.MAILBOX_OPTIONS});

        h = new MapHandler();
        register(h, new int[]{ScreenId.MAP, ScreenId.MAP_MENU, ScreenId.MAP_POINTS,
            ScreenId.MAP_TOOLTIP, ScreenId.MAP_CONTEXT_MENU, ScreenId.SAVE_LOCATION,
            ScreenId.MAP_ROUTE, ScreenId.MAP_STATUS, ScreenId.MAP_ROUTE_SELECT,
            ScreenId.MAP_SEARCH, ScreenId.MAP_OPTIONS, ScreenId.SAVED_LOCATIONS,
            ScreenId.SHARE_LOCATION, ScreenId.PEOPLE_NEARBY});

        h = new ProfileHandler();
        register(h, new int[]{ScreenId.USER_PROFILE, ScreenId.PROFILE_LOAD, ScreenId.PROFILE_EDIT,
            ScreenId.PEOPLE_SEARCH, ScreenId.SEARCH_RESULTS, ScreenId.SEARCH_RESULT_LIST,
            ScreenId.SEARCH_ENTRY, ScreenId.VCARD_ACTIONS});

        h = new DialogHandler();
        register(h, new int[]{ScreenId.STATUS_DIALOG, ScreenId.ABOUT, ScreenId.BLOCK_CONFIRM,
            ScreenId.UNBLOCK_CONFIRM, ScreenId.CONFIRM_EXIT, ScreenId.TRAFFIC_COST,
            ScreenId.EMOTICON_DIALOG, ScreenId.DELETE_CONFIRM, ScreenId.INPUT_DIALOG,
            ScreenId.STATUS_INPUT, ScreenId.SOFTKEY_MENU, ScreenId.EMOTICON_PICKER,
            ScreenId.CAPTCHA, ScreenId.CLEAR_NOTIFICATIONS, ScreenId.PRIVACY_MODE,
            ScreenId.STATUS_PREVIEW, ScreenId.PRESENCE_ACTION, ScreenId.BLOG_POST,
            ScreenId.CLEAR_SEARCH, ScreenId.ASYNC_CONFIRM, ScreenId.UPDATE_ALERT,
            ScreenId.INVITE_ALERT, ScreenId.TRAFFIC_STATS});

        h = new MiscHandler();
        register(h, new int[]{ScreenId.FIRST_RUN, ScreenId.VERSION_CHECK, ScreenId.TOS_SCREEN,
            ScreenId.EVENT_QUEUE, ScreenId.PHONE_INPUT, ScreenId.SERVER_ADDRESS,
            ScreenId.REGION_SELECTOR, ScreenId.PHONE_INPUT_ALT, ScreenId.URL_OPEN,
            ScreenId.CONVERSATION, ScreenId.VERSION_SELECT, ScreenId.EMPTY_SCREEN,
            ScreenId.WIFI_NETWORKS, ScreenId.REG_FORM, ScreenId.INVITE_TOS,
            ScreenId.FORM_LIST, ScreenId.PHONE_CONTACTS, ScreenId.EDIT_SCREEN,
            ScreenId.ASYNC_TASK, ScreenId.MAIN_SCREEN, ScreenId.SHARE_MEDIA,
            ScreenId.SHARE_ALERT, ScreenId.PHOTO_SELECTOR, ScreenId.PHOTO_VIEW});
    }

    private ScreenHandlerRegistry() {
    }

    public static ScreenHandler getHandler(int screenId) {
        return (screenId >= 0 && screenId < handlers.length) ? handlers[screenId] : null;
    }

    private static void register(ScreenHandler handler, int[] screenIds) {
        for (int i = 0; i < screenIds.length; i++) {
            handlers[screenIds[i]] = handler;
        }
    }
}
