package com.trykote.mobileagent.core;


import com.trykote.mobileagent.map.GeoRegion;
import com.trykote.mobileagent.map.MapPoint;
import com.trykote.mobileagent.map.MapRenderer;
import com.trykote.mobileagent.model.ContactListParser;
import com.trykote.mobileagent.model.Conversation;
import com.trykote.mobileagent.model.VCard;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.protocol.Account;
import com.trykote.mobileagent.protocol.AccountManager;
import com.trykote.mobileagent.protocol.ConnectionThread;
import com.trykote.mobileagent.protocol.mmp.MmpContact;
import com.trykote.mobileagent.protocol.mrim.MrimAccount;
import com.trykote.mobileagent.protocol.xmpp.XmppContactGroup;
import com.trykote.mobileagent.protocol.xmpp.XmppMailRuProtocol;
import com.trykote.mobileagent.protocol.xmpp.XmppProtocol;
import com.trykote.mobileagent.ui.MenuItem;
import com.trykote.mobileagent.ui.Screen;
import com.trykote.mobileagent.util.*;

import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;
import java.util.Vector;

/* renamed from: z */
/* loaded from: MobileAgent_3.9.jar:z.class */
public final class AsyncTask implements Runnable, CommandListener {

    /* renamed from: b */
    private int taskId;

    /* renamed from: c */
    private Object taskData;

    /* renamed from: a */
    public Thread thread;

    public AsyncTask(Object obj, int i, int i2) {
        RemoteLogger.log("TASK", "new AsyncTask type=" + i + " param=" + i2);
        if (obj != null) {
            AppController.dispatchCommand(obj, i, i2);
            return;
        }
        if (AppController.appLock != null) {
            synchronized (AppController.appLock) {
                AppController.isShuttingDown = true;
                ResourceManager.clearMathTables();
                ResourceManager.clearImageCache();
                Vector tasks = AppState.getVector(StateKeys.SLOT_MEDIA_CONTROL);
                if (tasks != null) {
                    synchronized (tasks) {
                        AppState.clearIndex(StateKeys.SLOT_MEDIA_CONTROL);
                    }
                }
                SocketWrapper.closeAll();
                AccountManager.saveState(AppController.saveOnExit, true);
                AppState.saveDelta(AppController.saveOnExit);
            }
        }
    }

    public AsyncTask() {
    }

    public AsyncTask(Screen screen, MenuItem menuItem) {
        Object[] objArr = (Object[]) menuItem.data;
        this.taskData = new Object[]{screen, menuItem};
        String str = (String) objArr[0];
        int maxLen = ((Integer) objArr[1]).intValue();
        XmppContactGroup.showTextInputDialog(AppState.emptyStr, str.length() > maxLen ? StringUtils.prefix(str, maxLen) : str, maxLen, ((Integer) objArr[2]).intValue(), (String) objArr[3], 1053, 1055, this);
    }

    public final void commandAction(Command command, Displayable displayable) {
        if (this.taskData == null) {
            String text = StringUtils.getTextBoxString((TextBox) displayable);
            AppState.setObject(StateKeys.SLOT_STATUS_TEXT, (Object) text);
            AppState.setBool(StateKeys.FLAG_STATUS_TEXT_SET, !StringUtils.isEmpty(text));
            if (command.getPriority() == 0) {
                IOUtils.postOkEvent();
                return;
            } else {
                IOUtils.postCancelEvent();
                return;
            }
        }
        if (command.getPriority() == 0) {
            String inputText = StringUtils.intern(((TextBox) displayable).getString());
            Screen screen = (Screen) ((Object[]) this.taskData)[0];
            MenuItem menuItem = (MenuItem) ((Object[]) this.taskData)[1];
            Object[] objArr = (Object[]) menuItem.data;
            if (!StringUtils.equalsObj(inputText, objArr[0])) {
                objArr[0] = inputText;
                String displayText = ((Integer) objArr[2]).intValue() != 327680 ? inputText : Utils.maskPassword(inputText);
                String str = StringUtils.isEmpty(displayText) ? null : displayText;
                menuItem.clear();
                if (objArr[4] instanceof String) {
                    menuItem.setLabel(Utils.appendSpace((String) objArr[4]));
                } else {
                    menuItem.setIcon(((Integer) objArr[4]).intValue());
                }
                if (str != null) {
                    menuItem.addText(str, 1, 7);
                } else {
                    menuItem.setDefaultFont();
                }
                screen.rebuildItems();
            }
        }
        AppState.setScreen(AppState.getCanvas().updateCommands());
    }

    public AsyncTask(int i) {
        this(i, (Object) null);
    }

    public AsyncTask(int i, Object obj) {
        this.taskId = i;
        this.taskData = obj;
        Thread thread = new Thread(this);
        this.thread = thread;
        thread.start();
    }

    public final void run() {
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
        AppController.onSoftKeyPressed();
    }

    private void taskDownloadPhoto() {
        Object[] args = (Object[]) this.taskData;
        HttpClient httpClient = null;
        Object result = null;
        try {
            NetworkLock.acquireNetworkLock();
            httpClient = HttpClient.createWithType3(args[0]);
            result = httpClient.getResponseCode() == 200 ? new ByteBuffer(httpClient).toImage() : ResourceManager.integerOf(465);
        } catch (Throwable e) {
            result = ResourceManager.integerOf(466);
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
            httpClient = HttpClient.createHttpClient(AppState.getString(StateKeys.STR_RES_HUGE_URL_3), null, 3);
            args[0] = httpClient.getResponseCode() == 200 ? new ByteBuffer(httpClient) : ResourceManager.integerOf(731);
        } catch (Throwable e) {
            args[0] = ResourceManager.integerOf(731);
            return;
        }
        HttpClient.closeAndUpdateStats(httpClient);
        NetworkLock.releaseNetworkLock();
    }

    private void taskConnectionLoop() {
        while (true) {
            Vector tasks = AppState.getVector(StateKeys.SLOT_MEDIA_CONTROL);
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
            Vector closeQueue = AppState.getVector(StateKeys.SLOT_MEDIA_VOLUME);
            if (closeQueue != null) {
                synchronized (closeQueue) {
                    IOUtils.closeConn((Connection) Utils.dequeue(closeQueue));
                }
            }
            try {
                Thread.sleep(100);
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
            httpClient = HttpClient.createWithType2(new ByteBuffer().writeCompressed(1442705).writeCompressed(987652).getStringAndClear());
            if (httpClient.getResponseCode() != 200) throw new Throwable();
            Vector children = new ByteBuffer(httpClient).parseXml().children;
            int i = children.size();
            while (--i >= 0) {
                XmlElement child = (XmlElement) children.elementAt(i);
                if (!child.tagName.equals("city")) continue;
                String cityId = child.getIntAttribute(131550);
                Vector regions = AppState.getVector(StateKeys.VEC_MAP_POINTS);
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
                int mapType = child.getAttrAsInt(29300);
                int zoomLevel = child.getAttrAsInt(98);
                region.zoomLevel = zoomLevel;
                region.mapType = mapType;
            }
        } catch (Throwable e) {
            return;
        }
        HttpClient.closeAndUpdateStats(httpClient);
        NetworkLock.releaseNetworkLock();
    }

    private void taskDelayedClose() {
        Object connObj = this.taskData;
        try {
            Thread.sleep(1000);
        } catch (Throwable e) {
        }
        Vector closeQueue = AppState.getVector(StateKeys.SLOT_MEDIA_VOLUME);
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
            contactInfo = XmppContactGroup.getContactInfoFromState(872);
            httpClient = HttpClient.createWithType2(requestData);
            if (httpClient.getResponseCode() != 200) throw new Throwable();
            Object mapPoints = XmppContactGroup.parseMapPointsFromStr(new ByteBuffer(httpClient).readUTFWithLen());
            AppState.pool[StateKeys.VEC_MESSAGE_LIST] = mapPoints;
        } catch (Throwable e) {
            AppState.pool[StateKeys.VEC_MESSAGE_LIST] = ObjectPool.newVector();
        }
        HttpClient.closeAndUpdateStats(httpClient);
        XmppContactGroup.removeContactInfoFromQueue(contactInfo);
        NetworkLock.releaseNetworkLock();
    }

    private void taskFetchGeoConfig() {
        HttpClient httpClient = null;
        try {
            NetworkLock.acquireNetworkLock();
            httpClient = HttpClient.createWithType2(new ByteBuffer().writeCompressed(1442705).writeCompressed(1905127).writeEncodedInt(254).getStringAndClear());
            if (httpClient.getResponseCode() != 200) throw new Throwable();
            StringUtils.parseGeoConfig(new ByteBuffer(httpClient).parseXml());
        } catch (Throwable e) {
            return;
        }
        HttpClient.closeAndUpdateStats(httpClient);
        NetworkLock.releaseNetworkLock();
    }

    private void taskFetchMmpRoute() {
        String requestUrl = (String) this.taskData;
        HttpClient httpClient = null;
        Object[] contactInfo = null;
        try {
            NetworkLock.acquireNetworkLock();
            contactInfo = XmppContactGroup.getContactInfoFromState(1000);
            httpClient = HttpClient.createWithType2(requestUrl);
            if (httpClient.getResponseCode() != 200) throw new Throwable();
            MmpContact.parseRouteFromJson(new ByteBuffer(httpClient));
            if (!MmpContact.routeRegions.isEmpty()) {
                MmpContact.setLocationEnabled(true);
                Object[] firstEntry;
                MapRenderer.setPosition(
                        !MmpContact.routeRegions.isEmpty() && (firstEntry = (Object[]) ((Object[]) MmpContact.routeRegions.firstElement())[1]).length > 0 ? (long) ((int[]) ((Object[]) firstEntry[1])[0])[0] : 0L,
                        !MmpContact.routeRegions.isEmpty() && (firstEntry = (Object[]) ((Object[]) MmpContact.routeRegions.firstElement())[1]).length > 0 ? (long) ((int[]) ((Object[]) firstEntry[1])[0])[1] : 0L);
            }
        } catch (Throwable e) {
            IOUtils.postNotification(AppState.getString(StateKeys.STR_DOWNLOAD_COMPLETE));
        }
        HttpClient.closeAndUpdateStats(httpClient);
        XmppContactGroup.removeContactInfoFromQueue(contactInfo);
        NetworkLock.releaseNetworkLock();
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
            if (httpClient.getResponseCode() != 200) throw new Throwable();
            ByteBuffer responseBuffer = new ByteBuffer(httpClient);
            synchronized (ServiceRegistry.photoCache) {
                String photoKey = ServiceRegistry.pendingPhotoKey;
                XmppMailRuProtocol.writeChunkedRecord(StringUtils.concat("upi", photoKey), responseBuffer);
                try {
                    ServiceRegistry.photoCache.put(photoKey, responseBuffer.toImage());
                } catch (Throwable e) {
                }
                ServiceRegistry.pendingPhotoKey = null;
                MapRenderer.needsRedraw = true;
            }
        } catch (Throwable e) {
            return;
        }
        HttpClient.closeAndUpdateStats(httpClient);
        NetworkLock.releaseNetworkLock();
    }

    private void taskFetchSharedContacts() {
        ResourceManager.fetchSharedContacts((String) this.taskData);
    }

    private void taskHttpFireAndForget() {
        String requestUrl = (String) this.taskData;
        HttpClient httpClient = null;
        try {
            NetworkLock.acquireNetworkLock();
            httpClient = HttpClient.createWithType2(requestUrl);
            if (httpClient.getResponseCode() != 200) throw new Throwable();
            new ByteBuffer(httpClient);
        } catch (Throwable e) {
            return;
        }
        HttpClient.closeAndUpdateStats(httpClient);
        NetworkLock.releaseNetworkLock();
    }

    private void taskSendSmsRequest() {
        ResourceManager.sendSmsRequest(this.taskData);
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
            contactInfo = XmppContactGroup.getContactInfoFromState(370);
            httpClient = HttpClient.createWithType2(args[0]);
            if (httpClient.getResponseCode() != 200) throw new Throwable();
            long[] coords = (long[]) args[1];
            ResourceManager.savedLocations = VCard.parseMapPointsFromJson(new ByteBuffer(httpClient), coords[0], coords[1]);
            IOUtils.postEvent(new ProtocolEvent(ProtocolEvent.MAP_LOCATIONS_LOADED, null));
        } catch (Throwable e) {
            return;
        }
        HttpClient.closeAndUpdateStats(httpClient);
        XmppContactGroup.removeContactInfoFromQueue(contactInfo);
        NetworkLock.releaseNetworkLock();
    }

    private void taskParseContactsSync() {
        Object[] args = (Object[]) this.taskData;
        HttpClient httpClient = null;
        Object[] contactInfo = null;
        try {
            NetworkLock.acquireNetworkLock();
            contactInfo = XmppContactGroup.getContactInfoFromState(505);
            httpClient = HttpClient.createWithType2(args[0]);
            if (httpClient.getResponseCode() != 200) throw new Throwable();
            ContactListParser.parseContactsSync(new ByteBuffer(httpClient), (Integer) args[1]);
        } catch (Throwable e) {
            return;
        }
        HttpClient.closeAndUpdateStats(httpClient);
        XmppContactGroup.removeContactInfoFromQueue(contactInfo);
        NetworkLock.releaseNetworkLock();
    }

    private void taskParseContactsAsync() {
        Object[] args = (Object[]) this.taskData;
        HttpClient httpClient = null;
        Object[] contactInfo = null;
        try {
            NetworkLock.acquireNetworkLock();
            contactInfo = XmppContactGroup.getContactInfoFromState(505);
            httpClient = HttpClient.createWithType2(args[0]);
            if (httpClient.getResponseCode() != 200) throw new Throwable();
            ContactListParser.parseContactsAsync(new ByteBuffer(httpClient), args[1], args[2]);
        } catch (Throwable e) {
            return;
        }
        HttpClient.closeAndUpdateStats(httpClient);
        XmppContactGroup.removeContactInfoFromQueue(contactInfo);
        NetworkLock.releaseNetworkLock();
    }

    private void taskFetchLocationProfile() {
        Object[] args = (Object[]) this.taskData;
        HttpClient httpClient = null;
        Object[] contactInfo = null;
        try {
            NetworkLock.acquireNetworkLock();
            contactInfo = XmppContactGroup.getContactInfoFromState(505);
            String baseUrl = AppState.getString(StateKeys.STR_RES_MEGA_URL_2);
            httpClient = HttpClient.createWithType2(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(baseUrl).append(args[1]).append(AppState.getString(StateKeys.STR_RES_NEWLINE)).append(args[2]).append(AppState.getString(StateKeys.STR_RES_VERY_LONG_API_2))));
            if (httpClient.getResponseCode() != 200) throw new Throwable();
            long[] coords = (long[]) args[3];
            MrimAccount account = (MrimAccount) args[0];
            Vector mapPoints = VCard.parseMapPointsFromJson(new ByteBuffer(httpClient), coords[0], coords[1]);
            account.setLocationProfile((MapPoint) mapPoints.firstElement());
            IOUtils.postAccountEvent(account);
        } catch (Throwable e) {
            String lat = (String) args[2];
            String lon = (String) args[1];
            MrimAccount account = (MrimAccount) args[0];
            account.setSimpleProfile(lon, lat);
            IOUtils.postAccountEvent(account);
            return;
        }
        HttpClient.closeAndUpdateStats(httpClient);
        XmppContactGroup.removeContactInfoFromQueue(contactInfo);
        NetworkLock.releaseNetworkLock();
    }

    private void taskExecuteRegistration() {
        RegistrationService.executeRegRequest((Object[]) this.taskData);
    }

    private void taskSendSmsDirect() {
        Object[] args = (Object[]) this.taskData;
        try {
            NetworkLock.acquireNetworkLock();
            String smsText = (String) args[1];
            String smsAddress = StringUtils.concatKeyObj(398209, args[0]);
            MessageConnection msgConn = null;
            try {
                Thread.sleep(100L);
                msgConn = (MessageConnection) IOUtils.registerResource((Object) Connector.open(smsAddress));
                TextMessage textMsg = (TextMessage) msgConn.newMessage(AppState.getString(StateKeys.STR_RES_BRACKET_OPEN));
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
            return;
        }
        NetworkLock.releaseNetworkLock();
    }

    private void taskWaitForCompletion() throws InterruptedException {
        AppController.waitForCompletion((Object[]) this.taskData);
    }

    private void taskProcessXmppStream() {
        ResourceManager.processXmppStream((Object[]) this.taskData);
    }

    private void taskPerformXmppAuth() {
        IOUtils.performXmppAuth((Object[]) this.taskData);
    }

    private void taskFetchHistory() {
        Conversation.fetchHistory((Object[]) this.taskData);
    }

    private void taskFetchUpdateStatus() {
        ResourceManager.fetchUpdateStatus();
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
            if (responseCode != 200) throw new Throwable(StringUtils.intern(Integer.toString(responseCode)));
            XmlElement xmlResponse = new ByteBuffer(httpClient).parseXmlStr();
            if (((Integer) args[3]).intValue() != 0) {
                XmppMailRuProtocol xmppProto = (XmppMailRuProtocol) args[0];
                if (StringUtils.matchesKey(333441, xmlResponse.tagName)) {
                    xmppProto.handleComplete();
                } else {
                    xmppProto.serverResourceId = StringUtils.concatKey(131550, StringUtils.fromBuffer(xmlResponse.findChildByKey(131550).textContent));
                }
            } else {
                String nextUrl = new ByteBuffer()
                    .writeCompressed(4069357)
                    .writeCompressed(1316925)
                    .writeObjectStr(args[1])
                    .writeCompressed(463517)
                    .writeObjectStr(args[4])
                    .writeCompressed(530513)
                    .writeRawString(
                        new ByteBuffer()
                            .writeCompressed(3282875)
                            .writeByte(58)
                            .writeObjectStr(args[1])
                            .writeByte(58)
                            .writeRawString(StringUtils.fromBuffer(xmlResponse.findChildByKey(330583).textContent))
                            .writeByte(58)
                            .writeRawString(
                                new ByteBuffer()
                                    .writeObjectStr(args[4])
                                    .writeCompressed(530521)
                                    .writeObjectStr(args[5])
                                    .encryptMD5()
                                    .toHexString()
                            )
                            .encryptMD5()
                            .toHexString()
                    )
                    .readAllByteStr();
                args[2] = nextUrl;
                args[3] = ResourceManager.integerOf(1);
                new AsyncTask(AsyncTaskId.XMPP_HTTP_AUTH, args);
            }
        } catch (Throwable error) {
            ((XmppProtocol) args[0]).setException(error);
            return;
        }
        HttpClient.closeAndUpdateStats(httpClient);
        NetworkLock.releaseNetworkLock();
    }
}
