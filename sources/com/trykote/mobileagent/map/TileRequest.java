package com.trykote.mobileagent.map;

import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.StringUtils;

public final class TileRequest {

    public static final int TYPE_MAP = 1;
    public static final int TYPE_OVERLAY = 3;

    // URL template fragments encoded as packed ints (written via ByteBuffer.writeUInt):
    // "=t&" + ("smaJ"|"spaM") + "=z&" + zoom + "=x&" + x + "=y&" + y
    private static final int URL_TYPE_PREFIX = 4027430;       // "=t&"
    private static final int URL_TYPE_OVERLAY = 1936548170;   // "smaJ"
    private static final int URL_TYPE_MAP = 1936744781;       // "spaM"
    private static final int URL_ZOOM_PREFIX = 4028966;       // "=z&"
    private static final int URL_X_PREFIX = 4028454;          // "=x&"
    private static final int URL_Y_PREFIX = 4028710;          // "=y&"

    public final int tileType;
    public final int zoomLevel;
    public final int tileX;
    public final int tileY;
    public final String tileUrl;

    public TileRequest(int tileType, int zoomLevel, int tileX, int tileY) {
        this.tileType = tileType;
        this.zoomLevel = zoomLevel;
        this.tileX = tileX;
        this.tileY = tileY;
        this.tileUrl = new ByteBuffer()
                .writeUInt(URL_TYPE_PREFIX)
                .writeUInt(tileType == TYPE_OVERLAY ? URL_TYPE_OVERLAY : URL_TYPE_MAP)
                .writeUInt(URL_ZOOM_PREFIX).writeIntAsString(zoomLevel)
                .writeUInt(URL_X_PREFIX).writeIntAsString(tileX)
                .writeUInt(URL_Y_PREFIX).writeIntAsString(tileY)
                .getStringAndClear();
    }

    public boolean equals(Object obj) {
        return obj != null && (obj instanceof TileRequest) && StringUtils.equals(this.tileUrl, ((TileRequest) obj).tileUrl);
    }

    public int hashCode() {
        return this.tileX ^ this.tileY;
    }
}
