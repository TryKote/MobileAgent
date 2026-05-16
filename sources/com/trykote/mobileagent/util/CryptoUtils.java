package com.trykote.mobileagent.util;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.key.StringResKeys;

public final class CryptoUtils {

    // SHA-256 block size
    private static final int SHA256_BLOCK_SIZE = 64;
    private static final int SHA256_DIGEST_SIZE = 32;
    private static final int SHA256_ROUNDS = 8;

    // HMAC padding bytes
    private static final byte HMAC_IPAD = 54;
    private static final byte HMAC_OPAD = 92;

    private static int[] getSHA256Constants() {
        return (int[]) AppState.getObject(StringResKeys.RES_EMOTICON_MAP);
    }

    private static int rotateLeft(int value, int shift) {
        return (value >>> shift) | (value << (32 - shift));
    }

    private static void writeIntToBytes(int value, byte[] dest, int offset) {
        dest[offset] = (byte) (value >> 24);
        dest[offset + 1] = (byte) (value >>> 16);
        dest[offset + 2] = (byte) (value >>> 8);
        dest[offset + 3] = (byte) value;
    }

    private static Object[] initHashState() {
        Object[] state = {new int[10], ObjectPool.newBytes(128)};
        int[] hashValues = (int[]) state[0];
        int[] constants = getSHA256Constants();
        for (int hi = SHA256_ROUNDS - 1; hi >= 0; hi--) {
            hashValues[hi] = constants[hi];
        }
        return state;
    }

    private static Object[] updateHashBuffer(Object[] state, byte[] data, int dataLen) {
        int[] hashValues = (int[]) state[0];
        byte[] blockBuffer = (byte[]) state[1];
        int buffered = hashValues[9];
        int copyLen = Utils.min(dataLen, SHA256_BLOCK_SIZE - buffered);
        System.arraycopy(data, 0, blockBuffer, buffered, copyLen);
        if (buffered + dataLen < SHA256_BLOCK_SIZE) {
            hashValues[9] = buffered + dataLen;
        } else {
            processSHA256Block(state, blockBuffer, 0, 1);
            int remaining = dataLen - copyLen;
            int fullBlocks = remaining >> 6;
            processSHA256Block(state, data, copyLen, fullBlocks);
            int processedOffset = copyLen + (fullBlocks << 6);
            int tailLen = remaining & 63;
            System.arraycopy(data, processedOffset, blockBuffer, 0, tailLen);
            hashValues[9] = tailLen;
            hashValues[SHA256_ROUNDS] = hashValues[SHA256_ROUNDS] + ((fullBlocks + 1) << 6);
        }
        return state;
    }

    private static byte[] finalizeSHA256(Object[] state) {
        int[] hashValues = (int[]) state[0];
        int buffered = hashValues[9];
        int paddingBlocks = 55 < (buffered & 63) ? 2 : 1;
        int totalBits = (hashValues[SHA256_ROUNDS] + buffered) << 3;
        byte[] blockBuffer = (byte[]) state[1];
        int paddingLen = paddingBlocks << 6;
        for (int ci = paddingLen - 1; ci >= buffered; ci--) {
            blockBuffer[ci] = 0;
        }
        blockBuffer[buffered] = -128;
        writeIntToBytes(totalBits, blockBuffer, paddingLen - 4);
        processSHA256Block(state, blockBuffer, 0, paddingBlocks);
        byte[] digest = new byte[SHA256_DIGEST_SIZE];
        for (int di = SHA256_ROUNDS - 1; di >= 0; di--) {
            writeIntToBytes(hashValues[di], digest, di << 2);
        }
        ObjectPool.releaseBytes(blockBuffer);
        return digest;
    }

    private static final int BLOWFISH_ROUNDS = 16;

    private static void processSHA256Block(Object[] state, byte[] data, int offset, int blockCount) {
        int[] hashValues = (int[]) state[0];
        int[] schedule = new int[SHA256_BLOCK_SIZE];
        int[] working = new int[SHA256_ROUNDS];
        int[] constants = getSHA256Constants();
        for (int bi = 0; bi < blockCount; bi++) {
            int wi = 0;
            do {
                int bytePos = offset + (bi << 6) + (wi << 2);
                schedule[wi] = (data[bytePos] << 24) | ((data[bytePos + 1] & 255) << 16) | ((data[bytePos + 2] & 255) << 8) | (data[bytePos + 3] & 255);
                wi++;
            } while (wi < BLOWFISH_ROUNDS);
            do {
                int si = wi;
                int w2 = schedule[si - 2];
                int sigma1 = ((rotateLeft(w2, 17) ^ rotateLeft(w2, 19)) ^ (w2 >>> 10)) + schedule[si - 7];
                int w15 = schedule[si - 15];
                schedule[si] = sigma1 + ((rotateLeft(w15, 7) ^ rotateLeft(w15, 18)) ^ (w15 >>> 3)) + schedule[si - 16];
                wi++;
            } while (wi < SHA256_BLOCK_SIZE);
            for (int ci = SHA256_ROUNDS - 1; ci >= 0; ci--) {
                working[ci] = hashValues[ci];
            }
            int ri = 0;
            do {
                int h = working[7];
                int e = working[4];
                int sum1 = h + ((rotateLeft(e, 6) ^ rotateLeft(e, 11)) ^ rotateLeft(e, 25));
                int eCopy = working[4];
                int t1 = sum1 + ((eCopy & working[5]) ^ ((eCopy ^ (-1)) & working[6])) + constants[ri + SHA256_ROUNDS] + schedule[ri];
                int a = working[0];
                int sum0 = (rotateLeft(a, 2) ^ rotateLeft(a, 13)) ^ rotateLeft(a, 22);
                int aCopy = working[0];
                int b = working[1];
                int c = working[2];
                working[7] = working[6];
                working[6] = working[5];
                working[5] = working[4];
                working[4] = working[3] + t1;
                working[3] = working[2];
                working[2] = working[1];
                working[1] = working[0];
                working[0] = t1 + sum0 + (((aCopy & b) ^ (aCopy & c)) ^ (b & c));
                ri++;
            } while (ri < SHA256_BLOCK_SIZE);
            int mi = 0;
            do {
                int idx = mi;
                hashValues[idx] = hashValues[idx] + working[mi];
                mi++;
            } while (mi < SHA256_ROUNDS);
        }
    }

    public static byte[] hmacSHA256(byte[] key, int keyLen, byte[] data, int dataOffset, int dataLen) {
        int effectiveKeyLen;
        byte[] innerPad = ObjectPool.newBytes(SHA256_BLOCK_SIZE);
        byte[] outerPad = ObjectPool.newBytes(SHA256_BLOCK_SIZE);
        if (keyLen == SHA256_BLOCK_SIZE) {
            effectiveKeyLen = SHA256_BLOCK_SIZE;
        } else {
            if (keyLen > SHA256_BLOCK_SIZE) {
                effectiveKeyLen = SHA256_DIGEST_SIZE;
                key = finalizeSHA256(updateHashBuffer(initHashState(), key, keyLen));
            } else {
                effectiveKeyLen = keyLen;
            }
            for (int pi = SHA256_BLOCK_SIZE - 1; pi >= effectiveKeyLen; pi--) {
                innerPad[pi] = HMAC_IPAD;
                outerPad[pi] = HMAC_OPAD;
            }
        }
        for (int ki = effectiveKeyLen - 1; ki >= 0; ki--) {
            innerPad[ki] = (byte) (key[ki] ^ HMAC_IPAD);
            outerPad[ki] = (byte) (key[ki] ^ HMAC_OPAD);
        }
        Object[] innerState = updateHashBuffer(initHashState(), innerPad, SHA256_BLOCK_SIZE);
        ObjectPool.releaseBytes(innerPad);
        Object[] outerState = updateHashBuffer(initHashState(), outerPad, SHA256_BLOCK_SIZE);
        ObjectPool.releaseBytes(outerPad);
        Object[] states = {innerState, outerState};
        updateHashBuffer((Object[]) states[0], data, dataOffset);
        return finalizeSHA256(updateHashBuffer((Object[]) states[1], finalizeSHA256((Object[]) states[0]), SHA256_DIGEST_SIZE));
    }
}
