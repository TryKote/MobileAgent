#!/usr/bin/env python3
"""
Generate StateKeys.java from index mapping and replace magic numbers in all source files.
"""

import re
import os
from collections import defaultdict

BASE = "sources/com/trykote/mobileagent"

# ============================================================
# COMPLETE INDEX -> CONSTANT NAME MAPPING
# Organized by domain category
# ============================================================

MAPPING = {
    # ========== TRAFFIC ACCOUNTING (0-33) ==========
    # Persisted delta - traffic counters
    # Layout: base=2, address = base + (period<<3) + (direction<<1) + protocol
    # Periods: 0=today_mrim(2-9), 1=today_mmp(10-17), 2=today_xmpp(18-25), 3=total(26-33)
    0: "DELTA_VERSION",
    1: "TRAFFIC_SAVED_DATE",
    2: "TRAFFIC_MRIM_SENT_BYTES",
    3: "TRAFFIC_MRIM_RECV_BYTES",
    4: "TRAFFIC_MRIM_SENT_PACKETS",
    5: "TRAFFIC_MRIM_RECV_PACKETS",
    6: "TRAFFIC_MRIM_SENT_MSGS",
    7: "TRAFFIC_MRIM_RECV_MSGS",
    8: "TRAFFIC_MRIM_SENT_FILES",
    9: "TRAFFIC_MRIM_RECV_FILES",
    10: "TRAFFIC_MMP_SENT_BYTES",
    11: "TRAFFIC_MMP_RECV_BYTES",
    12: "TRAFFIC_MMP_SENT_PACKETS",
    13: "TRAFFIC_MMP_RECV_PACKETS",
    14: "TRAFFIC_MMP_SENT_MSGS",
    15: "TRAFFIC_MMP_RECV_MSGS",
    16: "TRAFFIC_MMP_SENT_FILES",
    17: "TRAFFIC_MMP_RECV_FILES",
    18: "TRAFFIC_XMPP_SENT_BYTES",
    19: "TRAFFIC_XMPP_RECV_BYTES",
    20: "TRAFFIC_XMPP_SENT_PACKETS",
    21: "TRAFFIC_XMPP_RECV_PACKETS",
    22: "TRAFFIC_XMPP_SENT_MSGS",
    23: "TRAFFIC_XMPP_RECV_MSGS",
    24: "TRAFFIC_XMPP_SENT_FILES",
    25: "TRAFFIC_XMPP_RECV_FILES",
    26: "TRAFFIC_TOTAL_SENT_BYTES",
    27: "TRAFFIC_TOTAL_RECV_BYTES",
    28: "TRAFFIC_TOTAL_SENT_PACKETS",
    29: "TRAFFIC_TOTAL_RECV_PACKETS",
    30: "TRAFFIC_TOTAL_SENT_MSGS",
    31: "TRAFFIC_TOTAL_RECV_MSGS",
    32: "TRAFFIC_TOTAL_SENT_FILES",
    33: "TRAFFIC_TOTAL_RECV_FILES",

    # ========== MAP & GEO (35-43) ==========
    35: "MAP_LONGITUDE",  # stored as long (35,36)
    37: "MAP_LATITUDE",   # stored as long (37,38)
    39: "MAP_ZOOM_LEVEL",
    41: "MAP_GPS_ENABLED",
    43: "MAP_RESOURCE_URL",
    44: "MAP_INITIALIZED",

    # ========== UI SETTINGS (63-114) ==========
    63: "UI_COUNTER",
    65: "SETTING_FULLSCREEN",
    66: "SETTING_TRANSPARENCY",
    67: "SETTING_MAIL_TAB_ENABLED",
    68: "SETTING_SEARCH_TAB_ENABLED",
    69: "SETTING_FAST_CONNECTION",
    70: "SETTING_BOLD_TITLE_FONT",
    71: "SETTING_STATUS_BAR_VISIBLE",
    72: "SETTING_COLOR_THEME",
    73: "SETTING_FONT_SIZE_CHAT",
    74: "SETTING_FONT_SIZE_LIST",
    87: "SETTING_SOUND_ENABLED",
    88: "SETTING_VOLUME_LEVEL",
    89: "SETTING_NOTIFICATION_ENABLED",
    90: "SETTING_SHOW_IN_LIST",
    91: "SETTING_SHOW_POPUP",
    92: "SETTING_TRAFFIC_INFO_ENABLED",
    93: "STR_TRAFFIC_INFO_YES",
    94: "SETTING_TRAFFIC_INFO_TYPE",
    95: "STR_TRAFFIC_INFO_NO",
    96: "SETTING_AUTH_REQUIRED",
    97: "SETTING_TIMEOUT_VALUE",
    98: "SETTING_SORT_ORDER",
    99: "SETTING_SHOW_OFFLINE",
    100: "SETTING_GROUP_BY_STATUS",
    101: "SETTING_SHOW_GROUPS",
    102: "SETTING_MAX_CONTACTS",
    104: "SETTING_EXTENDED_PRESENCE",
    105: "SETTING_EXTENDED_STATUS",
    106: "SETTING_AUTO_RECONNECT",
    112: "SETTING_COMPRESSION_ENABLED",
    113: "SETTING_TRAFFIC_COST",
    114: "SETTING_BLOCK_SIZE_KB",
    117: "STR_CURRENCY_SYMBOL",

    # ========== PROTOCOL / SESSION (151, 205-206, 217-227, 236-254) ==========
    151: "STR_CONFIG_TYPE_BASE",
    205: "SETTING_KEY_STAR_ACTION",
    206: "SETTING_KEY_HASH_ACTION",
    217: "FLAG_INIT_COMPLETE",
    218: "FLAG_FULLSCREEN_REQUESTED",
    219: "TIMESTAMP_FIRST_RUN",   # stored as long (219,220)
    222: "SESSION_RANDOM_ID",
    223: "SESSION_KEY",
    227: "GEO_SAVED_DATA",
    230: "SETTING_CUSTOM_VIEW_MODE",
    236: "TIMESTAMP_LAST_XMPP_AUTH",  # stored as long (236,237)
    239: "SESSION_DEVICE_INFO",
    242: "SETTING_CONTACT_SORT_MODE",
    243: "SETTING_MULTI_ACCOUNT",
    244: "SETTING_CUSTOM_NOTE_ENABLED",
    245: "SETTING_HEADER_VISIBLE",
    246: "SETTING_TIMEZONE_OFFSET",

    # ========== ANALYTICS / COUNTERS (250-253, 274, 282, 287-294) ==========
    250: "COUNTER_MAP_CACHE_HIT",
    251: "COUNTER_MAP_CACHE_MISS",
    253: "FLAG_GPS_ACTIVE",
    254: "URL_GEO_CONFIG",
    264: "HIDDEN_CONTACTS_LIST",
    265: "CONTACT_REGISTRY_DATA",
    266: "FLAG_REGISTRATION_DONE",
    267: "LAST_ACCOUNT_NAME",
    268: "FLAG_HAS_SAVED_ACCOUNTS",
    269: "FLAG_HAS_XMPP_ACCOUNT",
    270: "FLAG_HAS_MRIM_ACCOUNT",
    271: "SETTING_NETWORK_MODE",
    272: "FLAG_KEEP_SCREEN_ON",
    274: "TIMESTAMP_LAST_CLEANUP",  # stored as long (274,275)
    276: "FLAG_MAP_VIEW_ACTIVE",
    277: "FLAG_CONTACT_LIST_ACTIVE",
    278: "FLAG_MAP_POI_SEARCH",
    279: "FLAG_MAP_ROUTE_SEARCH",
    280: "FLAG_MAP_DATA_LOADED",
    281: "FLAG_REFRESH_CONTACTS",
    282: "COUNTER_SEARCH_RESULTS",
    285: "FLAG_APP_STARTING",
    286: "FLAG_UPDATE_AVAILABLE",
    287: "TIMESTAMP_LAST_UPDATE_CHECK",  # stored as long (287,288)
    289: "SETTING_UPDATE_STATUS",

    290: "COUNTER_SCREEN_OPENS",
    291: "COUNTER_APP_STARTS",
    292: "COUNTER_ERRORS",
    293: "COUNTER_TOTAL_TRAFFIC",
    294: "COUNTER_RESERVED",

    # ========== RESOURCE BYTES (295) ==========
    295: "RES_STRING_DATA",

    # ========== UI STRING RESOURCES from object pool ==========
    299: "STR_XMPP_EVENT",
    308: "STR_DEFAULT_LANGUAGE",
    311: "STR_NAME_SEPARATOR",
    312: "STR_CONTACT_FIELD_LABELS",
    313: "STR_LABEL_COMPANY",
    314: "STR_LABEL_LOCATION",
    315: "STR_LABEL_AGE",
    316: "STR_LABEL_WEBSITE",
    317: "STR_LABEL_STATUS",
    318: "STR_GENDER_MALE",
    319: "STR_GENDER_FEMALE",
    323: "STR_AGE_UNKNOWN",
    324: "STR_SECTION_PHONE",
    325: "STR_SECTION_EMAIL",
    326: "STR_SECTION_ABOUT",
    328: "STR_SOFTKEY_BACK",
    330: "STR_SOFTKEY_MAP",
    332: "RANGE_TEMP_DATA_START",
    376: "STR_PROTOCOL_MRIM",
    377: "STR_PROTOCOL_MMP",
    396: "STR_ACCOUNTS_HEADER",
    402: "STR_PROTOCOL_XMPP",
    403: "SESSION_PLATFORM_INFO",
    420: "STR_MENU_CALL",
    421: "STR_MENU_SMS",
    424: "STR_INPUT_MODE_DEFAULT",
    425: "STR_INPUT_MODE_LATIN",
    426: "STR_INPUT_MODE_NUMERIC",
    430: "RES_EMOTICON_MAP",
    442: "STR_CONV_SUFFIX_BASE",
    445: "STR_CONV_UNREAD_PREFIX",
    446: "STR_CONV_SEPARATOR",
    447: "STR_PHONE_CONTACT_SUFFIX",
    450: "STR_PHONE_CONTACTS_PREFIX",
    451: "STR_ANONYMOUS_NAME",
    452: "STR_AUTH_GRANTED",
    453: "STR_AUTH_REQUEST",
    454: "STR_ADDED_TO_LIST",
    455: "STR_REMOVED_FROM_LIST",
    456: "STR_TYPING_NOTIFICATION",
    457: "STR_MESSAGE_SEPARATOR",
    458: "STR_STATUS_CHANGED",
    459: "STR_ACCOUNT_CONNECTED",
    460: "STR_ACCOUNT_SEPARATOR",
    464: "STR_NETWORK_ERROR",
    479: "STR_MMP_AUTH_ERROR",
    480: "STR_MMP_SYSTEM_MESSAGE",
    481: "STR_MMP_SPAM_REPORT",
    482: "STR_MMP_SPAM_SUFFIX",
    483: "STR_MMP_FILE_TRANSFER",
    488: "STR_MRIM_OFFLINE_SUFFIX",
    489: "STR_MRIM_AWAY_SUFFIX",
    494: "STR_OPERATION_COMPLETE",
    511: "STR_APP_BUILD_SUFFIX",
    519: "STR_APP_PROPERTY_NAME",
    522: "STR_LANGUAGE_PREFIX",
    524: "SLOT_LANG_OPTION_1",
    525: "SLOT_LANG_OPTION_2",
    560: "STR_MENU_SETTINGS",
    580: "RES_ICON_MAP",
    642: "STR_STATUS_ONLINE",
    643: "STR_STATUS_AWAY",
    644: "STR_STATUS_DND",
    645: "STR_STATUS_INVISIBLE",
    672: "STR_ALERT_PREFIX",
    683: "STR_SEARCH_TITLE",
    684: "STR_CITY_LIST",
    685: "STR_MONTH_NAMES",
    686: "STR_GENDER_LIST",
    687: "STR_AGE_RANGES",
    688: "STR_LABEL_MONTH",
    689: "STR_LABEL_COUNTRY",
    690: "STR_LABEL_REGION",
    691: "STR_LABEL_CITY",
    692: "STR_LABEL_AGE_RANGE",
    693: "STR_LABEL_GENDER",
    694: "STR_DOMAIN_LIST",
    696: "STR_SERVER_LIST",
    741: "STR_DEFAULT_GROUP_NAME",
    743: "STR_NOTIFICATION_NEW_MSG",
    744: "STR_LABEL_NOTES",
    760: "STR_DELETE_CONFIRM",
    768: "STR_BLOCK_CONFIRM",
    771: "STR_CHAT_NAME_PREFIX",
    776: "STR_FILE_TRANSFER_PREFIX",
    780: "STR_REGISTRATION_TEXT",
    786: "STR_MRIM_DISCONNECT",
    809: "STR_MENU_OPTIONS",
    810: "STR_MENU_REG_SMS",
    811: "STR_MENU_PHONE_PREFIX",
    817: "STR_SEARCH_URL",
    838: "STR_SEARCH_QUERY_PREFIX",
    839: "STR_EXPAND_MESSAGE",
    849: "STR_UNREAD_COUNT_PREFIX",
    867: "STR_MSG_FORWARDED",
    868: "STR_MSG_REPLIED",
    874: "STR_TRAFFIC_LABEL",
    879: "STR_PRIVACY_MODE_BASE",
    892: "STR_DEFAULT_CHATROOM",
    897: "STR_MAIN_CHATROOM",
    901: "STR_CHATROOM_PREFIX",
    902: "STR_NO_SUBJECT",
    903: "STR_PRIORITY_SUFFIX",
    905: "RES_BLOCKED_GUID",
    906: "RES_UNBLOCKED_GUID",
    907: "RES_SESSION_BYTES",
    908: "RES_AUTH_SLOT_GUIDS",
    909: "STR_MRIM_RENAME_CONTACT",
    910: "STR_CONFERENCE_INVITE",
    911: "STR_GROUP_MESSAGE",
    912: "STR_PRIVATE_MESSAGE",
    913: "STR_DEFAULT_CONTACT_NAME",
    914: "STR_CONTACT_NAME_PREFIX",
    915: "STR_REG_FIELD_NAMES",
    916: "STR_MAIL_PREFIX",
    917: "STR_NEW_MAIL_FROM",
    918: "STR_NEW_MAIL_COUNT",
    919: "STR_NEW_MAIL_SUFFIX",
    922: "STR_XMPP_SERVICE_MSG",
    923: "STR_BOT_SUFFIX",
    924: "STR_WELCOME_MESSAGE",
    944: "STR_STATUS_TITLE_PREFIX",
    945: "RES_MONTH_DAYS",
    946: "STR_ERROR_SEPARATOR",
    951: "STR_TIMEOUT_ERROR",
    954: "STR_HOUR_SEPARATOR",
    955: "STR_DISTANCE_SUFFIX",
    956: "STR_SOUND_TYPE_1",
    957: "STR_SOUND_TYPE_2",
    958: "STR_SOUND_LIST",
    961: "RES_BASE64_TABLE",
    962: "RES_EMOTICON_STATE",
    974: "STR_MAP_INFO_PREFIX",
    979: "STR_ROUTE_PREFIX",
    980: "STR_NO_ROUTE",
    981: "STR_ROUTE_LABEL",
    982: "STR_SHOW_ROUTE",
    983: "STR_DISTANCE_UNIT",
    984: "STR_INFINITY",
    986: "RES_LOG_BASE_TABLE",
    987: "RES_POW_BASE_TABLE",
    988: "RES_SHORT_INDEX_TABLE_1",
    989: "RES_SHORT_INDEX_TABLE_2",
    990: "RES_MULTIPLY_COEFFICIENTS",
    991: "RES_LOOKUP_TABLE",
    992: "RES_PALETTE_MAP_1",
    993: "RES_PALETTE_MAP_2",
    994: "STR_CHAT_DEFAULT_TOPIC",
    995: "STR_REGION_NAME_1",
    996: "STR_REGION_NAME_2",
    1001: "STR_DOWNLOAD_COMPLETE",
    1004: "RES_UPDATE_DATA",
    1016: "RANGE_ACCOUNT_CACHE_START",
    1022: "RANGE_SESSION_TEMP_START",
    1028: "STR_NOTIFY_MESSAGE",
    1029: "STR_EXIT_CONFIRM",
    1031: "STR_XMPP_AUTH_REQUEST",
    1037: "STR_PLACEHOLDER_TEXT",
    1038: "STR_EMPTY",
    1039: "STR_GROUP_DEFAULT",
    1040: "STR_GROUP_NOT_IN_LIST",
    1041: "STR_GROUP_IGNORE",
    1042: "STR_GROUP_TEMPORARY",
    1043: "STR_GROUP_PHONE_CONTACTS",
    1044: "STR_TAB_MAIL",
    1045: "STR_TAB_SEARCH",
    1046: "STR_SPACE",
    1047: "STR_TAB_CONTACTS",
    1048: "STR_SOFTKEY_YES",
    1050: "STR_SOFTKEY_NO",
    1055: "STR_SOFTKEY_CLOSE",
    1060: "STR_NOTIFICATION_SOUND",
    1062: "STR_SOFTKEY_MENU",

    # ========== RUNTIME OBJECT POOL (1224-1405) ==========
    1224: "STR_CMD_SHOW_MAP",
    1225: "STR_CMD_SHOW_LIST",
    1226: "STR_CONFIG_STATUS_PREFIX",
    1233: "STR_PHONE_SUFFIX",
    1234: "STR_PHONE_PREFIX",
    1236: "SLOT_SAVED_STRING",
    1237: "SLOT_CURRENT_CONTACT_ID",
    1238: "OBJ_CALLBACK_ARRAY",
    1239: "SLOT_INIT_PARAMS",
    1240: "SLOT_LANGUAGE_OPTION",
    1241: "VEC_ACCOUNTS",
    1242: "VEC_PENDING_CONNECTIONS",
    1243: "VEC_ONLINE_CONTACTS",
    1244: "VEC_ACTIVE_CONNECTIONS",
    1245: "VEC_TAB_ITEMS",
    1246: "VEC_TAB_BARS",
    1247: "VEC_POPUP_ITEMS",
    1248: "SLOT_SEARCH_QUERY",
    1249: "SLOT_TOOLTIP_TEXT_1",
    1250: "SLOT_TOOLTIP_TEXT_2",
    1251: "SLOT_ACTIVE_PROTOCOL_NAME",
    1252: "SLOT_ACCOUNT_LIST_TEXT",
    1253: "SLOT_MSG_ID_1",
    1254: "SLOT_MSG_ID_2",
    1255: "SLOT_TEMP_OBJECT_1",
    1256: "RANGE_PHONE_CONTACT_START",
    1257: "VEC_PHONE_RESULTS",
    1258: "OBJ_SEARCH_RESULT",
    1259: "OBJ_TEXT_BOX",
    1260: "SLOT_XMPP_COMMAND_1",
    1261: "SLOT_XMPP_COMMAND_2",
    1263: "SLOT_CLOCK_STRING",
    1264: "RANGE_MEDIA_RESOURCES_START",
    1265: "OBJ_MEDIA_PLAYER",
    1266: "VEC_EVENT_QUEUE",
    1267: "ARR_EVENT_TYPE_1",
    1268: "ARR_EVENT_TYPE_2",
    1269: "ARR_EVENT_TYPE_3",
    1270: "ARR_EVENT_TYPE_4",
    1271: "OBJ_REGISTRATION_DATA",
    1272: "VEC_SCREEN_STACK",
    1273: "GFX_CONTEXT_BASE",
    1274: "GFX_CONTEXT_BOLD",
    1275: "GFX_CONTEXT_TITLE",
    1276: "GFX_CONTEXT_NORMAL",
    1277: "GFX_CONTEXT_NORMAL_2",
    1278: "GFX_CONTEXT_BOLD_2",
    1279: "SLOT_STATUS_TEXT",
    1280: "SLOT_NOTIFICATION_TEXT",
    1281: "SLOT_CURRENT_ACCOUNT",
    1282: "SLOT_TEMP_ACCOUNT",
    1283: "VEC_FILTERED_ACCOUNTS",
    1284: "SLOT_SCREEN_TITLE",
    1285: "SLOT_SCREEN_SUBTITLE",
    1286: "SLOT_SCREEN_VALUE",
    1287: "SLOT_DEVICE_ID",
    1288: "SLOT_APP_VERSION_STRING",
    1289: "SLOT_SCREEN_DESCRIPTION",
    1290: "SLOT_CURRENT_MSG_TEXT",
    1291: "VEC_ACCOUNT_SELECTION",
    1292: "SLOT_CHAT_NAME",
    1293: "SLOT_PASSWORD",
    1294: "SLOT_NOTIFICATION_TITLE",
    1295: "SLOT_NEW_GROUP_NAME",
    1296: "SLOT_CONTACT_JID",
    1297: "SLOT_DISPLAY_NAME",
    1298: "SLOT_FIRST_NAME",
    1299: "SLOT_LAST_NAME",
    1300: "SLOT_REG_FIELD_1",
    1301: "SLOT_REG_FIELD_2",
    1302: "SLOT_INPUT_TEXT",
    1306: "SLOT_SEARCH_RESULT",
    1307: "SLOT_SEARCH_FIELD_1",
    1308: "SLOT_SEARCH_FIELD_2",
    1309: "SLOT_SEARCH_FIELD_3",
    1310: "SLOT_SEARCH_FIELD_4",
    1311: "SLOT_SEARCH_FIELD_5",
    1312: "SLOT_SEARCH_FIELD_6",
    1313: "SLOT_SEARCH_LABEL_1",
    1314: "SLOT_SELECTED_GROUP",
    1315: "SLOT_REG_PARAM_1",
    1316: "SLOT_REG_PARAM_2",
    1317: "SLOT_REG_PARAM_3",
    1318: "SLOT_REG_PARAM_4",
    1319: "SLOT_CONTACT_INFO",
    1320: "SLOT_GROUP_ADD_NAME",
    1321: "SLOT_GROUP_ADD_DISPLAY",
    1322: "SLOT_GROUP_ADD_GROUP",
    1323: "SLOT_MENU_ITEM_1",
    1324: "VEC_GROUP_LIST",
    1325: "SLOT_GROUP_LIST_INDEX",
    1327: "SLOT_TRAFFIC_COST_TEXT",
    1336: "OBJ_PHOTO_CACHE_1",
    1337: "OBJ_PHOTO_CACHE_2",
    1341: "SLOT_MAP_SEARCH_QUERY",
    1342: "STR_MAP_LOCATION_NAME",
    1343: "STR_MAP_LOCATION_URL",
    1344: "SLOT_MAP_POINT_1",
    1345: "SLOT_MAP_POINT_2",
    1346: "SLOT_MESSAGE_ID",
    1347: "SLOT_UNREAD_COUNT_TEXT",
    1348: "SLOT_MSG_SUBJECT",
    1349: "SLOT_MSG_SENDER",
    1350: "SLOT_MSG_BODY",
    1351: "SLOT_MSG_EXTRA_1",
    1352: "SLOT_MSG_EXTRA_2",
    1353: "SLOT_MSG_EXTRA_3",
    1354: "SLOT_TRAFFIC_STATUS_TEXT",
    1355: "SLOT_MEDIA_PLAYER",
    1356: "SLOT_MEDIA_STREAM",
    1357: "SLOT_MEDIA_RESOURCE",
    1358: "SLOT_MEDIA_CONTROL",
    1359: "SLOT_MEDIA_VOLUME",
    1360: "SLOT_MEDIA_CALLBACK",
    1361: "OBJ_GFX_CONTEXTS_ARRAY",
    1362: "ARR_GFX_HEIGHTS",
    1363: "OBJ_FONT_1",
    1364: "OBJ_FONT_2",
    1365: "SLOT_CURRENT_ENTITY",
    1366: "OBJ_MIDLET",
    1367: "OBJ_CALENDAR",
    1368: "OBJ_DATE",
    1369: "STR_SEPARATOR",
    1370: "ARR_EMPTY_INT",
    1371: "OBJ_CANVAS",
    1372: "OBJ_RANDOM",
    1373: "SLOT_MAP_TILE_REQUEST",
    1374: "SLOT_MAP_TILE_DATA",
    1375: "STR_APP_NAME",
    1376: "SLOT_ACCOUNT_LOGIN",
    1377: "SLOT_ACCOUNT_PASSWORD",
    1378: "SLOT_ACCOUNT_SERVER",
    1379: "SLOT_ACCOUNT_DISPLAY_NAME",
    1380: "SLOT_ACCOUNT_TYPE_STR",
    1381: "SLOT_SESSION_HASH",
    1382: "SLOT_SESSION_TOKEN",
    1383: "SLOT_MAP_DATA",
    1384: "SLOT_XMPP_SESSION_ID",
    1389: "VEC_MAP_POINTS",
    1390: "OBJ_GEO_REGION",
    1391: "OBJ_GEO_REGION_2",
    1392: "OBJ_MENU_ACTIONS",
    1393: "OBJ_MENU_LABELS",
    1394: "OBJ_TILE_CACHE",
    1395: "OBJ_TILE_REQUEST_ARRAY",
    1396: "OBJ_SEARCH_PARAMS_1",
    1397: "OBJ_SEARCH_PARAMS_2",
    1398: "VEC_CHATROOM_LIST",
    1399: "VEC_MESSAGE_LIST",
    1400: "VEC_CONTACT_GROUPS",
    1401: "VEC_PHOTO_QUEUE",
    1402: "VEC_TILE_QUEUE",
    1404: "OBJ_HTTP_CALLBACK",

    # ========== INT POOL - RUNTIME STATE (1406+) ==========
    1407: "INT_IMAGE_COUNTER",
    1408: "INT_POPUP_HEIGHT",
    1409: "FLAG_MAP_SCREEN_VISIBLE",
    1410: "MAP_SAVED_LONGITUDE",   # stored as long
    1412: "MAP_SAVED_LATITUDE",    # stored as long
    1414: "FLAG_MAP_OVERLAY_ACTIVE",
    1415: "MAP_VIEWPORT_WIDTH",
    1416: "MAP_VIEWPORT_HEIGHT",
    1418: "FLAG_ONLINE_CUSTOM_OFF",
    1419: "FLAG_ONLINE_CUSTOM_ON",
    1422: "FLAG_GPS_NO_MAP",
    1423: "FLAG_GPS_WITH_MAP",
    1438: "INT_ACCOUNT_INDEX",
    1442: "FLAG_MAP_MODE_ACTIVE",
    1443: "FLAG_NEW_MESSAGE",
    1444: "INT_PHONE_SCROLL_OFFSET",
    1445: "FLAG_PHONE_HAS_NEXT",
    1446: "FLAG_PHONE_HAS_PREV",
    1447: "INT_XMPP_COMMAND_INDEX",
    1448: "INT_XMPP_SELECTION_INDEX",
    1449: "FLAG_MRIM_DATA_LOADED",
    1450: "INT_FONT_HEIGHT",
    1451: "INT_BOLD_FONT_HEIGHT",
    1452: "INT_TITLE_FONT_HEIGHT",
    1453: "INT_NORMAL_FONT_HEIGHT",
    1454: "INT_NORMAL_FONT_HEIGHT_2",
    1455: "INT_BOLD_FONT_HEIGHT_2",
    1456: "FLAG_STATUS_TEXT_SET",
    1457: "INT_LAST_POLL_TIMESTAMP",
    1458: "INT_LAST_CHECK_TIMESTAMP",
    1459: "INT_LAST_LIST_SIZE",
    1460: "FLAG_RESOURCE_LOADING",
    1462: "FLAG_HAS_MULTIPLE_MRIM",
    1463: "FLAG_HAS_MRIM_ACCOUNTS",
    1464: "FLAG_HAS_MRIM_ACCOUNTS_2",
    1465: "FLAG_HAS_XMPP_ACCOUNTS",
    1466: "INT_TARGET_STATE",
    1467: "FLAG_SHOW_STATUS_FLAGS",
    1468: "FLAG_CAPTCHA_SHOWN",
    1469: "TIMESTAMP_SELECTED_MSG",  # stored as long
    1471: "FLAG_CLEANUP_DONE",
    1472: "FLAG_MULTIPLE_MRIM",
    1473: "FLAG_MULTIPLE_XMPP",
    1474: "INT_SERVER_INDEX",
    1475: "INT_PROTOCOL_TYPE",
    1476: "INT_CONNECTION_STATE",
    1477: "FLAG_LOADING",
    1478: "FLAG_CONTACTS_LOADED",
    1479: "FLAG_MAP_LOADING",
    1480: "INT_REGION_CODE",
    1481: "INT_COUNTRY_CODE",
    1482: "INT_SEARCH_PARAM_1",
    1483: "INT_SEARCH_PARAM_2",
    1484: "INT_SEARCH_PARAM_3",
    1485: "INT_SEARCH_COUNTRY",
    1486: "INT_SEARCH_REGION",
    1487: "INT_SEARCH_CITY",
    1488: "INT_SEARCH_AGE",
    1489: "INT_SEARCH_GENDER",
    1490: "FLAG_SEARCH_ONLINE_ONLY",
    1491: "INT_REG_DOMAIN_INDEX",
    1492: "FLAG_REG_SMS_MODE",
    1493: "INT_SELECTED_GROUP_INDEX",
    1494: "FLAG_IS_MRIM_CONTACT",
    1495: "FLAG_CONTACT_IS_MRIM",
    1496: "FLAG_CONTACT_IS_GROUP",
    1497: "FLAG_CONTACT_IS_USER",
    1498: "FLAG_CONTACT_IS_ONLINE",
    1499: "FLAG_CONTACT_HAS_UNREAD",
    1501: "FLAG_CONTACT_HAS_VCARD",
    1504: "FLAG_XMPP_CAN_EDIT",
    1505: "FLAG_SHOW_NOTIFICATION",
    1506: "INT_ERROR_MSG_INDEX",
    1507: "INT_GROUP_OPERATION_RESULT",
    1508: "FLAG_SHOW_PHOTO",
    1509: "FLAG_GROUP_ADD_RESULT",
    1510: "INT_PERIOD_INDEX",
    1511: "FLAG_FULLSCREEN_ACTIVE",
    1512: "INT_SCREEN_ACTION",
    1513: "INT_CHATROOM_ID",
    1514: "INT_SCROLL_OFFSET",
    1515: "INT_XMPP_ACTION",
    1516: "INT_XMPP_ACTION_TYPE",
    1517: "FLAG_CHATROOM_HAS_MORE",
    1518: "FLAG_MSG_READ_SELECTED",
    1519: "FLAG_MSG_UNREAD_SELECTED",
    1520: "FLAG_CHATROOM_HAS_MEMBERS",
    1521: "FLAG_IS_CHATROOM",
    1522: "FLAG_MSG_UNREAD",
    1523: "FLAG_MSG_READ",
    1524: "FLAG_SPECIAL_KEY_MODE",
    1525: "INT_CHAT_VIEW_MODE",
    1526: "FLAG_EXTENDED_CHAT_VIEW",
    1527: "INT_ACTIVE_CHATROOM_ID",
    1528: "INT_SCREEN_WIDTH",
    1529: "INT_SCREEN_HEIGHT",
    1530: "TIMESTAMP_CURRENT",      # stored as long
    1531: "INT_CURRENT_TIMESTAMP",
    1532: "TIMESTAMP_OFFSET",       # stored as long
    1534: "FLAG_BLINK_STATE",
    1535: "FLAG_SUPPORTS_ALPHA",
    1536: "FLAG_WIFI_CONNECTION",
    1537: "FLAG_KNOWN_PLATFORM",
    1538: "FLAG_ADVANCED_FEATURES",
    1543: "FLAG_KNOWN_DEVICE",
    1546: "FLAG_CHAT_HAS_ITEMS",
    1547: "FLAG_MAP_TILES_PENDING",
    1548: "INT_XMPP_TRAFFIC_BYTES",
    1549: "FLAG_TILES_READY",
    1550: "INT_MAX_PENDING_REQUESTS",
    1551: "FLAG_TILE_CACHE_ENABLED",
    1552: "INT_TILE_CACHE_SIZE",
    1553: "FLAG_MAP_SCROLLING",
    1556: "TIMESTAMP_MAP_SCROLL",    # stored as long
    1558: "MAP_SCROLL_LON",          # stored as long
    1560: "MAP_SCROLL_LAT",          # stored as long
    1564: "INT_MAP_SCROLL_DIRECTION",
    1566: "FLAG_XMPP_ROSTER_LOADED",
    1573: "FLAG_TYPING_INDICATOR",
    1574: "FLAG_TYPING_VISIBLE",
    1575: "FLAG_TYPING_HIDDEN",
    1576: "FLAG_PHOTO_REGISTRY_READY",
    1577: "FLAG_CONVERSATION_ACTIVE",

    # Higher int pool indices (sparsely used)
    2122: "INT_SCREEN_BUILDER_ACTION",
    2573: "INT_CONTACT_ICON_SIZE",
    2594: "INT_MESSAGE_ICON",
    2722: "FLAG_CHAT_ROOM_CREATED",
    3329: "INT_NOTIFICATION_SCREEN_ID",
    3510: "INT_CONTACT_TYPE_CODE",
    3650: "INT_CURRENT_SCREEN_ID",
    3705: "INT_OK_MENU_ACTION",
    3706: "INT_OK_MENU_TYPE",
    3707: "FLAG_CONTACT_MENU_MODE",
    3784: "INT_CANCEL_MENU_ACTION",
    3785: "INT_CANCEL_MENU_TYPE",
    3834: "INT_INFO_SCREEN_MODE",
    3897: "INT_ADD_CONTACT_MODE",
    3985: "INT_TRAFFIC_PERIOD_LABEL",
    3987: "INT_STAT_ROWS",
    3994: "INT_STAT_COLS",
    4113: "INT_DELETE_BUTTON_ICON",
    4118: "INT_BLOCK_BUTTON_ICON",
    4305: "INT_SETTINGS_THEME",
    4308: "INT_SETTINGS_ACTION",
    4313: "INT_SETTINGS_VALUE_1",
    4317: "INT_SETTINGS_VALUE_2",
    4486: "INT_HTTP_RESULT_SCREEN",
    4497: "INT_HTTP_PARAM_2",
    4498: "INT_HTTP_PARAM_1",
    4778: "INT_CHAT_LIST_MODE",
    4895: "INT_ASYNC_TASK_ID",
    4914: "PALETTE_COLORS_BASE",
    5042: "PALETTE_SCREEN_BASE",
    5050: "PALETTE_MAP_BASE",

    # ========== PACKED STRING RESOURCES (>5179) ==========
    # Format: (length << 16) | offset into byte array at pool[295]
    65747: "STR_RES_COLON",
    132297: "STR_RES_AT_SIGN",
    133118: "STR_RES_PROTOCOL_SEPARATOR",
    134123: "STR_RES_SLASH",
    197069: "STR_RES_ARROW",
    198546: "STR_RES_HTTP_PREFIX",
    198549: "STR_RES_HTTPS_PREFIX",
    201188: "STR_RES_NEWLINE",
    262589: "STR_RES_EQUALS",
    262852: "STR_RES_AMPERSAND",
    263250: "STR_RES_QUESTION_MARK",
    263849: "STR_RES_QUOTE",
    264068: "STR_RES_YES",
    264133: "STR_RES_CLOSE_TAG",
    264203: "STR_RES_OPEN_TAG",
    264254: "STR_RES_SEMICOLON",
    264258: "STR_RES_COMMA",
    264455: "STR_RES_DOT",
    266215: "STR_RES_DASH_SEPARATOR",
    266221: "STR_RES_SPACE_DASH_SPACE",
    267133: "STR_RES_BRACKET_OPEN",
    267762: "STR_RES_DATE_SEPARATOR",
    329636: "STR_RES_PARAM_1",
    329772: "STR_RES_PARAM_2",
    329785: "STR_RES_PARAM_3",
    329959: "STR_RES_PARAM_4",
    332005: "STR_RES_URL_PARAM_1",
    332816: "STR_RES_URL_PARAM_2",
    333027: "STR_RES_URL_PARAM_3",
    333508: "STR_RES_URL_PARAM_4",
    395134: "STR_RES_STATUS_LABEL",
    395262: "STR_RES_INFO_PREFIX",
    396261: "STR_RES_HEADER_1",
    396269: "STR_RES_HEADER_2",
    397287: "STR_RES_FIELD_NAME_1",
    397293: "STR_RES_FIELD_NAME_2",
    398406: "STR_RES_LABEL_TEXT_1",
    399049: "STR_RES_LABEL_TEXT_2",
    459255: "STR_RES_MENU_ITEM_1",
    459528: "STR_RES_MENU_ITEM_2",
    460784: "STR_RES_DIALOG_TITLE_1",
    460804: "STR_RES_DIALOG_TITLE_2",
    460837: "STR_RES_DIALOG_TITLE_3",
    462816: "STR_RES_COMMAND_1",
    463517: "STR_RES_COMMAND_2",
    525044: "STR_RES_CONTENT_TYPE",
    526244: "STR_RES_HEADER_NAME_1",
    526365: "STR_RES_HEADER_NAME_2",
    526385: "STR_RES_HEADER_NAME_3",
    529061: "STR_RES_URL_PATH_1",
    530129: "STR_RES_URL_PATH_2",
    530137: "STR_RES_URL_PATH_3",
    590588: "STR_RES_HTTP_METHOD",
    590694: "STR_RES_USER_AGENT",
    591768: "STR_RES_CONTENT_ENCODING",
    591847: "STR_RES_XMPP_NAMESPACE_1",
    591883: "STR_RES_XMPP_NAMESPACE_2",
    591892: "STR_RES_XMPP_NAMESPACE_3",
    592851: "STR_RES_XMPP_TAG_1",
    592860: "STR_RES_XMPP_TAG_2",
    594984: "STR_RES_XMPP_STANZA_1",
    655360: "STR_RES_PROTOCOL_TAG_1",
    655831: "STR_RES_PROTOCOL_TAG_2",
    656925: "STR_RES_PROTOCOL_TAG_3",
    657363: "STR_RES_PROTOCOL_TAG_4",
    657373: "STR_RES_PROTOCOL_TAG_5",
    658377: "STR_RES_PROTOCOL_TAG_6",
    660472: "STR_RES_PROTOCOL_ATTR_1",
    660501: "STR_RES_PROTOCOL_ATTR_2",
    660807: "STR_RES_PROTOCOL_ATTR_3",
    722608: "STR_RES_XML_TAG_1",
    722874: "STR_RES_XML_TAG_2",
    723889: "STR_RES_XML_ATTR_1",
    725650: "STR_RES_XML_ATTR_2",
    789512: "STR_RES_LONG_LABEL_1",
    852449: "STR_RES_MEDIA_CONTROL",
    854972: "STR_RES_LONG_LABEL_2",
    919536: "STR_RES_URL_TEMPLATE_1",
    919712: "STR_RES_URL_TEMPLATE_2",
    922626: "STR_RES_URL_TEMPLATE_3",
    989287: "STR_RES_URL_TEMPLATE_4",
    1050207: "STR_RES_LONG_URL_1",
    1052223: "STR_RES_LONG_URL_2",
    1052310: "STR_RES_LONG_URL_3",
    1114895: "STR_RES_LONG_URL_4",
    1115687: "STR_RES_LONG_URL_5",
    1245774: "STR_RES_VERY_LONG_URL_1",
    1377771: "STR_RES_API_URL_1",
    1377926: "STR_RES_API_URL_2",
    1377947: "STR_RES_API_URL_3",
    1379315: "STR_RES_API_URL_4",
    1508975: "STR_RES_API_URL_5",
    1509223: "STR_RES_API_URL_6",
    1511369: "STR_RES_API_URL_7",
    1574400: "STR_RES_LONG_API_URL_1",
    1574735: "STR_RES_LONG_API_URL_2",
    1640123: "STR_RES_LONG_API_URL_3",
    1640193: "STR_RES_LONG_API_URL_4",
    1640218: "STR_RES_LONG_API_URL_5",
    1771076: "STR_RES_VERY_LONG_API_1",
    1774025: "STR_RES_VERY_LONG_API_2",
    1836851: "STR_RES_VERY_LONG_API_3",
    1841038: "STR_RES_VERY_LONG_API_4",
    2098635: "STR_RES_HUGE_URL_1",
    2163862: "STR_RES_HUGE_URL_2",
    2295208: "STR_RES_HUGE_URL_3",
    2558996: "STR_RES_HUGE_URL_4",
    2755089: "STR_RES_HUGE_URL_5",
    2950249: "STR_RES_HUGE_URL_6",
    2950868: "STR_RES_HUGE_URL_7",
    2951781: "STR_RES_HUGE_URL_8",
    3607418: "STR_RES_MEGA_URL_1",
    3805583: "STR_RES_MEGA_URL_2",
    4788096: "STR_RES_MEGA_URL_3",
    14290598: "STR_RES_MEGA_URL_4",
    33819707: "STR_RES_MEGA_URL_5",

    # Indices only used in getAppProperty
    593549: "STR_RES_APP_PROPERTY_COMPRESSION",
}


def collect_all_indices():
    """Collect all unique indices used in AppState calls across all Java files."""
    index_info = defaultdict(lambda: {'methods': set(), 'files': set()})

    for root, dirs, files in os.walk(BASE):
        for f in files:
            if not f.endswith('.java'):
                continue
            path = os.path.join(root, f)
            with open(path) as fh:
                content = fh.read()
            for m in re.finditer(r'AppState\.(\w+)\(\s*(\d+)', content):
                method_name = m.group(1)
                index = int(m.group(2))
                index_info[index]['methods'].add(method_name)
                index_info[index]['files'].add(f.replace('.java', ''))

    return index_info


def find_unmapped_indices(index_info):
    """Find indices that are used but not in the mapping."""
    unmapped = []
    for idx in sorted(index_info.keys()):
        if idx not in MAPPING:
            info = index_info[idx]
            unmapped.append((idx, ','.join(sorted(info['methods'])), ','.join(sorted(info['files']))))
    return unmapped


def generate_state_keys_java():
    """Generate the StateKeys.java file content."""
    lines = []
    lines.append("package com.trykote.mobileagent.core;")
    lines.append("")
    lines.append("/**")
    lines.append(" * Named constants for all AppState index keys.")
    lines.append(" * Replaces magic numbers used with AppState.getInt/getBool/getString/etc.")
    lines.append(" */")
    lines.append("public final class StateKeys {")
    lines.append("    private StateKeys() {}")
    lines.append("")

    # Group by category based on naming prefix
    categories = [
        ("Traffic Accounting", "DELTA_", "TRAFFIC_"),
        ("Map & Geo", "MAP_", "GEO_"),
        ("UI Settings", "SETTING_", "UI_"),
        ("Flags & State", "FLAG_"),
        ("Counters & Analytics", "COUNTER_"),
        ("Timestamps", "TIMESTAMP_"),
        ("Session & Auth", "SESSION_", "URL_"),
        ("UI String Resources (pool)", "STR_"),
        ("Binary Resources (pool)", "RES_"),
        ("Runtime Object Slots", "SLOT_", "OBJ_", "VEC_", "ARR_", "GFX_"),
        ("Runtime Integer State", "INT_"),
        ("Palette & Colors", "PALETTE_"),
        ("Ranges", "RANGE_"),
        ("Packed String Resources (>5179)", "STR_RES_"),
    ]

    # Sort all entries by value
    sorted_entries = sorted(MAPPING.items(), key=lambda x: x[0])

    # Print by category
    printed = set()
    for cat_name, *prefixes in categories:
        entries = [(idx, name) for idx, name in sorted_entries
                   if any(name.startswith(p) for p in prefixes) and idx not in printed]
        if not entries:
            continue
        lines.append(f"    // === {cat_name} ===")
        for idx, name in entries:
            lines.append(f"    public static final int {name} = {idx};")
            printed.add(idx)
        lines.append("")

    # Any remaining
    remaining = [(idx, name) for idx, name in sorted_entries if idx not in printed]
    if remaining:
        lines.append("    // === Other ===")
        for idx, name in remaining:
            lines.append(f"    public static final int {name} = {idx};")
        lines.append("")

    lines.append("}")
    lines.append("")
    return "\n".join(lines)


def do_replacement():
    """Replace all magic numbers in source files with StateKeys constants."""
    replacements_made = 0
    files_modified = 0

    for root, dirs, files in os.walk(BASE):
        for f in files:
            if not f.endswith('.java'):
                continue
            path = os.path.join(root, f)
            with open(path) as fh:
                original = fh.read()

            modified = original

            # 1. Replace AppState.method(NUMBER patterns
            #    Match number followed by comma, paren, space, or end-of-expression
            def replace_method_call(m):
                method = m.group(1)
                number = int(m.group(2))
                if number in MAPPING:
                    return f"AppState.{method}(StateKeys.{MAPPING[number]}"
                return m.group(0)

            modified = re.sub(
                r'AppState\.(\w+)\(\s*(\d+)(?=[\s,\)])',
                replace_method_call,
                modified
            )

            # 2. Replace AppState.pool[NUMBER] patterns
            def replace_pool_access(m):
                number = int(m.group(1))
                if number in MAPPING:
                    return f"AppState.pool[StateKeys.{MAPPING[number]}]"
                return m.group(0)

            modified = re.sub(
                r'AppState\.pool\[(\d+)\]',
                replace_pool_access,
                modified
            )

            if modified != original:
                # Add import if not present
                if 'StateKeys.' in modified and \
                   'import com.trykote.mobileagent.core.StateKeys' not in modified:
                    if 'package com.trykote.mobileagent.core;' in modified:
                        # Same package - no import needed
                        pass
                    else:
                        import_line = "import com.trykote.mobileagent.core.StateKeys;\n"
                        pkg_match = re.search(r'^package [^;]+;\s*\n', modified)
                        if pkg_match:
                            insert_pos = pkg_match.end()
                            next_chars = modified[insert_pos:insert_pos+10]
                            if next_chars.startswith('\n'):
                                modified = modified[:insert_pos] + "\n" + import_line + modified[insert_pos:]
                            elif next_chars.startswith('import'):
                                modified = modified[:insert_pos] + import_line + modified[insert_pos:]
                            else:
                                modified = modified[:insert_pos] + "\n" + import_line + modified[insert_pos:]

                with open(path, 'w') as fh:
                    fh.write(modified)
                files_modified += 1
                replacements_made += len(re.findall(r'StateKeys\.', modified))

    return files_modified, replacements_made


if __name__ == "__main__":
    import sys

    if len(sys.argv) > 1 and sys.argv[1] == "--check":
        # Check for unmapped indices
        index_info = collect_all_indices()
        unmapped = find_unmapped_indices(index_info)
        if unmapped:
            print(f"Found {len(unmapped)} unmapped indices:")
            for idx, methods, files in unmapped:
                print(f"  {idx}: [{methods}] in {files}")
        else:
            print("All indices are mapped!")
        print(f"\nTotal mapped: {len(MAPPING)}")
        print(f"Total used: {len(index_info)}")

    elif len(sys.argv) > 1 and sys.argv[1] == "--generate":
        # Generate StateKeys.java
        content = generate_state_keys_java()
        outpath = os.path.join(BASE, "core", "StateKeys.java")
        with open(outpath, 'w') as f:
            f.write(content)
        print(f"Generated {outpath}")
        print(f"Total constants: {len(MAPPING)}")

    elif len(sys.argv) > 1 and sys.argv[1] == "--replace":
        # Do replacement
        files_mod, replacements = do_replacement()
        print(f"Modified {files_mod} files")
        print(f"Total StateKeys references: {replacements}")

    else:
        print("Usage:")
        print("  --check    : Check for unmapped indices")
        print("  --generate : Generate StateKeys.java")
        print("  --replace  : Replace magic numbers in source files")
