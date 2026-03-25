#!/bin/bash
# Converts resources-src/cities.xml (UTF-8, readable tags) to b (CP1251, obfuscated tags)
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
INPUT="$SCRIPT_DIR/resources-src/cities.xml"
DST_DIR="${1:?Usage: pack_cities.sh <output-dir>}"

sed 's/<countries>/<z>/g; s/<\/countries>/<\/z>/g
     s/<country /<a /g; s/<\/country>/<\/a>/g
     s/<region /<b /g; s/<\/region>/<\/b>/g
     s/<city /<c /g; s/<\/city>/<\/c>/g' "$INPUT" \
  | tr -d '\n' \
  | sed 's/> *</></g' \
  | iconv -f UTF-8 -t CP1251 > "$DST_DIR/b"

echo "Wrote $DST_DIR/b ($(wc -c < "$DST_DIR/b") bytes)"
