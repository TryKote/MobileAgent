package com.trykote.mobileagent.util;

import com.trykote.mobileagent.core.UIState;

import javax.microedition.lcdui.Image;

public final class ImageCache {

    private static final int IMAGE_CACHE_DATA_OFFSET = 29;
    private static final int IMAGE_CACHE_EXPIRE_THRESHOLD = 16;
    private static final int IMAGE_CACHE_FULL_EXPIRE = 32;
    private static final int IMAGE_CACHE_SMALL_PREFIX = 26;

    private static Object[] getImageCachePool() {
        return UIState.getGfxContextsArray();
    }

    private static int[] getImageTimestamps() {
        return UIState.getGfxHeightsArray();
    }

    public static void incrementCacheCounter() {
        synchronized (getImageCachePool()) {
            UIState.incrementImageCounter();
        }
    }

    public static void invalidateCachedImage(int slot) {
        Object[] cachePool = getImageCachePool();
        synchronized (cachePool) {
            cachePool[slot] = null;
            cachePool[slot + IMAGE_CACHE_DATA_OFFSET] = null;
        }
    }

    public static void cleanupExpiredImages() {
        Object[] cachePool = getImageCachePool();
        synchronized (cachePool) {
            int counter = UIState.getImageCounter();
            int[] timestamps = getImageTimestamps();
            for (int si = 28; si >= 0; si--) {
                int age = counter - timestamps[si];
                if (age > IMAGE_CACHE_EXPIRE_THRESHOLD) {
                    cachePool[si] = null;
                    if (age > IMAGE_CACHE_FULL_EXPIRE) {
                        ObjectPool.releaseBytes((byte[]) cachePool[si + IMAGE_CACHE_DATA_OFFSET]);
                        cachePool[si + IMAGE_CACHE_DATA_OFFSET] = null;
                    }
                }
            }
        }
    }

    public static Image getOrLoadImage(int slot) {
        Object[] cachePool = getImageCachePool();
        synchronized (cachePool) {
            getImageTimestamps()[slot] = UIState.getImageCounter();
            if (cachePool[slot] != null) {
                return (Image) cachePool[slot];
            }
            try {
                byte[] cachedData = (byte[]) cachePool[slot + IMAGE_CACHE_DATA_OFFSET];
                byte[] imageData = cachedData;
                if (cachedData == null) {
                    int dataSlot = slot + IMAGE_CACHE_DATA_OFFSET;
                    byte[] loadedData = new ByteBuffer(ObjectPool.unpackChars(slot < IMAGE_CACHE_SMALL_PREFIX ? 113724026151215L + (slot << 8) : 29113350693019951L + (slot << 16))).toByteArray();
                    imageData = loadedData;
                    cachePool[dataSlot] = loadedData;
                }
                Image image = Image.createImage(imageData, 0, imageData.length);
                cachePool[slot] = image;
                return image;
            } catch (Throwable unused) {
                cachePool[slot + IMAGE_CACHE_DATA_OFFSET] = null;
                cachePool[slot] = null;
                return Image.createImage(1, 1);
            }
        }
    }
}
