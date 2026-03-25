#!/usr/bin/env python3
"""
Tool for dumping/packing the MobileAgent cfg resource file.

The cfg file contains:
  - Object pool (1406 entries): strings (CP1251), integers, byte arrays, nulls
  - Int pool (3773 entries): integers only

The packed strings blob (index 295, RES_STRING_DATA) is stored inline in
config.json as a "packed_strings" entry with an "entries" array. Each entry
is either a text string ("value") or raw binary ("bytes" as base64).
Named sub-string references are stored in a separate "names" array with
offset/length pairs used to generate PackedStringKeys.java constants.

Subcommands:
  --dump <cfg_path> <output_dir>   Dump cfg to config.json
  --pack <input_dir> <cfg_path>    Pack config.json to cfg
  --verify <cfg_path> <input_dir>  Round-trip verify: dump -> pack -> compare
  --gen-java <input_dir> <output_java>  Generate PackedStringKeys.java from config.json
"""

import argparse
import base64
import json
import os
import sys

OBJECT_POOL_SIZE = 1406
INT_POOL_SIZE = 3773
DELTA_SIZE = 295
RAW_BYTES_START = 295
RAW_BYTES_END = 1036  # exclusive: indices 295..1035
SEPARATOR = "null"


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


def dump_cfg(cfg_path, output_dir):
    """Dump cfg binary to config.json."""
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
            if is_cp1251_text(value):
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

    # Read int pool
    int_pool = []
    for i in range(INT_POOL_SIZE):
        int_pool.append(reader.read_int_value())

    if reader.offset != len(data):
        print(f"Warning: {len(data) - reader.offset} bytes remaining after reading",
              file=sys.stderr)

    # Write config.json
    config = {
        'format': 'mobileagent-cfg-v1',
        'objectPool': objects,
        'intPool': int_pool
    }

    os.makedirs(output_dir, exist_ok=True)
    with open(config_path, 'w', encoding='utf-8') as f:
        json.dump(config, f, indent=2, ensure_ascii=False)

    entry_count = 0
    for obj in objects:
        if obj.get('type') == 'packed_strings':
            entry_count = len(obj['entries'])
    print(f"Wrote {config_path} ({len(objects)} objects, {len(int_pool)} ints, "
          f"{entry_count} packed string entries)")


def pack_cfg(input_dir, cfg_path):
    """Pack config.json to cfg binary."""
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
        elif obj_type == 'packed_strings_blob':
            # Legacy format support
            raw = base64.b64decode(obj['value'])
            writer.encode_byte_array(raw)
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

    # Write int pool
    for val in config['intPool']:
        writer.encode_int(val)

    result = writer.get_bytes()

    os.makedirs(os.path.dirname(os.path.abspath(cfg_path)) or '.', exist_ok=True)
    with open(cfg_path, 'wb') as f:
        f.write(result)
    print(f"Wrote {cfg_path} ({len(result)} bytes)")
    return result


def verify_cfg(cfg_path, input_dir):
    """Verify round-trip: dump original, pack from JSON, compare bytes."""
    import tempfile

    with open(cfg_path, 'rb') as f:
        original = f.read()

    with tempfile.TemporaryDirectory() as tmpdir:
        dump_cfg(cfg_path, tmpdir)
        packed_path = os.path.join(tmpdir, 'cfg_packed')
        pack_cfg(tmpdir, packed_path)

        with open(packed_path, 'rb') as f:
            packed = f.read()

    if original == packed:
        print(f"PASS: Round-trip verified ({len(original)} bytes)")
        return True
    else:
        min_len = min(len(original), len(packed))
        for i in range(min_len):
            if original[i] != packed[i]:
                print(f"FAIL: First difference at offset {i}: "
                      f"original=0x{original[i]:02x} packed=0x{packed[i]:02x}",
                      file=sys.stderr)
                break
        if len(original) != len(packed):
            print(f"FAIL: Size mismatch: original={len(original)} packed={len(packed)}",
                  file=sys.stderr)
        return False


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


def main():
    parser = argparse.ArgumentParser(description='MobileAgent cfg resource tool')
    group = parser.add_mutually_exclusive_group(required=True)
    group.add_argument('--dump', nargs=2, metavar=('CFG_PATH', 'OUTPUT_DIR'),
                       help='Dump cfg to config.json')
    group.add_argument('--pack', nargs=2, metavar=('INPUT_DIR', 'CFG_PATH'),
                       help='Pack config.json to cfg')
    group.add_argument('--verify', nargs=2, metavar=('CFG_PATH', 'INPUT_DIR'),
                       help='Round-trip verify: dump -> pack -> compare')
    group.add_argument('--gen-java', nargs=2, metavar=('INPUT_DIR', 'OUTPUT_JAVA'),
                       help='Generate PackedStringKeys.java from config.json')

    args = parser.parse_args()

    if args.dump:
        dump_cfg(args.dump[0], args.dump[1])
    elif args.pack:
        pack_cfg(args.pack[0], args.pack[1])
    elif args.verify:
        success = verify_cfg(args.verify[0], args.verify[1])
        sys.exit(0 if success else 1)
    elif args.gen_java:
        gen_java(args.gen_java[0], args.gen_java[1])


if __name__ == '__main__':
    main()
