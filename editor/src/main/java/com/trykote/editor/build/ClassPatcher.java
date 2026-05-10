package com.trykote.editor.build;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Java bytecode patchers for J2ME compatibility.
 * Replaces: tools/patch_stringbuilder.py, tools/patch_class_version.py
 */
public class ClassPatcher {

    // Constant pool tags
    private static final int UTF8 = 1;
    private static final int INTEGER = 3;
    private static final int FLOAT = 4;
    private static final int LONG = 5;
    private static final int DOUBLE = 6;
    private static final int CLASS = 7;
    private static final int STRING = 8;
    private static final int FIELDREF = 9;
    private static final int METHODREF = 10;
    private static final int INTERFACE_METHODREF = 11;
    private static final int NAME_AND_TYPE = 12;
    private static final int METHOD_HANDLE = 15;
    private static final int METHOD_TYPE = 16;
    private static final int DYNAMIC = 17;
    private static final int INVOKE_DYNAMIC = 18;
    private static final int MODULE = 19;
    private static final int PACKAGE = 20;

    private static final long CAFEBABE = 0xCAFEBABEL;

    /**
     * Replace StringBuilder references with StringBuffer in all .class files.
     * javac -target 1.5 uses StringBuilder, but CLDC 1.1 only has StringBuffer.
     */
    public static int patchStringBuilder(Path directory) throws IOException {
        int count = 0;
        try (var stream = Files.walk(directory)) {
            for (Path path : stream.filter(p -> p.toString().endsWith(".class")).toList()) {
                if (patchStringBuilderInClass(path)) count++;
            }
        }
        System.out.println("Patched StringBuilder->StringBuffer in " + count + " class files");
        return count;
    }

    private static boolean patchStringBuilderInClass(Path path) throws IOException {
        byte[] data = Files.readAllBytes(path);
        if (data.length < 10) return false;
        if (readU4(data, 0) != CAFEBABE) return false;

        int cpCount = readU2(data, 8);
        int pos = 10;

        // Parse constant pool entries: (tag, rawBytes) or null for wide-tag padding
        record CpEntry(int tag, byte[] data) {}
        var entries = new ArrayList<CpEntry>();
        boolean modified = false;

        int i = 1;
        while (i < cpCount) {
            int tag = data[pos] & 0xFF;
            pos++;

            if (tag == UTF8) {
                int len = readU2(data, pos);
                pos += 2;
                byte[] utf8Bytes = new byte[len];
                System.arraycopy(data, pos, utf8Bytes, 0, len);
                pos += len;

                String text = new String(utf8Bytes, "UTF-8");
                if (text.contains("StringBuilder")) {
                    String patched = text.replace("StringBuilder", "StringBuffer");
                    utf8Bytes = patched.getBytes("UTF-8");
                    modified = true;
                }
                entries.add(new CpEntry(tag, utf8Bytes));
            } else {
                int size = fixedSize(tag);
                if (size < 0) throw new IOException("Unknown tag " + tag + " in " + path);
                byte[] entryData = new byte[size];
                System.arraycopy(data, pos, entryData, 0, size);
                pos += size;
                entries.add(new CpEntry(tag, entryData));
                if (tag == LONG || tag == DOUBLE) {
                    entries.add(null); // wide-tag padding
                    i++;
                }
            }
            i++;
        }

        if (!modified) return false;

        // Rebuild class file with patched constant pool
        byte[] rest = new byte[data.length - pos];
        System.arraycopy(data, pos, rest, 0, rest.length);

        var out = new ByteArrayOutputStream(data.length);
        // Magic + version
        out.write(data, 0, 8);
        // Constant pool count
        out.write(data[8]); out.write(data[9]);
        // Constant pool entries
        for (var entry : entries) {
            if (entry == null) continue;
            out.write(entry.tag);
            if (entry.tag == UTF8) {
                out.write(entry.data.length >> 8);
                out.write(entry.data.length & 0xFF);
            }
            out.write(entry.data);
        }
        // Rest of class file
        out.write(rest);

        Files.write(path, out.toByteArray());
        return true;
    }

    /**
     * Patch class file version for J2ME compatibility.
     * Default target: 45.3 (Java 1.1).
     */
    public static int patchClassVersion(Path directory, int major, int minor) throws IOException {
        int count = 0;
        try (var stream = Files.walk(directory)) {
            for (Path path : stream.filter(p -> p.toString().endsWith(".class")).toList()) {
                if (patchVersionInClass(path, major, minor)) count++;
            }
        }
        System.out.println("Patched " + count + " class files to version " + major + "." + minor);
        return count;
    }

    private static boolean patchVersionInClass(Path path, int targetMajor, int targetMinor)
            throws IOException {
        byte[] data = Files.readAllBytes(path);
        if (data.length < 8) return false;
        if (readU4(data, 0) != CAFEBABE) return false;

        int minor = readU2(data, 4);
        int major = readU2(data, 6);

        if (major == targetMajor && minor == targetMinor) return false;

        writeU2(data, 4, targetMinor);
        writeU2(data, 6, targetMajor);
        Files.write(path, data);
        return true;
    }

    private static int fixedSize(int tag) {
        return switch (tag) {
            case INTEGER, FLOAT, FIELDREF, METHODREF, INTERFACE_METHODREF,
                 NAME_AND_TYPE, DYNAMIC, INVOKE_DYNAMIC -> 4;
            case LONG, DOUBLE -> 8;
            case CLASS, STRING, METHOD_TYPE, MODULE, PACKAGE -> 2;
            case METHOD_HANDLE -> 3;
            default -> -1;
        };
    }

    private static int readU2(byte[] data, int offset) {
        return ((data[offset] & 0xFF) << 8) | (data[offset + 1] & 0xFF);
    }

    private static long readU4(byte[] data, int offset) {
        return ((long)(data[offset] & 0xFF) << 24) | ((data[offset+1] & 0xFF) << 16)
             | ((data[offset+2] & 0xFF) << 8)  |  (data[offset+3] & 0xFF);
    }

    private static void writeU2(byte[] data, int offset, int value) {
        data[offset] = (byte) (value >> 8);
        data[offset + 1] = (byte) value;
    }
}
