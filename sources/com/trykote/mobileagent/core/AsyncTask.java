package com.trykote.mobileagent.core;

import com.trykote.mobileagent.core.event.AccountDataEvent;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.*;


import com.trykote.mobileagent.map.GeoRegion;
import com.trykote.mobileagent.map.MapController;
import com.trykote.mobileagent.map.MapPoint;
import com.trykote.mobileagent.map.MapRenderer;
import com.trykote.mobileagent.model.ContactListParser;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.VCard;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.ConnectionThread;
import com.trykote.mobileagent.protocol.ProtocolEvent;
import com.trykote.mobileagent.protocol.mmp.MmpContact;
import com.trykote.mobileagent.protocol.mrim.MrimAccount;
import com.trykote.mobileagent.protocol.mrim.RegistrationService;
import com.trykote.mobileagent.protocol.xmpp.XmppContactGroup;
import com.trykote.mobileagent.protocol.xmpp.XmppMailRuProtocol;
import com.trykote.mobileagent.protocol.xmpp.XmppProtocol;
import com.trykote.mobileagent.util.*;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;
import java.util.Vector;

public final class AsyncTask implements Runnable {

    private static final int HTTP_OK = 200;

    private static final int ERR_PHOTO_HTTP_FAILED = 465;
    private static final int ERR_PHOTO_EXCEPTION = 466;
    private static final int ERR_FETCH_FAILED = 731;

    private static final int CONNECTION_POLL_MS = 100;
    private static final int DELAYED_CLOSE_MS = 1000;
    private static final int SMS_SEND_DELAY_MS = 100;
    private static final int COMPLETION_TIMEOUT_MS = 15000;
    private static final int COMPLETION_POLL_MS = 500;

    /** Packed chars "tr" — XML attribute for traffic/map type */
    private static final int ATTR_TRAFFIC_TYPE = 29300;
    /** Packed chars "b" — XML attribute for zoom bounds */
    private static final int ATTR_ZOOM_BOUNDS = 98;

    /** ASCII ':' used as separator in auth digest construction */
    private static final int COLON = 58;

    /** State index: URL for map points API */
    private static final int RES_MAP_POINTS_URL = 872;
    /** State index: URL for MMP route API */
    private static final int RES_MMP_ROUTE_URL = 1000;
    /** State index: URL for saved locations API */
    private static final int RES_SAVED_LOCATIONS_URL = 370;
    /** State index: URL for contacts sync API */
    private static final int RES_CONTACTS_SYNC_URL = 505;

    private int taskId;

    private Object taskData;

    public Thread thread;

    public AsyncTask(Object target, int commandId, int commandParam) {
        RemoteLogger.log("TASK", "new AsyncTask type=" + commandId + " param=" + commandParam);
        AppController.dispatchCommand(target, commandId, commandParam);
    }

    public static void shutdown() {
        if (AppController.appLock != null) {
            synchronized (AppController.appLock) {
                AppController.isShuttingDown = true;
                SoftFloat.clearMathTables();
                AppController.clearImageCache();
                Vector tasks = UIState.getMediaControl();
                if (tasks != null) {
                    synchronized (tasks) {
                        UIState.clearMediaControl();
                    }
                }
                SocketWrapper.closeAll();
                RemoteLogger.log("PERSIST", "SHUTDOWN: saveOnExit=" + AppController.saveOnExit);
                AccountManager.saveState(AppController.saveOnExit, true);
                AppState.saveAllDeltas(AppController.saveOnExit);
                RemoteLogger.log("PERSIST", "SHUTDOWN: save complete");
            }
        }
    }

    public AsyncTask(int taskId) {
        this(taskId, (Object) null);
    }

    public AsyncTask(int taskId, Object taskData) {
        this.taskId = taskId;
        this.taskData = taskData;
        Thread thread = new Thread(this, "Task-" + taskId);
        this.thread = thread;
        thread.start();
    }

    public void run() {
        RemoteLogger.log("TASK", "run taskId=" + this.taskId);
        try {
            switch (this.taskId) {
                case AsyncTaskId.PROCESS_SOFTKEY: taskProcessSoftkey(); return;
                case AsyncTaskId.DOWNLOAD_PHOTO: taskDownloadPhoto(); return;
                case AsyncTaskId.FETCH_TILE_BUFFER: taskFetchTileBuffer(); return;
                case AsyncTaskId.CONNECTION_LOOP: taskConnectionLoop(); return;
                case AsyncTaskId.SOCKET_READER: taskSocketReader(); return;
                case AsyncTaskId.API_REAUTH: taskApiReauth(); return;
                case AsyncTaskId.FETCH_CITY_ZOOM: taskFetchCityZoom(); return;
                case AsyncTaskId.DELAYED_CLOSE: taskDelayedClose(); return;
                case AsyncTaskId.TILE_LOADER: taskTileLoader(); return;
                case AsyncTaskId.FETCH_MAP_POINTS: taskFetchMapPoints(); return;
                case AsyncTaskId.FETCH_GEO_CONFIG: taskFetchGeoConfig(); return;
                case AsyncTaskId.FETCH_MMP_ROUTE: taskFetchMmpRoute(); return;
                case AsyncTaskId.PERIODIC_TIME_SYNC: taskPeriodicTimeSync(); return;
                case AsyncTaskId.DOWNLOAD_CACHED_PHOTO: taskDownloadCachedPhoto(); return;
                case AsyncTaskId.FETCH_SHARED_CONTACTS: taskFetchSharedContacts(); return;
                case AsyncTaskId.HTTP_FIRE_AND_FORGET: taskHttpFireAndForget(); return;
                case AsyncTaskId.SEND_SMS_REQUEST: taskSendSmsRequest(); return;
                case AsyncTaskId.SEND_DIAGNOSTIC: taskSendDiagnostic(); return;
                case AsyncTaskId.FETCH_SAVED_LOCATIONS: taskFetchSavedLocations(); return;
                case AsyncTaskId.PARSE_CONTACTS_SYNC: taskParseContactsSync(); return;
                case AsyncTaskId.PARSE_CONTACTS_ASYNC: taskParseContactsAsync(); return;
                case AsyncTaskId.FETCH_LOCATION_PROFILE: taskFetchLocationProfile(); return;
                case AsyncTaskId.EXECUTE_REGISTRATION: taskExecuteRegistration(); return;
                case AsyncTaskId.SEND_SMS_DIRECT: taskSendSmsDirect(); return;
                case AsyncTaskId.WAIT_FOR_COMPLETION: taskWaitForCompletion(); return;
                case AsyncTaskId.PROCESS_XMPP_STREAM: taskProcessXmppStream(); return;
                case AsyncTaskId.PERFORM_XMPP_AUTH: taskPerformXmppAuth(); return;
                case AsyncTaskId.FETCH_HISTORY: taskFetchHistory(); return;
                case AsyncTaskId.FETCH_UPDATE_STATUS: taskFetchUpdateStatus(); return;
                case AsyncTaskId.RESOLVE_XMPP_SERVER: taskResolveXmppServer(); return;
                case AsyncTaskId.XMPP_HTTP_AUTH: taskXmppHttpAuth(); return;
            }
        } catch (Throwable e) {
            RemoteLogger.log("TASK", "FATAL exception in run taskId=" + this.taskId, e);
        }
    }

    private void taskProcessSoftkey() {
        AppController.runEventLoop();
    }

    private void taskDownloadPhoto() {
        Object[] args = (Object[]) this.taskData;
        HttpClient httpClient = null;
        Object result = null;
        try {
            NetworkLock.acquireNetworkLock();
            httpClient = HttpClient.createWithType3(args[0]);
            result = httpClient.getResponseCode() == HTTP_OK ? new ByteBuffer(httpClient).toImage() : ObjectPool.integerOf(ERR_PHOTO_HTTP_FAILED);
        } catch (Throwable e) {
            result = ObjectPool.integerOf(ERR_PHOTO_EXCEPTION);
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
            args[2] = result;
        }
    }

    private void taskFetchTileBuffer() {
        Object[] args = (Object[]) this.taskData;
        HttpClient httpClient = null;
        try {
            NetworkLock.acquireNetworkLock();
            httpClient = HttpClient.createHttpClient(ResourceAccessor.str(PackedStringKeys.URL_VERSION_CHECK), null, 3);
            args[0] = httpClient.getResponseCode() == HTTP_OK ? new ByteBuffer(httpClient) : ObjectPool.integerOf(ERR_FETCH_FAILED);
        } catch (Throwable e) {
            args[0] = ObjectPool.integerOf(ERR_FETCH_FAILED);
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskConnectionLoop() {
        while (true) {
            Vector tasks = UIState.getMediaControl();
            if (tasks == null) {
                return;
            }
            int idx = 0;
            while (true) {
                ConnectionThread conn;
                synchronized (tasks) {
                    if (idx >= tasks.size()) break;
                    conn = (ConnectionThread) tasks.elementAt(idx);
                }
                conn.process();
                ++idx;
            }
            Vector closeQueue = UIState.getMediaVolume();
            if (closeQueue != null) {
                synchronized (closeQueue) {
                    IOUtils.closeConn((Connection) Utils.dequeue(closeQueue));
                }
            }
            try {
                Thread.sleep(CONNECTION_POLL_MS);
            } catch (Throwable e) {
            }
        }
    }

    private void taskSocketReader() {
        ((SocketWrapper) this.taskData).asyncReaderLoop();
    }

    private void taskApiReauth() throws InterruptedException {
        ApiClient.executeWithReauth((Object[]) this.taskData);
    }

    private void taskFetchCityZoom() {
        HttpClient httpClient = null;
        try {
            NetworkLock.acquireNetworkLock();
            httpClient = HttpClient.createWithType2(new ByteBuffer().writeCompressed(PackedStringKeys.URL_MOBILE_MAIL_RU).writeCompressed(PackedStringKeys.API_JAMS_STATE).getStringAndClear());
            if (httpClient.getResponseCode() == HTTP_OK) {
                Vector children = new ByteBuffer(httpClient).parseXml().children;
                int i = children.size();
                while (--i >= 0) {
                    XmlElement child = (XmlElement) children.elementAt(i);
                    if (!child.tagName.equals("city")) continue;
                    String cityId = child.getIntAttribute(PackedStringKeys.ATTR_ID);
                    Vector regions = MapState.getMapPoints();
                    int j = regions.size();
                    GeoRegion region = null;
                    while (--j >= 0) {
                        GeoRegion candidate = (GeoRegion) regions.elementAt(j);
                        if (candidate.description.equals(cityId)) {
                            region = candidate;
                            break;
                        }
                    }
                    if (region == null) continue;
                    int mapType = child.getAttrAsInt(ATTR_TRAFFIC_TYPE);
                    int zoomLevel = child.getAttrAsInt(ATTR_ZOOM_BOUNDS);
                    region.zoomLevel = zoomLevel;
                    region.mapType = mapType;
                }
            }
        } catch (Throwable ignored) {
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskDelayedClose() {
        Object connObj = this.taskData;
        try {
            Thread.sleep(DELAYED_CLOSE_MS);
        } catch (Throwable e) {
        }
        Vector closeQueue = UIState.getMediaVolume();
        if (closeQueue == null) return;
        synchronized (closeQueue) {
            closeQueue.addElement(connObj);
        }
    }

    private void taskTileLoader() {
        StringUtils.tileLoaderLoop();
    }

    private void taskFetchMapPoints() {
        Object requestData = this.taskData;
        HttpClient httpClient = null;
        Object[] contactInfo = null;
        try {
            NetworkLock.acquireNetworkLock();
            contactInfo = XmppContactGroup.getContactInfoFromState(RES_MAP_POINTS_URL);
            httpClient = HttpClient.createWithType2(requestData);
            if (httpClient.getResponseCode() == HTTP_OK) {
                ChatState.setMessageList(XmppContactGroup.parseMapPointsFromStr(new ByteBuffer(httpClient).readUTFWithLen()));
            } else {
                ChatState.setMessageList(ObjectPool.newVector());
            }
        } catch (Throwable e) {
            ChatState.setMessageList(ObjectPool.newVector());
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            XmppContactGroup.removeContactInfoFromQueue(contactInfo);
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskFetchGeoConfig() {
        HttpClient httpClient = null;
        try {
            NetworkLock.acquireNetworkLock();
            httpClient = HttpClient.createWithType2(new ByteBuffer().writeCompressed(PackedStringKeys.URL_MOBILE_MAIL_RU).writeCompressed(PackedStringKeys.API_ROAD_INFO).writeEncodedInt(MapKeys.URL_GEO_CONFIG).getStringAndClear());
            if (httpClient.getResponseCode() == HTTP_OK) {
                StringUtils.parseGeoConfig(new ByteBuffer(httpClient).parseXml());
            }
        } catch (Throwable ignored) {
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskFetchMmpRoute() {
        String requestUrl = (String) this.taskData;
        HttpClient httpClient = null;
        Object[] contactInfo = null;
        try {
            NetworkLock.acquireNetworkLock();
            contactInfo = XmppContactGroup.getContactInfoFromState(RES_MMP_ROUTE_URL);
            httpClient = HttpClient.createWithType2(requestUrl);
            if (httpClient.getResponseCode() == HTTP_OK) {
                MmpContact.parseRouteFromJson(new ByteBuffer(httpClient));
                if (!MmpContact.routeRegions.isEmpty()) {
                    MmpContact.setLocationEnabled(true);
                    Object[] firstEntry;
                    MapRenderer.setPosition(
                            !MmpContact.routeRegions.isEmpty() && (firstEntry = (Object[]) ((Object[]) MmpContact.routeRegions.firstElement())[1]).length > 0 ? (long) ((int[]) ((Object[]) firstEntry[1])[0])[0] : 0L,
                            !MmpContact.routeRegions.isEmpty() && (firstEntry = (Object[]) ((Object[]) MmpContact.routeRegions.firstElement())[1]).length > 0 ? (long) ((int[]) ((Object[]) firstEntry[1])[0])[1] : 0L);
                }
            } else {
                EventDispatcher.postNotification(ResourceAccessor.str(StringResKeys.STR_DOWNLOAD_COMPLETE));
            }
        } catch (Throwable e) {
            EventDispatcher.postNotification(ResourceAccessor.str(StringResKeys.STR_DOWNLOAD_COMPLETE));
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            XmppContactGroup.removeContactInfoFromQueue(contactInfo);
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskPeriodicTimeSync() throws Throwable {
        XmppContactGroup.periodicTimeSync();
    }

    private void taskDownloadCachedPhoto() {
        String requestUrl = (String) this.taskData;
        HttpClient httpClient = null;
        try {
            NetworkLock.acquireNetworkLock();
            httpClient = HttpClient.createWithType2(requestUrl);
            if (httpClient.getResponseCode() == HTTP_OK) {
                ByteBuffer responseBuffer = new ByteBuffer(httpClient);
                synchronized (ServiceRegistry.photoCache) {
                    String photoKey = ServiceRegistry.pendingPhotoKey;
                    ChunkedRecordStore.writeChunkedRecord(StringUtils.concat("upi", photoKey), responseBuffer);
                    try {
                        ServiceRegistry.photoCache.put(photoKey, responseBuffer.toImage());
                    } catch (Throwable ignored) {
                    }
                    ServiceRegistry.pendingPhotoKey = null;
                    MapRenderer.needsRedraw = true;
                }
            }
        } catch (Throwable ignored) {
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskFetchSharedContacts() {
        ApiClient.fetchSharedContacts((String) this.taskData);
    }

    private void taskHttpFireAndForget() {
        String requestUrl = (String) this.taskData;
        HttpClient httpClient = null;
        try {
            NetworkLock.acquireNetworkLock();
            httpClient = HttpClient.createWithType2(requestUrl);
            if (httpClient.getResponseCode() == HTTP_OK) {
                new ByteBuffer(httpClient);
            }
        } catch (Throwable ignored) {
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskSendSmsRequest() {
        ApiClient.sendSmsRequest(this.taskData);
    }

    private void taskSendDiagnostic() {
        DiagnosticReporter.sendDiagnosticReport((String) this.taskData);
    }

    private void taskFetchSavedLocations() {
        Object[] args = (Object[]) this.taskData;
        HttpClient httpClient = null;
        Object[] contactInfo = null;
        try {
            NetworkLock.acquireNetworkLock();
            contactInfo = XmppContactGroup.getContactInfoFromState(RES_SAVED_LOCATIONS_URL);
            httpClient = HttpClient.createWithType2(args[0]);
            if (httpClient.getResponseCode() == HTTP_OK) {
                long[] coords = (long[]) args[1];
                MapController.savedLocations = VCard.parseMapPointsFromJson(new ByteBuffer(httpClient), coords[0], coords[1]);
                EventDispatcher.postEvent(new ProtocolEvent(ProtocolEvent.MAP_LOCATIONS_LOADED, null));
            }
        } catch (Throwable ignored) {
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            XmppContactGroup.removeContactInfoFromQueue(contactInfo);
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskParseContactsSync() {
        Object[] args = (Object[]) this.taskData;
        HttpClient httpClient = null;
        Object[] contactInfo = null;
        try {
            NetworkLock.acquireNetworkLock();
            contactInfo = XmppContactGroup.getContactInfoFromState(RES_CONTACTS_SYNC_URL);
            httpClient = HttpClient.createWithType2(args[0]);
            if (httpClient.getResponseCode() == HTTP_OK) {
                ContactListParser.parseContactsSync(new ByteBuffer(httpClient), (Integer) args[1]);
            }
        } catch (Throwable ignored) {
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            XmppContactGroup.removeContactInfoFromQueue(contactInfo);
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskParseContactsAsync() {
        Object[] args = (Object[]) this.taskData;
        HttpClient httpClient = null;
        Object[] contactInfo = null;
        try {
            NetworkLock.acquireNetworkLock();
            contactInfo = XmppContactGroup.getContactInfoFromState(RES_CONTACTS_SYNC_URL);
            httpClient = HttpClient.createWithType2(args[0]);
            if (httpClient.getResponseCode() == HTTP_OK) {
                ContactListParser.parseContactsAsync(new ByteBuffer(httpClient), args[1], args[2]);
            }
        } catch (Throwable ignored) {
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            XmppContactGroup.removeContactInfoFromQueue(contactInfo);
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskFetchLocationProfile() {
        Object[] args = (Object[]) this.taskData;
        HttpClient httpClient = null;
        Object[] contactInfo = null;
        try {
            NetworkLock.acquireNetworkLock();
            contactInfo = XmppContactGroup.getContactInfoFromState(RES_CONTACTS_SYNC_URL);
            String baseUrl = ResourceAccessor.str(PackedStringKeys.URL_GEO_OBJECT_SEARCH_2);
            httpClient = HttpClient.createWithType2(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(baseUrl).append(args[1]).append(ResourceAccessor.str(PackedStringKeys.PARAM_Y_EQ)).append(args[2]).append(ResourceAccessor.str(PackedStringKeys.PARAM_MAP_BESTOBJECT))));
            if (httpClient.getResponseCode() == HTTP_OK) {
                long[] coords = (long[]) args[3];
                MrimAccount account = (MrimAccount) args[0];
                Vector mapPoints = VCard.parseMapPointsFromJson(new ByteBuffer(httpClient), coords[0], coords[1]);
                account.profileManager.setMapLocation((MapPoint) mapPoints.firstElement());
                EventDispatcher.postAccountEvent(account);
            } else {
                MrimAccount account = (MrimAccount) args[0];
                account.profileManager.setSimpleLocation((String) args[1], (String) args[2]);
                EventDispatcher.postAccountEvent(account);
            }
        } catch (Throwable e) {
            MrimAccount account = (MrimAccount) args[0];
            account.profileManager.setSimpleLocation((String) args[1], (String) args[2]);
            EventDispatcher.postAccountEvent(account);
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            XmppContactGroup.removeContactInfoFromQueue(contactInfo);
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskExecuteRegistration() {
        RegistrationService.executeRegRequest((Object[]) this.taskData);
    }

    private void taskSendSmsDirect() {
        Object[] args = (Object[]) this.taskData;
        try {
            NetworkLock.acquireNetworkLock();
            String smsText = (String) args[1];
            String smsAddress = StringUtils.concatKeyObj(PackedStringKeys.SCHEME_SMS, args[0]);
            MessageConnection msgConn = null;
            try {
                Thread.sleep(SMS_SEND_DELAY_MS);
                msgConn = (MessageConnection) IOUtils.registerResource((Object) Connector.open(smsAddress));
                TextMessage textMsg = (TextMessage) msgConn.newMessage(ResourceAccessor.str(PackedStringKeys.CONTENT_TYPE_TEXT));
                textMsg.setAddress(smsAddress);
                textMsg.setPayloadText(smsText);
                msgConn.send((Message) textMsg);
            } catch (Throwable e) {
                IOUtils.closeConn(msgConn);
                throw e;
            }
            IOUtils.closeConn((Connection) msgConn);
            args[2] = args;
        } catch (Throwable e) {
            args[2] = e;
        } finally {
            NetworkLock.releaseNetworkLock();
        }
    }

    private void taskWaitForCompletion() throws InterruptedException {
        Object[] data = (Object[]) this.taskData;
        int remaining = COMPLETION_TIMEOUT_MS;
        do {
            remaining -= COMPLETION_POLL_MS;
            if (remaining < 0) {
                EventDispatcher.postEvent(new AccountDataEvent(data));
                return;
            }
            Thread.sleep(COMPLETION_POLL_MS);
        } while (!AppController.isShuttingDown);
    }

    private void taskProcessXmppStream() {
        XmppProtocol.processXmppStream((Object[]) this.taskData);
    }

    private void taskPerformXmppAuth() {
        XmppMailRuProtocol.performXmppAuth((Object[]) this.taskData);
    }

    private void taskFetchHistory() {
        Conversation.fetchHistory((Object[]) this.taskData);
    }

    private void taskFetchUpdateStatus() {
        RegistrationService.fetchUpdateStatus();
    }

    private void taskResolveXmppServer() {
        XmppMailRuProtocol.resolveXmppServer((Object[]) this.taskData);
    }

    private void taskXmppHttpAuth() {
        Object[] args = (Object[]) this.taskData;
        HttpClient httpClient = null;
        try {
            NetworkLock.acquireNetworkLock();
            httpClient = HttpClient.createHttpClient((String) args[2], (Account) args[0], 0);
            int responseCode = httpClient.getResponseCode();
            if (responseCode == HTTP_OK) {
                XmlElement xmlResponse = new ByteBuffer(httpClient).parseXmlStr();
                if (((Integer) args[3]).intValue() != 0) {
                    XmppMailRuProtocol xmppProto = (XmppMailRuProtocol) args[0];
                    if (StringUtils.matchesKey(PackedStringKeys.TAG_ERROR, xmlResponse.tagName)) {
                        xmppProto.handleComplete();
                    } else {
                        xmppProto.serverResourceId = StringUtils.concatKey(PackedStringKeys.ATTR_ID, StringUtils.fromBuffer(xmlResponse.findChildByKey(PackedStringKeys.ATTR_ID).textContent));
                    }
                } else {
                    String nextUrl = new ByteBuffer()
                        .writeCompressed(PackedStringKeys.URL_VK_AUTH_GET)
                        .writeCompressed(PackedStringKeys.AUTH_SESSION_NONCE)
                        .writeObjectStr((String) args[1])
                        .writeCompressed(PackedStringKeys.PARAM_LOGIN)
                        .writeObjectStr((String) args[4])
                        .writeCompressed(PackedStringKeys.PARAM_DIGEST)
                        .writeRawString(
                            new ByteBuffer()
                                .writeCompressed(PackedStringKeys.VK_API_SECRET)
                                .writeByte(COLON)
                                .writeObjectStr((String) args[1])
                                .writeByte(COLON)
                                .writeRawString(StringUtils.fromBuffer(xmlResponse.findChildByKey(PackedStringKeys.TAG_TOKEN).textContent))
                                .writeByte(COLON)
                                .writeRawString(
                                    new ByteBuffer()
                                        .writeObjectStr((String) args[4])
                                        .writeCompressed(PackedStringKeys.DOMAIN_VK_COM)
                                        .writeObjectStr((String) args[5])
                                        .encryptMD5()
                                        .toHexString()
                                )
                                .encryptMD5()
                                .toHexString()
                        )
                        .readAllByteStr();
                    args[2] = nextUrl;
                    args[3] = ObjectPool.integerOf(1);
                    new AsyncTask(AsyncTaskId.XMPP_HTTP_AUTH, args);
                }
            } else {
                ((XmppProtocol) args[0]).setException(new Throwable(StringUtils.intern(Integer.toString(responseCode))));
            }
        } catch (Throwable error) {
            ((XmppProtocol) args[0]).setException(error);
        } finally {
            HttpClient.closeAndUpdateStats(httpClient);
            NetworkLock.releaseNetworkLock();
        }
    }
}
