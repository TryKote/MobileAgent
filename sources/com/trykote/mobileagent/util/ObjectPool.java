package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.*;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Object pool for reusing frequently allocated objects (byte arrays,
 * StringBuffers, Vectors) to reduce garbage collection pressure on J2ME.
 *
 * <p>All pools are fixed-size arrays. When a pool slot is consumed, remaining
 * entries are shifted left so that free slots always appear at the tail.
 */
public abstract class ObjectPool {

    private static final int BYTE_POOL_SIZE = 20;
    private static final int BUFFER_POOL_SIZE = 5;
    private static final int VECTOR_POOL_SIZE = 5;

    /** Pool of reusable byte arrays (max 20 entries, each up to 2048 bytes). */
    public static byte[][] bytePool;

    /** Pool of reusable StringBuffers (max 5 entries). */
    public static StringBuffer[] bufferPool;

    /** Pool of reusable Vectors (max 5 entries). */
    public static Vector[] vectorPool;

    /** Interning cache — maps strings to their canonical instances. */
    public static Hashtable stringCache;

    public static void initPools() {
        bytePool = new byte[BYTE_POOL_SIZE][];
        bufferPool = new StringBuffer[BUFFER_POOL_SIZE];
        vectorPool = new Vector[VECTOR_POOL_SIZE];
        stringCache = new Hashtable();
    }

    /**
     * Obtains a zeroed byte array of at least {@code size} bytes.
     * Returns a pooled array if one of sufficient length is available,
     * otherwise allocates a new one. Arrays larger than 2048 are never pooled.
     */
    public static final byte[] newBytes(int size) {
        if (size > 2048) {
            return new byte[size];
        }
        byte[][] pool = bytePool;
        synchronized (pool) {
            for (int idx = 0; idx < 20; idx++) {
                byte[] entry = pool[idx];
                if (entry != null && entry.length >= size) {
                    int remaining = entry.length;
                    while (true) {
                        remaining--;
                        if (remaining < 0) {
                            Utils.arraycopy(pool, idx + 1, pool, idx, 19 - idx);
                            pool[19] = null;
                            return entry;
                        }
                        entry[remaining] = 0;
                    }
                }
            }
            return new byte[size];
        }
    }

    /**
     * Reallocates {@code source} into a pooled byte array of at least
     * {@code requiredSize} bytes, copying existing data and returning
     * the old array to the pool. Returns {@code null} if no suitable
     * pooled array is available or if {@code requiredSize} exceeds 2048.
     */
    public static final byte[] reallocBytes(byte[] source, int requiredSize) {
        int entryLength;
        if (requiredSize > 2048) {
            return null;
        }
        byte[][] pool = bytePool;
        synchronized (pool) {
            byte[] bestMatch = null;
            int bestSize = Integer.MAX_VALUE;
            int bestIndex = 0;
            for (int idx = 0; idx < 20; idx++) {
                byte[] entry = pool[idx];
                if (entry != null && (entryLength = entry.length) >= requiredSize && entryLength < bestSize) {
                    bestMatch = entry;
                    bestSize = entryLength;
                    bestIndex = idx;
                }
            }
            if (bestMatch == null) {
                return null;
            }
            Utils.arraycopy((Object) source, 0, (Object) bestMatch, 0, requiredSize);
            if (bestIndex != 19) {
                Utils.arraycopy(pool, bestIndex + 1, pool, bestIndex, 19 - bestIndex);
            }
            pool[19] = null;
            releaseBytes(source);
            return bestMatch;
        }
    }

    /**
     * Returns a byte array to the pool. Arrays that are {@code null},
     * larger than 2048, or 8 bytes or smaller are silently ignored.
     */
    public static final void releaseBytes(byte[] buffer) {
        if (buffer == null || buffer.length > 2048 || buffer.length <= 8) {
            return;
        }
        byte[][] pool = bytePool;
        synchronized (pool) {
            int slot = 0;
            while (slot < 20) {
                if (pool[slot] == null) {
                    break;
                } else {
                    slot++;
                }
            }
            if (slot == 20) {
                Utils.arraycopy(pool, 1, pool, 0, 19);
                slot--;
            }
            pool[slot] = buffer;
        }
    }

    /**
     * Obtains a Vector from the pool, or creates a new one if the pool
     * is empty.
     */
    public static final Vector newVector() {
        Vector[] pool = vectorPool;
        synchronized (pool) {
            for (int idx = 0; idx < 5; idx++) {
                Vector entry = pool[idx];
                if (entry != null) {
                    Utils.arraycopy(pool, idx + 1, pool, idx, 4 - idx);
                    pool[4] = null;
                    return entry;
                }
            }
            return new Vector();
        }
    }

    /**
     * Returns a Vector to the pool after clearing its contents.
     * If the pool is full the Vector is silently discarded.
     */
    public static final void releaseVector(Vector vector) {
        if (vector != null) {
            vector.removeAllElements();
            Utils.trimIfEmpty(vector);
            Vector[] pool = vectorPool;
            synchronized (pool) {
                for (int idx = 0; idx < 5; idx++) {
                    if (pool[idx] == null) {
                        pool[idx] = vector;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Obtains a StringBuffer from the pool, or creates a new one if the
     * pool is empty.
     */
    public static final StringBuffer newStringBuffer() {
        StringBuffer[] pool = bufferPool;
        synchronized (pool) {
            int idx = 0;
            do {
                StringBuffer entry = pool[idx];
                if (entry != null) {
                    Utils.arraycopy(pool, idx + 1, pool, idx, 4 - idx);
                    pool[4] = null;
                    return entry;
                }
                idx++;
            } while (idx != 5);
            return new StringBuffer();
        }
    }

    /**
     * Converts the StringBuffer contents to a cached/interned String,
     * then clears and returns the buffer to the pool.
     *
     * @return the interned string value
     */
    public static final String toStringAndRelease(StringBuffer sb) {
        String result = internOrLookup(sb.toString());
        sb.setLength(0);
        StringBuffer[] pool = bufferPool;
        synchronized (pool) {
            int idx = 0;
            while (true) {
                if (idx >= 5) {
                    break;
                }
                if (pool[idx] == null) {
                    pool[idx] = sb;
                    break;
                }
                idx++;
            }
        }
        return result;
    }

    /**
     * Converts the StringBuffer contents to a cached/interned String,
     * optionally returning the buffer to the pool.
     *
     * @param releaseToPool if {@code true}, the buffer is released to the pool
     * @return the interned string value
     */
    public static final String toString(StringBuffer sb, boolean releaseToPool) {
        if (releaseToPool) {
            return toStringAndRelease(sb);
        }
        String result = internOrLookup(sb.toString());
        sb.setLength(0);
        return result;
    }

    /**
     * Creates a new pooled StringBuffer pre-filled with the string value
     * stored at the given {@code stateKey} in AppState.
     */
    public static final StringBuffer newBufferFromState(int stateKey) {
        return newStringBuffer().append(AppState.getString(stateKey));
    }

    /**
     * Adds a string to the interning cache so that future lookups
     * return the same instance.
     */
    public static final void cacheString(String value) {
        stringCache.put(value, value);
    }

    /**
     * Returns a cached instance of the given string if present,
     * otherwise interns it via {@link StringUtils#intern(String)}.
     */
    private static String internOrLookup(String key) {
        String cached = (String) stringCache.get(key);
        return cached != null ? cached : StringUtils.intern(key);
    }

    /**
     * Decodes a Windows-1251 encoded byte array into a String,
     * using a pooled StringBuffer.
     */
    public static final String decodeWin1251(byte[] win1251Bytes) {
        StringBuffer[] pool = bufferPool;
        synchronized (pool) {
            int idx = 0;
            do {
                StringBuffer sb = pool[idx];
                if (sb != null) {
                    return decodeWin1251Impl(win1251Bytes, sb, false);
                }
                idx++;
            } while (idx != 5);
            return decodeWin1251Impl(win1251Bytes, new StringBuffer(), true);
        }
    }

    /**
     * Decodes a Windows-1251 byte array by appending each byte (converted
     * via {@link Utils#win1251ToChar(int)}) to the given StringBuffer.
     */
    private static final String decodeWin1251Impl(byte[] win1251Bytes, StringBuffer sb, boolean releaseToPool) {
        for (byte b : win1251Bytes) {
            sb.append(Utils.win1251ToChar((int) b));
        }
        return toString(sb, releaseToPool);
    }

    /**
     * Unpacks a {@code long} value into a String by extracting each byte
     * (little-endian order) as a character until the value is exhausted.
     * Uses a pooled StringBuffer.
     */
    public static final String unpackChars(long packed) {
        StringBuffer[] pool = bufferPool;
        synchronized (pool) {
            int idx = 0;
            do {
                StringBuffer sb = pool[idx];
                if (sb != null) {
                    return unpackCharsImpl(packed, sb, false);
                }
                idx++;
            } while (idx != 5);
            return unpackCharsImpl(packed, new StringBuffer(), true);
        }
    }

    /**
     * Unpacks bytes from a {@code long} into characters appended to the
     * given StringBuffer (little-endian: least significant byte first).
     */
    private static final String unpackCharsImpl(long packed, StringBuffer sb, boolean releaseToPool) {
        while (packed != 0) {
            sb.append((char) (packed & 255));
            packed >>>= 8;
        }
        return toString(sb, releaseToPool);
    }
}
