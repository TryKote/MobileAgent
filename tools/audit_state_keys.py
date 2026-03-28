#!/usr/bin/env python3
"""
Audit StateKeys usage across the codebase.

For each constant in StateKeys.java, finds all files that reference it,
and classifies references as read (getInt/getString/getBool/getOrDefault/pool[])
or write (setInt/setString/setBool/setObject).

Output: TSV table and summary statistics.
"""

import os
import re
import sys
from collections import defaultdict

SRC_ROOT = os.path.join(os.path.dirname(__file__), '..', 'sources')
STATE_KEYS_PATH = os.path.join(SRC_ROOT, 'com', 'trykote', 'mobileagent', 'core', 'StateKeys.java')

# Patterns that indicate a write operation
WRITE_PATTERNS = [
    r'setInt\s*\(',
    r'setString\s*\(',
    r'setBool\s*\(',
    r'setObject\s*\(',
    r'setLong\s*\(',
    r'pool\s*\[.*\]\s*=',
    r'intPool\s*\[.*\]\s*=',
    r'delta\s*\[.*\]\s*=',
]

# Patterns that indicate a read operation
READ_PATTERNS = [
    r'getInt\s*\(',
    r'getString\s*\(',
    r'getBool\s*\(',
    r'getOrDefault\s*\(',
    r'getLong\s*\(',
    r'getObject\s*\(',
    r'pool\s*\[',
    r'intPool\s*\[',
]


def parse_state_keys():
    """Extract all constant names and values from StateKeys.java."""
    constants = []
    current_section = "Unknown"
    with open(STATE_KEYS_PATH, 'r') as f:
        for line in f:
            # Track section comments
            section_match = re.match(r'\s*//\s*===\s*(.+?)\s*===', line)
            if section_match:
                current_section = section_match.group(1).strip()
                continue

            match = re.match(r'\s*public\s+static\s+final\s+int\s+(\w+)\s*=\s*(\d+)\s*;', line)
            if match:
                name = match.group(1)
                value = int(match.group(2))
                constants.append((name, value, current_section))
    return constants


def find_java_files():
    """Find all Java source files."""
    java_files = []
    for root, dirs, files in os.walk(SRC_ROOT):
        for f in files:
            if f.endswith('.java') and f != 'StateKeys.java':
                java_files.append(os.path.join(root, f))
    return java_files


def classify_line(line, key_name):
    """Classify a line as read, write, or declaration."""
    # Skip import lines
    if line.strip().startswith('import '):
        return None

    # Check if line contains the key name
    if key_name not in line:
        return None

    # Check for write patterns
    for pattern in WRITE_PATTERNS:
        if re.search(pattern, line) and key_name in line:
            return 'write'

    # Check for read patterns
    for pattern in READ_PATTERNS:
        if re.search(pattern, line) and key_name in line:
            return 'read'

    # Could be passed as parameter, used in comparison, etc.
    # Check for common read-like patterns
    if re.search(r'(==|!=|<|>|<=|>=|switch\s*\()', line):
        return 'read'

    # Default: if it's referenced, it's likely a read (parameter passing, etc.)
    return 'read'


def get_short_path(filepath):
    """Get a short display path."""
    # Remove source root prefix
    rel = os.path.relpath(filepath, SRC_ROOT)
    # Remove com/trykote/mobileagent/ prefix
    rel = rel.replace('com/trykote/mobileagent/', '')
    # Remove .java extension
    rel = rel.replace('.java', '')
    return rel


def audit():
    constants = parse_state_keys()
    java_files = find_java_files()

    print(f"Auditing {len(constants)} constants across {len(java_files)} files...", file=sys.stderr)

    # Pre-read all files
    file_contents = {}
    for filepath in java_files:
        with open(filepath, 'r') as f:
            file_contents[filepath] = f.readlines()

    # For each constant, find all usages
    results = []
    unused = []

    for name, value, section in constants:
        readers = defaultdict(int)  # file -> count
        writers = defaultdict(int)  # file -> count

        for filepath, lines in file_contents.items():
            for line in lines:
                if name not in line:
                    continue
                # Make sure it's a whole word match
                if not re.search(r'\b' + name + r'\b', line):
                    continue

                classification = classify_line(line, name)
                short = get_short_path(filepath)
                if classification == 'write':
                    writers[short] += 1
                elif classification == 'read':
                    readers[short] += 1

        if not readers and not writers:
            unused.append((name, value, section))
            continue

        # Determine lifecycle
        lifecycle = classify_lifecycle(name, value, section, readers, writers)

        results.append({
            'name': name,
            'value': value,
            'section': section,
            'readers': dict(readers),
            'writers': dict(writers),
            'lifecycle': lifecycle,
        })

    return results, unused


def classify_lifecycle(name, value, section, readers, writers):
    """Heuristic lifecycle classification."""
    # Persistent: indices 0-294 (saved in delta)
    if value < 295:
        return 'persistent'

    # String resources
    if name.startswith('STR_'):
        return 'resource'

    # Packed string keys
    if name.startswith('PS_'):
        return 'resource'

    # Graphics context indices
    if name.startswith('GFX_'):
        return 'resource'

    # Vectors (session lifetime)
    if name.startswith('VEC_'):
        return 'session'

    # Slots (typically screen/transition)
    if name.startswith('SLOT_'):
        return 'transition'

    # Flags - need more context
    if name.startswith('FLAG_'):
        writer_count = len(writers)
        reader_count = len(readers)
        if writer_count <= 1 and reader_count <= 2:
            return 'screen'
        return 'session'

    # INT_ constants - typically runtime
    if name.startswith('INT_'):
        return 'runtime'

    # Offset constants
    if name.startswith('OFFSET_'):
        return 'resource'

    return 'unknown'


def propose_domain(name, section, readers, writers):
    """Propose which domain class this key belongs to."""
    # By name prefix
    if name.startswith('TRAFFIC_'):
        return 'TrafficState'
    if name.startswith('MAP_') or name.startswith('GEO_'):
        return 'MapState'
    if name.startswith('SETTING_'):
        return 'SettingsState'
    if name.startswith('FLAG_MAP_') or name.startswith('FLAG_GPS_'):
        return 'MapState'
    if name.startswith('FLAG_CONTACT_') or name.startswith('FLAG_IS_MRIM') or name.startswith('FLAG_IS_CHATROOM'):
        return 'ContactState'
    if name.startswith('FLAG_MSG_') or name.startswith('FLAG_CHATROOM_'):
        return 'ChatState'
    if name.startswith('FLAG_TILE') or name.startswith('FLAG_MAP_'):
        return 'MapState'

    # By section
    section_map = {
        'Traffic Accounting': 'TrafficState',
        'Map & Geo': 'MapState',
        'UI Settings': 'SettingsState',
        'Session & Auth': 'SessionState',
        'String Resources': 'StringResources',
        'Packed Strings': 'StringResources',
        'Graphics Context': 'GraphicsState',
        'Runtime Vectors': 'SessionState',
        'Runtime Objects & Slots': 'SessionState',
        'Runtime Integers': 'RuntimeState',
    }
    if section in section_map:
        return section_map[section]

    # By primary writer
    all_files = set(list(readers.keys()) + list(writers.keys()))
    if all(('map/' in f or 'Map' in f) for f in all_files if f):
        return 'MapState'
    if all(('protocol/' in f or 'Account' in f) for f in all_files if f):
        return 'ProtocolState'

    return 'CoreState'


def print_report(results, unused):
    """Print the audit report."""
    # Sort by section, then by value
    results.sort(key=lambda r: (r['section'], r['value']))

    # === MAIN TABLE ===
    print("# StateKeys Audit Report\n")
    print(f"Total constants: {len(results) + len(unused)}")
    print(f"Used: {len(results)}, Unused: {len(unused)}\n")

    # Group by section
    current_section = None
    for r in results:
        if r['section'] != current_section:
            current_section = r['section']
            print(f"\n## {current_section}\n")
            print(f"| Key | Value | Lifecycle | Writers | Readers | Domain |")
            print(f"|-----|-------|-----------|---------|---------|--------|")

        writers_str = ', '.join(sorted(r['writers'].keys()))
        readers_str = ', '.join(sorted(r['readers'].keys()))
        domain = propose_domain(r['name'], r['section'], r['readers'], r['writers'])

        # Truncate long strings
        if len(writers_str) > 60:
            writers_str = writers_str[:57] + '...'
        if len(readers_str) > 60:
            readers_str = readers_str[:57] + '...'

        print(f"| {r['name']} | {r['value']} | {r['lifecycle']} | {writers_str} | {readers_str} | {domain} |")

    # === UNUSED ===
    if unused:
        print(f"\n## Unused Constants ({len(unused)})\n")
        print("| Key | Value | Section |")
        print("|-----|-------|---------|")
        for name, value, section in unused:
            print(f"| {name} | {value} | {section} |")

    # === STATISTICS ===
    print("\n## Statistics\n")

    # Lifecycle distribution
    lifecycle_counts = defaultdict(int)
    for r in results:
        lifecycle_counts[r['lifecycle']] += 1
    print("### By Lifecycle")
    for lc, count in sorted(lifecycle_counts.items(), key=lambda x: -x[1]):
        print(f"- {lc}: {count}")

    # Domain distribution
    domain_counts = defaultdict(int)
    for r in results:
        domain = propose_domain(r['name'], r['section'], r['readers'], r['writers'])
        domain_counts[domain] += 1
    print("\n### By Proposed Domain")
    for domain, count in sorted(domain_counts.items(), key=lambda x: -x[1]):
        print(f"- {domain}: {count}")

    # Multi-writer keys (potential conflicts)
    print("\n### Multi-Writer Keys (Potential Conflicts)\n")
    print("| Key | Value | Writers |")
    print("|-----|-------|---------|")
    for r in results:
        if len(r['writers']) > 2:
            writers_str = ', '.join(sorted(r['writers'].keys()))
            print(f"| {r['name']} | {r['value']} | {writers_str} |")

    # Keys with most total references
    print("\n### Most Referenced Keys (top 30)\n")
    print("| Key | Value | Total Refs | Writers | Readers |")
    print("|-----|-------|------------|---------|---------|")
    by_refs = sorted(results, key=lambda r: -(sum(r['readers'].values()) + sum(r['writers'].values())))
    for r in by_refs[:30]:
        total = sum(r['readers'].values()) + sum(r['writers'].values())
        print(f"| {r['name']} | {r['value']} | {total} | {len(r['writers'])} files | {len(r['readers'])} files |")


if __name__ == '__main__':
    results, unused = audit()
    print_report(results, unused)
