package com.trykote.mobileagent.map;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
/* Extracted from AppController: map coordinate math utilities */
public final class MapUtils {

    /* renamed from: d */
    public static final long getZoomNumerator(int i) {
        return (1 << (17 - i)) * ((i < 8 || i > 17) ? 119432 : 1194329);
    }

    /* renamed from: e */
    public static final long getZoomDenominator(int i) {
        return (i < 8 || i > 17) ? 100000L : 1000000L;
    }

    /* renamed from: a */
    public static long coordToPixel(long j, int i) {
        return (j * getZoomDenominator(i)) / getZoomNumerator(i);
    }

    /* renamed from: a */
    public static final long pixelToCoord(int i, int i2) {
        return (i * getZoomNumerator(i2)) / getZoomDenominator(i2);
    }

    /* renamed from: a */
    public static final int computeColor(int i, int i2, int i3, int i4) {
        return Utils.abs(i2 - i4) + Utils.abs(i - i3);
    }
}
