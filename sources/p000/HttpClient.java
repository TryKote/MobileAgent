package p000;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

/* renamed from: ax */
/* loaded from: MobileAgent_3.9.jar:ax.class */
public final class HttpClient {

    /* renamed from: a */
    public Connection connection;

    /* renamed from: b */
    private InputStream inputStream;

    /* renamed from: c */
    private OutputStream outputStream;

    /* renamed from: d */
    private Account account;

    /* renamed from: e */
    private int requestType;

    /* renamed from: f */
    private int mockMode;

    /* renamed from: g */
    private int bytesSent;

    /* renamed from: h */
    private int bytesReceived;

    /* renamed from: i */
    private String url;

    /* renamed from: j */
    private ByteBuffer responseBuffer;

    /* renamed from: a */
    public static final HttpClient createHttpClient(String str, Account abstractC0037h, int i) throws IOException {
        return new HttpClient(str, abstractC0037h, i);
    }

    /* renamed from: a */
    public static final HttpClient createWithType3(Object obj) throws IOException {
        return createHttpClient((String) obj, (Account) null, 3);
    }

    /* renamed from: b */
    public static final HttpClient createWithType2(Object obj) throws IOException {
        return createHttpClient((String) obj, (Account) null, 2);
    }

    /* renamed from: a */
    public static final HttpClient createMockClient(String str) {
        return new HttpClient(str);
    }

    /* renamed from: a */
    public static final void closeAndUpdateStats(HttpClient c0024ax) {
        try {
            if (c0024ax.account != null && c0024ax.requestType == 0) {
                AppController.updateAccountStatus(c0024ax.account, c0024ax.bytesReceived);
                AppController.setAccountOption(c0024ax.account, c0024ax.bytesSent);
            } else if (c0024ax.requestType == 1) {
                AppController.addSentBytes(c0024ax.bytesReceived);
                AppController.addReceivedBytes(c0024ax.bytesSent);
            } else if (c0024ax.requestType == 2) {
                AppController.addDownloadBytes(c0024ax.bytesReceived);
                AppController.addUploadBytes(c0024ax.bytesSent);
            } else {
                AppController.addConnectionBytes(c0024ax.bytesReceived);
                AppController.addProtocolBytes(c0024ax.bytesSent);
            }
            if (c0024ax.mockMode == 0) {
                try {
                    if (c0024ax.inputStream != null) {
                        c0024ax.inputStream.close();
                    }
                } catch (Throwable unused) {
                }
                try {
                    if (c0024ax.outputStream != null) {
                        c0024ax.outputStream.close();
                    }
                } catch (Throwable unused2) {
                }
                try {
                    c0024ax.connection.close();
                } catch (Throwable unused3) {
                }
            }
        } catch (Throwable unused4) {
        }
    }

    private HttpClient(String str) {
        this.mockMode = 1;
        this.requestType = 2;
        this.url = str.startsWith(AppState.getString(459255)) ? StringUtils.suffix(str, 7) : str;
    }

    private HttpClient(String str, Account abstractC0037h, int i) throws IOException {
        this.connection = Connector.open(str, 3);
        this.account = abstractC0037h;
        this.requestType = i;
        this.url = str;
    }

    /* renamed from: a */
    public final int getResponseCode() throws IOException {
        if (this.mockMode != 0) {
            getOutputStream().flush();
            return Integer.parseInt(new String(readHeaders().data, 9, 3));
        }
        int responseCode = ((HttpConnection) this.connection).getResponseCode();
        this.bytesSent += this.url.length() + 127;
        this.bytesReceived += 255;
        return responseCode;
    }

    /* renamed from: b */
    public final void setRequestMethod(String str) throws IOException {
        ((HttpConnection) this.connection).setRequestMethod(str);
    }

    /* renamed from: a */
    public final HttpClient setRequestProperty(String str, String str2) throws IOException {
        if (this.mockMode == 0) {
            ((HttpConnection) this.connection).setRequestProperty(str, str2);
        } else {
            writeBuffer(new ByteBuffer().writeRawString(str).writeUInt(8250).writeRawString(str2).writeUInt(2573));
        }
        this.bytesSent += str.length() + str2.length() + 4;
        return this;
    }

    /* renamed from: a */
    public final HttpClient writeData(byte[] bArr, int i) throws IOException {
        if (i > 0) {
            getOutputStream().write(bArr, 0, i);
            this.bytesSent += i;
        }
        return this;
    }

    /* renamed from: a */
    public final int readData(byte[] bArr) throws IOException {
        int i = getInputStream().read(bArr);
        if (i > 0) {
            this.bytesReceived += i;
        }
        return i;
    }

    /* renamed from: c */
    private final InputStream getInputStream() throws IOException {
        if (this.inputStream != null) {
            return this.inputStream;
        }
        InputStream inputStreamOpenInputStream = ((HttpConnection) this.connection).openInputStream();
        this.inputStream = inputStreamOpenInputStream;
        return inputStreamOpenInputStream;
    }

    /* renamed from: d */
    private final OutputStream getOutputStream() throws IOException {
        if (this.outputStream != null) {
            return this.outputStream;
        }
        OutputStream outputStreamOpenOutputStream = ((HttpConnection) this.connection).openOutputStream();
        this.outputStream = outputStreamOpenOutputStream;
        return outputStreamOpenOutputStream;
    }

    /* renamed from: a */
    public final HttpClient writeBuffer(ByteBuffer c0043n) throws IOException {
        writeData(c0043n.data, c0043n.length);
        return this;
    }

    /* renamed from: a */
    public final HttpClient sendHttpRequest(int i, int i2, int i3) throws IOException {
        String str = this.url;
        this.connection = Connector.open(new ByteBuffer().writeCompressed(593549).writeRawString(StringUtils.prefix(str, str.indexOf(47))).getStringAndClear(), 3);
        writeBuffer(new ByteBuffer().writeUInt(i2).writeByte(32).writeRawString(StringUtils.suffix(str, str.indexOf(47))).writeCompressed(2951238).writeRawString(StringUtils.prefix(str, str.indexOf(58))).writeEncodedInt(i3).writeUInt(2573));
        if (i2 == 1414745936) {
            setRequestHeader(788628, 2164851);
            setRequestHeader(919726, 788668);
        }
        return setRequestProperty(AppState.getString(919712), StringUtils.intern(Integer.toString(i))).setRequestHeader(657608, 329938).writeBuffer(new ByteBuffer().writeUInt(2573));
    }

    /* renamed from: a */
    private final HttpClient setRequestHeader(int i, int i2) throws IOException {
        return setRequestProperty(AppState.getString(i), AppState.getString(i2));
    }

    /* renamed from: b */
    public final ByteBuffer readChunkedResponse() throws IOException, NumberFormatException {
        int i;
        ByteBuffer c0043nM645e = readHeaders();
        String str = new String(c0043nM645e.data, 0, c0043nM645e.length);
        int iIndexOf = StringUtils.intern(str.toLowerCase()).indexOf(AppState.getString(1052310)) + 16;
        int i2 = Integer.parseInt(StringUtils.substring(str, iIndexOf, str.indexOf(13, iIndexOf)));
        ByteBuffer c0043n = new ByteBuffer();
        byte[] bArrM1211a = NetworkUtils.newBytes(i2);
        do {
            i = getInputStream().read(bArrM1211a, 0, i2 - c0043n.length);
            if (i > 0) {
                this.bytesReceived += i;
            }
            if (i > 0) {
                c0043n.writeBytesAt(bArrM1211a, 0, i);
            }
            if (c0043n.length == i2) {
                break;
            }
        } while (i != -1);
        NetworkUtils.releaseBytes(bArrM1211a);
        return c0043n;
    }

    /* renamed from: e */
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
                    if (i == 34) {
                        return this.responseBuffer.compact();
                    }
                } else if (i2 == 13) {
                    i += 16;
                }
            }
        }
    }
}
