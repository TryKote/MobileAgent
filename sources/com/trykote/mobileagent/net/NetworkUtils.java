package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.StateKeys;
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
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import javax.microedition.io.StreamConnection;

/* renamed from: k */
/* loaded from: MobileAgent_3.9.jar:k.class */
public final class NetworkUtils {

    /* renamed from: a */
    public final int type;

    /* renamed from: b */
    public final int port;

    /* renamed from: c */
    public final String host;

    /* renamed from: d */
    public String url;

    /* renamed from: e */
    public final int status;

    /* renamed from: f */
    public final String protocol;

    /* renamed from: g */
    public static byte[][] bytePool;

    /* renamed from: h */
    public static StringBuffer[] bufferPool;

    /* renamed from: i */
    public static Vector[] vectorPool;

    /* renamed from: j */
    public static Hashtable stringCache;

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

    /* renamed from: a */
    public static final void checkCrashReport() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis > AppState.getLong(StateKeys.TIMESTAMP_LAST_CLEANUP) + 7776000000L) {
            AppState.setLong(StateKeys.TIMESTAMP_LAST_CLEANUP, jCurrentTimeMillis);
            new AsyncTask(18);
        }
    }

    /* renamed from: a */
    public static final void sendDiagnosticReport(String str) {
        HttpClient httpClient;
        byte[] outArr;
        int outIdx;
        int nextIdx;
        byte encodedByte;
        byte[] outArr2;
        int outIdx2;
        int nextIdx2;
        byte encodedByte2;
        int writePos;
        byte[] outputBuffer = newBytes(3000);
        try {
            Thread.sleep(1000L);
            System.gc();
            Thread.sleep(1000L);
            NetworkLock.acquireNetworkLock();
            if (str == null) {
                HttpClient diagClient = HttpClient.createWithType2((Object) new ByteBuffer().writeCompressed(1442705).writeCompressed(524308).writeCompressed(720924).getStringAndClear());
                httpClient = diagClient;
                if (diagClient.getResponseCode() == 200) {
                    Vector children = new ByteBuffer(httpClient).parseXmlStr().children;
                    XmlElement report = new XmlElement(103).setLongKeyAttr(103, AppState.getString(StateKeys.SESSION_KEY)).setLongKeyAttr(102, AppController.getAppVersion()).setLongKeyAttr(116, StringUtils.intern(Long.toString(Runtime.getRuntime().totalMemory()))).setLongKeyAttr(112, StringUtils.intern(Integer.toString(0))).setLongKeyAttr(115, StringUtils.intern(ResourceManager.booleanOf(false).toString()));
                    for (int i6 = 0; i6 < children.size(); i6++) {
                        XmlElement element = (XmlElement) children.elementAt(i6);
                        String tag = element.tagName;
                        String propName = StringUtils.fromBuffer(element.textContent);
                        if (startsWithChar(tag, 'p')) {
                            report.addChild(createDiagElement('p', propName, getSystemPropertySafe(propName)));
                        } else if (startsWithChar(tag, 'j')) {
                            report.addChild(createDiagElement('j', propName, getAppPropertySafe(propName)));
                        } else if (startsWithChar(tag, 'e')) {
                            report.addChild(createDiagElement('e', propName, (Object) classExists(propName)));
                        }
                    }
                    new AsyncTask(18, report.toString());
                }
            } else {
                ByteBuffer urlBuffer = new ByteBuffer().writeCompressed(131082);
                ByteBuffer dataBuffer = new ByteBuffer().writeUTFNoLen(str);
                for (int i7 = 0; i7 < dataBuffer.length; i7 += 600) {
                    int pos = i7;
                    int limit = Utils.min(pos + 600, dataBuffer.length);
                    byte[] base64Table = AppState.getBytes(StateKeys.RES_BASE64_TABLE);
                    int outPos = 0;
                    boolean z = true;
                    while (z) {
                        int byte1 = 0;
                        int byte2 = 0;
                        int byte3 = 0;
                        int byteCount = 0;
                        if (pos < limit) {
                            int idx = pos;
                            pos++;
                            byte1 = dataBuffer.data[idx] & 255;
                            byteCount = 0 + 1;
                        }
                        if (pos < limit) {
                            int idx = pos;
                            pos++;
                            byte2 = dataBuffer.data[idx] & 255;
                            byteCount++;
                        }
                        if (pos < limit) {
                            int idx = pos;
                            pos++;
                            byte3 = dataBuffer.data[idx] & 255;
                            byteCount++;
                        } else {
                            z = false;
                        }
                        if (byteCount > 0) {
                            int triplet = (byte1 << 16) | (byte2 << 8) | byte3;
                            int sextet0 = (triplet >> 18) & 63;
                            if (sextet0 < 62) {
                                outArr = outputBuffer;
                                outIdx = outPos;
                                nextIdx = outPos + 1;
                                encodedByte = base64Table[sextet0];
                            } else {
                                int curIdx = outPos;
                                int nextPos = outPos + 1;
                                outputBuffer[curIdx] = 37;
                                int nextPos2 = nextPos + 1;
                                outputBuffer[nextPos] = 50;
                                outArr = outputBuffer;
                                outIdx = nextPos2;
                                nextIdx = nextPos2 + 1;
                                encodedByte = sextet0 == 62 ? (byte) 66 : (byte) 70;
                            }
                            outArr[outIdx] = encodedByte;
                            int sextet1 = (triplet >> 12) & 63;
                            if (sextet1 < 62) {
                                outArr2 = outputBuffer;
                                outIdx2 = nextIdx;
                                nextIdx2 = nextIdx + 1;
                                encodedByte2 = base64Table[sextet1];
                            } else {
                                int curIdx = nextIdx;
                                int nextPos = nextIdx + 1;
                                outputBuffer[curIdx] = 37;
                                int nextPos2 = nextPos + 1;
                                outputBuffer[nextPos] = 50;
                                outArr2 = outputBuffer;
                                outIdx2 = nextPos2;
                                nextIdx2 = nextPos2 + 1;
                                encodedByte2 = sextet1 == 62 ? (byte) 66 : (byte) 70;
                            }
                            outArr2[outIdx2] = encodedByte2;
                            if (byteCount > 1) {
                                int sextet2 = (triplet >> 6) & 63;
                                if (sextet2 < 62) {
                                    int curIdx = nextIdx2;
                                    writePos = nextIdx2 + 1;
                                    outputBuffer[curIdx] = base64Table[sextet2];
                                } else {
                                    int curIdx = nextIdx2;
                                    int nextPos = nextIdx2 + 1;
                                    outputBuffer[curIdx] = 37;
                                    int nextPos2 = nextPos + 1;
                                    outputBuffer[nextPos] = 50;
                                    writePos = nextPos2 + 1;
                                    outputBuffer[nextPos2] = sextet2 == 62 ? (byte) 66 : (byte) 70;
                                }
                            } else {
                                int curIdx = nextIdx2;
                                int nextPos = nextIdx2 + 1;
                                outputBuffer[curIdx] = 37;
                                int nextPos2 = nextPos + 1;
                                outputBuffer[nextPos] = 51;
                                writePos = nextPos2 + 1;
                                outputBuffer[nextPos2] = 68;
                            }
                            if (byteCount > 2) {
                                int sextet3 = triplet & 63;
                                if (sextet3 < 62) {
                                    int curIdx = writePos;
                                    outPos = writePos + 1;
                                    outputBuffer[curIdx] = base64Table[sextet3];
                                } else {
                                    int curIdx = writePos;
                                    int nextPos = writePos + 1;
                                    outputBuffer[curIdx] = 37;
                                    int nextPos2 = nextPos + 1;
                                    outputBuffer[nextPos] = 50;
                                    outPos = nextPos2 + 1;
                                    outputBuffer[nextPos2] = sextet3 == 62 ? (byte) 66 : (byte) 70;
                                }
                            } else {
                                int curIdx = writePos;
                                int nextPos = writePos + 1;
                                outputBuffer[curIdx] = 37;
                                int nextPos2 = nextPos + 1;
                                outputBuffer[nextPos] = 51;
                                outPos = nextPos2 + 1;
                                outputBuffer[nextPos2] = 68;
                            }
                        }
                    }
                    urlBuffer.writeBytesAt(outputBuffer, 0, outPos);
                }
                dataBuffer.clear();
                HttpClient uploadClient = HttpClient.createMockClient(new ByteBuffer().writeCompressed(1311655).writeCompressed(524300).writeCompressed(720924).getStringAndClear());
                httpClient = uploadClient;
                uploadClient.sendHttpRequest(urlBuffer.length, 1414745936, 1038).writeBuffer(urlBuffer).getResponseCode();
            }
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
            releaseBytes(outputBuffer);
        } catch (RuntimeException th) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            releaseBytes(outputBuffer);
            throw th;
        } catch (Throwable th) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            releaseBytes(outputBuffer);
            throw new RuntimeException(th);
        }
    }

    /* renamed from: a */
    private static final XmlElement createDiagElement(char c, String str, Object obj) {
        return new XmlElement(c).setLongKeyAttr(110, str).appendText(obj);
    }

    /* renamed from: a */
    private static final boolean startsWithChar(String str, char c) {
        return str.charAt(0) == c;
    }

    /* renamed from: c */
    private static final Boolean classExists(String str) {
        try {
            Class.forName(str);
            return ResourceManager.boolTrue;
        } catch (Throwable unused) {
            return ResourceManager.boolFalse;
        }
    }

    /* renamed from: d */
    private static final Object getSystemPropertySafe(String str) {
        String result = null;
        try {
            result = StringUtils.intern(System.getProperty(str));
            return result;
        } catch (Throwable th) {
            return result;
        }
    }

    /* renamed from: e */
    private static final Object getAppPropertySafe(String str) {
        String result = null;
        try {
            result = StringUtils.intern(AppState.getMidlet().getAppProperty(str));
            return result;
        } catch (Throwable th) {
            return result;
        }
    }

    /* renamed from: a */
    public static final int handleRegSubmit(Object[] objArr) {
        AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
        String statusCode = (String) objArr[20];
        if (statusCode != null) {
            if (Utils.parseInt((Object) statusCode) == 0) {
                String email = (String) objArr[7];
                int errorCode = AccountManager.validateCredentials(0, (Account) null, email, (String) objArr[9]);
                if (0 != errorCode) {
                    return NotificationHelper.showError(errorCode);
                }
                AccountManager.setCurrentAccount(AccountManager.createAccount(0, email));
                return 4;
            }
            if (Utils.parseInt((Object) statusCode) == 4004) {
                objArr[21] = ResourceManager.integerOf(-1);
            }
        }
        AppState.pool[StateKeys.OBJ_REGISTRATION_DATA] = objArr;
        return 164;
    }

    /* renamed from: b */
    public static final void processRegForm() {
        String domain;
        Object[] objArr = (Object[]) AppState.pool[StateKeys.OBJ_REGISTRATION_DATA];
        AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
        String email = (String) objArr[7];
        String login = email;
        int atIndex = email.indexOf(64);
        if (atIndex >= 0) {
            domain = StringUtils.suffix(login, atIndex);
            login = StringUtils.prefix(login, atIndex);
        } else {
            domain = AppState.emptyStr;
        }
        int i = 0;
        Vector domains = Utils.splitNonEmpty(AppState.getString(StateKeys.STR_DOMAIN_LIST), (char) 0);
        int size = domains.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else if (StringUtils.equalsObj(domain, domains.elementAt(size))) {
                i = size;
            }
        }
        releaseVector(domains);
        AppState.pool[StateKeys.SLOT_MAP_SEARCH_QUERY] = objArr[3];
        AppState.pool[StateKeys.STR_MAP_LOCATION_NAME] = objArr[4];
        AppState.pool[StateKeys.STR_MAP_LOCATION_URL] = objArr[5];
        AppState.pool[StateKeys.SLOT_CHAT_NAME] = login;
        AppState.setInt(StateKeys.INT_SERVER_INDEX, i);
        AppState.pool[StateKeys.SLOT_PASSWORD] = objArr[9];
        AppState.pool[StateKeys.SLOT_SCREEN_TITLE] = objArr[10];
        AppState.setInt(StateKeys.INT_SETTINGS_THEME, ((Integer) objArr[11]).intValue());
        AppState.pool[StateKeys.SLOT_DEVICE_ID] = objArr[12];
        AppState.pool[StateKeys.SLOT_APP_VERSION_STRING] = objArr[13];
        AppState.pool[StateKeys.SLOT_FIRST_NAME] = objArr[14];
        AppState.pool[StateKeys.SLOT_LAST_NAME] = objArr[15];
        AppState.setInt(StateKeys.INT_SEARCH_GENDER, ((Integer) objArr[16]).intValue());
        AppState.setInt(StateKeys.INT_SEARCH_AGE, ((Integer) objArr[17]).intValue());
        AppState.setInt(StateKeys.INT_REG_DOMAIN_INDEX, ((Integer) objArr[18]).intValue());
        AppState.setInt(StateKeys.INT_COUNTRY_CODE, ((Integer) objArr[19]).intValue());
        AppState.pool[StateKeys.SLOT_DISPLAY_NAME] = objArr[20];
        AppState.setInt(StateKeys.INT_REGION_CODE, ((Integer) objArr[21]).intValue());
        ScreenManager.showScreen(ScreenManager.createScreen(4399));
        String statusStr = AppState.getString(StateKeys.SLOT_DISPLAY_NAME);
        if (statusStr == null) {
            RemoteLogger.log("NET", "triggering refreshContactList from NetworkUtils");
            AppController.refreshContactList();
            return;
        }
        int statusCode = Utils.parseInt((Object) statusStr);
        int messageId = statusCode == 78 ? 818 : statusCode == 101 ? 819 : statusCode == 114 ? 820 : statusCode == 150 ? 821 : statusCode == 152 ? 822 : statusCode == 154 ? 823 : statusCode == 155 ? 824 : statusCode == 175 ? 825 : statusCode == 555 ? 826 : statusCode == 573 ? 827 : statusCode == 4003 ? 828 : statusCode == 4004 ? 829 : statusCode == 5005 ? 830 : 831;
        int msgIdx = messageId;
        String message = AppState.getString(messageId);
        NotificationHelper.showNotification(msgIdx != 831 ? message : new StringBuffer().append(message).append(statusCode).toString());
    }

    /* renamed from: c */
    public static final void closeAllConnections() {
        RemoteLogger.log("NET", "closeAllConnections");
        Vector connections = AppState.getVector(StateKeys.SLOT_MAP_TILE_REQUEST);
        int size = connections.size();
        while (true) {
            size--;
            if (size < 0) {
                return;
            } else {
                closeConnectionImpl((Object[]) connections.elementAt(size), true);
            }
        }
    }

    /* renamed from: b */
    public static final void closeConnection(Object[] objArr) {
        RemoteLogger.log("NET", "closeConnection");
        closeConnectionImpl(objArr, false);
    }

    /* renamed from: a */
    private static final void closeConnectionImpl(Object[] objArr, boolean z) {
        if (objArr != null) {
            IOUtils.closeInput((InputStream) objArr[1]);
            IOUtils.closeOutput((OutputStream) objArr[2]);
            Connection connection = (Connection) objArr[0];
            if (connection == null || z) {
                IOUtils.closeConn(connection);
            } else {
                new AsyncTask(7, connection);
            }
            objArr[0] = null;
            objArr[1] = null;
            objArr[2] = null;
            Utils.removeFrom(AppState.getVector(StateKeys.SLOT_MAP_TILE_REQUEST), objArr);
        }
    }

    /* renamed from: a */
    public static final Object[] openSocket(String str, boolean z) throws IOException {
        Object[] objArr = new Object[z ? 5 : 3];
        try {
            SocketConnection socketConnection = (SocketConnection) IOUtils.registerResource((Object) Connector.open(str, 3));
            objArr[0] = socketConnection;
            try {
                if (socketConnection instanceof SocketConnection) {
                    byte b = 5;
                    while (true) {
                        byte b2 = (byte) (b - 1);
                        b = b2;
                        if (b2 < 2) {
                            break;
                        }
                        SocketConnection socketConnection2 = socketConnection;
                        try {
                            int optionValue = AppState.getInt(b + 107);
                            if (optionValue >= 0) {
                                socketConnection2.setSocketOption(b, optionValue);
                            }
                        } catch (Throwable unused) {
                        }
                    }
                }
            } catch (Throwable unused2) {
            }
            objArr[1] = IOUtils.registerResource((Object) socketConnection.openInputStream());
            objArr[2] = IOUtils.registerResource((Object) socketConnection.openOutputStream());
            if (z) {
                objArr[4] = new ByteBuffer();
                new AsyncTask(4, objArr);
            }
            AppState.getVector(StateKeys.SLOT_MAP_TILE_REQUEST).addElement(objArr);
            return objArr;
        } catch (Throwable th) {
            closeConnectionImpl(objArr, true);
            throw th;
        }
    }

    /* renamed from: f */
    private static boolean isAsyncSocket(Object[] objArr) {
        return objArr.length > 3;
    }

    /* renamed from: c */
    public static final int availableBytes(Object[] objArr) throws IOException {
        if (!isAsyncSocket(objArr)) {
            return ((InputStream) objArr[1]).available();
        }
        synchronized (objArr) {
            int i = ((ByteBuffer) objArr[4]).length;
            if (i > 0) {
                return i;
            }
            Throwable th = (Throwable) objArr[3];
            if (th != null) {
                if (th instanceof IOException) throw (IOException) th;
                if (th instanceof RuntimeException) throw (RuntimeException) th;
                if (th instanceof Error) throw (Error) th;
                throw new RuntimeException(th);
            }
            return 0;
        }
    }

    /* renamed from: a */
    public static final void writeSocket(Object[] objArr, byte[] bArr, int i) throws IOException {
        ((OutputStream) objArr[2]).write(bArr, 0, i);
        ((OutputStream) objArr[2]).flush();
    }

    /* renamed from: a */
    public static final int readSocket(Object[] objArr, byte[] bArr, int i, int i2) throws IOException {
        if (!isAsyncSocket(objArr)) {
            return ((InputStream) objArr[1]).read(bArr, i, i2);
        }
        synchronized (objArr) {
            ((ByteBuffer) objArr[4]).readInto(bArr, i, i2);
        }
        return i2;
    }

    /* renamed from: a */
    private static final int readWithTimeout(Object[] objArr, byte[] bArr) throws IOException {
        long jCurrentTimeMillis = System.currentTimeMillis();
        try {
            return ((InputStream) objArr[1]).read(bArr);
        } catch (IOException th) {
            long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
            if (jCurrentTimeMillis2 >= 50000 && jCurrentTimeMillis2 <= 70000) {
                return 0;
            }
            throw th;
        } catch (RuntimeException th) {
            long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
            if (jCurrentTimeMillis2 >= 50000 && jCurrentTimeMillis2 <= 70000) {
                return 0;
            }
            throw th;
        } catch (Throwable th) {
            long jCurrentTimeMillis2 = System.currentTimeMillis() - jCurrentTimeMillis;
            if (jCurrentTimeMillis2 >= 50000 && jCurrentTimeMillis2 <= 70000) {
                return 0;
            }
            throw new RuntimeException(th);
        }
    }

    /* renamed from: d */
    public static final void asyncReaderLoop(Object[] objArr) {
        int bytesRead;
        byte[] bArr = new byte[1024];
        do {
            try {
                bytesRead = readWithTimeout(objArr, bArr);
                if (bytesRead > 0) {
                    synchronized (objArr) {
                        ((ByteBuffer) objArr[4]).writeBytesAt(bArr, 0, bytesRead);
                    }
                }
                if (bytesRead < 1024) {
                    Thread.sleep(100L);
                }
            } catch (Throwable th) {
                try {
                    Thread.sleep(3000);
                } catch (Throwable unused) {
                }
                objArr[3] = th;
                releaseBytes(bArr);
                return;
            }
        } while (bytesRead >= 0);
        throw new RuntimeException(new EOFException());
    }

    /* renamed from: a */
    public static final Screen addContactItems(Screen screen, Vector vector) {
        MenuItem menuItem;
        int count = Utils.vectorSize(vector);
        for (int i = 0; i < count; i++) {
            Object item = vector.elementAt(i);
            if (item instanceof Contact) {
                menuItem = ((Contact) item).createMenuItem();
            } else if (item instanceof ContactGroup) {
                menuItem = ((ContactGroup) item).createMenuItem(-1);
            } else if (item instanceof ContactInfo) {
                ContactInfo contactInfo = (ContactInfo) item;
                if (contactInfo.getAccount() instanceof MrimAccount) {
                    MenuItem entry = MenuItem.createDefault().setIcon(AppController.handleServerAction(Utils.parseIntBounded(contactInfo.getString(10), 0, 4, 0), contactInfo.getString(12))).addText(Utils.withComma(contactInfo.getDisplayName()), 1, 0).setLabel(contactInfo.getString(3));
                    entry.data = contactInfo;
                    menuItem = entry;
                } else {
                    MenuItem entry = MenuItem.createDefault();
                    int gender = Utils.parseInt((Object) contactInfo.getString(61));
                    MenuItem entry2 = entry.setIcon(gender == 0 ? 255 : gender == 1 ? 256 : 263).setLabel(Utils.appendSpace(contactInfo.getString(60))).addText(Utils.withComma(contactInfo.getDisplayName()), 1, 0).setLabel(StringUtils.concat(Utils.appendSpace(contactInfo.getFirstName()), contactInfo.getLastName()));
                    entry2.data = contactInfo;
                    menuItem = entry2;
                }
            } else {
                menuItem = ((Account) item).createMenuItem();
            }
            screen.addItem(menuItem);
        }
        return screen;
    }

    /* renamed from: a */
    private static final int processFormField(int i, Object obj) {
        int nextIdx;
        int idx = i + 1;
        switch (AppState.getInt(i)) {
            case 1:
                idx += 2;
                break;
            case 2:
                AppState.setBool(AppState.getInt(idx + 1), ((Boolean) obj).booleanValue());
                idx += 2;
                break;
            case 3:
                AppState.setIntInd(idx + 2, ((Integer) ((Object[]) obj)[0]).intValue());
                idx += 3;
                break;
            case 4:
                idx++;
                break;
            case 5:
                int baseIdx = idx + 3;
                String value = (String) ((Object[]) obj)[0];
                int curIdx = baseIdx + 1;
                if (AppState.getInt(baseIdx) == 2) {
                    int minIdx = curIdx + 1;
                    int minValue = AppState.getInt(curIdx);
                    int maxIdx = minIdx + 1;
                    int maxValue = AppState.getInt(minIdx);
                    int defIdx = maxIdx + 1;
                    int parsedValue = Utils.parseIntBounded(value, minValue, maxValue, AppState.getInt(maxIdx));
                    nextIdx = defIdx + 1;
                    AppState.setIntInd(defIdx, parsedValue);
                } else {
                    nextIdx = curIdx + 1;
                    AppState.setStringInd(curIdx, value);
                }
                idx += nextIdx - idx;
                break;
            case 6:
                idx++;
                break;
            case 7:
            case 8:
                idx += 3;
                break;
            case 9:
                AppState.setStringInd(idx + 1, ((String[]) obj)[1]);
                idx += 2;
                break;
            case 10:
                AppState.setStringInd(idx, (String) obj);
                idx++;
                break;
            case 11:
                idx++;
                break;
            case 12:
                processFormField(AppState.getInt(idx), obj);
                idx++;
                break;
        }
        return idx;
    }

    /* renamed from: d */
    public static final int processScreenForm() {
        Screen screen = ScreenManager.getCurrentScreen();
        int i = screen.screenFlags + 9;
        Vector items = screen.menuItems;
        int fieldIdx = i + 1;
        int fieldCount = AppState.getInt(i);
        for (int i2 = 0; i2 < fieldCount; i2++) {
            fieldIdx = processFormField(fieldIdx, ((MenuItem) items.elementAt(i2)).data);
        }
        return 0;
    }

    /* renamed from: e */
    public static final StringBuffer getMessageBuffer() {
        StringBuffer sb = newStringBuffer();
        String prefix = Utils.defaultStr(AppState.getString(StateKeys.SLOT_STATUS_TEXT));
        StringBuffer result = sb.append(prefix);
        int length = prefix.length();
        if (length != 0 && prefix.charAt(length - 1) != ' ') {
            result.append(' ');
        }
        return result;
    }

    /* renamed from: a */
    public static final void showAlertBuffer(int i, StringBuffer stringBuffer) {
        AppState.setInt(StateKeys.INT_HTTP_RESULT_SCREEN, i);
        AppState.setFromBuffer(StateKeys.SLOT_MAP_POINT_1, stringBuffer);
        ScreenManager.showScreen(ScreenManager.createScreen(4485));
        AppState.clearIndex(StateKeys.SLOT_MAP_POINT_1);
    }

    /* renamed from: a */
    public static final void showAlertById(int i, int i2) {
        AppState.setInt(StateKeys.INT_HTTP_RESULT_SCREEN, i);
        AppState.setFromPool(StateKeys.SLOT_MAP_POINT_1, i2);
        ScreenManager.showScreen(ScreenManager.createScreen(4485));
        AppState.clearIndex(StateKeys.SLOT_MAP_POINT_1);
    }

    /* renamed from: a */
    public static final void showErrorOrConfirm(int i, int i2, int i3) {
        if (i3 != 0) {
            NotificationHelper.showMessageById(i3);
        } else {
            showConfirmDialog(i, i2);
        }
    }

    /* renamed from: b */
    public static final void showConfirmDialog(int i, int i2) {
        AppState.setInt(StateKeys.INT_HTTP_PARAM_1, i);
        AppState.setInt(StateKeys.INT_HTTP_PARAM_2, i2);
        ScreenManager.showScreen(ScreenManager.createScreen(4497));
    }

    /* renamed from: f */
    public static final int getIconOffset() {
        return AppState.getBool(StateKeys.SETTING_FAST_CONNECTION) ? 10 : 55;
    }

    /* renamed from: a */
    public static final void createSingleContact(MrimAccount account, int i, ByteBuffer buffer) {
        ContactInfo contactInfo = ContactInfo.createForAccount(account);
        switch (i) {
            case 0:
                contactInfo.setContactName(AppState.getString(StateKeys.STR_DEFAULT_CONTACT_NAME));
                break;
            case 1:
                contactInfo = (ContactInfo) parseMrimContacts(account, buffer).elementAt(0);
                break;
            default:
                contactInfo.setContactName(bufToStringCached(newStringBuffer().append(AppState.getString(StateKeys.STR_CONTACT_NAME_PREFIX)).append(i)));
                break;
        }
        AppState.pool[StateKeys.SLOT_REG_PARAM_1] = contactInfo;
    }

    /* renamed from: b */
    public static final void parseContactInfoResponse(MrimAccount account, int i, ByteBuffer buffer) {
        int nameIndex = 0;
        Vector contacts = null;
        switch (i) {
            case 0:
                nameIndex = 913;
                break;
            case 1:
                contacts = parseMrimContacts(account, buffer);
                break;
            default:
                nameIndex = 914;
                break;
        }
        AppState.setInt(StateKeys.INT_ERROR_MSG_INDEX, nameIndex);
        AppState.pool[StateKeys.SLOT_REG_PARAM_4] = contacts;
    }

    /* renamed from: c */
    public static final void updateContactName(MrimAccount account, int i, ByteBuffer buffer) {
        int nameIndex;
        switch (i) {
            case 0:
                nameIndex = 913;
                break;
            case 1:
                ContactInfo contactInfo = (ContactInfo) parseMrimContacts(account, buffer).elementAt(0);
                MrimContact contact = (MrimContact) account.contactMap.get(contactInfo.getEmailOrMmpId());
                if (null != contact) {
                    contact.setDisplayName(contactInfo.getFullName());
                    return;
                }
                return;
            default:
                nameIndex = 914;
                break;
        }
        IOUtils.postNotification(AppState.getString(nameIndex));
    }

    /* renamed from: d */
    public static final void addContactToGroup(MrimAccount account, int i, ByteBuffer buffer) {
        if (i == 1) {
            ContactInfo contactInfo = (ContactInfo) parseMrimContacts(account, buffer).elementAt(0);
            Hashtable contactMap = account.contactMap;
            String email = contactInfo.getEmailOrMmpId();
            MrimContact contact = (MrimContact) contactMap.get(email);
            if (null != contact) {
                String fullName = contactInfo.getFullName();
                contact.setDisplayName(fullName);
                account.validateGroupAdd(email, fullName, AppState.getString(StateKeys.STR_DEFAULT_GROUP_NAME), (ContactGroup) account.getFirstContactGroup(), true);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x0106 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0114 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0123 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0132 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0141 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0173 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0181 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x018f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x019e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:62:0x01ac A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01b5 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x01be A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x01cc A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x01da A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:67:0x01e8 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x01f5 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0203 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0212 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x00f8 A[SYNTHETIC] */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static final Vector parseMrimContacts(MrimAccount account, ByteBuffer buffer) {
        Vector result = newVector();
        Vector fieldNames = Utils.splitByNull(AppState.getString(StateKeys.STR_REG_FIELD_NAMES));
        int fieldCount = buffer.readInt();
        int contactCount = buffer.readInt();
        buffer.readInt();
        Vector fieldTypes = newVector();
        for (int i = 0; i < fieldCount; i++) {
            fieldTypes.addElement(buffer.readHexStr());
        }
        for (int i2 = 0; i2 < contactCount && buffer.length > 0; i2++) {
            ContactInfo contactInfo = ContactInfo.createForAccount(account);
            result.addElement(contactInfo);
            int i3 = 0;
            while (i3 < fieldCount) {
                int i4 = i3;
                i3++;
                String fieldType = (String) fieldTypes.elementAt(i4);
                int fieldIdx = Utils.vectorSize(fieldNames);
                do {
                    fieldIdx--;
                    if (fieldIdx < 0) {
                    }
                    switch (fieldIdx) {
                        case 0:
                            contactInfo.setPhoneHome(buffer.readWideStr());
                            break;
                        case 1:
                            contactInfo.setPhoneMobile(buffer.readWideStr());
                            break;
                        case 2:
                            contactInfo.setDisplayName(buffer.readUTF8Str((String) null));
                            break;
                        case 3:
                            contactInfo.setFirstName(buffer.readUTF8Str((String) null));
                            break;
                        case 4:
                            contactInfo.setLastName(buffer.readUTF8Str((String) null));
                            break;
                        case 5:
                            int maritalStatus = Utils.parseIntBounded(buffer.readWideStr(), 1, 2, 0);
                            if (1 == maritalStatus) {
                                contactInfo.setMaritalMarried();
                                break;
                            } else if (2 == maritalStatus) {
                                contactInfo.setMaritalSingle();
                                break;
                            } else {
                                break;
                            }
                        case 6:
                            contactInfo.setCompany(buffer.readWideStr());
                            break;
                        case 7:
                            contactInfo.setWebsite(buffer.readWideStr());
                            break;
                        case 8:
                            contactInfo.setWorkPhone(buffer.readUTF8Str((String) null));
                            break;
                        case 9:
                            contactInfo.setBirthdayMonth(buffer.readWideStr());
                            break;
                        case 10:
                            buffer.readWideStr();
                            break;
                        case 11:
                            buffer.readWideStr();
                            break;
                        case 12:
                            contactInfo.setAltEmail(buffer.readWideStr());
                            break;
                        case 13:
                            contactInfo.setJobTitle(buffer.readWideStr());
                            break;
                        case 14:
                            contactInfo.setLocation(buffer.readWideStr());
                            break;
                        case 15:
                            contactInfo.setIconId(buffer.readWideStr());
                            break;
                        case 16:
                            contactInfo.setIconName(buffer.readUTF8Str((String) null));
                            break;
                        case 17:
                            contactInfo.setDescription(buffer.readUTF8Str((String) null));
                            break;
                        default:
                            buffer.readWideStr();
                            break;
                    }
                } while (!StringUtils.equals(fieldType, (String) fieldNames.elementAt(fieldIdx)));
                switch (fieldIdx) {
                }
            }
            contactInfo.setEmailAddress(bufToStringCached(newStringBuffer().append(contactInfo.getString(50)).append('@').append(contactInfo.getString(51))));
        }
        releaseVector(fieldNames);
        releaseVector(fieldTypes);
        return result;
    }

    /* renamed from: f */
    private static String getCachedString(String str) {
        String str2 = (String) stringCache.get(str);
        return str2 != null ? str2 : StringUtils.intern(str);
    }

    /* renamed from: b */
    public static final void cacheString(String str) {
        stringCache.put(str, str);
    }

    /* renamed from: a */
    public static final void releaseBytes(byte[] bArr) {
        if (bArr == null || bArr.length > 2048 || bArr.length <= 8) {
            return;
        }
        byte[][] bArr2 = bytePool;
        synchronized (bArr2) {
            int i = 0;
            while (i < 20) {
                if (bArr2[i] == null) {
                    break;
                } else {
                    i++;
                }
            }
            if (i == 20) {
                Utils.arraycopy(bArr2, 1, bArr2, 0, 19);
                i--;
            }
            bArr2[i] = bArr;
        }
    }

    /* renamed from: a */
    public static final byte[] allocBytes(byte[] bArr, int i) {
        int length;
        if (i > 2048) {
            return null;
        }
        byte[][] bArr2 = bytePool;
        synchronized (bArr2) {
            byte[] bArr3 = null;
            int i2 = Integer.MAX_VALUE;
            int i3 = 0;
            for (int i4 = 0; i4 < 20; i4++) {
                byte[] bArr4 = bArr2[i4];
                if (bArr4 != null && (length = bArr4.length) >= i && length < i2) {
                    bArr3 = bArr4;
                    i2 = length;
                    i3 = i4;
                }
            }
            if (bArr3 == null) {
                return null;
            }
            Utils.arraycopy((Object) bArr, 0, (Object) bArr3, 0, i);
            if (i3 != 19) {
                Utils.arraycopy(bArr2, i3 + 1, bArr2, i3, 19 - i3);
            }
            bArr2[19] = null;
            releaseBytes(bArr);
            return bArr3;
        }
    }

    /* renamed from: a */
    public static final byte[] newBytes(int i) {
        if (i > 2048) {
            return new byte[i];
        }
        byte[][] bArr = bytePool;
        synchronized (bArr) {
            for (int i2 = 0; i2 < 20; i2++) {
                byte[] bArr2 = bArr[i2];
                if (bArr2 != null && bArr2.length >= i) {
                    int length = bArr2.length;
                    while (true) {
                        length--;
                        if (length < 0) {
                            Utils.arraycopy(bArr, i2 + 1, bArr, i2, 19 - i2);
                            bArr[19] = null;
                            return bArr2;
                        }
                        bArr2[length] = 0;
                    }
                }
            }
            return new byte[i];
        }
    }

    /* renamed from: a */
    public static final void releaseVector(Vector vector) {
        if (vector != null) {
            vector.removeAllElements();
            Utils.trimIfEmpty(vector);
            Vector[] vectorArr = vectorPool;
            synchronized (vectorArr) {
                for (int i = 0; i < 5; i++) {
                    if (vectorArr[i] == null) {
                        vectorArr[i] = vector;
                        return;
                    }
                }
            }
        }
    }

    /* renamed from: g */
    public static final Vector newVector() {
        Vector[] vectorArr = vectorPool;
        synchronized (vectorArr) {
            for (int i = 0; i < 5; i++) {
                Vector vector = vectorArr[i];
                if (vector != null) {
                    Utils.arraycopy(vectorArr, i + 1, vectorArr, i, 4 - i);
                    vectorArr[4] = null;
                    return vector;
                }
            }
            return new Vector();
        }
    }

    /* renamed from: a */
    public static final String bufToString(StringBuffer stringBuffer, boolean z) {
        if (z) {
            return bufToStringCached(stringBuffer);
        }
        String result = getCachedString(stringBuffer.toString());
        stringBuffer.setLength(0);
        return result;
    }

    /* renamed from: a */
    public static final String bufToStringCached(StringBuffer stringBuffer) {
        String result = getCachedString(stringBuffer.toString());
        stringBuffer.setLength(0);
        StringBuffer[] stringBufferArr = bufferPool;
        synchronized (stringBufferArr) {
            int i = 0;
            while (true) {
                if (i >= 5) {
                    break;
                }
                if (stringBufferArr[i] == null) {
                    stringBufferArr[i] = stringBuffer;
                    break;
                }
                i++;
            }
        }
        return result;
    }

    /* renamed from: b */
    public static final StringBuffer appendFromState(int i) {
        return newStringBuffer().append(AppState.getString(i));
    }

    /* renamed from: h */
    public static final StringBuffer newStringBuffer() {
        StringBuffer[] stringBufferArr = bufferPool;
        synchronized (stringBufferArr) {
            int i = 0;
            do {
                StringBuffer stringBuffer = stringBufferArr[i];
                if (stringBuffer != null) {
                    Utils.arraycopy(stringBufferArr, i + 1, stringBufferArr, i, 4 - i);
                    stringBufferArr[4] = null;
                    return stringBuffer;
                }
                i++;
            } while (i != 5);
            return new StringBuffer();
        }
    }

    /* renamed from: b */
    public static final String bytesToString(byte[] bArr) {
        StringBuffer[] stringBufferArr = bufferPool;
        synchronized (stringBufferArr) {
            int i = 0;
            do {
                StringBuffer stringBuffer = stringBufferArr[i];
                if (stringBuffer != null) {
                    return bytesToCyrillic(bArr, stringBuffer, false);
                }
                i++;
            } while (i != 5);
            return bytesToCyrillic(bArr, new StringBuffer(), true);
        }
    }

    /* renamed from: a */
    private static final String bytesToCyrillic(byte[] bArr, StringBuffer stringBuffer, boolean z) {
        for (byte b : bArr) {
            stringBuffer.append(Utils.win1251ToChar((int) b));
        }
        return bufToString(stringBuffer, z);
    }

    /* renamed from: a */
    private static final String longToChars(long j, StringBuffer stringBuffer, boolean z) {
        while (j != 0) {
            stringBuffer.append((char) (j & 255));
            j >>>= 8;
        }
        return bufToString(stringBuffer, z);
    }

    /* renamed from: a */
    public static final String longToHex(long j) {
        StringBuffer[] stringBufferArr = bufferPool;
        synchronized (stringBufferArr) {
            int i = 0;
            do {
                StringBuffer stringBuffer = stringBufferArr[i];
                if (stringBuffer != null) {
                    return longToChars(j, stringBuffer, false);
                }
                i++;
            } while (i != 5);
            return longToChars(j, new StringBuffer(), true);
        }
    }

    /* renamed from: i */
    public static final Object[] newRequest() {
        String url = AppState.getString(StateKeys.STR_RES_HUGE_URL_6);
        String empty = AppState.emptyStr;
        Integer zero = ResourceManager.integerCache[0];
        Integer minusOne = ResourceManager.integerOf(-1);
        return startAsyncRequest(0, url, new Object[]{null, null, null, null, null, null, null, empty, zero, empty, empty, zero, empty, empty, empty, empty, zero, zero, minusOne, zero, null, minusOne});
    }

    /* renamed from: a */
    public static final Object[] createRegRequest(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, int i2, int i3, int i4, int i5, int i6, String str8, String str9) {
        return startAsyncRequest(2, bufToStringCached(Utils.appendParam(Utils.appendIntParam(Utils.appendParam(Utils.appendIntParam(Utils.appendIntParam(Utils.appendIntParam(Utils.appendIntParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(newStringBuffer().append(AppState.getString(StateKeys.STR_RES_HUGE_URL_2)), 1311927, str8), 1115339, StringUtils.prefix(str, str.indexOf(64))), 1246428, StringUtils.getDomain(str)), 591087, str2), 1049848, str3), 1180936, str4), 1049882, str5), 656682, str6), 591156, str7), 591165, i2), 722246, i3), 656721, i4), 263515, i5), 1181023, str9), 1443185, i6), 198023, AppState.getString(StateKeys.STR_SEARCH_URL))), new Object[]{null, null, null, null, null, null, null, str, ResourceManager.integerOf(0), str2, str3, ResourceManager.integerCache[0], str4, str5, str6, str7, ResourceManager.integerOf(i2), ResourceManager.integerOf(i3), ResourceManager.integerOf(i4), ResourceManager.integerOf(i5), null, ResourceManager.integerOf(i6)});
    }

    /* renamed from: a */
    private static final Object[] startAsyncRequest(int i, String str, Object[] objArr) {
        objArr[0] = null;
        objArr[1] = ResourceManager.integerOf(i);
        objArr[2] = str;
        new AsyncTask(24, objArr);
        return objArr;
    }

    /* renamed from: e */
    public static final void executeRegRequest(Object[] objArr) {
        try {
            try {
                NetworkLock.acquireNetworkLock();
                HttpClient httpClient = HttpClient.createWithType3(objArr[2]);
                int responseCode = httpClient.getResponseCode();
                if (responseCode == 200) {
                    ByteBuffer buffer = new ByteBuffer(httpClient);
                    switch (((Integer) objArr[1]).intValue()) {
                        case 0:
                            parseRegResponse(objArr, buffer.parseXmlStr());
                            HttpClient.closeAndUpdateStats(httpClient);
                            NetworkLock.releaseNetworkLock();
                            return;
                        case 1:
                            objArr[3] = buffer.toImage();
                            HttpClient.closeAndUpdateStats(httpClient);
                            NetworkLock.releaseNetworkLock();
                            return;
                        case 2:
                            parseRegResponse(objArr, buffer.parseXmlStr());
                            HttpClient.closeAndUpdateStats(httpClient);
                            NetworkLock.releaseNetworkLock();
                            return;
                    }
                }
                throw new Throwable(StringUtils.intern(Integer.toString(responseCode)));
            } catch (Throwable th) {
                objArr[0] = th;
                HttpClient.closeAndUpdateStats((HttpClient) null);
                NetworkLock.releaseNetworkLock();
            }
        } catch (Throwable th2) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            throw th2;
        }
    }

    /* renamed from: a */
    private static final void parseRegResponse(Object[] objArr, XmlElement element) {
        Vector children = element.children;
        int size = children.size();
        while (true) {
            size--;
            if (size < 0) {
                if (objArr[4] == null || objArr[6] == null || objArr[5] == null) {
                    throw new RuntimeException();
                }
                objArr[3] = null;
                startAsyncRequest(1, new ByteBuffer().writeCompressed(2163862).writeObjectStr(objArr[6]).getStringAndClear(), objArr);
                return;
            }
            XmlElement child = (XmlElement) children.elementAt(size);
            String attrValue = child.getIntAttribute(329117);
            String attrName = child.getIntAttribute(262601);
            if (StringUtils.matchesKey(132297, attrName)) {
                objArr[4] = attrValue;
            } else if (StringUtils.matchesKey(1115488, attrName)) {
                objArr[5] = attrValue;
            } else if (StringUtils.matchesKey(1246602, attrName)) {
                objArr[6] = attrValue;
            } else if (StringUtils.matchesKey(394658, attrName)) {
                objArr[20] = attrValue;
                if (Integer.parseInt(attrValue) == 0) {
                    return;
                }
            } else {
                continue;
            }
        }
    }
}
