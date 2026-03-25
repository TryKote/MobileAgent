package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.util.*;
import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;

public final class ApiClient {

    public static final Object[] createAuthRequest(StringBuffer stringBuffer) {
        Object[] objArr = new Object[9];
        objArr[0] = ObjectPool.unpackChars(5522759);
        objArr[2] = ObjectPool.toStringAndRelease(stringBuffer);
        return objArr;
    }

    public static final Object[] createUploadRequest(String str, StringBuffer stringBuffer) {
        Object[] objArr = new Object[9];
        objArr[0] = ObjectPool.unpackChars(1414745936);
        objArr[2] = str;
        objArr[3] = ObjectPool.toStringAndRelease(stringBuffer).getBytes();
        return objArr;
    }

    public static final Object[] submitAsync(Object[] objArr) {
        objArr[7] = new AsyncTask(AsyncTaskId.API_REAUTH, objArr);
        return objArr;
    }

    public static final void executeWithReauth(Object[] objArr) throws InterruptedException {
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        Object[] objArrM1151a = executeHttpRequest(objArr, c0028ba);
        if (objArr[8] != null) {
            objArr[4] = objArrM1151a;
            return;
        }
        if (IOUtils.isHttpSuccess(objArrM1151a) && JsonParser.isSuccess(JsonParser.parseJson(((ByteBuffer) objArrM1151a[3]).duplicate()))) {
            objArr[4] = objArrM1151a;
            return;
        }
        objArr[8] = objArr;
        MrimAccount c0028ba2 = (MrimAccount) AppState.getAccount();
        Object[] objArrM1147a = createAuthRequest(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_VERY_LONG_API_1)).append(c0028ba2.login).append(AppState.getString(StateKeys.STR_RES_PROTOCOL_TAG_3)).append(c0028ba2.password).append(AppState.getString(StateKeys.SLOT_SESSION_HASH)));
        objArrM1147a[8] = objArrM1147a;
        ((AsyncTask) submitAsync(objArrM1147a)[7]).thread.join();
        c0028ba.jabberId = (String) objArrM1147a[6];
        objArr[4] = executeHttpRequest(objArr, c0028ba);
    }

    private static final Object[] executeHttpRequest(Object[] objArr, MrimAccount c0028ba) {
        String strM1215a;
        HttpClient c0024ax = null;
        try {
            try {
                try {
                    try {
                        NetworkLock.acquireNetworkLock();
                        String str = (String) objArr[5];
                        if (str == null) {
                            strM1215a = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_LONG_URL_5)).append(objArr[2]));
                        } else {
                            strM1215a = str;
                        }
                        HttpClient c0024axM629a = HttpClient.createHttpClient(strM1215a, c0028ba, 1);
                        c0024ax = c0024axM629a;
                        Object[] objArrM1152a = sendHttpRequest(objArr, c0024axM629a);
                        HttpClient.closeAndUpdateStats(c0024ax);
                        NetworkLock.releaseNetworkLock();
                        return objArrM1152a;
                    } catch (ConnectionNotFoundException e) {
                        Object[] objArrM798a = IOUtils.createConnectError((Throwable) null);
                        HttpClient.closeAndUpdateStats(c0024ax);
                        NetworkLock.releaseNetworkLock();
                        return objArrM798a;
                    }
                } catch (Throwable th) {
                    Object[] objArrM801d = IOUtils.createReceiveError((Throwable) null);
                    HttpClient.closeAndUpdateStats(c0024ax);
                    NetworkLock.releaseNetworkLock();
                    return objArrM801d;
                }
            } catch (IllegalArgumentException e2) {
                Object[] objArrM799b = IOUtils.createAuthError((Throwable) null);
                HttpClient.closeAndUpdateStats(c0024ax);
                NetworkLock.releaseNetworkLock();
                return objArrM799b;
            } catch (SecurityException e3) {
                Object[] objArrM800c = IOUtils.createSendError((Throwable) null);
                HttpClient.closeAndUpdateStats(c0024ax);
                NetworkLock.releaseNetworkLock();
                return objArrM800c;
            }
        } catch (Throwable th2) {
            HttpClient.closeAndUpdateStats(c0024ax);
            NetworkLock.releaseNetworkLock();
            throw th2;
        }
    }

    private static final Object[] sendHttpRequest(Object[] objArr, HttpClient c0024ax) {
        Object[] M1155b;
        try {
            c0024ax.setRequestMethod((String) objArr[0]);
            setHeaderFromState(c0024ax, 919726, 788668);
            setHeaderFromState(c0024ax, 657608, 329938);
            setOptionalHeader(c0024ax, 395489, ((MrimAccount) AppState.getAccount()).jabberId);
            byte[] bArr = (byte[]) objArr[3];
            if (bArr != null) {
                setHeaderFromState(c0024ax, 788628, 2164851);
                c0024ax.writeData(bArr, bArr.length);
            }
            M1155b = readHttpResponse(objArr, c0024ax);
            return M1155b;
        } catch (Throwable th) {
            return IOUtils.createProtocolError(th);
        }
    }

    public static final void setHeaderFromState(HttpClient c0024ax, int i, int i2) throws IOException {
        setOptionalHeader(c0024ax, i, AppState.getString(i2));
    }

    private static void setOptionalHeader(HttpClient c0024ax, int i, String str) throws IOException {
        if (str != null) {
            c0024ax.setRequestProperty(AppState.getString(i), str);
        }
    }

    private static final Object[] readHttpResponse(Object[] objArr, HttpClient c0024ax) {
        Object[] M804a;
        try {
            int iM634a = c0024ax.getResponseCode();
            int i = 0;
            while (true) {
                try {
                    String headerFieldKey = ((javax.microedition.io.HttpConnection) c0024ax.connection).getHeaderFieldKey(i);
                    String headerField = ((javax.microedition.io.HttpConnection) c0024ax.connection).getHeaderField(i);
                    if (headerFieldKey == null && headerField == null) {
                        break;
                    }
                    if (headerFieldKey != null && headerField != null && headerField.startsWith(AppState.getString(StateKeys.STR_RES_PARAM_4)) && StringUtils.matchesKey(657623, StringUtils.intern(headerFieldKey.toLowerCase()))) {
                        objArr[6] = StringUtils.prefix(headerField, headerField.indexOf(59));
                    }
                    i++;
                } catch (Throwable unused) {
                }
            }
            M804a = IOUtils.createHttpRequest(iM634a, StringUtils.intern(Integer.toString(iM634a)), new ByteBuffer(c0024ax));
            return M804a;
        } catch (Throwable th) {
            return IOUtils.createGenericError(th);
        }
    }

    public static final Object[] getAsyncResult(Object[] objArr) {
        Object[] objArr2 = (Object[]) objArr[4];
        if (objArr2 != null) {
            return objArr2;
        }
        return null;
    }
}
