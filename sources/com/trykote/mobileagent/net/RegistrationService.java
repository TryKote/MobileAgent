package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

public abstract class RegistrationService {

    /* renamed from: a */
    public static final int handleRegSubmit(Object[] objArr) {
        Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
        String statusCode = (String) objArr[20];
        if (statusCode != null) {
            if (Utils.parseInt((Object) statusCode) == 0) {
                String email = (String) objArr[7];
                int errorCode = AccountManager.validateCredentials(0, (Account) null, email, (String) objArr[9]);
                if (0 != errorCode) {
                    return NotificationHelper.showError(errorCode);
                }
                AccountManager.addToAccountSelection(AccountManager.findAccountByLogin(0, email));
                return 4;
            }
            if (Utils.parseInt((Object) statusCode) == 4004) {
                objArr[21] = ObjectPool.integerOf(-1);
            }
        }
        Storage.state().setObject(RegistrationKeys.OBJ_REGISTRATION_DATA, objArr);
        return 164;
    }

    /* renamed from: b */
    public static final void processRegForm() {
        String domain;
        Object[] objArr = (Object[]) Storage.state().getObject(RegistrationKeys.OBJ_REGISTRATION_DATA);
        Storage.state().clearIndex(RegistrationKeys.OBJ_REGISTRATION_DATA);
        String email = (String) objArr[7];
        String login = email;
        int atIndex = email.indexOf(64);
        if (atIndex >= 0) {
            domain = StringUtils.suffix(login, atIndex);
            login = StringUtils.prefix(login, atIndex);
        } else {
            domain = Storage.emptyStr;
        }
        int i = 0;
        Vector domains = Utils.splitNonEmpty(Storage.resources().getString(StringResKeys.STR_DOMAIN_LIST), (char) 0);
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
        Storage.state().setObject(MapKeys.SLOT_MAP_SEARCH_QUERY, objArr[3]);
        Storage.state().setObject(MapKeys.STR_MAP_LOCATION_NAME, objArr[4]);
        Storage.state().setObject(MapKeys.STR_MAP_LOCATION_URL, objArr[5]);
        Storage.state().setObject(ChatKeys.SLOT_CHAT_NAME, login);
        Storage.state().setInt(SessionKeys.INT_SERVER_INDEX, i);
        Storage.state().setObject(RegistrationKeys.SLOT_PASSWORD, objArr[9]);
        Storage.state().setObject(UIKeys.SLOT_SCREEN_TITLE, objArr[10]);
        Storage.state().setInt(SettingsKeys.INT_SETTINGS_THEME, ((Integer) objArr[11]).intValue());
        Storage.state().setObject(RegistrationKeys.SLOT_DEVICE_ID, objArr[12]);
        Storage.state().setObject(UIKeys.SLOT_APP_VERSION_STRING, objArr[13]);
        Storage.state().setObject(RegistrationKeys.SLOT_FIRST_NAME, objArr[14]);
        Storage.state().setObject(RegistrationKeys.SLOT_LAST_NAME, objArr[15]);
        Storage.state().setInt(RegistrationKeys.INT_SEARCH_GENDER, ((Integer) objArr[16]).intValue());
        Storage.state().setInt(RegistrationKeys.INT_SEARCH_AGE, ((Integer) objArr[17]).intValue());
        Storage.state().setInt(RegistrationKeys.INT_REG_DOMAIN_INDEX, ((Integer) objArr[18]).intValue());
        Storage.state().setInt(RegistrationKeys.INT_COUNTRY_CODE, ((Integer) objArr[19]).intValue());
        Storage.state().setObject(ContactKeys.SLOT_DISPLAY_NAME, objArr[20]);
        Storage.state().setInt(RegistrationKeys.INT_REGION_CODE, ((Integer) objArr[21]).intValue());
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.REGISTRATION_FORM));
        String statusStr = Storage.state().getString(ContactKeys.SLOT_DISPLAY_NAME);
        if (statusStr == null) {
            RemoteLogger.log("NET", "triggering refreshContactList from RegistrationService");
            ContactListManager.refreshContactList();
            return;
        }
        int statusCode = Utils.parseInt((Object) statusStr);
        int messageId = statusCode == 78 ? 818 : statusCode == 101 ? 819 : statusCode == 114 ? 820 : statusCode == 150 ? 821 : statusCode == 152 ? 822 : statusCode == 154 ? 823 : statusCode == 155 ? 824 : statusCode == 175 ? 825 : statusCode == 555 ? 826 : statusCode == 573 ? 827 : statusCode == 4003 ? 828 : statusCode == 4004 ? 829 : statusCode == 5005 ? 830 : 831;
        int msgIdx = messageId;
        String message = Storage.state().getString(messageId);
        NotificationHelper.showNotification(msgIdx != 831 ? message : new StringBuffer().append(message).append(statusCode).toString());
    }

    /* renamed from: i */
    public static final Object[] newRequest() {
        String url = Storage.resources().getString(PackedStringKeys.URL_SIGNUP);
        String empty = Storage.emptyStr;
        Integer zero = ObjectPool.integerOf(0);
        Integer minusOne = ObjectPool.integerOf(-1);
        return startAsyncRequest(0, url, new Object[]{null, null, null, null, null, null, null, empty, zero, empty, empty, zero, empty, empty, empty, empty, zero, zero, minusOne, zero, null, minusOne});
    }

    /* renamed from: a */
    public static final Object[] createRegRequest(String str, int i, String str2, String str3, String str4, String str5, String str6, String str7, int i2, int i3, int i4, int i5, int i6, String str8, String str9) {
        return startAsyncRequest(2, ObjectPool.toStringAndRelease(Utils.appendParam(Utils.appendIntParam(Utils.appendParam(Utils.appendIntParam(Utils.appendIntParam(Utils.appendIntParam(Utils.appendIntParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(ObjectPool.newStringBuffer().append(Storage.resources().getString(PackedStringKeys.URL_XHTML_WAP_MAIL_RU)), 1311927, str8), 1115339, StringUtils.prefix(str, str.indexOf(64))), 1246428, StringUtils.getDomain(str)), 591087, str2), 1049848, str3), 1180936, str4), 1049882, str5), 656682, str6), 591156, str7), 591165, i2), 722246, i3), 656721, i4), 263515, i5), 1181023, str9), 1443185, i6), 198023, Storage.resources().getString(StringResKeys.STR_SEARCH_URL))), new Object[]{null, null, null, null, null, null, null, str, ObjectPool.integerOf(0), str2, str3, ObjectPool.integerOf(0), str4, str5, str6, str7, ObjectPool.integerOf(i2), ObjectPool.integerOf(i3), ObjectPool.integerOf(i4), ObjectPool.integerOf(i5), null, ObjectPool.integerOf(i6)});
    }

    /* renamed from: a */
    private static final Object[] startAsyncRequest(int i, String str, Object[] objArr) {
        objArr[0] = null;
        objArr[1] = ObjectPool.integerOf(i);
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
        } catch (RuntimeException e) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            throw e;
        } catch (Error e) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            throw e;
        }
    }

    private static void setUpdateFlag(byte b) {
        Storage.state().getBytes(UIKeys.SLOT_MEDIA_RESOURCE)[0] = b;
    }

    private static boolean isUpdatePending() {
        return Storage.state().getBytes(UIKeys.SLOT_MEDIA_RESOURCE)[0] != 0;
    }

    public static int checkForUpdates() {
        synchronized (Storage.state().getObject(UIKeys.SLOT_MEDIA_RESOURCE)) {
            if (!isUpdatePending() && System.currentTimeMillis() > Storage.state().getLong(SessionKeys.TIMESTAMP_LAST_UPDATE_CHECK) + 86400000) {
                Storage.state().setLong(SessionKeys.TIMESTAMP_LAST_UPDATE_CHECK, System.currentTimeMillis());
                setUpdateFlag((byte) 1);
                new AsyncTask(AsyncTaskId.FETCH_UPDATE_STATUS);
            }
            if (isUpdatePending()) {
                return -1;
            }
            return Storage.state().getInt(SettingsKeys.SETTING_UPDATE_STATUS);
        }
    }

    public static void fetchUpdateStatus() {
        try {
            NetworkLock.acquireNetworkLock();
            HttpClient httpConn = HttpClient.createHttpClient(Storage.resources().getString(PackedStringKeys.URL_SETTINGS_XML), (Account) null, 3);
            if (httpConn.getResponseCode() == 200) {
                ByteBuffer buffer = new ByteBuffer(httpConn);
                synchronized (Storage.state().getObject(UIKeys.SLOT_MEDIA_RESOURCE)) {
                    Storage.state().setInt(SettingsKeys.SETTING_UPDATE_STATUS, Integer.parseInt(buffer.parseXmlStr().getIntAttribute(PackedStringKeys.TAG_SNAP_LOGINS)) != 0 ? 1 : 0);
                }
                synchronized (Storage.state().getObject(UIKeys.SLOT_MEDIA_RESOURCE)) {
                    setUpdateFlag((byte) 0);
                }
                HttpClient.closeAndUpdateStats(httpConn);
                NetworkLock.releaseNetworkLock();
            } else {
                synchronized (Storage.state().getObject(UIKeys.SLOT_MEDIA_RESOURCE)) {
                    setUpdateFlag((byte) 0);
                    HttpClient.closeAndUpdateStats((HttpClient) null);
                    NetworkLock.releaseNetworkLock();
                }
            }
        } catch (Throwable unused) {
            synchronized (Storage.state().getObject(UIKeys.SLOT_MEDIA_RESOURCE)) {
                setUpdateFlag((byte) 0);
                HttpClient.closeAndUpdateStats((HttpClient) null);
                NetworkLock.releaseNetworkLock();
            }
        }
    }

    public static void processUpdateResult() {
        boolean showMessage = Storage.state().getBool(UIKeys.FLAG_SHOW_NOTIFICATION);
        Object obj = Storage.state().getObjectArray(RegistrationKeys.OBJ_REGISTRATION_DATA)[0];
        if (obj instanceof Integer) {
            if (showMessage) {
                NotificationHelper.showMessageById(((Integer) obj).intValue());
            }
            return;
        }
        try {
            StringBuffer versionSb = ObjectPool.newStringBuffer();
            StringBuffer urlSb = ObjectPool.newStringBuffer();
            ByteBuffer buffer = (ByteBuffer) obj;
            while (buffer.length > 0) {
                int ch = buffer.readUByte();
                if (32 == ch) {
                    break;
                }
                versionSb.append((char) ch);
            }
            while (buffer.length > 0) {
                int ch = buffer.readUByte();
                if (32 == ch) {
                    break;
                }
                urlSb.append((char) ch);
            }
            Storage.state().setFromBuffer(UIKeys.SLOT_SCREEN_TITLE, versionSb);
            Storage.state().setFromBuffer(UIKeys.SLOT_SCREEN_SUBTITLE, urlSb);
            if (parseVersionNumber(Storage.resources().getString(StringResKeys.STR_APP_NAME)) >= parseVersionNumber(Storage.state().getString(UIKeys.SLOT_SCREEN_TITLE))) {
                if (showMessage) {
                    NotificationHelper.showMessageById(731);
                }
            } else {
                ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.PROFILE_LIST));
            }
        } catch (Throwable unused) {
            if (showMessage) {
                NotificationHelper.showMessageById(731);
            }
        }
    }

    public static int applyVersionLabel() {
        Storage.state().setFromPool(UIKeys.SLOT_SAVED_STRING, UIKeys.SLOT_SCREEN_SUBTITLE);
        return 0;
    }

    private static int parseVersionNumber(String str) {
        int version = 0;
        int part = 0;
        for (int idx = 0; idx < str.length(); idx++) {
            char ch = str.charAt(idx);
            if (ch == '.') {
                version = (version * 100) + part;
                part = 0;
            } else if (ch >= '0' && ch <= '9') {
                part = ((part * 10) + ch) - 48;
            }
        }
        return (version * 100) + part;
    }

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
                startAsyncRequest(1, new ByteBuffer().writeCompressed(PackedStringKeys.URL_XHTML_WAP_MAIL_RU).writeObjectStr((String) objArr[6]).getStringAndClear(), objArr);
                return;
            }
            XmlElement child = (XmlElement) children.elementAt(size);
            String attrValue = child.getIntAttribute(PackedStringKeys.ATTR_VALUE);
            String attrName = child.getIntAttribute(PackedStringKeys.ATTR_NAME);
            if (StringUtils.matchesKey(PackedStringKeys.ATTR_ID_UPPER, attrName)) {
                objArr[4] = attrValue;
            } else if (StringUtils.matchesKey(PackedStringKeys.TAG_SECURITY_IMAGE_ID, attrName)) {
                objArr[5] = attrValue;
            } else if (StringUtils.matchesKey(PackedStringKeys.TAG_SECURITY_IMAGE_LINK, attrName)) {
                objArr[6] = attrValue;
            } else if (StringUtils.matchesKey(PackedStringKeys.TAG_STATUS, attrName)) {
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
