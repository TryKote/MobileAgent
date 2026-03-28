#!/usr/bin/env python3
"""Patch class file version for J2ME compatibility.

Usage: python3 patch_class_version.py <directory> [major.minor]

Rewrites the version in all .class files under <directory>.
Default target version is 45.3 (Java 1.1), matching original MobileAgent JAR.
"""
import os, struct, sys

def patch(directory, target_major=45, target_minor=3):
    count = 0
    for root, dirs, files in os.walk(directory):
        for name in files:
            if not name.endswith('.class'):
                continue
            path = os.path.join(root, name)
            with open(path, 'r+b') as f:
                header = f.read(8)
                magic, minor, major = struct.unpack('>IHH', header)
                if magic != 0xCAFEBABE:
                    continue
                if major != target_major or minor != target_minor:
                    f.seek(4)
                    f.write(struct.pack('>HH', target_minor, target_major))
                    count += 1
    return count

if __name__ == '__main__':
    if len(sys.argv) < 2:
        print(__doc__.strip(), file=sys.stderr)
        sys.exit(1)
    version = sys.argv[2] if len(sys.argv) > 2 else '45.3'
    parts = version.split('.')
    major = int(parts[0])
    minor = int(parts[1]) if len(parts) > 1 else 0
    n = patch(sys.argv[1], major, minor)
    print(f"Patched {n} class files to version {major}.{minor}")
