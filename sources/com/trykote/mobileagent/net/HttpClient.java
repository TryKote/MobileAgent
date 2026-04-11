package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.util.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

public final class HttpClient {

    private static final int REQUEST_HEADER_OVERHEAD = 127;
    private static final int RESPONSE_HEADER_OVERHEAD = 255;
    private static final int DOUBLE_CRLF_MARKER = 34;

    public Connection connection;

    private InputStream inputStream;

    private OutputStream outputStream;

    private Account account;

    private int requestType;

    private int mockMode;

    private int bytesSent;

    private int bytesReceived;

    private String url;

    private ByteBuffer responseBuffer;

    public static final HttpClient createHttpClient(String str, Account account, int i) throws IOException {
        return new HttpClient(str, account, i);
    }

    public static final HttpClient createWithType3(Object obj) throws IOException {
        return createHttpClient((String) obj, (Account) null, 3);
    }

    public static final HttpClient createWithType2(Object obj) throws IOException {
        return createHttpClient((String) obj, (Account) null, 2);
    }

    public static final HttpClient createMockClient(String str) {
        return new HttpClient(str);
    }

    public static final void closeAndUpdateStats(HttpClient client) {
        try {
            if (client.account != null && client.requestType == 0) {
                AccountManager.recordInboundTraffic(client.account, client.bytesReceived);
                AccountManager.recordOutboundTraffic(client.account, client.bytesSent);
            } else if (client.requestType == 1) {
                TrafficAccounting.addMmpInbound(client.bytesReceived);
                TrafficAccounting.addMmpOutbound(client.bytesSent);
            } else if (client.requestType == 2) {
                TrafficAccounting.addXmppInbound(client.bytesReceived);
                TrafficAccounting.addXmppOutbound(client.bytesSent);
            } else {
                TrafficAccounting.addHttpInbound(client.bytesReceived);
                TrafficAccounting.addHttpOutbound(client.bytesSent);
            }
            if (client.mockMode == 0) {
                try {
                    if (client.inputStream != null) {
                        client.inputStream.close();
                    }
                } catch (Throwable unused) {
                }
                try {
                    if (client.outputStream != null) {
                        client.outputStream.close();
                    }
                } catch (Throwable unused2) {
                }
                try {
                    client.connection.close();
                } catch (Throwable unused3) {
                }
            }
        } catch (Throwable unused4) {
        }
    }

    private HttpClient(String str) {
        this.mockMode = 1;
        this.requestType = 2;
        this.url = str.startsWith(Storage.resources().getString(PackedStringKeys.SCHEME_HTTP)) ? StringUtils.suffix(str, 7) : str;
    }

    private HttpClient(String str, Account account, int i) throws IOException {
        this.connection = Connector.open(str, 3);
        this.account = account;
        this.requestType = i;
        this.url = str;
    }

    public final int getResponseCode() throws IOException {
        if (this.mockMode != 0) {
            getOutputStream().flush();
            return Integer.parseInt(new String(readHeaders().data, 9, 3));
        }
        int responseCode = ((HttpConnection) this.connection).getResponseCode();
        this.bytesSent += this.url.length() + REQUEST_HEADER_OVERHEAD;
        this.bytesReceived += RESPONSE_HEADER_OVERHEAD;
        return responseCode;
    }

    public final void setRequestMethod(String str) throws IOException {
        ((HttpConnection) this.connection).setRequestMethod(str);
    }

    public final HttpClient setRequestProperty(String str, String str2) throws IOException {
        if (this.mockMode == 0) {
            ((HttpConnection) this.connection).setRequestProperty(str, str2);
        } else {
            writeBuffer(new ByteBuffer().writeRawString(str).writeUInt(8250).writeRawString(str2).writeUInt(2573));
        }
        this.bytesSent += str.length() + str2.length() + 4;
        return this;
    }

    public final HttpClient writeData(byte[] bArr, int i) throws IOException {
        if (i > 0) {
            getOutputStream().write(bArr, 0, i);
            this.bytesSent += i;
        }
        return this;
    }

    public final int readData(byte[] bArr) throws IOException {
        int i = getInputStream().read(bArr);
        if (i > 0) {
            this.bytesReceived += i;
        }
        return i;
    }

    private final InputStream getInputStream() throws IOException {
        if (this.inputStream != null) {
            return this.inputStream;
        }
        InputStream is = ((HttpConnection) this.connection).openInputStream();
        this.inputStream = is;
        return is;
    }

    private final OutputStream getOutputStream() throws IOException {
        if (this.outputStream != null) {
            return this.outputStream;
        }
        OutputStream os = ((HttpConnection) this.connection).openOutputStream();
        this.outputStream = os;
        return os;
    }

    public final HttpClient writeBuffer(ByteBuffer buffer) throws IOException {
        writeData(buffer.data, buffer.length);
        return this;
    }

    public final HttpClient sendHttpRequest(int i, int i2, int i3) throws IOException {
        String str = this.url;
        this.connection = Connector.open(new ByteBuffer().writeCompressed(PackedStringKeys.SCHEME_SOCKET).writeRawString(StringUtils.prefix(str, str.indexOf(47))).getStringAndClear(), 3);
        writeBuffer(new ByteBuffer().writeUInt(i2).writeByte(32).writeRawString(StringUtils.suffix(str, str.indexOf(47))).writeCompressed(PackedStringKeys.HTTP_REQUEST_HEADER).writeRawString(StringUtils.prefix(str, str.indexOf(58))).writeEncodedInt(i3).writeUInt(2573));
        if (i2 == 1414745936) {
            setRequestHeader(788628, 2164851);
            setRequestHeader(919726, 788668);
        }
        return setRequestProperty(Storage.resources().getString(PackedStringKeys.HEADER_CONTENT_LENGTH), StringUtils.intern(Integer.toString(i))).setRequestHeader(657608, 329938).writeBuffer(new ByteBuffer().writeUInt(2573));
    }

    private final HttpClient setRequestHeader(int i, int i2) throws IOException {
        return setRequestProperty(Storage.state().getString(i), Storage.state().getString(i2));
    }

    public final ByteBuffer readChunkedResponse() throws IOException, NumberFormatException {
        int i;
        ByteBuffer headerBuf = readHeaders();
        String str = new String(headerBuf.data, 0, headerBuf.length);
        int idx = StringUtils.intern(str.toLowerCase()).indexOf(Storage.resources().getString(PackedStringKeys.HEADER_CONTENT_LENGTH_LC)) + 16;
        int i2 = Integer.parseInt(StringUtils.substring(str, idx, str.indexOf(13, idx)));
        ByteBuffer buffer = new ByteBuffer();
        byte[] readBuf = ObjectPool.newBytes(i2);
        do {
            i = getInputStream().read(readBuf, 0, i2 - buffer.length);
            if (i > 0) {
                this.bytesReceived += i;
            }
            if (i > 0) {
                buffer.writeBytesAt(readBuf, 0, i);
            }
            if (buffer.length == i2) {
                break;
            }
        } while (i != -1);
        ObjectPool.releaseBytes(readBuf);
        return buffer;
    }

    private final ByteBuffer readHeaders() throws IOException {
        if (this.responseBuffer != null) {
            return this.responseBuffer.compact();
        }
        this.responseBuffer = new ByteBuffer();
        while (true) {
            int i = 0;
            while (true) {
                int i2 = getInputStream().read();
                if (i2 == -1) {
                    throw new EOFException(this.responseBuffer.getStringAndClear());
                }
                this.responseBuffer.writeByte(i2);
                this.bytesReceived++;
                if (i2 == 10) {
                    i++;
                    if (i == DOUBLE_CRLF_MARKER) {
                        return this.responseBuffer.compact();
                    }
                } else if (i2 == 13) {
                    i += 16;
                }
            }
        }
    }
}
