package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.UIState;

import javax.microedition.lcdui.Image;
import java.util.Hashtable;
import java.util.Vector;

public final class InlineImageCache {

    private static final int MAX_CACHED_IMAGES = 4;
    public static final int MAX_IMAGE_SIZE = 524288;

    private static final Hashtable cache = new Hashtable();
    private static final Vector keys = new Vector();
    private static final Hashtable tooLarge = new Hashtable();
    private static final Hashtable downloading = new Hashtable();
    private static Image pendingImage;
    public static final int PHASE_NONE = -1;
    public static final int PHASE_CONNECTING = 0;
    public static final int PHASE_HEADERS = 1;
    public static final int PHASE_BODY = 2;
    public static final int PHASE_DONE = 3;

    public static final String LABEL_IMAGE = "[Картинка]";
    public static final String LABEL_TOO_LARGE = "[Файл слишком большой]";
    public static final String LABEL_CONNECTING = "[Подключение...]";

    private static String progressUrl;
    private static int progressPercent;
    private static int progressPhase = PHASE_NONE;
    private static boolean stateChanged;

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
        stateChanged = true;
    }

    /** Frees all decoded images. Used as a recovery action after OutOfMemoryError. */
    public static synchronized int clearCache() {
        int freed = keys.size();
        keys.removeAllElements();
        cache.clear();
        stateChanged = true;
        return freed;
    }

    public static synchronized boolean isTooLarge(String url) {
        return tooLarge.containsKey(url);
    }

    public static synchronized void markTooLarge(String url) {
        tooLarge.put(url, url);
        stateChanged = true;
    }

    public static synchronized boolean isDownloading(String url) {
        return downloading.containsKey(url);
    }

    public static synchronized void markDownloading(String url) {
        downloading.put(url, url);
        stateChanged = true;
    }

    public static synchronized void clearDownloading(String url) {
        downloading.remove(url);
        if (url != null && url.equals(progressUrl)) {
            progressUrl = null;
            progressPhase = PHASE_NONE;
            progressPercent = 0;
        }
        stateChanged = true;
    }

    public static synchronized void setProgress(String url, int phase, int percent) {
        progressUrl = url;
        progressPhase = phase;
        progressPercent = percent;
        stateChanged = true;
    }

    public static synchronized boolean consumeStateChange() {
        boolean wasChanged = stateChanged;
        stateChanged = false;
        return wasChanged;
    }

    public static synchronized String resolveImageLabel(String url) {
        if (cache.containsKey(url)) return LABEL_IMAGE;
        if (tooLarge.containsKey(url)) return LABEL_TOO_LARGE;
        if (url.equals(progressUrl)
                && progressPhase != PHASE_NONE
                && progressPhase != PHASE_DONE) {
            return buildProgressLabel(progressPhase, progressPercent);
        }
        return LABEL_IMAGE;
    }

    private static String buildProgressLabel(int phase, int percent) {
        if (phase == PHASE_CONNECTING || phase == PHASE_HEADERS) {
            return LABEL_CONNECTING;
        }
        int filled = percent / 10;
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < 10; i++) {
            sb.append(i < filled ? '#' : '-');
        }
        sb.append(' ');
        sb.append(percent);
        sb.append("%]");
        return sb.toString();
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

        int[] srcRow = new int[srcW];
        int[] dstPixels = new int[dstW * dstH];
        int prevSy = -1;
        for (int dy = 0; dy < dstH; dy++) {
            int sy = (dy * srcH) / dstH;
            if (sy != prevSy) {
                source.getRGB(srcRow, 0, srcW, 0, sy, srcW, 1);
                prevSy = sy;
            }
            int rowBase = dy * dstW;
            for (int dx = 0; dx < dstW; dx++) {
                int sx = (dx * srcW) / dstW;
                dstPixels[rowBase + dx] = srcRow[sx];
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
