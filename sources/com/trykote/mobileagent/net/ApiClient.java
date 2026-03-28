package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
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
        if (isHttpSuccess(objArrM1151a) && JsonParser.isSuccess(JsonParser.parseJson(((ByteBuffer) objArrM1151a[3]).duplicate()))) {
            objArr[4] = objArrM1151a;
            return;
        }
        objArr[8] = objArr;
        MrimAccount c0028ba2 = (MrimAccount) AppState.getAccount();
        Object[] objArrM1147a = createAuthRequest(ObjectPool.newStringBuffer().append(AppState.getString(StringResKeys.STR_RES_VERY_LONG_API_1)).append(c0028ba2.login).append(AppState.getString(StringResKeys.STR_RES_PROTOCOL_TAG_3)).append(c0028ba2.password).append(AppState.getString(SessionKeys.SLOT_SESSION_HASH)));
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
                            strM1215a = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StringResKeys.STR_RES_LONG_URL_5)).append(objArr[2]));
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
                        Object[] objArrM798a = createConnectError((Throwable) null);
                        HttpClient.closeAndUpdateStats(c0024ax);
                        NetworkLock.releaseNetworkLock();
                        return objArrM798a;
                    }
                } catch (Throwable th) {
                    Object[] objArrM801d = createReceiveError((Throwable) null);
                    HttpClient.closeAndUpdateStats(c0024ax);
                    NetworkLock.releaseNetworkLock();
                    return objArrM801d;
                }
            } catch (IllegalArgumentException e2) {
                Object[] objArrM799b = createAuthError((Throwable) null);
                HttpClient.closeAndUpdateStats(c0024ax);
                NetworkLock.releaseNetworkLock();
                return objArrM799b;
            } catch (SecurityException e3) {
                Object[] objArrM800c = createSendError((Throwable) null);
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
            return createProtocolError(th);
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
                    if (headerFieldKey != null && headerField != null && headerField.startsWith(AppState.getString(StringResKeys.STR_RES_PARAM_4)) && StringUtils.matchesKey(PackedStringKeys.HEADER_SET_COOKIE, StringUtils.intern(headerFieldKey.toLowerCase()))) {
                        objArr[6] = StringUtils.prefix(headerField, headerField.indexOf(59));
                    }
                    i++;
                } catch (Throwable unused) {
                }
            }
            M804a = createHttpRequest(iM634a, StringUtils.intern(Integer.toString(iM634a)), new ByteBuffer(c0024ax));
            return M804a;
        } catch (Throwable th) {
            return createGenericError(th);
        }
    }

    public static final Object[] getAsyncResult(Object[] objArr) {
        Object[] objArr2 = (Object[]) objArr[4];
        if (objArr2 != null) {
            return objArr2;
        }
        return null;
    }

    /* renamed from: a */
    public static final Object[] createHttpRequest(int i, String str, ByteBuffer c0043n) {
        return createHttpResult(0, str, i, c0043n);
    }

    /* renamed from: a */
    private static final Object[] createHttpResult(int i, Object obj, int i2, ByteBuffer c0043n) {
        return new Object[]{ResourceManager.integerOf(i), ResourceManager.integerOf(i2), obj.toString(), c0043n};
    }

    /* renamed from: a */
    private static final Object[] createErrorResult(int i, int i2, Object obj) {
        return createHttpResult(i, ObjectPool.newStringBuffer().append(AppState.getString(i2)).append(AppState.getString(StringResKeys.STR_ERROR_SEPARATOR)).append(obj), 0, (ByteBuffer) null);
    }

    /* renamed from: a */
    public static final Object[] createConnectError(Throwable th) {
        return createErrorResult(1, 948, th);
    }

    /* renamed from: b */
    public static final Object[] createAuthError(Throwable th) {
        return createErrorResult(2, 947, th);
    }

    /* renamed from: c */
    public static final Object[] createSendError(Throwable th) {
        return createErrorResult(4, 950, th);
    }

    /* renamed from: d */
    public static final Object[] createReceiveError(Throwable th) {
        return createErrorResult(3, 949, th);
    }

    /* renamed from: e */
    public static final Object[] createProtocolError(Throwable th) {
        return createErrorResult(5, 951, th);
    }

    /* renamed from: f */
    public static final Object[] createGenericError(Throwable th) {
        return createErrorResult(6, 951, th);
    }

    /* renamed from: a */
    public static final boolean isHttpSuccess(Object[] objArr) {
        return ((Integer) objArr[0]).intValue() == 0 && ((Integer) objArr[1]).intValue() == 200;
    }

    /* renamed from: e */
    private static Object parseJsonResponse(Object[] objArr) {
        try {
            return JsonParser.parseJson((ByteBuffer) objArr[3]);
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: k */
    public static final Object[] pollAsyncResult() {
        Object[] objArrM609l = AppState.getObjectArray(RegistrationKeys.OBJ_REGISTRATION_DATA);
        if (objArrM609l != null && getAsyncResult(objArrM609l) != null) {
            AppState.clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
        }
        return objArrM609l;
    }

    /* renamed from: a */
    public static final StringBuffer appendAuthParams(StringBuffer stringBuffer, String str) {
        return stringBuffer.append(AppState.getString(SessionKeys.SLOT_SESSION_HASH)).append(AppState.getString(StringResKeys.STR_RES_STATUS_LABEL)).append(str);
    }

    /* renamed from: c */
    public static final int validateJsonResponse(Object[] objArr) {
        AppState.clearIndex(UIKeys.SLOT_MEDIA_PLAYER);
        if (!isHttpSuccess(objArr)) {
            return NotificationHelper.showError(888);
        }
        Object objM806e = parseJsonResponse(objArr);
        if (objM806e == null) {
            return NotificationHelper.showError(889);
        }
        if (!JsonParser.isSuccess(objM806e)) {
            return NotificationHelper.showError(890);
        }
        AppState.pool[UIKeys.SLOT_MEDIA_PLAYER] = objM806e;
        return 0;
    }

    /* renamed from: l */
    public static final Object getJsonPayload() {
        Object obj = AppState.pool[UIKeys.SLOT_MEDIA_PLAYER];
        AppState.clearIndex(UIKeys.SLOT_MEDIA_PLAYER);
        return JsonParser.getVectorElement(obj, 2);
    }
}
