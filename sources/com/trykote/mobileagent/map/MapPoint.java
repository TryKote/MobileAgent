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
/* renamed from: an */
/* loaded from: MobileAgent_3.9.jar:an.class */
public final class MapPoint implements ListItem {

    /* renamed from: a */
    public String name;

    /* renamed from: b */
    public long boundsMinLon;

    /* renamed from: c */
    public long boundsMinLat;

    /* renamed from: d */
    public long boundsMaxLon;

    /* renamed from: e */
    public long boundsMaxLat;

    /* renamed from: f */
    public long longitude;

    /* renamed from: g */
    public long latitude;

    /* renamed from: h */
    public int zoomLevel;

    /* renamed from: i */
    public boolean selected;

    /* renamed from: j */
    public boolean dirty;

    /* renamed from: k */
    public int height;

    /* renamed from: l */
    public int typeCode;

    /* renamed from: m */
    public int objectCode;

    /* renamed from: n */
    private SizeCache sizeCache;

    public MapPoint() {
        this.sizeCache = new SizeCache();
    }

    public MapPoint(String str, long j, long j2, long j3, long j4, long j5, long j6, int i) {
        this.name = str;
        this.boundsMinLon = 0L;
        this.boundsMinLat = 0L;
        this.boundsMaxLon = 0L;
        this.boundsMaxLat = 0L;
        this.longitude = j5;
        this.latitude = j6;
        this.zoomLevel = i;
        this.sizeCache = new SizeCache();
    }

    public MapPoint(String str, long j, long j2, int i) {
        this.name = str;
        this.longitude = j;
        this.latitude = j2;
        this.zoomLevel = i;
        this.sizeCache = new SizeCache();
    }

    public MapPoint(String str) {
        try {
            this.name = Conversation.decodeMessage(str);
            this.longitude = MapUtils.longitudeToPixel(Conversation.extractFrom(str));
            this.latitude = MapUtils.latitudeToPixel(Conversation.extractTo(str));
            this.zoomLevel = Integer.parseInt(Conversation.extractSubject(str));
        } catch (Throwable unused) {
        }
        this.sizeCache = new SizeCache();
    }

    public MapPoint(ByteBuffer buffer) {
        this.name = buffer.readUTF8Str((String) null);
        this.boundsMinLon = buffer.readLong();
        this.boundsMinLat = buffer.readLong();
        this.boundsMaxLon = buffer.readLong();
        this.boundsMaxLat = buffer.readLong();
        this.longitude = buffer.readLong();
        this.latitude = buffer.readLong();
        this.zoomLevel = buffer.readInt();
        this.height = buffer.readInt();
        this.objectCode = buffer.readInt();
        this.typeCode = buffer.readInt();
        this.sizeCache = new SizeCache();
    }

    /* renamed from: a */
    public final String getDisplayName() {
        int endIdx;
        try {
            int startIdx = this.name.indexOf(59);
            return (startIdx < 0 || (endIdx = this.name.indexOf(59, startIdx + 1)) < 0) ? this.name : StringUtils.prefix(this.name, endIdx);
        } catch (Throwable unused) {
            return this.name;
        }
    }

    /* renamed from: b */
    public final void markActive() {
        this.selected = true;
        this.dirty = true;
    }

    /* renamed from: c */
    public final void markInactive() {
        this.selected = false;
        this.dirty = true;
    }

    /* renamed from: c */
    public final long getLonAtZoom(int i) {
        return MapUtils.coordToPixel(this.longitude, i);
    }

    /* renamed from: d */
    public final long getLatAtZoom(int i) {
        return MapUtils.coordToPixel(this.latitude, i);
    }

    /* renamed from: d */
    public final String getResourceUrl() {
        return MapUtils.buildTileRequestUrl(this.longitude, this.latitude, this.zoomLevel, this.name);
    }

    @Override // p000.ListItem
    /* renamed from: x */
    public final String getText() {
        return getDisplayName();
    }

    @Override // p000.ListItem
    /* renamed from: v */
    public final int getWidth() {
        return (int) this.longitude;
    }

    @Override // p000.ListItem
    /* renamed from: w */
    public final int getBaseHeight() {
        return (int) this.latitude;
    }

    @Override // p000.ListItem
    /* renamed from: r */
    public final int getHeight() {
        return this.height;
    }

    @Override // p000.ListItem
    /* renamed from: s */
    public final boolean isSelected() {
        return this.selected;
    }

    @Override // p000.ListItem
    /* renamed from: t */
    public final void select() {
        this.selected = false;
    }

    @Override // p000.ListItem
    /* renamed from: u */
    public final void deselect() {
        this.selected = true;
    }

    @Override // p000.ListItem
    /* renamed from: y */
    public final int getCommandCount() {
        if (this.typeCode != 0) {
            return getMarkerType(this.typeCode);
        }
        return 10;
    }

    @Override // p000.ListItem
    /* renamed from: z */
    public final boolean isHighlighted() {
        return true;
    }

    @Override // p000.ListItem
    /* renamed from: a */
    public final int getCommandId(int i) {
        return this.sizeCache.getWidth(i, this);
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public final int executeCommand(int i) {
        return this.sizeCache.getHeight(i, this);
    }

    /* renamed from: e */
    public static final int getMarkerType(int i) {
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 8:
            case 9:
            case 13:
            case 2000:
            case 2001:
                return 5;
            case 5:
            case 6:
            case 10:
            case 11:
            case 14:
            case 15:
            case 16:
            case 18:
            case 19:
            case 20:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 444:
            case 445:
            case 456:
            case 480:
            case 509:
            case 510:
            case 558:
            case 559:
            case 560:
                return 13;
            case 30:
            case 32:
            case 33:
            case 40:
            case 41:
            case 43:
            case 44:
            case 45:
            case 47:
            case 48:
            case 49:
            case 50:
            case 51:
            case 53:
            case 54:
            case 132:
            case 133:
            case 134:
            case 451:
                return 16;
            case 154:
            case 155:
            case 156:
            case 157:
                return 17;
            case 476:
                return 3;
            case 504:
                return 4;
            default:
                return 10;
        }
    }
}
