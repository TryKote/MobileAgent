package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.UIState;

import javax.microedition.lcdui.Image;
import java.util.Hashtable;
import java.util.Vector;

public final class InlineImageCache {

    private static final int MAX_CACHED_IMAGES = 4;
    public static final int MAX_IMAGE_SIZE = 204800;

    private static final Hashtable cache = new Hashtable();
    private static final Vector keys = new Vector();
    private static final Hashtable tooLarge = new Hashtable();
    private static final Hashtable downloading = new Hashtable();
    private static Image pendingImage;

    public static synchronized Image getImage(String url) {
        return (Image) cache.get(url);
    }

    public static synchronized void putImage(String url, Image image) {
        if (cache.containsKey(url)) {
            return;
        }
        while (keys.size() >= MAX_CACHED_IMAGES) {
            String oldest = (String) keys.elementAt(0);
            keys.removeElementAt(0);
            cache.remove(oldest);
        }
        cache.put(url, image);
        keys.addElement(url);
    }

    public static synchronized boolean isTooLarge(String url) {
        return tooLarge.containsKey(url);
    }

    public static synchronized void markTooLarge(String url) {
        tooLarge.put(url, url);
    }

    public static synchronized boolean isDownloading(String url) {
        return downloading.containsKey(url);
    }

    public static synchronized void markDownloading(String url) {
        downloading.put(url, url);
    }

    public static synchronized void clearDownloading(String url) {
        downloading.remove(url);
    }

    public static synchronized void setPendingImage(Image image) {
        pendingImage = image;
    }

    public static synchronized Image takePendingImage() {
        Image img = pendingImage;
        pendingImage = null;
        return img;
    }

    public static String toHttpUrl(String url) {
        if (!url.startsWith("https://")) {
            return url;
        }
        String httpUrl = "http://" + url.substring(8);
        int portStart = httpUrl.indexOf(':', 7);
        if (portStart < 0) {
            return httpUrl;
        }
        int portEnd = httpUrl.indexOf('/', portStart);
        if (portEnd < 0) {
            return httpUrl;
        }
        String port = httpUrl.substring(portStart + 1, portEnd);
        if ("7443".equals(port)) {
            return httpUrl.substring(0, portStart + 1) + "7070" + httpUrl.substring(portEnd);
        }
        if ("443".equals(port)) {
            return httpUrl.substring(0, portStart) + httpUrl.substring(portEnd);
        }
        return httpUrl;
    }

    public static Image scaleToFit(Image source) {
        int maxWidth = (UIState.getScreenWidth() * 9 / 10) - 20;
        int srcW = source.getWidth();
        if (srcW <= maxWidth) {
            return source;
        }
        int srcH = source.getHeight();
        int dstW = maxWidth;
        int dstH = (srcH * dstW) / srcW;

        int[] srcPixels = new int[srcW * srcH];
        source.getRGB(srcPixels, 0, srcW, 0, 0, srcW, srcH);

        int[] dstPixels = new int[dstW * dstH];
        for (int dy = 0; dy < dstH; dy++) {
            int sy = (dy * srcH) / dstH;
            for (int dx = 0; dx < dstW; dx++) {
                int sx = (dx * srcW) / dstW;
                dstPixels[dy * dstW + dx] = srcPixels[sy * srcW + sx];
            }
        }

        return Image.createRGBImage(dstPixels, dstW, dstH, true);
    }

    public static boolean isImageUrl(String text) {
        if (text == null || text.length() < 10) {
            return false;
        }
        char c0 = text.charAt(0);
        if (c0 != 'h' && c0 != 'H') {
            return false;
        }
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
                return false;
            }
        }
        String lower = text.toLowerCase();
        if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
            return false;
        }
        return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg");
    }
}
