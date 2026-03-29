#!/usr/bin/env python3
"""
Annotate config.json objectPool entries with key names, class names, and zones.

This script parses all *Keys.java files to build a reverse map from pool index
to (class_name, constant_name), then adds "key", "class", and "zone" fields
to each objectPool entry in config.json.

Contiguous blocks (accessed via base+offset patterns in Java code) are annotated
with "block" and "block_offset" fields.

IntPool keys (>= 1406) and non-index constants are added to separate sections.

Usage:
    python3 tools/annotate_pool.py resources-src/
"""

import json
import os
import re
import sys

OBJECT_POOL_SIZE = 1406

# Constants that are NOT pool indices — they're array indices, offsets, counts.
# These happen to have small numeric values that collide with pool indices.
NON_INDEX_CONSTANTS = {
    'GFX_INDEX_DEFAULT',       # = 0, graphics context array index
    'GFX_INDEX_BOLD',          # = 1, graphics context array index
    'GFX_INDEX_ITALIC',        # = 2, graphics context array index (if exists)
    'GFX_CONTEXT_COUNT',       # = 58, array size
    'GFX_HEIGHT_COUNT',        # = 29, array size
    'OFFSET_BOLD_FONT_HEIGHT', # = 1, relative offset
}

# Contiguous blocks: accessed via getString(i + BASE) or getInt(i + BASE)
# Each block occupies [base, base+count) in the pool
BLOCKS = [
    {"key": "PHONE_STRINGS",      "class": "StringResKeys", "base": 48,   "count": 15, "zone": "resources"},
    {"key": "SOUND_CONFIG",       "class": "SettingsKeys",  "base": 75,   "count": 15, "zone": "state"},
    {"key": "SOCKET_OPTIONS",     "class": "SettingsKeys",  "base": 107,  "count": 5,  "zone": "state"},
    {"key": "MAILBOX_NAMES_EN",   "class": "StringResKeys", "base": 891,  "count": 5,  "zone": "resources"},
    {"key": "MAILBOX_NAMES_RU",   "class": "StringResKeys", "base": 896,  "count": 5,  "zone": "resources"},
    {"key": "EMOTICON_NAMES",     "class": "StringResKeys", "base": 1063, "count": 60, "zone": "resources"},
    {"key": "MMP_EMOTICONS",      "class": "StringResKeys", "base": 1141, "count": 43, "zone": "resources"},
    {"key": "XMPP_EMOTICONS",     "class": "StringResKeys", "base": 1184, "count": 47, "zone": "resources"},
    {"key": "CONTACT_NAME_PARTS", "class": "UIKeys",        "base": 1303, "count": 3,  "zone": "state"},
]

# Pool indices that are written at runtime (despite being in the 295-1405 range)
# These entries are "state" not "resources"
# Identified from SessionKeys, RuntimeKeys, UIKeys, ContactKeys, ChatKeys
# with SLOT_*, VEC_*, OBJ_*, FLAG_* constants that reference pool indices >= 295
RUNTIME_POOL_INDICES = set()  # Will be populated from key classes


def is_non_index_constant(name):
    """Check if a constant is a non-index value (array index, offset, count)."""
    return name in NON_INDEX_CONSTANTS


def parse_key_classes(src_dir):
    """Parse all *Keys.java files and return categorized constants."""
    core_dir = os.path.join(src_dir, 'sources/com/trykote/mobileagent/core')

    pool_keys = {}       # pool_index -> (class_name, const_name)
    intpool_keys = []     # [(class_name, const_name, value)]
    pure_constants = []   # [(class_name, const_name, value)]

    for path in sorted(os.listdir(core_dir)):
        if not path.endswith('Keys.java') or path == 'PackedStringKeys.java':
            continue
        classname = path.replace('.java', '')
        with open(os.path.join(core_dir, path)) as f:
            content = f.read()

        for name, val_str in re.findall(
                r'public static final int (\w+)\s*=\s*(\d+);', content):
            val = int(val_str)

            if is_non_index_constant(name):
                pure_constants.append((classname, name, val))
            elif val < OBJECT_POOL_SIZE:
                # Pool index — first one wins (avoid duplicates)
                if val not in pool_keys:
                    pool_keys[val] = (classname, name)
            else:
                intpool_keys.append((classname, name, val))

    return pool_keys, intpool_keys, pure_constants


def build_block_map():
    """Build a map from pool index to block info."""
    block_map = {}  # index -> {"block": name, "block_offset": offset}
    for block in BLOCKS:
        for offset in range(block['count']):
            idx = block['base'] + offset
            block_map[idx] = {
                "block": block['key'],
                "block_offset": offset,
            }
    return block_map


def classify_zone(index, pool_keys, is_state_key):
    """Determine if a pool entry is 'state' or 'resources'."""
    if index < 295:
        return "state"  # delta zone — always read-write

    # Check if this index is referenced by a state-like key
    if index in is_state_key:
        return "state"

    # Check by key name patterns (VEC_*, SLOT_*, OBJ_*, FLAG_* typically = state)
    if index in pool_keys:
        _, name = pool_keys[index]
        if any(name.startswith(p) for p in ('VEC_', 'SLOT_', 'OBJ_', 'FLAG_', 'COUNTER_')):
            return "state"

    return "resources"


def find_state_keys(pool_keys):
    """Identify pool indices that are written at runtime."""
    state_indices = set()
    for idx, (cls, name) in pool_keys.items():
        if idx >= 295:
            # Keys in these classes with SLOT/VEC/OBJ/FLAG prefix are state
            if any(name.startswith(p) for p in ('VEC_', 'SLOT_', 'OBJ_', 'FLAG_', 'COUNTER_',
                                                  'SESSION_', 'TIMESTAMP_')):
                state_indices.add(idx)
    return state_indices


def annotate(input_dir):
    """Annotate config.json with key/class/zone metadata."""
    project_root = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
    config_path = os.path.join(input_dir, 'config.json')

    with open(config_path, 'r', encoding='utf-8') as f:
        config = json.load(f)

    pool_keys, intpool_keys, pure_constants = parse_key_classes(project_root)
    block_map = build_block_map()
    state_indices = find_state_keys(pool_keys)

    # Stats
    annotated = 0
    block_annotated = 0

    for obj in config['objectPool']:
        idx = obj['index']

        # Add key and class if named
        if idx in pool_keys:
            cls, name = pool_keys[idx]
            obj['key'] = name
            obj['class'] = cls
            annotated += 1

        # Add block info
        if idx in block_map:
            obj.update(block_map[idx])
            block_annotated += 1

        # Add zone
        obj['zone'] = classify_zone(idx, pool_keys, state_indices)

    # Add intPoolKeys section
    config['intPoolKeys'] = [
        {"key": name, "class": cls, "value": val}
        for cls, name, val in sorted(intpool_keys, key=lambda x: (x[0], x[2]))
    ]

    # Add constants section
    config['constants'] = [
        {"key": name, "class": cls, "value": val}
        for cls, name, val in sorted(pure_constants, key=lambda x: (x[0], x[2]))
    ]

    # Add block definitions
    config['blocks'] = BLOCKS

    with open(config_path, 'w', encoding='utf-8') as f:
        json.dump(config, f, indent=2, ensure_ascii=False)
        f.write('\n')

    total = len(config['objectPool'])
    print(f"Annotated {config_path}:")
    print(f"  {annotated}/{total} entries have key+class")
    print(f"  {block_annotated} entries in {len(BLOCKS)} blocks")
    print(f"  {len(intpool_keys)} intPool keys")
    print(f"  {len(pure_constants)} pure constants")
    print(f"  State indices (>=295): {len(state_indices)}")


def main():
    if len(sys.argv) != 2:
        print(f"Usage: {sys.argv[0]} <input_dir>", file=sys.stderr)
        sys.exit(1)
    annotate(sys.argv[1])


if __name__ == '__main__':
    main()
