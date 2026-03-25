#!/usr/bin/env python3
"""
Tool for serializing/deserializing the MobileAgent cfg resource file.

The cfg file contains:
  - Object pool (1406 entries): strings (CP1251), integers, byte arrays, nulls
  - Screen data (3605 ints): 136 screen definitions with items and trailing data
    (runtime variable defaults formerly in intPool header are now set in AppState.java)

The packed strings blob (index 295, RES_STRING_DATA) is stored inline in
config.json as a "packed_strings" entry with an "entries" array. Each entry
is either a text string ("value") or raw binary ("bytes" as base64).
Named sub-string references are stored in a separate "names" array with
offset/length pairs used to generate PackedStringKeys.java constants.

Subcommands:
  --deserialize <cfg_path> <output_dir>   Deserialize binary cfg to config.json
  --serialize <input_dir> <cfg_path>      Serialize config.json to binary cfg
  --round-trip <cfg_path> <input_dir>     Round-trip verify: serialize -> deserialize -> compare
  --gen-java <input_dir> <output_java>    Generate PackedStringKeys.java from config.json
  --gen-screens <input_dir> <output_java> Generate ScreenDef.java from config.json
  --gen-palette <input_dir> <output_java> Generate PaletteKeys.java from palette.json
"""

import argparse
import base64
import json
import os
import sys

OBJECT_POOL_SIZE = 1406
SCREEN_DATA_SIZE = 3605
INT_POOL_HEADER_SIZE = 172
DELTA_SIZE = 295
RAW_BYTES_START = 295
RAW_BYTES_END = 1036  # exclusive: indices 295..1035
SEPARATOR = "null"

# Screen types (lower 4 bits of typeAndFlags)
SCREEN_TYPE_NAMES = [
    'fullscreen', 'fullscreen_alt', 'dialog_center', 'dialog_bottom',
    'dialog_corner', 'fullscreen_noscroll', 'map', 'toast',
    'toast_center', 'fullscreen_noscroll_alt', 'popup', 'dialog_low',
    'map_alt',
]
SCREEN_TYPE_MAP = {name: i for i, name in enumerate(SCREEN_TYPE_NAMES)}

# Item types
ITEM_ACTION = 0
ITEM_SEPARATOR = 1
ITEM_CHECKBOX = 2
ITEM_DROPDOWN = 3
ITEM_TEXT_SEPARATOR = 4
ITEM_TEXT_INPUT = 5
ITEM_LABEL_SEPARATOR = 6
ITEM_CONDITIONAL_IF = 7
ITEM_CONDITIONAL_UNLESS = 8
ITEM_LOGIN = 9
ITEM_PASSWORD = 10
ITEM_IMAGE = 11
ITEM_REDIRECT = 12

ITEM_TYPE_NAMES = {
    0: 'action', 1: 'separator', 2: 'checkbox', 3: 'dropdown',
    4: 'text_separator', 5: 'text_input', 6: 'label_separator',
    7: 'conditional_if', 8: 'conditional_unless', 9: 'login',
    10: 'password', 11: 'image', 12: 'redirect',
}
ITEM_TYPE_MAP = {v: k for k, v in ITEM_TYPE_NAMES.items()}

FLAG_CHECKBOXES = 16
FLAG_DYNAMIC = 32

# All known screen offsets (pool indices) -> names
# Sorted by offset for sequential parsing
KNOWN_SCREENS = [
    ('MAP_VIEW', 1578),
    ('MAP_VIEW_ALT', 1588),
    ('VIEW_MODE', 1600),
    ('GPS_SETTINGS', 1632),
    ('REGION_SELECTOR', 1691),
    ('MAP_POINTS', 1701),
    ('MAP_OVERLAY', 1717),
    ('MAP_TOOLTIP', 1727),
    ('PEOPLE_NEARBY', 1743),
    ('XMPP_MAP_CONTEXT', 1753),
    ('SAVE_LOCATION', 1876),
    ('MAP_ROUTE', 1892),
    ('MAP_STATUS', 1902),
    ('SEND_TO_CONTACT', 1930),
    ('CHAT_ROOM_OPTIONS', 1940),
    ('MAP_ROUTE_SELECT', 1958),
    ('CHAT_LIST_OPTIONS', 1968),
    ('MMP_ACCOUNT_SELECT', 1990),
    ('PEOPLE_SEARCH', 2043),
    ('SAVED_LOCATIONS', 2075),
    ('SHARE_LOCATION', 2085),
    ('GROUP_MANAGEMENT_ALT', 2101),
    ('MAP_SEARCH', 2111),
    ('WIFI_ACCOUNT_LIST', 2140),
    ('MAILBOX_OPTIONS', 2158),
    ('FORM_LIST', 2176),
    ('MRIM_ACCOUNT_SELECT', 2186),
    ('MAP_OPTIONS', 2198),
    ('NEARBY_SETTINGS', 2218),
    ('CONTACT_POPUP', 2237),
    ('SEARCH_ENTRY', 2247),
    ('EDIT_SCREEN', 2279),
    ('STATUS_PREVIEW', 2299),
    ('CHOICE_DIALOG', 2351),
    ('ACCOUNT_LIST', 2361),
    ('KEY_MAPPING', 2421),
    ('ABOUT', 2448),
    ('BLOG_POST', 2482),
    ('MESSAGE_INPUT', 2501),
    ('GROUP_MANAGEMENT', 2523),
    ('CONTACT_LIST_TEMPLATE', 2571),
    ('MULTI_ACCOUNT_LIST', 2581),
    ('MESSAGE_SUMMARY', 2591),
    ('SERVER_ADDRESS', 2601),
    ('PHONE_INPUT', 2611),
    ('EMOTICON_PICKER', 2621),
    ('MESSAGE_DETAIL', 2631),
    ('MAP_MENU', 2641),
    ('CONTACT_GROUPS', 2697),
    ('ACCOUNT_SWITCHER', 2719),
    ('ACCOUNTS_MENU', 2733),
    ('XMPP_CONTEXT_MENU', 2777),
    ('XMPP_LOGIN', 2803),
    ('PRIVACY_SETTINGS', 2817),
    ('THEME_SETTINGS', 2917),
    ('SOUND_SETTINGS', 2978),
    ('CONTACT_SETTINGS', 3052),
    ('MULTI_ACCOUNT_SETTINGS', 3102),
    ('CHAT_VIEW_MODE', 3170),
    ('TRAFFIC_COST', 3183),
    ('NOTIFICATION_SETTINGS', 3214),
    ('EXT_SETTINGS', 3272),
    ('MAP_VIEW_SETTINGS', 3302),
    ('NOTIFICATION_DIALOG', 3328),
    ('CREATE_GROUP', 3340),
    ('REGION_CHOICE', 3356),
    ('XMPP_LOGIN_ALT', 3443),
    ('XMPP_LOGIN_ALT2', 3463),
    ('EMPTY_SCREEN', 3479),
    ('CONTACT_EDITOR', 3501),
    ('ADD_MRIM_CONTACT', 3535),
    ('RENAME_GROUP', 3553),
    ('ADD_CONTACT', 3569),
    ('PHONE_GROUPS', 3627),
    ('CHAT_STATUS', 3647),
    ('CONTACT_GROUP_MENU', 3686),
    ('CONTACT_ACTIONS_MENU', 3704),
    ('CONTACT_MENU', 3783),
    ('CONTACT_INFO_VIEW_SCREEN', 3830),
    ('CAPTCHA', 3840),
    ('PROFILE_LIST', 3850),
    ('SEARCH_RESULT_LIST', 3868),
    ('CONTACT_INFO_DETAIL_SCREEN', 3878),
    ('CONTACT_ADD_SCREEN', 3888),
    ('CONTACT_LIST_SCREEN', 3920),
    ('ADD_CONTACT_FORM', 3939),
    ('SETTINGS_MENU', 3959),
    ('TRAFFIC_STATS', 3985),
    ('SETTINGS_MAIN', 4038),
    ('DELETE_CONTACT_LIST', 4050),
    ('UNBLOCK_CONTACT_LIST', 4060),
    ('BLOCK_CONTACT_LIST', 4070),
    ('PHONE_INPUT_ALT', 4080),
    ('URL_OPEN', 4090),
    ('ACCOUNT_SWITCH_OPTIONS', 4100),
    ('CREATE_CHAT_ROOM', 4138),
    ('GROUP_MEMBERS', 4159),
    ('EDIT_MEMBERS', 4169),
    ('CHAT_OPTIONS', 4179),
    ('COLOR_PICKER', 4204),
    ('GROUP_MOVE', 4238),
    ('CONTACT_INFO_EDITOR', 4248),
    ('PROFILE_EDIT', 4258),
    ('CHAT_DETAIL', 4270),
    ('MAIL_ACCOUNT_LIST', 4292),
    ('CHAT_ROOM_CONFIG', 4302),
    ('FILE_SELECTOR', 4318),
    ('PHOTO_SELECTOR_ALT', 4331),
    ('MAIN_SCREEN', 4369),
    ('ACCOUNT_SETUP', 4381),
    ('REGISTRATION_FORM', 4403),
    ('REGISTRATION', 4471),
    ('ERROR_ALERT', 4489),
    ('CONFIRM_DIALOG', 4501),
    ('GENERIC_LIST', 4511),
    ('INPUT_FORM', 4521),
    ('CONTACT_DETAILS', 4531),
    ('MESSAGE_PREVIEW', 4541),
    ('COMPOSE_RECIPIENTS', 4555),
    ('CHAT_ROOM_CONTEXT', 4593),
    ('SOFTKEY_MENU', 4637),
    ('MAIL_MENU', 4671),
    ('INPUT_DIALOG', 4715),
    ('GROUP_SELECTOR', 4733),
    ('NOTIFICATION_OPTIONS', 4751),
    ('SEARCH_RESULTS', 4773),
    ('COMPOSE_MESSAGE', 4810),
    ('THEME_OPTIONS', 4840),
    ('DIALOG_SCREEN', 4856),
    ('VERSION_SELECT', 4866),
    ('VCARD_ACTIONS', 4896),
    ('FORM_SETTINGS', 5094),
    ('INVITE_TOS_SCREEN', 5120),
    ('ASYNC_CONFIRM_SCREEN', 5135),
    ('WIFI_NETWORKS', 5145),
    ('CONNECTION_SETTINGS', 5161),
]

# ScreenId reverse lookup (value -> name)
SCREEN_ID_NAMES = {
    0: 'NONE', 1: 'ACCOUNT_LIST', 2: 'SETTINGS', 3: 'STATUS_DIALOG',
    4: 'CONTACT_LIST', 5: 'ACCOUNTS_MENU', 6: 'MAP', 7: 'MAP_MENU',
    8: 'SETTINGS_MENU', 9: 'ABOUT', 10: 'BLOCK_CONFIRM', 11: 'UNBLOCK_CONFIRM',
    12: 'CLOSE', 13: 'CONFIRM_EXIT', 14: 'TRAFFIC_COST', 15: 'ACCOUNT_SWITCHER',
    16: 'REGISTRATION', 17: 'EMOTICON_DIALOG', 19: 'CONTACT_EDITOR',
    20: 'GPS_SETTINGS', 21: 'ADD_CONTACT', 22: 'ADD_MRIM_CONTACT',
    25: 'MULTI_ACCOUNT_LIST', 26: 'THEME_SETTINGS', 27: 'NOTIFICATION_SETTINGS',
    28: 'SOUND_SETTINGS', 29: 'MULTI_ACCOUNT_SETTINGS', 30: 'CONTACT_GROUP_MENU',
    32: 'CHOICE_DIALOG', 33: 'PRIVACY_SETTINGS', 34: 'TRAFFIC_STATS',
    35: 'CONNECTION_SETTINGS', 36: 'MAIL_ACCOUNT_LIST', 37: 'CHAT_ROOMS',
    38: 'CHAT_ROOM_INIT', 39: 'ACCOUNT_CHECKBOX_LIST', 40: 'CLEAR_SEARCH',
    41: 'CHAT_ROOM_MESSAGES', 42: 'CHAT_ROOM_INVITE', 43: 'CHAT_ROOM_VIEW',
    44: 'SUBMIT_REGISTRATION', 47: 'CONTACT_GROUPS', 48: 'MESSAGE_DETAIL',
    49: 'CHAT_ROOM_CONFIG', 50: 'CHAT_VIEW_MODE', 51: 'CHAT_ROOM_CONTEXT',
    52: 'MESSAGE_PREVIEW', 53: 'COMPOSE_RECIPIENTS', 54: 'COMPOSE_MESSAGE',
    55: 'DELETE_CONFIRM', 56: 'CONTACT_SETTINGS', 57: 'FIRST_RUN',
    58: 'GROUP_SELECTOR', 59: 'VERSION_CHECK', 60: 'INPUT_DIALOG',
    61: 'CHAT_ROOM_ALERT', 62: 'MAIL_MENU', 63: 'STATUS_INPUT',
    64: 'ACCOUNT_SWITCH_OPTIONS', 65: 'PHONE_GROUPS', 66: 'ADD_CONTACT_INFO',
    67: 'SOFTKEY_MENU', 68: 'SEARCH_RESULTS', 69: 'CREATE_GROUP',
    70: 'RENAME_GROUP', 71: 'DELETE_ENTITY', 72: 'BATCH_DELETE',
    73: 'SEARCH_RESULT_LIST', 76: 'XMPP_LOGIN', 77: 'ACCOUNT_DELETE_CONFIRM',
    78: 'SHARE_MEDIA', 79: 'SHARE_ALERT', 80: 'NOTIFICATION_OPTIONS',
    81: 'SEND_MAIL', 82: 'REPLY_MAIL', 83: 'PRIVACY_MODE',
    84: 'STATUS_PREVIEW', 85: 'CONTACT_DELETE', 86: 'GROUP_MOVE',
    87: 'CHAT_STATUS', 88: 'THEME_OPTIONS', 89: 'TOS_SCREEN',
    90: 'EVENT_QUEUE', 91: 'VIEW_MODE', 92: 'CONTACT_MENU',
    93: 'EMOTICON_PICKER', 94: 'PHONE_INPUT', 95: 'SERVER_ADDRESS',
    96: 'CONTACT_INFO_VIEW', 97: 'REGION_SELECTOR', 98: 'PHONE_INPUT_ALT',
    99: 'URL_OPEN', 100: 'MAP_POINTS', 101: 'CONVERSATION',
    102: 'USER_PROFILE', 103: 'CONTACT_INFO_DETAIL', 104: 'COLOR_PICKER',
    105: 'XMPP_LOGIN_ALT', 106: 'CAPTCHA', 107: 'PROFILE_LOAD',
    108: 'CONTACT_LIST_KEY', 109: 'VERSION_SELECT', 110: 'MAP_TOOLTIP',
    111: 'PEOPLE_NEARBY', 112: 'CLEAR_NOTIFICATIONS', 113: 'MAP_CONTEXT_MENU',
    114: 'SAVE_LOCATION', 115: 'MESSAGE_INPUT', 116: 'MAP_ROUTE',
    117: 'MAP_STATUS', 118: 'SEND_TO_CONTACT', 119: 'CHAT_ROOM_OPTIONS',
    120: 'MAP_ROUTE_SELECT', 121: 'CHAT_LIST_OPTIONS', 122: 'PRESENCE_ACTION',
    123: 'MESSAGE_SUMMARY', 124: 'EMPTY_SCREEN', 125: 'BLOCK_CONTACT_LIST',
    126: 'UNBLOCK_CONTACT_LIST', 127: 'DELETE_CONTACT_LIST',
    128: 'DELETE_MESSAGES', 129: 'MMP_ACCOUNT_SELECT', 130: 'VCARD_ACTIONS',
    131: 'PEOPLE_SEARCH', 132: 'KEY_MAPPING', 133: 'UNUSED_133',
    134: 'UNUSED_134', 135: 'UNUSED_135', 136: 'UNUSED_136',
    137: 'MAIN_SCREEN', 138: 'UNUSED_138', 139: 'UNUSED_139',
    140: 'FORM_SETTINGS', 142: 'GROUP_MEMBERS', 143: 'CREATE_CHAT_ROOM',
    144: 'EDIT_MEMBERS', 145: 'CONTACT_DELETE_MRIM', 146: 'GROUP_MANAGEMENT',
    147: 'BLOG_POST', 149: 'UNUSED_149', 150: 'CONTACT_MODIFY',
    151: 'EXT_SETTINGS', 152: 'MAP_VIEW_SETTINGS', 153: 'WIFI_NETWORKS',
    154: 'SHARE_LOCATION', 155: 'VISIBLE_CONTACTS', 156: 'PHOTO_SELECTOR',
    157: 'ACCOUNT_SETUP', 158: 'MAP_SEARCH', 159: 'WIFI_ACCOUNT_LIST',
    160: 'PROFILE_EDIT', 161: 'SEND_CONFIRM', 162: 'CHAT_DETAIL',
    163: 'NOTIFY_MESSAGE', 164: 'REG_FORM', 165: 'ASYNC_CONFIRM',
    166: 'CHAT_OPTIONS', 167: 'MAILBOX_OPTIONS', 168: 'INVITE_TOS',
    169: 'SAVED_LOCATIONS', 170: 'FORM_LIST', 171: 'UPDATE_ALERT',
    172: 'MRIM_ACCOUNT_SELECT', 173: 'INVITE_ALERT', 174: 'MAP_OPTIONS',
    175: 'NEARBY_SETTINGS', 176: 'PHONE_CONTACTS', 177: 'SEARCH_ENTRY',
    178: 'EDIT_SCREEN', 179: 'SEND_DATA', 180: 'ASYNC_TASK',
}


# CP1251 <-> Unicode conversion (matches Utils.win1251ToChar / charToWin1251)
def win1251_to_char(b):
    b = b & 0xFF
    if b >= 192:
        return chr(b + 848)
    if b == 168:
        return chr(1025)  # Ё
    if b == 184:
        return chr(1105)  # ё
    return chr(b)


def char_to_win1251(c):
    code = ord(c)
    if 1040 <= code <= 1103:
        return (code - 848) & 0xFF
    if code == 1025:
        return 0xA8  # Ё
    if code == 1105:
        return 0xB8  # ё
    return code & 0xFF


def is_cp1251_text(data):
    """Heuristic: byte array is text if all bytes are printable CP1251 or whitespace."""
    return len(data) > 0 and all(b >= 0x20 or b in (0x0A, 0x0D, 0x09) for b in data)


def is_null_separated_text(data):
    """Heuristic: byte array is a null-separated list of CP1251 text strings."""
    segments = data.split(b'\x00')
    if len(segments) < 2:
        return False
    non_empty = sum(1 for s in segments if len(s) > 0)
    if non_empty < 2:
        return False
    return all(len(s) == 0 or is_cp1251_text(s) for s in segments)


def decode_cp1251(data):
    return ''.join(win1251_to_char(b) for b in data)


def encode_cp1251(s):
    return bytes(char_to_win1251(c) for c in s)


class CfgReader:
    def __init__(self, data):
        self.data = data
        self.offset = 0

    def read_ubyte(self):
        b = self.data[self.offset] & 0xFF
        self.offset += 1
        return b

    def read_bytes(self, n):
        result = self.data[self.offset:self.offset + n]
        self.offset += n
        return result

    def read_int_value(self):
        """Read an integer using the variable-length encoding."""
        flag = self.read_ubyte()
        if flag & 0x40:
            return flag & 0x3F
        if flag & 0x20:
            return ((flag & 0x1F) << 8) + self.read_ubyte()
        byte_count = flag & 0x07
        value = 0
        for _ in range(byte_count):
            value = (value << 8) + self.read_ubyte()
        return value

    def read_object(self, index):
        """Read an object from the pool. Returns (type, value) tuple."""
        flag = self.read_ubyte()
        if flag & 0x80:
            # Byte array
            if flag & 0x40:
                length = flag & 0x3F
            else:
                length = ((flag & 0x1F) << 8) + self.read_ubyte()
            payload = self.read_bytes(length)

            if RAW_BYTES_START <= index < RAW_BYTES_END:
                return ('bytes', payload)

            # Decode as CP1251 string
            decoded = decode_cp1251(payload)
            if decoded == SEPARATOR:
                return ('null', None)
            return ('string', decoded)

        # Integer encodings
        if flag & 0x40:
            return ('int', flag & 0x3F)
        if flag & 0x20:
            return ('int', ((flag & 0x1F) << 8) + self.read_ubyte())
        byte_count = flag & 0x07
        value = 0
        for _ in range(byte_count):
            value = (value << 8) + self.read_ubyte()
        return ('int', value)


class CfgWriter:
    def __init__(self):
        self.data = bytearray()

    def write_byte(self, b):
        self.data.append(b & 0xFF)

    def write_bytes(self, bs):
        self.data.extend(bs)

    def encode_int(self, value):
        """Encode an integer using the large-int format (0x08 | byte_count).

        The original cfg encoder always uses this format, never the compact
        (0x40 | val) or medium (0x20 | val) forms.
        """
        if value == 0:
            self.write_byte(0x09)  # 0x08 | 1
            self.write_byte(0x00)
            return
        byte_list = []
        v = value
        while v > 0:
            byte_list.append(v & 0xFF)
            v >>= 8
        byte_list.reverse()
        self.write_byte(0x08 | len(byte_list))
        for b in byte_list:
            self.write_byte(b)

    def encode_byte_array(self, payload):
        """Encode a byte array (string or raw bytes)."""
        length = len(payload)
        if length < 64:
            self.write_byte(0x80 | 0x40 | length)
        else:
            self.write_byte(0x80 | ((length >> 8) & 0x1F))
            self.write_byte(length & 0xFF)
        self.write_bytes(payload)

    def get_bytes(self):
        return bytes(self.data)


def _blob_to_entries(blob):
    """Split packed strings blob into entries with value or bytes.

    The blob is split by null bytes. Empty segments (consecutive nulls) are
    preserved as {"value": ""} to ensure byte-exact round-trip.
    """
    entries = []
    for raw in blob.split(b'\x00'):
        if len(raw) == 0:
            entries.append({'value': ''})
        elif is_cp1251_text(raw):
            entries.append({'value': decode_cp1251(raw)})
        else:
            entries.append({'bytes': base64.b64encode(raw).decode('ascii')})
    return entries


def _load_existing_names(config_path):
    """Load existing names array from config.json with packed_strings type."""
    if not os.path.exists(config_path):
        return None
    try:
        with open(config_path, 'r', encoding='utf-8') as f:
            config = json.load(f)
        for obj in config['objectPool']:
            if obj.get('type') == 'packed_strings' and 'names' in obj:
                return obj['names']
    except (json.JSONDecodeError, KeyError, TypeError):
        pass
    return None


# ---- Screen decompile/compile ----

def _obj_comment(obj_pool, key):
    """Resolve objectPool index to string for human-readable comment."""
    if key <= 0 or key >= len(obj_pool):
        return None
    obj = obj_pool[key]
    if obj.get('type') == 'string':
        val = obj.get('value', '')
        return val if len(val) <= 60 else val[:57] + '...'
    if obj.get('type') == 'string_list':
        items = obj.get('value', [])
        preview = ', '.join(items[:4])
        if len(items) > 4:
            preview += f', ... ({len(items)} total)'
        return preview if len(preview) <= 60 else preview[:57] + '...'
    return None


def _screen_id_comment(screen_id):
    """Resolve screenId to ScreenId constant name."""
    return SCREEN_ID_NAMES.get(screen_id)


def _compute_item_size(int_pool, pos):
    """Compute size (in ints) of one screen item starting at int_pool[pos]."""
    type_flags = int_pool[pos]
    item_type = type_flags & 0xF
    is_dynamic = (type_flags & FLAG_DYNAMIC) != 0

    if item_type == ITEM_ACTION:
        return (1 + 1 + 3) if is_dynamic else (1 + 3)  # 5 or 4
    elif item_type == ITEM_SEPARATOR:
        return 3  # typeFlags + label + sublabel
    elif item_type == ITEM_CHECKBOX:
        return 3  # typeFlags + label + stateKey
    elif item_type == ITEM_DROPDOWN:
        return 4  # typeFlags + label + choices + indexKey
    elif item_type == ITEM_TEXT_SEPARATOR:
        return 2  # typeFlags + label
    elif item_type == ITEM_TEXT_INPUT:
        # typeFlags + dataKey + inputType + hint + validationType + ...
        validation = int_pool[pos + 4]  # 4th after typeFlags
        return 9 if validation == 2 else 6
    elif item_type == ITEM_LABEL_SEPARATOR:
        return 2  # typeFlags + label
    elif item_type in (ITEM_CONDITIONAL_IF, ITEM_CONDITIONAL_UNLESS):
        return 5  # typeFlags + condKey + label + icon + cmd
    elif item_type == ITEM_LOGIN:
        return 3  # typeFlags + label + value
    elif item_type == ITEM_PASSWORD:
        return 2  # typeFlags + value
    elif item_type == ITEM_IMAGE:
        return 2  # typeFlags + poolIndex
    elif item_type == ITEM_REDIRECT:
        return 2  # typeFlags + targetOffset
    else:
        return 2  # unknown: typeFlags + 1 data int (default handler)


def _decompile_item(int_pool, pos, obj_pool):
    """Decompile one screen item into a dict. Returns (item_dict, size)."""
    type_flags = int_pool[pos]
    item_type = type_flags & 0xF
    is_text_style = (type_flags & FLAG_CHECKBOXES) != 0
    is_dynamic = (type_flags & FLAG_DYNAMIC) != 0
    type_name = ITEM_TYPE_NAMES.get(item_type, f'unknown_{item_type}')

    item = {'type': type_name}

    if item_type == ITEM_ACTION:
        if is_dynamic:
            item['extra'] = int_pool[pos + 1]
            item['condKey'] = int_pool[pos + 2]
            item['icon'] = int_pool[pos + 3]
            item['cmd'] = int_pool[pos + 4]
            if is_text_style:
                item['style'] = 'text'
            size = 5
        else:
            item['label'] = int_pool[pos + 1]
            item['icon'] = int_pool[pos + 2]
            item['cmd'] = int_pool[pos + 3]
            if is_text_style:
                item['style'] = 'text'
            size = 4

    elif item_type == ITEM_SEPARATOR:
        item['label'] = int_pool[pos + 1]
        item['sublabel'] = int_pool[pos + 2]
        size = 3

    elif item_type == ITEM_CHECKBOX:
        item['label'] = int_pool[pos + 1]
        item['stateKey'] = int_pool[pos + 2]
        size = 3

    elif item_type == ITEM_DROPDOWN:
        item['label'] = int_pool[pos + 1]
        item['choices'] = int_pool[pos + 2]
        item['indexKey'] = int_pool[pos + 3]
        size = 4

    elif item_type == ITEM_TEXT_SEPARATOR:
        item['label'] = int_pool[pos + 1]
        size = 2

    elif item_type == ITEM_TEXT_INPUT:
        item['dataKey'] = int_pool[pos + 1]
        item['inputType'] = int_pool[pos + 2]
        item['hint'] = int_pool[pos + 3]
        validation = int_pool[pos + 4]
        item['validation'] = validation
        if validation == 2:
            item['min'] = int_pool[pos + 5]
            item['max'] = int_pool[pos + 6]
            item['default'] = int_pool[pos + 7]
            item['stateKey'] = int_pool[pos + 8]
            size = 9
        else:
            item['valueKey'] = int_pool[pos + 5]
            size = 6

    elif item_type == ITEM_LABEL_SEPARATOR:
        item['label'] = int_pool[pos + 1]
        size = 2

    elif item_type in (ITEM_CONDITIONAL_IF, ITEM_CONDITIONAL_UNLESS):
        item['condKey'] = int_pool[pos + 1]
        item['label'] = int_pool[pos + 2]
        item['icon'] = int_pool[pos + 3]
        item['cmd'] = int_pool[pos + 4]
        if is_text_style:
            item['style'] = 'text'
        size = 5

    elif item_type == ITEM_LOGIN:
        item['label'] = int_pool[pos + 1]
        item['value'] = int_pool[pos + 2]
        size = 3

    elif item_type == ITEM_PASSWORD:
        item['value'] = int_pool[pos + 1]
        size = 2

    elif item_type == ITEM_IMAGE:
        item['poolIndex'] = int_pool[pos + 1]
        size = 2

    elif item_type == ITEM_REDIRECT:
        item['targetOffset'] = int_pool[pos + 1]
        size = 2

    else:
        # Unknown type — store raw ints
        item['data'] = int_pool[pos + 1]
        size = 2

    # Add human-readable comments
    if 'label' in item:
        c = _obj_comment(obj_pool, item['label'])
        if c:
            item['label_'] = c
    if 'sublabel' in item:
        c = _obj_comment(obj_pool, item['sublabel'])
        if c:
            item['sublabel_'] = c
    if 'hint' in item:
        c = _obj_comment(obj_pool, item['hint'])
        if c:
            item['hint_'] = c
    if 'choices' in item:
        c = _obj_comment(obj_pool, item['choices'])
        if c:
            item['choices_'] = c

    return item, size


def _compile_item(item):
    """Compile one screen item dict back to a list of ints."""
    type_name = item['type']
    item_type = ITEM_TYPE_MAP.get(type_name)
    if item_type is None:
        raise ValueError(f"Unknown item type: {type_name}")

    type_flags = item_type
    if item.get('style') == 'text':
        type_flags |= FLAG_CHECKBOXES

    result = []

    if item_type == ITEM_ACTION:
        if 'extra' in item:
            type_flags |= FLAG_DYNAMIC
            result.append(type_flags)
            result.append(item['extra'])
            result.append(item['condKey'])
            result.append(item['icon'])
            result.append(item['cmd'])
        else:
            result.append(type_flags)
            result.append(item['label'])
            result.append(item['icon'])
            result.append(item['cmd'])

    elif item_type == ITEM_SEPARATOR:
        result = [type_flags, item['label'], item['sublabel']]

    elif item_type == ITEM_CHECKBOX:
        result = [type_flags, item['label'], item['stateKey']]

    elif item_type == ITEM_DROPDOWN:
        result = [type_flags, item['label'], item['choices'], item['indexKey']]

    elif item_type == ITEM_TEXT_SEPARATOR:
        result = [type_flags, item['label']]

    elif item_type == ITEM_TEXT_INPUT:
        result = [type_flags, item['dataKey'], item['inputType'],
                  item['hint'], item['validation']]
        if item['validation'] == 2:
            result.extend([item['min'], item['max'], item['default'],
                          item['stateKey']])
        else:
            result.append(item['valueKey'])

    elif item_type == ITEM_LABEL_SEPARATOR:
        result = [type_flags, item['label']]

    elif item_type in (ITEM_CONDITIONAL_IF, ITEM_CONDITIONAL_UNLESS):
        result = [type_flags, item['condKey'], item['label'],
                  item['icon'], item['cmd']]

    elif item_type == ITEM_LOGIN:
        result = [type_flags, item['label'], item['value']]

    elif item_type == ITEM_PASSWORD:
        result = [type_flags, item['value']]

    elif item_type == ITEM_IMAGE:
        result = [type_flags, item['poolIndex']]

    elif item_type == ITEM_REDIRECT:
        result = [type_flags, item['targetOffset']]

    else:
        result = [type_flags, item.get('data', 0)]

    return result


def decompile_screens(int_pool, obj_pool):
    """Parse intPool[172:] into an array of structured screen blocks.

    Uses KNOWN_SCREENS catalog to determine screen boundaries. Items between
    the end of one screen's items and the start of the next screen become
    trailingData for byte-exact round-trip.
    """
    screens = []
    sorted_offsets = [(name, offset) for name, offset in KNOWN_SCREENS]
    # Already sorted by offset

    for i, (name, offset) in enumerate(sorted_offsets):
        idx = offset - OBJECT_POOL_SIZE  # intPool index

        # Determine available space until next screen
        if i + 1 < len(sorted_offsets):
            next_idx = sorted_offsets[i + 1][1] - OBJECT_POOL_SIZE
        else:
            next_idx = len(int_pool)

        # Parse 10-int header
        title = int_pool[idx]
        screen_id = int_pool[idx + 1]
        type_flags = int_pool[idx + 2]
        header_mode = int_pool[idx + 3]
        left_label = int_pool[idx + 4]
        right_label = int_pool[idx + 5]
        left_cmd = int_pool[idx + 6]
        right_cmd = int_pool[idx + 7]
        extra_cmd = int_pool[idx + 8]
        item_count = int_pool[idx + 9]

        screen_type = type_flags & 0xF
        has_checkboxes = (type_flags & FLAG_CHECKBOXES) != 0

        screen = {'name': name}

        # Title with comment
        screen['title'] = title
        tc = _obj_comment(obj_pool, title)
        if tc:
            screen['title_'] = tc

        # Screen ID with comment
        screen['screenId'] = screen_id
        sc = _screen_id_comment(screen_id)
        if sc:
            screen['screenId_'] = sc

        # Type as symbolic name
        screen['type'] = SCREEN_TYPE_NAMES[screen_type] if screen_type < len(SCREEN_TYPE_NAMES) else screen_type
        if has_checkboxes:
            screen['checkboxes'] = True
        screen['headerMode'] = header_mode

        # Soft keys
        lsk = {'label': left_label, 'cmd': left_cmd}
        lc = _obj_comment(obj_pool, left_label)
        if lc:
            lsk['label_'] = lc
        screen['leftSoftKey'] = lsk

        rsk = {'label': right_label, 'cmd': right_cmd}
        rc = _obj_comment(obj_pool, right_label)
        if rc:
            rsk['label_'] = rc
        screen['rightSoftKey'] = rsk

        screen['extraCmd'] = extra_cmd

        # Parse items
        items = []
        pos = idx + 10
        for _ in range(item_count):
            item, size = _decompile_item(int_pool, pos, obj_pool)
            items.append(item)
            pos += size

        screen['items'] = items

        # Trailing data
        trailing = int_pool[pos:next_idx]
        if trailing:
            screen['trailingData'] = list(trailing)

        screens.append(screen)

    return screens


def compile_screens(screens):
    """Compile screen blocks back to a flat intPool segment (after header)."""
    result = []

    for screen in screens:
        # Type flags
        type_name = screen['type']
        if isinstance(type_name, int):
            screen_type = type_name
        else:
            screen_type = SCREEN_TYPE_MAP[type_name]
        type_flags = screen_type
        if screen.get('checkboxes'):
            type_flags |= FLAG_CHECKBOXES

        items_data = []
        for item in screen.get('items', []):
            items_data.extend(_compile_item(item))

        # 10-int header
        lsk = screen['leftSoftKey']
        rsk = screen['rightSoftKey']
        header = [
            screen['title'],
            screen['screenId'],
            type_flags,
            screen['headerMode'],
            lsk['label'],
            rsk['label'],
            lsk['cmd'],
            rsk['cmd'],
            screen['extraCmd'],
            len(screen.get('items', [])),
        ]

        result.extend(header)
        result.extend(items_data)

        # Trailing data
        trailing = screen.get('trailingData', [])
        result.extend(trailing)

    return result


PALETTE_THEME_COUNT = 8
PALETTE_TOTAL_INTS = 176  # 22 roles * 8 themes

# Fixed role order — defines binary layout in the cfg file.
# First 16 are indexed colors (accessed by setColorFromPalette(0..15)),
# last 6 are extended entries (accessed by named PaletteKeys constants).
PALETTE_ROLES = [
    'text', 'background', 'background_alt', 'link',
    'online', 'away', 'offline', 'accent',
    'selection_bg', 'selection_text', 'separator', 'disabled',
    'border', 'scrollbar', 'popup_bg', 'popup_text',
    'gradient_start', 'map_fill', 'map_border', 'map_bg',
    'map_pulse', 'gradient_end',
]
PALETTE_INDEXED_COUNT = 16  # roles[0:16] are indexed colors
PALETTE_EXTENDED_ROLES = PALETTE_ROLES[PALETTE_INDEXED_COUNT:]  # roles[16:] get named constants


def _parse_hex_color(s):
    """Parse '#RRGGBB' to int."""
    s = s.strip()
    if s.startswith('#'):
        s = s[1:]
    if len(s) != 6:
        raise ValueError(f"Invalid hex color: #{s}")
    return int(s, 16)


def load_palette(palette_path):
    """Load theme-oriented palette.json and return a flat list of 176 ints.

    Binary layout: for each role (in PALETTE_ROLES order), 8 ints
    (one per theme). This matches how the runtime indexes the palette.
    """
    with open(palette_path, 'r', encoding='utf-8') as f:
        palette = json.load(f)

    themes = palette['themes']
    if len(themes) != PALETTE_THEME_COUNT:
        raise ValueError(f"Expected {PALETTE_THEME_COUNT} themes, got {len(themes)}")

    flat = []
    for role in PALETTE_ROLES:
        for theme in themes:
            if role not in theme:
                raise ValueError(f"Theme '{theme.get('_', '?')}' missing role '{role}'")
            flat.append(_parse_hex_color(theme[role]))

    if len(flat) != PALETTE_TOTAL_INTS:
        raise ValueError(f"Palette has {len(flat)} ints, expected {PALETTE_TOTAL_INTS}")
    return flat


def _inject_palette(config, input_dir):
    """If palette.json exists, inject it as VCARD_ACTIONS trailingData."""
    palette_path = os.path.join(input_dir, 'palette.json')
    if not os.path.exists(palette_path):
        return
    flat = load_palette(palette_path)
    for screen in config['screens']:
        if screen['name'] == 'VCARD_ACTIONS':
            screen['trailingData'] = flat
            return
    raise ValueError("VCARD_ACTIONS screen not found in config.json")


def _compute_screen_offset_and_size(config, screen_name):
    """Compute the intPool offset and block size for a named screen."""
    offset = OBJECT_POOL_SIZE + INT_POOL_HEADER_SIZE
    for screen in config['screens']:
        items_size = 0
        for item in screen.get('items', []):
            items_size += len(_compile_item(item))
        trailing_size = len(screen.get('trailingData', []))
        block_size = 10 + items_size + trailing_size
        if screen['name'] == screen_name:
            return offset, 10 + items_size, block_size
        offset += block_size
    raise ValueError(f"Screen {screen_name} not found")


def gen_palette(input_dir, output_java):
    """Generate PaletteKeys.java from palette.json + config.json."""
    config_path = os.path.join(input_dir, 'config.json')

    with open(config_path, 'r', encoding='utf-8') as f:
        config = json.load(f)

    # Inject palette so offset computation includes it
    _inject_palette(config, input_dir)

    # Compute VCARD_ACTIONS offset and header+items size
    vcard_offset, header_items_size, _ = _compute_screen_offset_and_size(config, 'VCARD_ACTIONS')

    colors_base = vcard_offset + header_items_size

    lines = []
    lines.append('package com.trykote.mobileagent.core;')
    lines.append('')
    lines.append('/**')
    lines.append(' * Palette intPool offsets.')
    lines.append(' * Generated by tools/cfg_tool.py --gen-palette. Do not edit manually.')
    lines.append(' */')
    lines.append('public final class PaletteKeys {')
    lines.append('    private PaletteKeys() {}')
    lines.append('')
    lines.append(f'    public static final int COLORS_BASE = {colors_base};')
    lines.append('')

    ext_base = colors_base + PALETTE_INDEXED_COUNT * PALETTE_THEME_COUNT
    for role in PALETTE_EXTENDED_ROLES:
        name = role.upper()
        lines.append(f'    public static final int {name} = {ext_base};')
        ext_base += PALETTE_THEME_COUNT
    lines.append('}')
    lines.append('')

    os.makedirs(os.path.dirname(os.path.abspath(output_java)) or '.', exist_ok=True)
    with open(output_java, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))

    count = 1 + len(PALETTE_EXTENDED_ROLES)
    print(f"Wrote {output_java} ({count} palette constants)")


# ---- serialize/deserialize/round-trip ----

def deserialize_cfg(cfg_path, output_dir):
    """Deserialize binary cfg to config.json."""
    with open(cfg_path, 'rb') as f:
        data = f.read()

    reader = CfgReader(data)

    # Load existing names from config.json (if present)
    config_path = os.path.join(output_dir, 'config.json')
    existing_names = _load_existing_names(config_path)

    # Read object pool
    objects = []

    for i in range(OBJECT_POOL_SIZE):
        obj_type, value = reader.read_object(i)
        if i == RAW_BYTES_START:
            # Packed strings blob — split into entries
            entries = _blob_to_entries(value)
            obj = {
                'index': i,
                'type': 'packed_strings',
                'entries': entries
            }
            if existing_names is not None:
                obj['names'] = existing_names
            objects.append(obj)
        elif obj_type == 'bytes':
            if is_null_separated_text(value):
                segments = value.split(b'\x00')
                objects.append({
                    'index': i,
                    'type': 'string_list',
                    'value': [decode_cp1251(s) for s in segments]
                })
            elif is_cp1251_text(value):
                objects.append({
                    'index': i,
                    'type': 'string',
                    'value': decode_cp1251(value)
                })
            else:
                objects.append({
                    'index': i,
                    'type': 'bytes',
                    'value': base64.b64encode(value).decode('ascii')
                })
        elif obj_type == 'null':
            objects.append({'index': i, 'type': 'null'})
        elif obj_type == 'string':
            objects.append({'index': i, 'type': 'string', 'value': value})
        elif obj_type == 'int':
            objects.append({'index': i, 'type': 'int', 'value': value})

    # Read screen data (3601 ints, no header — defaults are in AppState.java)
    int_pool_raw = []
    while reader.offset < len(data):
        int_pool_raw.append(reader.read_int_value())

    if len(int_pool_raw) != SCREEN_DATA_SIZE:
        print(f"Warning: unexpected screen data size {len(int_pool_raw)}, "
              f"expected {SCREEN_DATA_SIZE}", file=sys.stderr)

    # Pad with zeros for header so KNOWN_SCREENS offsets work
    int_pool = [0] * INT_POOL_HEADER_SIZE + int_pool_raw

    screens = decompile_screens(int_pool, objects)

    # Write config.json (v2 format — screens only, no header)
    config = {
        'format': 'mobileagent-cfg-v2',
        'objectPool': objects,
        'screens': screens,
    }

    os.makedirs(output_dir, exist_ok=True)
    with open(config_path, 'w', encoding='utf-8') as f:
        json.dump(config, f, indent=2, ensure_ascii=False)

    entry_count = 0
    for obj in objects:
        if obj.get('type') == 'packed_strings':
            entry_count = len(obj['entries'])
    print(f"Wrote {config_path} ({len(objects)} objects, {len(screens)} screens, "
          f"{entry_count} packed string entries)")


def _build_screen_data(config, input_dir=None):
    """Build screen data ints from config screens array.

    If input_dir is given and palette.json exists, inject palette as
    VCARD_ACTIONS trailingData before compiling.
    """
    if input_dir:
        _inject_palette(config, input_dir)
    return compile_screens(config['screens'])


def serialize_cfg(input_dir, cfg_path):
    """Serialize config.json to binary cfg."""
    config_path = os.path.join(input_dir, 'config.json')

    with open(config_path, 'r', encoding='utf-8') as f:
        config = json.load(f)

    writer = CfgWriter()

    # Index objects by position
    obj_map = {}
    for obj in config['objectPool']:
        obj_map[obj['index']] = obj

    for i in range(OBJECT_POOL_SIZE):
        obj = obj_map.get(i)
        if obj is None:
            raise ValueError(f"Missing object at index {i}")

        obj_type = obj['type']
        if obj_type == 'packed_strings':
            segments = []
            for entry in obj['entries']:
                if 'bytes' in entry:
                    segments.append(base64.b64decode(entry['bytes']))
                else:
                    segments.append(encode_cp1251(entry['value']))
            blob = b'\x00'.join(segments)
            writer.encode_byte_array(blob)
        elif obj_type == 'string_list':
            segments = [encode_cp1251(s) for s in obj['value']]
            blob = b'\x00'.join(segments)
            writer.encode_byte_array(blob)
        elif obj_type == 'bytes':
            raw = base64.b64decode(obj['value'])
            writer.encode_byte_array(raw)
        elif obj_type == 'null':
            writer.encode_byte_array(encode_cp1251(SEPARATOR))
        elif obj_type == 'string':
            writer.encode_byte_array(encode_cp1251(obj['value']))
        elif obj_type == 'int':
            writer.encode_int(obj['value'])
        else:
            raise ValueError(f"Unknown type '{obj_type}' at index {i}")

    # Write screen data (no header — defaults are set in AppState.java)
    screen_data = _build_screen_data(config, input_dir)
    for val in screen_data:
        writer.encode_int(val)

    result = writer.get_bytes()

    os.makedirs(os.path.dirname(os.path.abspath(cfg_path)) or '.', exist_ok=True)
    with open(cfg_path, 'wb') as f:
        f.write(result)
    print(f"Wrote {cfg_path} ({len(result)} bytes)")
    return result


def _compare_binaries(first, second):
    """Compare two byte sequences and return a detailed report.

    Returns (is_equal, report_lines) tuple.
    """
    lines = []
    if first == second:
        lines.append(f"Byte-exact match ({len(first)} bytes)")
        return True, lines

    lines.append(f"Size: first={len(first)} bytes, second={len(second)} bytes")

    diffs = []
    min_len = min(len(first), len(second))
    for i in range(min_len):
        if first[i] != second[i]:
            diffs.append(i)

    if len(first) != len(second):
        lines.append(f"Size mismatch: {len(first)} vs {len(second)} "
                     f"(delta={len(second) - len(first):+d})")

    if diffs:
        lines.append(f"Content differences: {len(diffs)} bytes differ")
        for offset in diffs[:10]:
            lines.append(f"  offset {offset}: 0x{first[offset]:02x} -> 0x{second[offset]:02x}")
        if len(diffs) > 10:
            lines.append(f"  ... and {len(diffs) - 10} more")

    return False, lines


def round_trip_cfg(cfg_path, input_dir):
    """Round-trip verify: serialize -> deserialize -> serialize -> compare.

    Steps:
      1. Serialize config.json -> binary cfg (A)
      2. Deserialize binary cfg (A) -> config.json (intermediate)
      3. Serialize config.json (intermediate) -> binary cfg (B)
      4. Compare A and B byte-by-byte
    """
    import tempfile

    with tempfile.TemporaryDirectory() as tmpdir:
        first_path = os.path.join(tmpdir, 'cfg_first')
        serialize_cfg(input_dir, first_path)
        with open(first_path, 'rb') as f:
            first = f.read()

        intermediate_dir = os.path.join(tmpdir, 'intermediate')
        deserialize_cfg(first_path, intermediate_dir)

        second_path = os.path.join(tmpdir, 'cfg_second')
        serialize_cfg(intermediate_dir, second_path)
        with open(second_path, 'rb') as f:
            second = f.read()

    print("--- Round-trip report ---")
    print(f"  Step 1: serialize config.json -> {len(first)} bytes")
    print(f"  Step 2: deserialize -> intermediate config.json")
    print(f"  Step 3: serialize intermediate -> {len(second)} bytes")

    is_equal, report = _compare_binaries(first, second)
    if is_equal:
        print(f"  Result: PASS — {report[0]}")
    else:
        print(f"  Result: FAIL", file=sys.stderr)
        for line in report:
            print(f"    {line}", file=sys.stderr)

    return is_equal


def gen_java(input_dir, output_java):
    """Generate PackedStringKeys.java from config.json names array."""
    config_path = os.path.join(input_dir, 'config.json')
    with open(config_path, 'r', encoding='utf-8') as f:
        config = json.load(f)

    # Find the packed_strings entry
    ps_obj = None
    for obj in config['objectPool']:
        if obj.get('type') == 'packed_strings':
            ps_obj = obj
            break

    if ps_obj is None:
        print("Error: No packed_strings entry found in config.json", file=sys.stderr)
        sys.exit(1)

    # Reconstruct blob to resolve sub-string values
    segments = []
    for entry in ps_obj['entries']:
        if 'bytes' in entry:
            segments.append(base64.b64decode(entry['bytes']))
        else:
            segments.append(encode_cp1251(entry['value']))
    blob = b'\x00'.join(segments)

    names = ps_obj.get('names', [])
    # Sort by id (= length << 16 | offset) to match original ordering
    names = sorted(names, key=lambda e: (e['length'] << 16) | e['offset'])

    lines = []
    lines.append('package com.trykote.mobileagent.core;')
    lines.append('')
    lines.append('/**')
    lines.append(' * Named constants for packed string keys (stored in RES_STRING_DATA blob).')
    lines.append(' * Generated by tools/cfg_tool.py --gen-java. Do not edit manually.')
    lines.append(' */')
    lines.append('public final class PackedStringKeys {')
    lines.append('    private PackedStringKeys() {}')
    lines.append('')

    count = 0
    for entry in names:
        name = entry['name']
        offset = entry['offset']
        length = entry['length']
        sid = (length << 16) | offset
        value = decode_cp1251(blob[offset:offset + length])
        comment = value if len(value) <= 80 else value[:77] + '...'
        comment = comment.replace('*/', '* /')
        lines.append(f'    /** "{comment}" */')
        lines.append(f'    public static final int {name} = {sid};')
        lines.append('')
        count += 1

    lines.append('}')
    lines.append('')

    os.makedirs(os.path.dirname(os.path.abspath(output_java)) or '.', exist_ok=True)
    with open(output_java, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))
    print(f"Wrote {output_java} ({count} named constants)")


def gen_screens(input_dir, output_java):
    """Generate ScreenDef.java from config.json screens array."""
    config_path = os.path.join(input_dir, 'config.json')
    with open(config_path, 'r', encoding='utf-8') as f:
        config = json.load(f)

    if 'screens' not in config:
        print("Error: No 'screens' key in config.json", file=sys.stderr)
        sys.exit(1)

    # Inject palette so VCARD_ACTIONS trailingData is included in offset computation
    _inject_palette(config, input_dir)

    lines = []
    lines.append('package com.trykote.mobileagent.core;')
    lines.append('')
    lines.append('/**')
    lines.append(' * IntPool offsets for screen definitions used by ScreenManager.createScreen().')
    lines.append(' * Generated by tools/cfg_tool.py --gen-screens. Do not edit manually.')
    lines.append(' */')
    lines.append('public final class ScreenDef {')
    lines.append('    private ScreenDef() {}')
    lines.append('')

    # Compute offsets: first screen starts at OBJECT_POOL_SIZE + INT_POOL_HEADER_SIZE
    offset = OBJECT_POOL_SIZE + INT_POOL_HEADER_SIZE
    count = 0

    for screen in config['screens']:
        name = screen['name']
        lines.append(f'    public static final int {name} = {offset};')

        # Compute block size: 10 (header) + items + trailing
        items_size = 0
        for item in screen.get('items', []):
            items_size += len(_compile_item(item))
        trailing_size = len(screen.get('trailingData', []))
        block_size = 10 + items_size + trailing_size
        offset += block_size
        count += 1

    lines.append('}')
    lines.append('')

    os.makedirs(os.path.dirname(os.path.abspath(output_java)) or '.', exist_ok=True)
    with open(output_java, 'w', encoding='utf-8') as f:
        f.write('\n'.join(lines))
    print(f"Wrote {output_java} ({count} screen definitions)")


def main():
    parser = argparse.ArgumentParser(description='MobileAgent cfg resource tool')
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument('--deserialize', nargs=2, metavar=('CFG_PATH', 'OUTPUT_DIR'),
                       help='Deserialize binary cfg to config.json')
    group.add_argument('--serialize', nargs=2, metavar=('INPUT_DIR', 'CFG_PATH'),
                       help='Serialize config.json to binary cfg')
    group.add_argument('--round-trip', nargs=2, metavar=('CFG_PATH', 'INPUT_DIR'),
                       help='Round-trip verify: serialize -> deserialize -> compare')
    group.add_argument('--gen-java', nargs=2, metavar=('INPUT_DIR', 'OUTPUT_JAVA'),
                       help='Generate PackedStringKeys.java from config.json')
    group.add_argument('--gen-screens', nargs=2, metavar=('INPUT_DIR', 'OUTPUT_JAVA'),
                       help='Generate ScreenDef.java from config.json')
    group.add_argument('--gen-palette', nargs=2, metavar=('INPUT_DIR', 'OUTPUT_JAVA'),
                       help='Generate PaletteKeys.java from palette.json')

    args = parser.parse_args()

    if args.deserialize:
        deserialize_cfg(args.deserialize[0], args.deserialize[1])
    elif args.serialize:
        serialize_cfg(args.serialize[0], args.serialize[1])
    elif args.round_trip:
        success = round_trip_cfg(args.round_trip[0], args.round_trip[1])
        sys.exit(0 if success else 1)
    elif args.gen_java:
        gen_java(args.gen_java[0], args.gen_java[1])
    elif args.gen_screens:
        gen_screens(args.gen_screens[0], args.gen_screens[1])
    elif args.gen_palette:
        gen_palette(args.gen_palette[0], args.gen_palette[1])


if __name__ == '__main__':
    main()
