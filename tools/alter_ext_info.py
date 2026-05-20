#!/usr/bin/env python3
import sys
import os

def usage():
    print("Usage: %s EXT_FILE NEW_EXT_FILE CERT_FILENAME" % os.path.basename(sys.argv[0]))

def find_cert_name(data, cert_filename):
    needle = bytes([len(cert_filename)]) + cert_filename.encode('ascii')
    idx = data.find(needle)
    if idx < 0:
        print("ERROR: cert filename '%s' not found in ext_info.sys" % cert_filename)
        sys.exit(1)
    return idx

def dump_hex(data):
    for i in range(0, len(data), 16):
        hex_part = ' '.join('%02x' % data[j] for j in range(i, min(i+16, len(data))))
        ascii_part = ''.join(chr(data[j]) if 32 <= data[j] < 127 else '.' for j in range(i, min(i+16, len(data))))
        print('  %04x: %-48s  %s' % (i, hex_part, ascii_part))

HEADER_MAGIC = [0x00, 0x01, 0x00, 0x01, 0x41, 0x02, 0x10, 0x14, 0x00, 0x14, 0x14]

PERM_CODE_SIGNING = [0x06, 0x08, 0x2b, 0x06, 0x01, 0x05, 0x05, 0x07, 0x03, 0x03]

PERM_TMO = [0x06, 0x08, 0x2b, 0x06, 0x01, 0x05, 0x05, 0x07, 0x03, 0x03,
            0x06, 0x0c, 0x2b, 0x06, 0x01, 0x04, 0x01, 0x2a, 0x02, 0x6e, 0x02, 0x02, 0x02, 0x01]

PERM_ALT = [0x06, 0x08, 0x2b, 0x06, 0x01, 0x05, 0x05, 0x07, 0x03, 0x03,
            0x06, 0x0a, 0x2b, 0x06, 0x01, 0x04, 0x01, 0x5e, 0x01, 0x31, 0x04, 0x01,
            0x06, 0x08, 0x2b, 0x06, 0x01, 0x05, 0x05, 0x07, 0x03, 0x01]

def modify_ext_info(data, cert_filename, perm_bytes):
    data = bytearray(data)
    cert_idx = find_cert_name(data, cert_filename)

    for i, b in enumerate(HEADER_MAGIC):
        data[i + 1] = b

    # duplicate 20 bytes before cert name
    src_start = cert_idx - 40
    dst_start = cert_idx - 20
    if src_start >= 0:
        for i in range(20):
            data[dst_start + i] = data[src_start + i]

    # write permission bytes after cert filename + 1 null byte
    perm_idx = cert_idx + len(cert_filename) + 2
    perm_record = [len(perm_bytes)] + perm_bytes
    for i, b in enumerate(perm_record):
        if perm_idx + i < len(data):
            data[perm_idx + i] = b
        else:
            data.append(b)

    # truncate after permission record
    end = perm_idx + len(perm_record)
    data = data[:end]

    # zero-pad to multiple of 4 (at least 1 padding byte)
    data.append(0)
    while len(data) % 4 != 0:
        data.append(0)

    # update record length
    data[0] = len(data)

    return bytes(data)

if __name__ == "__main__":
    if len(sys.argv) < 4:
        usage()
        sys.exit(1)

    ext_file, out_file, cert_name = sys.argv[1], sys.argv[2], sys.argv[3]
    with open(ext_file, 'rb') as f:
        data = f.read()

    print("Original (%d bytes):" % len(data))
    dump_hex(data)

    # try code-signing only first (non-TMo)
    result = modify_ext_info(data, cert_name, PERM_CODE_SIGNING)

    print("\nModified (%d bytes):" % len(result))
    dump_hex(result)

    with open(out_file, 'wb') as f:
        f.write(result)
    print("\nWrote: %s" % out_file)
