package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.util.*;
import java.io.IOException;
import java.util.Vector;
import javax.microedition.io.ConnectionNotFoundException;

public final class ApiClient {

    public static final Object[] createAuthRequest(StringBuffer urlBuffer) {
        Object[] request = new Object[9];
        request[0] = ObjectPool.unpackChars(5522759);
        request[2] = ObjectPool.toStringAndRelease(urlBuffer);
        return request;
    }

    public static final Object[] createUploadRequest(String path, StringBuffer bodyBuffer) {
        Object[] request = new Object[9];
        request[0] = ObjectPool.unpackChars(1414745936);
        request[2] = path;
        request[3] = ObjectPool.toStringAndRelease(bodyBuffer).getBytes();
        return request;
    }

    public static final Object[] submitAsync(Object[] objArr) {
        objArr[7] = new AsyncTask(AsyncTaskId.API_REAUTH, objArr);
        return objArr;
    }

    public static final void executeWithReauth(Object[] objArr) throws InterruptedException {
        MrimAccount account = (MrimAccount) AppState.getAccount();
        Object[] response = executeHttpRequest(objArr, account);
        if (objArr[8] != null) {
            objArr[4] = response;
            return;
        }
        if (isHttpSuccess(response) && JsonParser.isSuccess(JsonParser.parseJson(((ByteBuffer) response[3]).duplicate()))) {
            objArr[4] = response;
            return;
        }
        objArr[8] = objArr;
        MrimAccount currentAccount = (MrimAccount) AppState.getAccount();
        Object[] authRequest = createAuthRequest(ObjectPool.newStringBuffer().append(ResourceAccessor.str(PackedStringKeys.URL_PATH_AUTH_LOGIN)).append(currentAccount.login).append(ResourceAccessor.str(PackedStringKeys.PARAM_PASSWORD_EQ)).append(currentAccount.password).append(SessionState.getSessionHash()));
        authRequest[8] = authRequest;
        ((AsyncTask) submitAsync(authRequest)[7]).thread.join();
        account.jabberId = (String) authRequest[6];
        objArr[4] = executeHttpRequest(objArr, account);
    }

    private static final Object[] executeHttpRequest(Object[] objArr, MrimAccount account) {
        String requestUrl;
        HttpClient httpClient = null;
        try {
            try {
                try {
                    try {
                        NetworkLock.acquireNetworkLock();
                        String overrideUrl = (String) objArr[5];
                        if (overrideUrl == null) {
                            requestUrl = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(PackedStringKeys.URL_AJ_MAIL_RU)).append(objArr[2]));
                        } else {
                            requestUrl = overrideUrl;
                        }
                        HttpClient connection = HttpClient.createHttpClient(requestUrl, account, 1);
                        httpClient = connection;
                        Object[] result = sendHttpRequest(objArr, connection);
                        HttpClient.closeAndUpdateStats(httpClient);
                        NetworkLock.releaseNetworkLock();
                        return result;
                    } catch (ConnectionNotFoundException e) {
                        Object[] connectError = createConnectError((Throwable) null);
                        HttpClient.closeAndUpdateStats(httpClient);
                        NetworkLock.releaseNetworkLock();
                        return connectError;
                    }
                } catch (Throwable th) {
                    Object[] receiveError = createReceiveError((Throwable) null);
                    HttpClient.closeAndUpdateStats(httpClient);
                    NetworkLock.releaseNetworkLock();
                    return receiveError;
                }
            } catch (IllegalArgumentException e2) {
                Object[] authError = createAuthError((Throwable) null);
                HttpClient.closeAndUpdateStats(httpClient);
                NetworkLock.releaseNetworkLock();
                return authError;
            } catch (SecurityException e3) {
                Object[] sendError = createSendError((Throwable) null);
                HttpClient.closeAndUpdateStats(httpClient);
                NetworkLock.releaseNetworkLock();
                return sendError;
            }
        } catch (RuntimeException e) {
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
            throw e;
        } catch (Error e) {
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
            throw e;
        }
    }

    private static final Object[] sendHttpRequest(Object[] objArr, HttpClient httpClient) {
        try {
            httpClient.setRequestMethod((String) objArr[0]);
            setHeaderFromState(httpClient, 919726, 788668);
            setHeaderFromState(httpClient, 657608, 329938);
            setOptionalHeader(httpClient, 395489, ((MrimAccount) AppState.getAccount()).jabberId);
            byte[] bodyData = (byte[]) objArr[3];
            if (bodyData != null) {
                setHeaderFromState(httpClient, 788628, 2164851);
                httpClient.writeData(bodyData, bodyData.length);
            }
            return readHttpResponse(objArr, httpClient);
        } catch (Throwable th) {
            return createProtocolError(th);
        }
    }

    public static final void setHeaderFromState(HttpClient httpClient, int headerKey, int valueKey) throws IOException {
        setOptionalHeader(httpClient, headerKey, AppState.getString(valueKey));
    }

    private static void setOptionalHeader(HttpClient httpClient, int headerKey, String value) throws IOException {
        if (value != null) {
            httpClient.setRequestProperty(AppState.getString(headerKey), value);
        }
    }

    private static final Object[] readHttpResponse(Object[] objArr, HttpClient httpClient) {
        try {
            int responseCode = httpClient.getResponseCode();
            int headerIndex = 0;
            while (true) {
                try {
                    String headerName = ((javax.microedition.io.HttpConnection) httpClient.connection).getHeaderFieldKey(headerIndex);
                    String headerValue = ((javax.microedition.io.HttpConnection) httpClient.connection).getHeaderField(headerIndex);
                    if (headerName == null && headerValue == null) {
                        break;
                    }
                    if (headerName != null && headerValue != null && headerValue.startsWith(ResourceAccessor.str(PackedStringKeys.COOKIE_MPOP)) && StringUtils.matchesKey(PackedStringKeys.HEADER_SET_COOKIE, StringUtils.intern(headerName.toLowerCase()))) {
                        objArr[6] = StringUtils.prefix(headerValue, headerValue.indexOf(59));
                    }
                    headerIndex++;
                } catch (Throwable unused) {
                }
            }
            return createHttpRequest(responseCode, StringUtils.intern(Integer.toString(responseCode)), new ByteBuffer(httpClient));
        } catch (Throwable th) {
            return createGenericError(th);
        }
    }

    public static final Object[] getAsyncResult(Object[] request) {
        Object[] result = (Object[]) request[4];
        if (result != null) {
            return result;
        }
        return null;
    }

    public static final Object[] createHttpRequest(int statusCode, String statusText, ByteBuffer body) {
        return createHttpResult(0, statusText, statusCode, body);
    }

    private static final Object[] createHttpResult(int errorType, Object description, int statusCode, ByteBuffer body) {
        return new Object[]{ObjectPool.integerOf(errorType), ObjectPool.integerOf(statusCode), description.toString(), body};
    }

    private static final Object[] createErrorResult(int errorType, int messageKey, Object detail) {
        return createHttpResult(errorType, ObjectPool.newStringBuffer().append(AppState.getString(messageKey)).append(ResourceAccessor.str(StringResKeys.STR_ERROR_SEPARATOR)).append(detail), 0, (ByteBuffer) null);
    }

    public static final Object[] createConnectError(Throwable th) {
        return createErrorResult(1, 948, th);
    }

    public static final Object[] createAuthError(Throwable th) {
        return createErrorResult(2, 947, th);
    }

    public static final Object[] createSendError(Throwable th) {
        return createErrorResult(4, 950, th);
    }

    public static final Object[] createReceiveError(Throwable th) {
        return createErrorResult(3, 949, th);
    }

    public static final Object[] createProtocolError(Throwable th) {
        return createErrorResult(5, 951, th);
    }

    public static final Object[] createGenericError(Throwable th) {
        return createErrorResult(6, 951, th);
    }

    public static final boolean isHttpSuccess(Object[] objArr) {
        return ((Integer) objArr[0]).intValue() == 0 && ((Integer) objArr[1]).intValue() == 200;
    }

    private static Object parseJsonResponse(Object[] objArr) {
        try {
            return JsonParser.parseJson((ByteBuffer) objArr[3]);
        } catch (Throwable unused) {
            return null;
        }
    }

    public static final Object[] pollAsyncResult() {
        Object[] pendingRequest = RegistrationState.getRegistrationData();
        if (pendingRequest != null && getAsyncResult(pendingRequest) != null) {
            RegistrationState.clearRegistrationData();
        }
        return pendingRequest;
    }

    public static final StringBuffer appendAuthParams(StringBuffer buffer, String data) {
        return buffer.append(SessionState.getSessionHash()).append(ResourceAccessor.str(PackedStringKeys.PARAM_DATA_EQ)).append(data);
    }

    public static final int validateJsonResponse(Object[] objArr) {
        UIState.clearJsonResult();
        if (!isHttpSuccess(objArr)) {
            return NotificationHelper.showError(888);
        }
        Object jsonResult = parseJsonResponse(objArr);
        if (jsonResult == null) {
            return NotificationHelper.showError(889);
        }
        if (!JsonParser.isSuccess(jsonResult)) {
            return NotificationHelper.showError(890);
        }
        UIState.setJsonResult(jsonResult);
        return 0;
    }

    public static final Object getJsonPayload() {
        Object obj = UIState.getJsonResult();
        UIState.clearJsonResult();
        return JsonParser.getVectorElement(obj, 2);
    }

    public static void sendSmsRequest(Object obj) {
        if (!(obj instanceof String)) {
            return;
        }
        HttpClient httpClient = null;
        try {
            NetworkLock.acquireNetworkLock();
            httpClient = HttpClient.createWithType3(obj);
            if (httpClient.getResponseCode() == 200) {
                String url = null;
                boolean statusOk = false;
                Vector children = new ByteBuffer(httpClient).parseXml().children;
                for (int i = children.size() - 1; i >= 0; i--) {
                    XmlElement xmlElement = (XmlElement) children.elementAt(i);
                    String tagName = xmlElement.tagName;
                    String textValue = StringUtils.fromBuffer(xmlElement.textContent);
                    if (StringUtils.matchesKey(PackedStringKeys.TAG_STATUS, tagName) && StringUtils.matchesKey(PackedStringKeys.HTTP_STATUS_200, textValue)) {
                        statusOk = true;
                    } else if (StringUtils.matchesKey(PackedStringKeys.TAG_LINK, tagName)) {
                        url = textValue;
                    }
                    if (statusOk && url != null) {
                        ObjectPool.releaseVector(children);
                        EventDispatcher.postNotification(ResourceAccessor.str(StringResKeys.STR_OPERATION_COMPLETE));
                        break;
                    }
                }
            } else {
                EventDispatcher.postNotification(StringUtils.concatKeyObj(493, (Object) null));
            }
        } catch (RuntimeException e) {
            EventDispatcher.postNotification(StringUtils.concatKeyObj(493, (Object) null));
            throw e;
        } catch (Error e) {
            EventDispatcher.postNotification(StringUtils.concatKeyObj(493, (Object) null));
            throw e;
        } catch (Throwable th) {
            EventDispatcher.postNotification(StringUtils.concatKeyObj(493, (Object) null));
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
        }
    }

    public static void fetchSharedContacts(String url) {
        HttpClient http = null;
        try {
            NetworkLock.acquireNetworkLock();
            http = HttpClient.createWithType2((Object) url);
            if (http.getResponseCode() != 200) {
                throw new Throwable();
            }
            Vector lines = Utils.splitReplace(new ByteBuffer(http).readUTFWithLen(), '\n', '\r');
            XmppContactGroup.sharedContactList.removeAllElements();
            for (int i = lines.size() - 1; i >= 0; i--) {
                Vector fields = Utils.splitMerge((String) lines.elementAt(i), '|');
                if (fields.size() == 5) {
                    XmppContactGroup.sharedContactList.addElement(new Object[]{fields.elementAt(0), new long[]{Long.parseLong((String) fields.elementAt(1)), Long.parseLong((String) fields.elementAt(2))}, fields.elementAt(4)});
                }
                ObjectPool.releaseVector(fields);
            }
            ObjectPool.releaseVector(lines);
        } catch (RuntimeException th) {
            throw th;
        } catch (Throwable th) {
            throw new RuntimeException(th.toString());
        } finally {
            HttpClient.closeAndUpdateStats(http);
            NetworkLock.releaseNetworkLock();
        }
    }

    public static Object[] getUrlComponents(String baseUrl) {
        return new Object[]{ObjectPool.integerOf(20), baseUrl};
    }
}
