package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

public abstract class RegistrationService {

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
        ObjectPool.releaseVector(domains);
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
            RemoteLogger.log("NET", "triggering refreshContactList from RegistrationService");
            AppController.refreshContactList();
            return;
        }
        int statusCode = Utils.parseInt((Object) statusStr);
        int messageId = statusCode == 78 ? 818 : statusCode == 101 ? 819 : statusCode == 114 ? 820 : statusCode == 150 ? 821 : statusCode == 152 ? 822 : statusCode == 154 ? 823 : statusCode == 155 ? 824 : statusCode == 175 ? 825 : statusCode == 555 ? 826 : statusCode == 573 ? 827 : statusCode == 4003 ? 828 : statusCode == 4004 ? 829 : statusCode == 5005 ? 830 : 831;
        int msgIdx = messageId;
        String message = AppState.getString(messageId);
        NotificationHelper.showNotification(msgIdx != 831 ? message : new StringBuffer().append(message).append(statusCode).toString());
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
        return startAsyncRequest(2, ObjectPool.toStringAndRelease(Utils.appendParam(Utils.appendIntParam(Utils.appendParam(Utils.appendIntParam(Utils.appendIntParam(Utils.appendIntParam(Utils.appendIntParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_RES_HUGE_URL_2)), 1311927, str8), 1115339, StringUtils.prefix(str, str.indexOf(64))), 1246428, StringUtils.getDomain(str)), 591087, str2), 1049848, str3), 1180936, str4), 1049882, str5), 656682, str6), 591156, str7), 591165, i2), 722246, i3), 656721, i4), 263515, i5), 1181023, str9), 1443185, i6), 198023, AppState.getString(StateKeys.STR_SEARCH_URL))), new Object[]{null, null, null, null, null, null, null, str, ResourceManager.integerOf(0), str2, str3, ResourceManager.integerCache[0], str4, str5, str6, str7, ResourceManager.integerOf(i2), ResourceManager.integerOf(i3), ResourceManager.integerOf(i4), ResourceManager.integerOf(i5), null, ResourceManager.integerOf(i6)});
    }

    /* renamed from: a */
    private static final Object[] startAsyncRequest(int i, String str, Object[] objArr) {
        objArr[0] = null;
        objArr[1] = ResourceManager.integerOf(i);
        objArr[2] = str;
        new AsyncTask(AsyncTaskId.EXECUTE_REGISTRATION, objArr);
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
