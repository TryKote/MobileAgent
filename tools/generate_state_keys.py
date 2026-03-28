#!/usr/bin/env python3
"""
Verify that all AppState index constants are defined in domain key classes.

Domain key classes (in core/):
  TrafficKeys, MapKeys, SettingsKeys, ChatKeys, ContactKeys,
  RegistrationKeys, SessionKeys, UIKeys, StringResKeys, RuntimeKeys

Usage:
  --check : Scan all AppState.xxx(NUMBER) calls and verify they map to a named constant
"""

import re
import os
from collections import defaultdict

BASE = "sources/com/trykote/mobileagent"

DOMAIN_KEY_CLASSES = [
    "TrafficKeys", "MapKeys", "SettingsKeys", "ChatKeys", "ContactKeys",
    "RegistrationKeys", "SessionKeys", "UIKeys", "StringResKeys", "RuntimeKeys",
]


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


def collect_defined_constants():
    """Collect all constants defined in domain key classes."""
    constants = {}  # value -> (class_name, const_name)
    core_dir = os.path.join(BASE, "core")

    for cls in DOMAIN_KEY_CLASSES:
        path = os.path.join(core_dir, cls + ".java")
        if not os.path.exists(path):
            print(f"WARNING: {path} not found")
            continue
        with open(path) as f:
            for line in f:
                m = re.match(r'\s+public\s+static\s+final\s+int\s+(\w+)\s*=\s*(-?\d+)\s*;', line)
                if m:
                    name = m.group(1)
                    value = int(m.group(2))
                    constants[value] = (cls, name)

    return constants


def check():
    """Check for unmapped indices."""
    index_info = collect_all_indices()
    constants = collect_defined_constants()

    unmapped = []
    for idx in sorted(index_info.keys()):
        if idx not in constants:
            info = index_info[idx]
            unmapped.append((idx, ','.join(sorted(info['methods'])), ','.join(sorted(info['files']))))

    if unmapped:
        print(f"Found {len(unmapped)} unmapped indices:")
        for idx, methods, files in unmapped:
            print(f"  {idx}: [{methods}] in {files}")
    else:
        print("All indices are mapped!")

    print(f"\nTotal defined constants: {len(constants)}")
    print(f"Total raw indices found in code: {len(index_info)}")

    # Show distribution
    class_counts = defaultdict(int)
    for _, (cls, _) in constants.items():
        class_counts[cls] += 1
    print("\nConstants per class:")
    for cls in DOMAIN_KEY_CLASSES:
        print(f"  {cls}: {class_counts.get(cls, 0)}")


if __name__ == "__main__":
    import sys

    if len(sys.argv) > 1 and sys.argv[1] == "--check":
        check()
    else:
        print("Usage:")
        print("  --check : Check for unmapped AppState indices")
