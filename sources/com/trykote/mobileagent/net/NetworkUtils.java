package com.trykote.mobileagent.net;


import com.trykote.mobileagent.util.ByteBuffer;

public final class NetworkUtils {

    public final int type;

    public final int port;

    public final String host;

    public String url;

    public final int status;

    public final String protocol;

    public NetworkUtils(ByteBuffer buffer) {
        this.type = buffer.readIntBE();
        this.port = buffer.readIntBE();
        this.host = buffer.readUTF8Str((String) null);
        this.url = buffer.readWideStr();
        this.status = buffer.readIntBE();
        this.protocol = buffer.readWideStr();
    }

    public NetworkUtils(int i, String str, int i2, String str2) {
        this.type = 1;
        this.port = i;
        this.host = str;
        this.url = null;
        this.status = i2;
        this.protocol = str2;
    }
}
