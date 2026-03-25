#!/bin/bash
# Copies PNG images and binary resources from resources-src/ to output directory.
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
SRC_DIR="$SCRIPT_DIR/resources-src"
DST_DIR="${1:?Usage: pack_resources.sh <output-dir>}"

# Copy PNGs using mapping (mapping: obfuscated_name -> readable_name)
python3 -c "
import json, shutil, sys
with open('$SRC_DIR/images/mapping.json') as f:
    mapping = json.load(f)
count = 0
for obf, readable in mapping.items():
    src = '$SRC_DIR/images/' + readable
    dst = '$DST_DIR/' + obf
    shutil.copy2(src, dst)
    count += 1
print(f'Copied {count} images')
"

# Copy binary resource
cp "$SRC_DIR/xmpp_data.bin" "$DST_DIR/a"
echo "Copied xmpp_data.bin -> a"
