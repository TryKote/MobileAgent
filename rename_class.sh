#!/bin/bash
# Usage: ./rename_class.sh OldClassName NewClassName
# Replaces all occurrences of OldClassName with NewClassName in all .java files,
# then renames the file.

set -e

OLD="$1"
NEW="$2"
SRC_DIR="sources"

if [ -z "$OLD" ] || [ -z "$NEW" ]; then
    echo "Usage: $0 OldClassName NewClassName"
    exit 1
fi

echo "Renaming $OLD -> $NEW"

# Replace all occurrences in all Java files
find "$SRC_DIR" -name '*.java' -exec sed -i "s/\b${OLD}\b/${NEW}/g" {} +

# Rename the file if it exists
OLD_FILE=$(find "$SRC_DIR" -name "${OLD}.java" | head -1)
if [ -n "$OLD_FILE" ]; then
    NEW_FILE="$(dirname "$OLD_FILE")/${NEW}.java"
    mv "$OLD_FILE" "$NEW_FILE"
    echo "  Renamed file: $OLD_FILE -> $NEW_FILE"
else
    echo "  Warning: No file named ${OLD}.java found"
fi
