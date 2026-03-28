package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

public abstract class DiagnosticReporter {

    /* renamed from: a */
    public static final void checkCrashReport() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (jCurrentTimeMillis > AppState.getLong(SessionKeys.TIMESTAMP_LAST_CLEANUP) + 7776000000L) {
            AppState.setLong(SessionKeys.TIMESTAMP_LAST_CLEANUP, jCurrentTimeMillis);
            new AsyncTask(AsyncTaskId.SEND_DIAGNOSTIC);
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
        byte[] outputBuffer = ObjectPool.newBytes(3000);
        try {
            Thread.sleep(1000L);
            System.gc();
            Thread.sleep(1000L);
            NetworkLock.acquireNetworkLock();
            if (str == null) {
                HttpClient diagClient = HttpClient.createWithType2((Object) new ByteBuffer().writeCompressed(PackedStringKeys.URL_MOBILE_MAIL_RU).writeCompressed(PackedStringKeys.API_DATA_GET).writeCompressed(PackedStringKeys.API_PHONE_INFO).getStringAndClear());
                httpClient = diagClient;
                if (diagClient.getResponseCode() == 200) {
                    Vector children = new ByteBuffer(httpClient).parseXmlStr().children;
                    XmlElement report = new XmlElement(103).setLongKeyAttr(103, AppState.getString(SessionKeys.SESSION_KEY)).setLongKeyAttr(102, AppController.getFreeMemoryString()).setLongKeyAttr(116, StringUtils.intern(Long.toString(Runtime.getRuntime().totalMemory()))).setLongKeyAttr(112, StringUtils.intern(Integer.toString(0))).setLongKeyAttr(115, StringUtils.intern(ResourceManager.booleanOf(false).toString()));
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
                    new AsyncTask(AsyncTaskId.SEND_DIAGNOSTIC, report.toString());
                }
            } else {
                ByteBuffer urlBuffer = new ByteBuffer().writeCompressed(PackedStringKeys.PARAM_Q_EQ);
                ByteBuffer dataBuffer = new ByteBuffer().writeUTFNoLen(str);
                for (int i7 = 0; i7 < dataBuffer.length; i7 += 600) {
                    int pos = i7;
                    int limit = Utils.min(pos + 600, dataBuffer.length);
                    byte[] base64Table = AppState.getBytes(StringResKeys.RES_BASE64_TABLE);
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
                HttpClient uploadClient = HttpClient.createMockClient(new ByteBuffer().writeCompressed(PackedStringKeys.HOST_MOBILE_MAIL_RU_2041).writeCompressed(PackedStringKeys.API_DATA_ADD).writeCompressed(PackedStringKeys.API_PHONE_INFO).getStringAndClear());
                httpClient = uploadClient;
                uploadClient.sendHttpRequest(urlBuffer.length, 1414745936, 1038).writeBuffer(urlBuffer).getResponseCode();
            }
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
            ObjectPool.releaseBytes(outputBuffer);
        } catch (RuntimeException th) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            ObjectPool.releaseBytes(outputBuffer);
            throw th;
        } catch (Throwable th) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            ObjectPool.releaseBytes(outputBuffer);
            throw new RuntimeException(th.toString());
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
}
