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
public final class GeoRegion {

    public String name;

    public long minLat;

    public long maxLon;

    public long maxLat;

    public long minLon;

    public long centerLat;

    public long centerLon;

    public int zoomLevel;

    public int mapType;

    public String description;

    public int precision;

    private GeoRegion() {
        this.zoomLevel = -1;
        this.precision = 16;
    }

    public GeoRegion(String str, long j, long j2, long j3, long j4) {
        this();
        this.name = str;
        this.minLat = j;
        this.maxLon = j2;
        this.maxLat = j3;
        this.minLon = j4;
    }

    public GeoRegion(ByteBuffer buffer) {
        this(buffer.readUTF8Str((String) null), buffer.readLong(), buffer.readLong(), buffer.readLong(), buffer.readLong());
        this.description = buffer.readUTF8Str((String) null);
        this.centerLat = buffer.readLong();
        this.centerLon = buffer.readLong();
        this.precision = buffer.readInt();
    }

    public final boolean containsPoint(long j, long j2) {
        return j < this.maxLat && j > this.minLat && j2 < this.maxLon && j2 > this.minLon;
    }
}
