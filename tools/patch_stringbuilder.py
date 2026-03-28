#!/usr/bin/env python3
"""Replace StringBuilder references with StringBuffer in class files.

Usage: python3 patch_stringbuilder.py <directory>

Patches all .class files under <directory>, replacing java/lang/StringBuilder
with java/lang/StringBuffer in the constant pool. This is needed because
javac -target 1.5 compiles string concatenation using StringBuilder, but
CLDC 1.1 only has StringBuffer. The APIs are identical.
"""
import os
import struct
import sys

# Constant pool tags
CONSTANT_UTF8 = 1
CONSTANT_INTEGER = 3
CONSTANT_FLOAT = 4
CONSTANT_LONG = 5
CONSTANT_DOUBLE = 6
CONSTANT_CLASS = 7
CONSTANT_STRING = 8
CONSTANT_FIELDREF = 9
CONSTANT_METHODREF = 10
CONSTANT_INTERFACE_METHODREF = 11
CONSTANT_NAME_AND_TYPE = 12
CONSTANT_METHOD_HANDLE = 15
CONSTANT_METHOD_TYPE = 16
CONSTANT_DYNAMIC = 17
CONSTANT_INVOKE_DYNAMIC = 18
CONSTANT_MODULE = 19
CONSTANT_PACKAGE = 20

# Size of data after the tag byte for fixed-size constant pool entries
FIXED_SIZES = {
    CONSTANT_INTEGER: 4,
    CONSTANT_FLOAT: 4,
    CONSTANT_LONG: 8,
    CONSTANT_DOUBLE: 8,
    CONSTANT_CLASS: 2,
    CONSTANT_STRING: 2,
    CONSTANT_FIELDREF: 4,
    CONSTANT_METHODREF: 4,
    CONSTANT_INTERFACE_METHODREF: 4,
    CONSTANT_NAME_AND_TYPE: 4,
    CONSTANT_METHOD_HANDLE: 3,
    CONSTANT_METHOD_TYPE: 2,
    CONSTANT_DYNAMIC: 4,
    CONSTANT_INVOKE_DYNAMIC: 4,
    CONSTANT_MODULE: 2,
    CONSTANT_PACKAGE: 2,
}

# Tags that occupy two constant pool slots
WIDE_TAGS = {CONSTANT_LONG, CONSTANT_DOUBLE}


def patch_class(path):
    with open(path, 'rb') as f:
        data = bytearray(f.read())

    magic = struct.unpack_from('>I', data, 0)[0]
    if magic != 0xCAFEBABE:
        return False

    minor, major = struct.unpack_from('>HH', data, 4)
    cp_count = struct.unpack_from('>H', data, 8)[0]

    # Parse constant pool entries
    entries = []  # list of (tag, raw_bytes) or None for wide-tag padding
    pos = 10
    i = 1
    while i < cp_count:
        tag = data[pos]
        pos += 1

        if tag == CONSTANT_UTF8:
            length = struct.unpack_from('>H', data, pos)[0]
            pos += 2
            utf8_bytes = bytes(data[pos:pos + length])
            pos += length
            entries.append((tag, utf8_bytes))
        elif tag in FIXED_SIZES:
            size = FIXED_SIZES[tag]
            entry_data = bytes(data[pos:pos + size])
            pos += size
            entries.append((tag, entry_data))
            if tag in WIDE_TAGS:
                entries.append(None)
                i += 1
        else:
            raise ValueError(f"Unknown constant pool tag {tag} at position {pos - 1} in {path}")

        i += 1

    rest_of_file = bytes(data[pos:])

    # Replace StringBuilder -> StringBuffer in Utf8 entries
    modified = False
    new_entries = []
    for entry in entries:
        if entry is None:
            new_entries.append(None)
            continue
        tag, entry_data = entry
        if tag == CONSTANT_UTF8:
            text = entry_data.decode('utf-8', errors='replace')
            if 'StringBuilder' in text:
                new_text = text.replace('StringBuilder', 'StringBuffer')
                new_bytes = new_text.encode('utf-8')
                new_entries.append((tag, new_bytes))
                modified = True
                continue
        new_entries.append(entry)

    if not modified:
        return False

    # Rebuild class file
    out = bytearray()
    out += struct.pack('>I', magic)
    out += struct.pack('>HH', minor, major)
    out += struct.pack('>H', cp_count)

    for entry in new_entries:
        if entry is None:
            continue
        tag, entry_data = entry
        out.append(tag)
        if tag == CONSTANT_UTF8:
            out += struct.pack('>H', len(entry_data))
            out += entry_data
        else:
            out += entry_data

    out += rest_of_file

    with open(path, 'wb') as f:
        f.write(out)
    return True


def main():
    if len(sys.argv) < 2:
        print(__doc__.strip(), file=sys.stderr)
        sys.exit(1)

    directory = sys.argv[1]
    count = 0

    for root, dirs, files in os.walk(directory):
        for name in files:
            if not name.endswith('.class'):
                continue
            path = os.path.join(root, name)
            if patch_class(path):
                count += 1

    print(f"Patched StringBuilder->StringBuffer in {count} class files")


if __name__ == '__main__':
    main()
