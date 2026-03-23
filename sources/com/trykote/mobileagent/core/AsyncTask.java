package com.trykote.mobileagent.core;


import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;

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
                Vector tasks = AppState.getVector(1358);
                if (tasks != null) {
                    synchronized (tasks) {
                        AppState.clearIndex(1358);
                    }
                }
                NetworkUtils.closeAllConnections();
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
            AppState.setObject(1279, (Object) text);
            AppState.setBool(1456, !StringUtils.isEmpty(text));
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

    /*  JADX ERROR: Types fix failed
        java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryPossibleTypes(FixTypesVisitor.java:183)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:242)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
        */
    /* JADX DEBUG: Another duplicated slice has different insns count: {[INVOKE, CONST]}, finally: {[INVOKE] complete} */
    /* JADX DEBUG: Incorrect finally slice size: {[INVOKE, CONST] complete}, expected: {[INVOKE] complete} */
    /* JADX WARN: Failed to calculate best type for var: r9v4 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:156)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:133)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:238)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:221)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:91)
     */
    /* JADX WARN: Failed to calculate best type for var: r9v4 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
     */
    /* JADX WARN: Failed to calculate best type for var: r9v7 ??
    java.lang.NullPointerException: Cannot invoke "jadx.core.dex.instructions.args.InsnArg.getType()" because "changeArg" is null
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.moveListener(TypeUpdate.java:439)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.runListeners(TypeUpdate.java:232)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.requestUpdate(TypeUpdate.java:212)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeForSsaVar(TypeUpdate.java:183)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.updateTypeChecked(TypeUpdate.java:112)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:83)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:56)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:145)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:123)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$2(TypeInferenceVisitor.java:101)
    	at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:101)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
     */
    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Not initialized variable reg: 10, insn: 0x0105: MOVE (r2 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:18:0x00fc */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x00fc: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:18:0x00fc */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x0a79: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:341:0x0a79 */
    @Override // java.lang.Runnable
    /* Decompiled by CFR (JADX failed on this method) */
    public final void run() {
        RemoteLogger.log("TASK", "run taskId=" + this.taskId);
        try {
            switch (this.taskId) {
                case 0: {
                    AppController.onSoftKeyPressed();
                    return;
                }
                case 1: {
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
                    return;
                }
                case 2: {
                    Object[] args = (Object[]) this.taskData;
                    HttpClient httpClient = null;
                    try {
                        NetworkLock.acquireNetworkLock();
                        httpClient = HttpClient.createHttpClient(AppState.getString(2295208), null, 3);
                        args[0] = httpClient.getResponseCode() == 200 ? new ByteBuffer(httpClient) : ResourceManager.integerOf(731);
                    } catch (Throwable e) {
                        args[0] = ResourceManager.integerOf(731);
                        return;
                    }
                    HttpClient.closeAndUpdateStats(httpClient);
                    NetworkLock.releaseNetworkLock();
                    return;
                }
                case 3: {
                    while (true) {
                        Vector tasks = AppState.getVector(1358);
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
                        Vector closeQueue = AppState.getVector(1359);
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
                case 4: {
                    NetworkUtils.asyncReaderLoop((Object[]) this.taskData);
                    return;
                }
                case 5: {
                    ConnectionThread.executeWithReauth((Object[]) this.taskData);
                    return;
                }
                case 6: {
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
                            Vector regions = AppState.getVector(1389);
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
                    return;
                }
                case 7: {
                    Object connObj = this.taskData;
                    try {
                        Thread.sleep(1000);
                    } catch (Throwable e) {
                    }
                    Vector closeQueue = AppState.getVector(1359);
                    if (closeQueue == null) return;
                    synchronized (closeQueue) {
                        closeQueue.addElement(connObj);
                        return;
                    }
                }
                case 8: {
                    StringUtils.tileLoaderLoop();
                    return;
                }
                case 9: {
                    Object requestData = this.taskData;
                    HttpClient httpClient = null;
                    Object[] contactInfo = null;
                    try {
                        NetworkLock.acquireNetworkLock();
                        contactInfo = XmppContactGroup.getContactInfoFromState(872);
                        httpClient = HttpClient.createWithType2(requestData);
                        if (httpClient.getResponseCode() != 200) throw new Throwable();
                        Object mapPoints = XmppContactGroup.parseMapPointsFromStr(new ByteBuffer(httpClient).readUTFWithLen());
                        AppState.pool[1399] = mapPoints;
                    } catch (Throwable e) {
                        AppState.pool[1399] = NetworkUtils.newVector();
                    }
                    HttpClient.closeAndUpdateStats(httpClient);
                    XmppContactGroup.removeContactInfoFromQueue(contactInfo);
                    NetworkLock.releaseNetworkLock();
                    return;
                }
                case 10: {
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
                    return;
                }
                case 11: {
                    String requestUrl = (String) this.taskData;
                    HttpClient httpClient = null;
                    Object[] contactInfo = null;
                    try {
                        NetworkLock.acquireNetworkLock();
                        contactInfo = XmppContactGroup.getContactInfoFromState(1000);
                        httpClient = HttpClient.createWithType2(requestUrl);
                        if (httpClient.getResponseCode() != 200) throw new Throwable();
                        MmpContact.parseRouteFromJson(new ByteBuffer(httpClient));
                        if (MmpContact.routeRegions.size() > 0) {
                            MmpContact.setLocationEnabled(true);
                            Object[] firstEntry;
                            MapRenderer.setPosition(
                                MmpContact.routeRegions.size() > 0 && (firstEntry = (Object[]) ((Object[]) MmpContact.routeRegions.firstElement())[1]).length > 0 ? (long) ((int[]) ((Object[]) firstEntry[1])[0])[0] : 0L,
                                MmpContact.routeRegions.size() > 0 && (firstEntry = (Object[]) ((Object[]) MmpContact.routeRegions.firstElement())[1]).length > 0 ? (long) ((int[]) ((Object[]) firstEntry[1])[0])[1] : 0L);
                        }
                    } catch (Throwable e) {
                        IOUtils.postEvent((Object) AppState.getString(1001));
                    }
                    HttpClient.closeAndUpdateStats(httpClient);
                    XmppContactGroup.removeContactInfoFromQueue(contactInfo);
                    NetworkLock.releaseNetworkLock();
                    return;
                }
                case 12: {
                    return;
                }
                case 13: {
                    XmppContactGroup.periodicTimeSync();
                    return;
                }
                case 14: {
                    String requestUrl = (String) this.taskData;
                    HttpClient httpClient = null;
                    try {
                        NetworkLock.acquireNetworkLock();
                        httpClient = HttpClient.createWithType2(requestUrl);
                        if (httpClient.getResponseCode() != 200) throw new Throwable();
                        ByteBuffer responseBuffer = new ByteBuffer(httpClient);
                        synchronized (ConnectionThread.photoCache) {
                            String photoKey = ConnectionThread.pendingPhotoKey;
                            XmppMailRuProtocol.writeChunkedRecord(StringUtils.concat("upi", photoKey), responseBuffer);
                            try {
                                ConnectionThread.photoCache.put(photoKey, responseBuffer.toImage());
                            } catch (Throwable e) {
                            }
                            ConnectionThread.pendingPhotoKey = null;
                            MapRenderer.needsRedraw = true;
                        }
                    } catch (Throwable e) {
                        return;
                    }
                    HttpClient.closeAndUpdateStats(httpClient);
                    NetworkLock.releaseNetworkLock();
                    return;
                }
                case 15: {
                    ResourceManager.fetchSharedContacts((String) this.taskData);
                    return;
                }
                case 16: {
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
                    return;
                }
                case 17: {
                    ResourceManager.sendSmsRequest(this.taskData);
                    return;
                }
                case 18: {
                    NetworkUtils.sendDiagnosticReport((String) this.taskData);
                    return;
                }
                case 19: {
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
                        IOUtils.postEvent(new IOUtils(3, null));
                    } catch (Throwable e) {
                        return;
                    }
                    HttpClient.closeAndUpdateStats(httpClient);
                    XmppContactGroup.removeContactInfoFromQueue(contactInfo);
                    NetworkLock.releaseNetworkLock();
                    return;
                }
                case 20: {
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
                    return;
                }
                case 21: {
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
                    return;
                }
                case 22: {
                    Object[] args = (Object[]) this.taskData;
                    HttpClient httpClient = null;
                    Object[] contactInfo = null;
                    try {
                        NetworkLock.acquireNetworkLock();
                        contactInfo = XmppContactGroup.getContactInfoFromState(505);
                        String baseUrl = AppState.getString(3805583);
                        httpClient = HttpClient.createWithType2(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(baseUrl).append(args[1]).append(AppState.getString(201188)).append(args[2]).append(AppState.getString(1774025))));
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
                    return;
                }
                case 23: {
                    Conversation.fetchMapData((String) this.taskData);
                    return;
                }
                case 24: {
                    NetworkUtils.executeRegRequest((Object[]) this.taskData);
                    return;
                }
                case 25: {
                    return;
                }
                case 26: {
                    Object[] args = (Object[]) this.taskData;
                    try {
                        NetworkLock.acquireNetworkLock();
                        String smsText = (String) args[1];
                        String smsAddress = StringUtils.concatKeyObj(398209, args[0]);
                        MessageConnection msgConn = null;
                        try {
                            Thread.sleep(100L);
                            msgConn = (MessageConnection) IOUtils.registerResource((Object) Connector.open(smsAddress));
                            TextMessage textMsg = (TextMessage) msgConn.newMessage(AppState.getString(267133));
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
                    return;
                }
                case 27: {
                    AppController.waitForCompletion((Object[]) this.taskData);
                    return;
                }
                case 28: {
                    return;
                }
                case 29: {
                    ResourceManager.processXmppStream((Object[]) this.taskData);
                    return;
                }
                case 30: {
                    IOUtils.performXmppAuth((Object[]) this.taskData);
                    return;
                }
                case 31: {
                    Conversation.fetchHistory((Object[]) this.taskData);
                    return;
                }
                case 32: {
                    ResourceManager.fetchUpdateStatus();
                    return;
                }
                case 33: {
                    XmppMailRuProtocol.resolveXmppServer((Object[]) this.taskData);
                    return;
                }
                case 34: {
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
                            new AsyncTask(34, args);
                        }
                    } catch (Throwable error) {
                        ((XmppProtocol) args[0]).setException(error);
                        return;
                    }
                    HttpClient.closeAndUpdateStats(httpClient);
                    NetworkLock.releaseNetworkLock();
                    return;
                }
            }
            return;
        } catch (Throwable e) {
            RemoteLogger.log("TASK", "FATAL exception in run taskId=" + this.taskId, e);
            return;
        }
    }
    /* Original JADX bytecode dump preserved below:
        r8 = this;
        r0 = r8
            int r0 = r0.taskId     // Catch: java.lang.Throwable -> La84
            switch(r0) {
                case 0: goto La0;
                case 1: goto La4;
                case 2: goto L10a;
                case 3: goto L16c;
                case 4: goto L1e4;
                case 5: goto L1ef;
                case 6: goto L1fa;
                case 7: goto L2eb;
                case 8: goto L31e;
                case 9: goto L322;
                case 10: goto L3a3;
                case 11: goto L408;
                case 12: goto L4fa;
                case 13: goto L4fb;
                case 14: goto L4ff;
                case 15: goto L58b;
                case 16: goto L599;
                case 17: goto L5e2;
                case 18: goto L5ea;
                case 19: goto L5f5;
                case 20: goto L683;
                case 21: goto L6f6;
                case 22: goto L768;
                case 23: goto L846;
                case 24: goto L854;
                case 25: goto L85f;
                case 26: goto L860;
                case 27: goto L8e2;
                case 28: goto L8ed;
                case 29: goto L8ee;
                case 30: goto L8f9;
                case 31: goto L904;
                case 32: goto L90f;
                case 33: goto L913;
                case 34: goto L91e;
                default: goto La83;
            }     // Catch: java.lang.Throwable -> La84
        La0:
            p000.AppController.onSoftKeyPressed()     // Catch: java.lang.Throwable -> La84
            return
        La4:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r9 = r0
            r0 = 0
            r10 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> Le6 java.lang.Throwable -> Lfa java.lang.Throwable -> La84
            r0 = r8
            r1 = 0
            r0 = r0[r1]     // Catch: java.lang.Throwable -> Le6 java.lang.Throwable -> Lfa java.lang.Throwable -> La84
            ax r0 = p000.HttpClient.createWithType3(r0)     // Catch: java.lang.Throwable -> Le6 java.lang.Throwable -> Lfa java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> Le6 java.lang.Throwable -> Lfa java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto Ld3
            n r0 = new n     // Catch: java.lang.Throwable -> Le6 java.lang.Throwable -> Lfa java.lang.Throwable -> La84
            r1 = r0
            r2 = r9
            r1.<init>(r2)     // Catch: java.lang.Throwable -> Le6 java.lang.Throwable -> Lfa java.lang.Throwable -> La84
            javax.microedition.lcdui.Image r0 = r0.toImage()     // Catch: java.lang.Throwable -> Le6 java.lang.Throwable -> Lfa java.lang.Throwable -> La84
            r10 = r0
            goto Lda
        Ld3:
            r0 = 465(0x1d1, float:6.52E-43)
            java.lang.Integer r0 = p000.ResourceManager.integerOf(r0)     // Catch: java.lang.Throwable -> Le6 java.lang.Throwable -> Lfa java.lang.Throwable -> La84
            r10 = r0
        Lda:
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r8
            r1 = 2
            r2 = r10
            r0[r1] = r2     // Catch: java.lang.Throwable -> La84
            return
        Le6:
            r0 = 466(0x1d2, float:6.53E-43)
            java.lang.Integer r0 = p000.ResourceManager.integerOf(r0)     // Catch: java.lang.Throwable -> Lfa java.lang.Throwable -> La84
            r10 = r0
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r8
            r1 = 2
            r2 = r10
            r0[r1] = r2     // Catch: java.lang.Throwable -> La84
            return
        Lfa:
            r12 = move-exception
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r8
            r1 = 2
            r2 = r10
            r0[r1] = r2     // Catch: java.lang.Throwable -> La84
            r0 = r12
            throw r0     // Catch: java.lang.Throwable -> La84
        L10a:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r9 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L150 java.lang.Throwable -> L162 java.lang.Throwable -> La84
            r0 = 2295208(0x2305a8, float:3.216271E-39)
            java.lang.String r0 = p000.AppState.getString(r0)     // Catch: java.lang.Throwable -> L150 java.lang.Throwable -> L162 java.lang.Throwable -> La84
            r1 = 0
            r10 = r1
            r1 = 0
            r13 = r1
            r1 = 0
            r2 = 3
            ax r0 = p000.HttpClient.createHttpClient(r0, r1, r2)     // Catch: java.lang.Throwable -> L150 java.lang.Throwable -> L162 java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L150 java.lang.Throwable -> L162 java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L13f
            r0 = r8
            r1 = 0
            n r2 = new n     // Catch: java.lang.Throwable -> L150 java.lang.Throwable -> L162 java.lang.Throwable -> La84
            r3 = r2
            r4 = r9
            r3.<init>(r4)     // Catch: java.lang.Throwable -> L150 java.lang.Throwable -> L162 java.lang.Throwable -> La84
            r0[r1] = r2     // Catch: java.lang.Throwable -> L150 java.lang.Throwable -> L162 java.lang.Throwable -> La84
            goto L148
        L13f:
            r0 = r8
            r1 = 0
            r2 = 731(0x2db, float:1.024E-42)
            java.lang.Integer r2 = p000.ResourceManager.integerOf(r2)     // Catch: java.lang.Throwable -> L150 java.lang.Throwable -> L162 java.lang.Throwable -> La84
            r0[r1] = r2     // Catch: java.lang.Throwable -> L150 java.lang.Throwable -> L162 java.lang.Throwable -> La84
        L148:
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L150:
            r0 = r8
            r1 = 0
            r2 = 731(0x2db, float:1.024E-42)
            java.lang.Integer r2 = p000.ResourceManager.integerOf(r2)     // Catch: java.lang.Throwable -> L162 java.lang.Throwable -> La84
            r0[r1] = r2     // Catch: java.lang.Throwable -> L162 java.lang.Throwable -> La84
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L162:
            r11 = move-exception
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r11
            throw r0     // Catch: java.lang.Throwable -> La84
        L16c:
            goto L1d8
        L16f:
            r0 = 0
            r9 = r0
        L171:
            r0 = r8
            r1 = r0
            r16 = r1
            monitor-enter(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r9
            r1 = r8
            int r1 = r1.size()     // Catch: java.lang.Throwable -> L194 java.lang.Throwable -> La84
            if (r0 < r1) goto L184
            r0 = r16
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L194 java.lang.Throwable -> La84
            goto L1a5
        L184:
            r0 = r8
            r1 = r9
            java.lang.Object r0 = r0.elementAt(r1)     // Catch: java.lang.Throwable -> L194 java.lang.Throwable -> La84
            j r0 = (p000.ConnectionThread) r0     // Catch: java.lang.Throwable -> L194 java.lang.Throwable -> La84
            r15 = r0
            r0 = r16
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L194 java.lang.Throwable -> La84
            goto L19a
        L194:
            r8 = move-exception
            r0 = r16
            monitor-exit(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r8
            throw r0     // Catch: java.lang.Throwable -> La84
        L19a:
            r0 = r15
            r0.process()     // Catch: java.lang.Throwable -> La84
            int r9 = r9 + 1
            goto L171
        L1a5:
            r0 = 1359(0x54f, float:1.904E-42)
            java.util.Vector r0 = p000.AppState.getVector(r0)     // Catch: java.lang.Throwable -> La84
            r1 = r0
            r8 = r1
            if (r0 == 0) goto L1cb
            r0 = r8
            r1 = r0
            r15 = r1
            monitor-enter(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r8
            java.lang.Object r0 = p000.Utils.dequeue(r0)     // Catch: java.lang.Throwable -> L1c5 java.lang.Throwable -> La84
            javax.microedition.io.Connection r0 = (javax.microedition.io.Connection) r0     // Catch: java.lang.Throwable -> L1c5 java.lang.Throwable -> La84
            p000.IOUtils.closeConn(r0)     // Catch: java.lang.Throwable -> L1c5 java.lang.Throwable -> La84
            r0 = r15
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L1c5 java.lang.Throwable -> La84
            goto L1cb
        L1c5:
            r8 = move-exception
            r0 = r15
            monitor-exit(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r8
            throw r0     // Catch: java.lang.Throwable -> La84
        L1cb:
            r0 = 0
            r12 = r0
            r0 = 100
            long r0 = (long) r0     // Catch: java.lang.Throwable -> L1d7 java.lang.Throwable -> La84
            java.lang.Thread.sleep(r0)     // Catch: java.lang.Throwable -> L1d7 java.lang.Throwable -> La84
            goto L1d8
        L1d7:
        L1d8:
            r0 = 1358(0x54e, float:1.903E-42)
            java.util.Vector r0 = p000.AppState.getVector(r0)     // Catch: java.lang.Throwable -> La84
            r1 = r0
            r8 = r1
            if (r0 != 0) goto L16f
            return
        L1e4:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            p000.NetworkUtils.asyncReaderLoop(r0)     // Catch: java.lang.Throwable -> La84
            return
        L1ef:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            p000.ConnectionThread.executeWithReauth(r0)     // Catch: java.lang.Throwable -> La84
            return
        L1fa:
            r0 = 0
            r8 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r0 = 1442705(0x160391, float:2.02166E-39)
            r15 = r0
            n r0 = new n     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = r15
            n r0 = r0.writeCompressed(r1)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = 987652(0xf1204, float:1.383995E-39)
            n r0 = r0.writeCompressed(r1)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            java.lang.String r0 = r0.getStringAndClear()     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            ax r0 = p000.HttpClient.createWithType2(r0)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = r0
            r8 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L2d0
            n r0 = new n     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = r0
            r2 = r8
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = 0
            r15 = r1
            r1 = 0
            r16 = r1
            av r0 = r0.parseXml()     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = 0
            r12 = r1
            java.util.Vector r0 = r0.children     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = r0
            r16 = r1
            int r0 = r0.size()     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r10 = r0
            goto L2c1
        L246:
            r0 = r16
            r1 = r10
            java.lang.Object r0 = r0.elementAt(r1)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            av r0 = (p000.XmlElement) r0     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = r0
            r11 = r1
            java.lang.String r0 = r0.tagName     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            java.lang.String r1 = "city"
            boolean r0 = r0.equals(r1)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            if (r0 == 0) goto L2c1
            r0 = r11
            r1 = 131550(0x201de, float:1.84341E-40)
            java.lang.String r0 = r0.getIntAttribute(r1)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r17 = r0
            r0 = 1389(0x56d, float:1.946E-42)
            java.util.Vector r0 = p000.AppState.getVector(r0)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = r0
            r18 = r1
            int r0 = r0.size()     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r19 = r0
            goto L292
        L275:
            r0 = r18
            r1 = r19
            java.lang.Object r0 = r0.elementAt(r1)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            x r0 = (p000.GeoRegion) r0     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = r0
            r20 = r1
            java.lang.String r0 = r0.description     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = r17
            boolean r0 = r0.equals(r1)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            if (r0 == 0) goto L292
            r0 = r20
            goto L29b
        L292:
            int r19 = r19 + (-1)
            r0 = r19
            if (r0 >= 0) goto L275
            r0 = 0
        L29b:
            r1 = r0
            r17 = r1
            if (r0 == 0) goto L2c1
            r0 = r17
            r1 = r11
            r2 = 98
            int r1 = r1.getAttrAsInt(r2)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r2 = r11
            r3 = 29300(0x7274, float:4.1058E-41)
            int r2 = r2.getAttrAsInt(r3)     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r13 = r2
            r11 = r1
            r1 = r0
            r12 = r1
            r1 = r11
            r0.zoomLevel = r1     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r0 = r12
            r1 = r13
            r0.mapType = r1     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
        L2c1:
            int r10 = r10 + (-1)
            r0 = r10
            if (r0 >= 0) goto L246
            r0 = r8
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L2d0:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
            throw r0     // Catch: java.lang.Throwable -> L2d8 java.lang.Throwable -> L2e1 java.lang.Throwable -> La84
        L2d8:
            r0 = r8
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L2e1:
            r9 = move-exception
            r0 = r8
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r9
            throw r0     // Catch: java.lang.Throwable -> La84
        L2eb:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r12 = r0
            r0 = 1000(0x3e8, float:1.401E-42)
            long r0 = (long) r0     // Catch: java.lang.Throwable -> L2fd java.lang.Throwable -> La84
            java.lang.Thread.sleep(r0)     // Catch: java.lang.Throwable -> L2fd java.lang.Throwable -> La84
            goto L2fe
        L2fd:
        L2fe:
            r0 = 1359(0x54f, float:1.904E-42)
            java.util.Vector r0 = p000.AppState.getVector(r0)     // Catch: java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            if (r0 == 0) goto L31d
            r0 = r9
            r1 = r0
            r15 = r1
            monitor-enter(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r9
            r1 = r8
            r0.addElement(r1)     // Catch: java.lang.Throwable -> L317 java.lang.Throwable -> La84
            r0 = r15
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L317 java.lang.Throwable -> La84
            return
        L317:
            r8 = move-exception
            r0 = r15
            monitor-exit(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r8
            throw r0     // Catch: java.lang.Throwable -> La84
        L31d:
            return
        L31e:
            p000.StringUtils.tileLoaderLoop()     // Catch: java.lang.Throwable -> La84
            return
        L322:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r9 = r0
            r0 = 0
            r15 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r0 = 872(0x368, float:1.222E-42)
            java.lang.Object[] r0 = p000.XmppContactGroup.getContactInfoFromState(r0)     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r15 = r0
            r0 = r8
            ax r0 = p000.HttpClient.createWithType2(r0)     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L36d
            n r0 = new n     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r1 = r0
            r2 = r9
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            java.lang.String r0 = r0.readUTFWithLen()     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            java.util.Vector r0 = p000.XmppContactGroup.parseMapPointsFromStr(r0)     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r10 = r0
            r0 = 0
            r12 = r0
            java.lang.Object[] r0 = p000.AppState.pool     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r1 = 1399(0x577, float:1.96E-42)
            r2 = r10
            r0[r1] = r2     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L36d:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
            throw r0     // Catch: java.lang.Throwable -> L375 java.lang.Throwable -> L392 java.lang.Throwable -> La84
        L375:
            java.util.Vector r0 = p000.NetworkUtils.newVector()     // Catch: java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r10 = r0
            r0 = 0
            r12 = r0
            java.lang.Object[] r0 = p000.AppState.pool     // Catch: java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r1 = 1399(0x577, float:1.96E-42)
            r2 = r10
            r0[r1] = r2     // Catch: java.lang.Throwable -> L392 java.lang.Throwable -> La84
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L392:
            r16 = move-exception
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r16
            throw r0     // Catch: java.lang.Throwable -> La84
        L3a3:
            r0 = 0
            r8 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            r0 = 1442705(0x160391, float:2.02166E-39)
            r15 = r0
            n r0 = new n     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            r1 = r15
            n r0 = r0.writeCompressed(r1)     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            r1 = 1905127(0x1d11e7, float:2.669652E-39)
            n r0 = r0.writeCompressed(r1)     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            r1 = 254(0xfe, float:3.56E-43)
            n r0 = r0.writeEncodedInt(r1)     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            java.lang.String r0 = r0.getStringAndClear()     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            ax r0 = p000.HttpClient.createWithType2(r0)     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            r1 = r0
            r8 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L3ed
            n r0 = new n     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            r1 = r0
            r2 = r8
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            r1 = 0
            r15 = r1
            av r0 = r0.parseXml()     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            p000.StringUtils.parseGeoConfig(r0)     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            r0 = r8
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L3ed:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
            throw r0     // Catch: java.lang.Throwable -> L3f5 java.lang.Throwable -> L3fe java.lang.Throwable -> La84
        L3f5:
            r0 = r8
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L3fe:
            r9 = move-exception
            r0 = r8
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r9
            throw r0     // Catch: java.lang.Throwable -> La84
        L408:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            r1 = 0
            r13 = r1
            java.lang.String r0 = (java.lang.String) r0     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r9 = r0
            r0 = 0
            r15 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r0 = 1000(0x3e8, float:1.401E-42)
            java.lang.Object[] r0 = p000.XmppContactGroup.getContactInfoFromState(r0)     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r15 = r0
            r0 = r8
            ax r0 = p000.HttpClient.createWithType2(r0)     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L4c6
            n r0 = new n     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r1 = r0
            r2 = r9
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            p000.MmpContact.parseRouteFromJson(r0)     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            java.util.Vector r0 = p000.MmpContact.routeRegions     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            int r0 = r0.size()     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            if (r0 <= 0) goto L44a
            r0 = 1
            goto L44b
        L44a:
            r0 = 0
        L44b:
            if (r0 == 0) goto L4b9
            r0 = 1
            p000.MmpContact.setLocationEnabled(r0)     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            java.util.Vector r0 = p000.MmpContact.routeRegions     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            int r0 = r0.size()     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            if (r0 <= 0) goto L483
            java.util.Vector r0 = p000.MmpContact.routeRegions     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            java.lang.Object r0 = r0.firstElement()     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r1 = 1
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r1 = r0
            r10 = r1
            int r0 = r0.length     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            if (r0 <= 0) goto L483
            r0 = r10
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r1 = 1
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r1 = 0
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            int[] r0 = (int[]) r0     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r1 = 0
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            long r0 = (long) r0     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            goto L484
        L483:
            r0 = 0
        L484:
            java.util.Vector r1 = p000.MmpContact.routeRegions     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            int r1 = r1.size()     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            if (r1 <= 0) goto L4b5
            java.util.Vector r1 = p000.MmpContact.routeRegions     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            java.lang.Object r1 = r1.firstElement()     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            java.lang.Object[] r1 = (java.lang.Object[]) r1     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r2 = 1
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            java.lang.Object[] r1 = (java.lang.Object[]) r1     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r2 = r1
            r10 = r2
            int r1 = r1.length     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            if (r1 <= 0) goto L4b5
            r1 = r10
            java.lang.Object[] r1 = (java.lang.Object[]) r1     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r2 = 1
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            java.lang.Object[] r1 = (java.lang.Object[]) r1     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r2 = 0
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            int[] r1 = (int[]) r1     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r2 = 1
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            long r1 = (long) r1     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            goto L4b6
        L4b5:
            r1 = 0
        L4b6:
            p000.MapRenderer.setPosition(r0, r1)     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
        L4b9:
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L4c6:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            throw r0     // Catch: java.lang.Throwable -> L4ce java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
        L4ce:
            r0 = 1001(0x3e9, float:1.403E-42)
            r1 = 0
            r8 = r1
            java.lang.String r0 = p000.AppState.getString(r0)     // Catch: java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r1 = 0
            r8 = r1
            p000.IOUtils.postEvent(r0)     // Catch: java.lang.Throwable -> L4e9 java.lang.Throwable -> La84
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L4e9:
            r16 = move-exception
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r16
            throw r0     // Catch: java.lang.Throwable -> La84
        L4fa:
            return
        L4fb:
            p000.XmppContactGroup.periodicTimeSync()     // Catch: java.lang.Throwable -> La84
            return
        L4ff:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            r1 = 0
            r13 = r1
            java.lang.String r0 = (java.lang.String) r0     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r9 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r0 = r8
            ax r0 = p000.HttpClient.createWithType2(r0)     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L56e
            n r0 = new n     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r1 = r0
            r2 = r9
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r16 = r0
            java.util.Hashtable r0 = p000.ConnectionThread.photoCache     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r1 = r0
            r10 = r1
            monitor-enter(r0)     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            java.lang.String r0 = p000.ConnectionThread.pendingPhotoKey     // Catch: java.lang.Throwable -> L55f java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r1 = r0
            r11 = r1
            r17 = r0
            java.lang.String r0 = "upi"
            r1 = r17
            java.lang.String r0 = p000.StringUtils.concat(r0, r1)     // Catch: java.lang.Throwable -> L55f java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r1 = r16
            p000.XmppMailRuProtocol.writeChunkedRecord(r0, r1)     // Catch: java.lang.Throwable -> L55f java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            java.util.Hashtable r0 = p000.ConnectionThread.photoCache     // Catch: java.lang.Throwable -> L551 java.lang.Throwable -> L55f java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r1 = r11
            r2 = r16
            javax.microedition.lcdui.Image r2 = r2.toImage()     // Catch: java.lang.Throwable -> L551 java.lang.Throwable -> L55f java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            java.lang.Object r0 = r0.put(r1, r2)     // Catch: java.lang.Throwable -> L551 java.lang.Throwable -> L55f java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            goto L552
        L551:
        L552:
            r0 = 0
            p000.ConnectionThread.pendingPhotoKey = r0     // Catch: java.lang.Throwable -> L55f java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r0 = 1
            p000.MapRenderer.needsRedraw = r0     // Catch: java.lang.Throwable -> L55f java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r0 = r10
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L55f java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            goto L566
        L55f:
            r16 = move-exception
            r0 = r10
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r0 = r16
            throw r0     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
        L566:
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L56e:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
            throw r0     // Catch: java.lang.Throwable -> L576 java.lang.Throwable -> L57f java.lang.Throwable -> La84
        L576:
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L57f:
            r15 = move-exception
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r15
            throw r0     // Catch: java.lang.Throwable -> La84
        L58b:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            r1 = 0
            r13 = r1
            java.lang.String r0 = (java.lang.String) r0     // Catch: java.lang.Throwable -> La84
            p000.ResourceManager.fetchSharedContacts(r0)     // Catch: java.lang.Throwable -> La84
            return
        L599:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            r1 = 0
            r13 = r1
            java.lang.String r0 = (java.lang.String) r0     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r9 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L5cf java.lang.Throwable -> L5d8 java.lang.Throwable -> La84
            r0 = r8
            ax r0 = p000.HttpClient.createWithType2(r0)     // Catch: java.lang.Throwable -> L5cf java.lang.Throwable -> L5d8 java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L5cf java.lang.Throwable -> L5d8 java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L5c7
            n r0 = new n     // Catch: java.lang.Throwable -> L5cf java.lang.Throwable -> L5d8 java.lang.Throwable -> La84
            r1 = r9
            r0.<init>(r1)     // Catch: java.lang.Throwable -> L5cf java.lang.Throwable -> L5d8 java.lang.Throwable -> La84
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L5c7:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> L5cf java.lang.Throwable -> L5d8 java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L5cf java.lang.Throwable -> L5d8 java.lang.Throwable -> La84
            throw r0     // Catch: java.lang.Throwable -> L5cf java.lang.Throwable -> L5d8 java.lang.Throwable -> La84
        L5cf:
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L5d8:
            r11 = move-exception
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r11
            throw r0     // Catch: java.lang.Throwable -> La84
        L5e2:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            p000.ResourceManager.sendSmsRequest(r0)     // Catch: java.lang.Throwable -> La84
            return
        L5ea:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.String r0 = (java.lang.String) r0     // Catch: java.lang.Throwable -> La84
            p000.NetworkUtils.sendDiagnosticReport(r0)     // Catch: java.lang.Throwable -> La84
            return
        L5f5:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r9 = r0
            r0 = 0
            r15 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r0 = 370(0x172, float:5.18E-43)
            java.lang.Object[] r0 = p000.XmppContactGroup.getContactInfoFromState(r0)     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r15 = r0
            r0 = r8
            r1 = 0
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            ax r0 = p000.HttpClient.createWithType2(r0)     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L65c
            n r0 = new n     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r1 = r0
            r2 = r9
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r1 = r8
            r2 = 1
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            long[] r1 = (long[]) r1     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r2 = r1
            r10 = r2
            r2 = 0
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r2 = r10
            r3 = 1
            r2 = r2[r3]     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r19 = r2
            r17 = r1
            r1 = 0
            r11 = r1
            r1 = r17
            r2 = r19
            java.util.Vector r0 = p000.VCard.parseMapPointsFromJson(r0, r1, r2)     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            p000.ResourceManager.savedLocations = r0     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            bb r0 = new bb     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r1 = r0
            r2 = 3
            r3 = 0
            r1.<init>(r2, r3)     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            p000.IOUtils.postEvent(r0)     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L65c:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
            throw r0     // Catch: java.lang.Throwable -> L664 java.lang.Throwable -> L672 java.lang.Throwable -> La84
        L664:
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L672:
            r16 = move-exception
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r16
            throw r0     // Catch: java.lang.Throwable -> La84
        L683:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r9 = r0
            r0 = 0
            r15 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            r0 = 505(0x1f9, float:7.08E-43)
            java.lang.Object[] r0 = p000.XmppContactGroup.getContactInfoFromState(r0)     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            r15 = r0
            r0 = r8
            r1 = 0
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            ax r0 = p000.HttpClient.createWithType2(r0)     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L6cf
            n r0 = new n     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            r1 = r0
            r2 = r9
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            r1 = r8
            r2 = 1
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            java.lang.Integer r1 = (java.lang.Integer) r1     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            int r1 = r1.intValue()     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            r2 = 0
            r10 = r2
            p000.ContactListParser.parseContactsSync(r0, r1)     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L6cf:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
            throw r0     // Catch: java.lang.Throwable -> L6d7 java.lang.Throwable -> L6e5 java.lang.Throwable -> La84
        L6d7:
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L6e5:
            r16 = move-exception
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r16
            throw r0     // Catch: java.lang.Throwable -> La84
        L6f6:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r9 = r0
            r0 = 0
            r15 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            r0 = 505(0x1f9, float:7.08E-43)
            java.lang.Object[] r0 = p000.XmppContactGroup.getContactInfoFromState(r0)     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            r15 = r0
            r0 = r8
            r1 = 0
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            ax r0 = p000.HttpClient.createWithType2(r0)     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L741
            n r0 = new n     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            r1 = r0
            r2 = r9
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            r1 = r8
            r2 = 1
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            r2 = r8
            r3 = 2
            r2 = r2[r3]     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            r11 = r2
            r2 = 0
            r10 = r2
            r2 = r11
            p000.ContactListParser.parseContactsAsync(r0, r1, r2)     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L741:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
            throw r0     // Catch: java.lang.Throwable -> L749 java.lang.Throwable -> L757 java.lang.Throwable -> La84
        L749:
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L757:
            r16 = move-exception
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r16
            throw r0     // Catch: java.lang.Throwable -> La84
        L768:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r9 = r0
            r0 = 0
            r15 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r0 = 505(0x1f9, float:7.08E-43)
            java.lang.Object[] r0 = p000.XmppContactGroup.getContactInfoFromState(r0)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r15 = r0
            r0 = 3805583(0x3a118f, float:5.332758E-39)
            r1 = 0
            r10 = r1
            java.lang.String r0 = p000.AppState.getString(r0)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r10 = r0
            java.lang.StringBuffer r0 = p000.NetworkUtils.newStringBuffer()     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r1 = r10
            java.lang.StringBuffer r0 = r0.append(r1)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r1 = r8
            r2 = 1
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            java.lang.StringBuffer r0 = r0.append(r1)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r1 = 201188(0x311e4, float:2.81924E-40)
            java.lang.String r1 = p000.AppState.getString(r1)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            java.lang.StringBuffer r0 = r0.append(r1)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r1 = r8
            r2 = 2
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            java.lang.StringBuffer r0 = r0.append(r1)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r1 = 1774025(0x1b11c9, float:2.485939E-39)
            java.lang.String r1 = p000.AppState.getString(r1)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            java.lang.StringBuffer r0 = r0.append(r1)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            java.lang.String r0 = p000.NetworkUtils.bufToStringCached(r0)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            ax r0 = p000.HttpClient.createWithType2(r0)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto L7fe
            n r0 = new n     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r1 = r0
            r2 = r9
            r1.<init>(r2)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r1 = r8
            r2 = 0
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            ba r1 = (p000.MrimAccount) r1     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r2 = r8
            r3 = 3
            r2 = r2[r3]     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            long[] r2 = (long[]) r2     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r18 = r2
            r2 = 0
            r17 = r2
            r11 = r1
            r1 = r18
            r2 = 0
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r2 = r18
            r3 = 1
            r2 = r2[r3]     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            java.util.Vector r0 = p000.VCard.parseMapPointsFromJson(r0, r1, r2)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r10 = r0
            r0 = r11
            r1 = r10
            java.lang.Object r1 = r1.firstElement()     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            an r1 = (p000.MapPoint) r1     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r0.setLocationProfile(r1)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r0 = r11
            p000.IOUtils.postAccountEvent(r0)     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L7fe:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r1 = r0
            r1.<init>()     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
            throw r0     // Catch: java.lang.Throwable -> L806 java.lang.Throwable -> L835 java.lang.Throwable -> La84
        L806:
            r0 = r8
            r1 = 0
            r0 = r0[r1]     // Catch: java.lang.Throwable -> L835 java.lang.Throwable -> La84
            ba r0 = (p000.MrimAccount) r0     // Catch: java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r1 = r8
            r2 = 1
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L835 java.lang.Throwable -> La84
            java.lang.String r1 = (java.lang.String) r1     // Catch: java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r2 = r8
            r3 = 2
            r2 = r2[r3]     // Catch: java.lang.Throwable -> L835 java.lang.Throwable -> La84
            java.lang.String r2 = (java.lang.String) r2     // Catch: java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r17 = r2
            r11 = r1
            r1 = r0
            r10 = r1
            r1 = r11
            r2 = r17
            r0.setSimpleProfile(r1, r2)     // Catch: java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r0 = r10
            p000.IOUtils.postAccountEvent(r0)     // Catch: java.lang.Throwable -> L835 java.lang.Throwable -> La84
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        L835:
            r16 = move-exception
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            r0 = r15
            p000.XmppContactGroup.removeContactInfoFromQueue(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r16
            throw r0     // Catch: java.lang.Throwable -> La84
        L846:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            r1 = 0
            r13 = r1
            java.lang.String r0 = (java.lang.String) r0     // Catch: java.lang.Throwable -> La84
            p000.Conversation.fetchMapData(r0)     // Catch: java.lang.Throwable -> La84
            return
        L854:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            p000.NetworkUtils.executeRegRequest(r0)     // Catch: java.lang.Throwable -> La84
            return
        L85f:
            return
        L860:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            r8 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            r0 = 398209(0x61381, float:5.5801E-40)
            r1 = r8
            r2 = 0
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            java.lang.String r0 = p000.StringUtils.concatKeyObj(r0, r1)     // Catch: java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            r1 = r8
            r2 = 1
            r1 = r1[r2]     // Catch: java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            java.lang.String r1 = (java.lang.String) r1     // Catch: java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            r11 = r1
            r12 = r0
            r0 = 0
            r9 = r0
            r0 = 100
            java.lang.Thread.sleep(r0)     // Catch: java.lang.Throwable -> L8c2 java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            r0 = r12
            r1 = 0
            r13 = r1
            javax.microedition.io.Connection r0 = javax.microedition.io.Connector.open(r0)     // Catch: java.lang.Throwable -> L8c2 java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            java.lang.Object r0 = p000.IOUtils.registerResource(r0)     // Catch: java.lang.Throwable -> L8c2 java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            javax.wireless.messaging.MessageConnection r0 = (javax.wireless.messaging.MessageConnection) r0     // Catch: java.lang.Throwable -> L8c2 java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            r1 = 267133(0x4137d, float:3.74333E-40)
            java.lang.String r1 = p000.AppState.getString(r1)     // Catch: java.lang.Throwable -> L8c2 java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            javax.wireless.messaging.Message r0 = r0.newMessage(r1)     // Catch: java.lang.Throwable -> L8c2 java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            javax.wireless.messaging.TextMessage r0 = (javax.wireless.messaging.TextMessage) r0     // Catch: java.lang.Throwable -> L8c2 java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            r1 = r0
            r13 = r1
            r1 = r12
            r0.setAddress(r1)     // Catch: java.lang.Throwable -> L8c2 java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            r0 = r13
            r1 = r11
            r0.setPayloadText(r1)     // Catch: java.lang.Throwable -> L8c2 java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            r0 = r9
            r1 = r13
            r0.send(r1)     // Catch: java.lang.Throwable -> L8c2 java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            r0 = r9
            p000.IOUtils.closeConn(r0)     // Catch: java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            goto L8cb
        L8c2:
            r14 = move-exception
            r0 = r9
            p000.IOUtils.closeConn(r0)     // Catch: java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            r0 = r14
            throw r0     // Catch: java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
        L8cb:
            r0 = r8
            r1 = 2
            r2 = r8
            r0[r1] = r2     // Catch: java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> L8d3 java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            return
        L8d3:
            r9 = move-exception
            r0 = r8
            r1 = 2
            r2 = r9
            r0[r1] = r2     // Catch: java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> L8dc java.lang.Throwable -> La84
            return
        L8dc:
            r10 = move-exception
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r10
            throw r0     // Catch: java.lang.Throwable -> La84
        L8e2:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            p000.AppController.waitForCompletion(r0)     // Catch: java.lang.Throwable -> La84
            return
        L8ed:
            return
        L8ee:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            p000.ResourceManager.processXmppStream(r0)     // Catch: java.lang.Throwable -> La84
            return
        L8f9:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            p000.IOUtils.performXmppAuth(r0)     // Catch: java.lang.Throwable -> La84
            return
        L904:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            p000.Conversation.fetchHistory(r0)     // Catch: java.lang.Throwable -> La84
            return
        L90f:
            p000.ResourceManager.fetchUpdateStatus()     // Catch: java.lang.Throwable -> La84
            return
        L913:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            p000.XmppMailRuProtocol.resolveXmppServer(r0)     // Catch: java.lang.Throwable -> La84
            return
        L91e:
            r0 = r8
            java.lang.Object r0 = r0.taskData     // Catch: java.lang.Throwable -> La84
            java.lang.Object[] r0 = (java.lang.Object[]) r0     // Catch: java.lang.Throwable -> La84
            r8 = r0
            r0 = 0
            r9 = r0
            p000.NetworkLock.acquireNetworkLock()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r0 = r8
            r1 = 2
            r0 = r0[r1]     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r1 = 0
            r11 = r1
            java.lang.String r0 = (java.lang.String) r0     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r1 = r8
            r2 = 0
            r1 = r1[r2]     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            h r1 = (p000.Account) r1     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r2 = 0
            ax r0 = p000.HttpClient.createHttpClient(r0, r1, r2)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r1 = r0
            r9 = r1
            int r0 = r0.getResponseCode()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r1 = r0
            r15 = r1
            r1 = 200(0xc8, float:2.8E-43)
            if (r0 != r1) goto La50
            n r0 = new n     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r1 = r0
            r2 = r9
            r1.<init>(r2)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            av r0 = r0.parseXmlStr()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r15 = r0
            r0 = r8
            r1 = 3
            r0 = r0[r1]     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            java.lang.Integer r0 = (java.lang.Integer) r0     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            int r0 = r0.intValue()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            if (r0 != 0) goto La13
            r0 = r8
            r1 = r15
            r10 = r1
            r1 = r0
            r15 = r1
            r1 = 2
            r2 = 4069357(0x3e17ed, float:5.702384E-39)
            r11 = r2
            n r2 = new n     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r3 = r2
            r3.<init>()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r3 = r11
            n r2 = r2.writeCompressed(r3)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r3 = 1316925(0x14183d, float:1.845405E-39)
            n r2 = r2.writeCompressed(r3)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r3 = r15
            r4 = 1
            r3 = r3[r4]     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            n r2 = r2.writeObjectStr(r3)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r3 = 463517(0x7129d, float:6.49526E-40)
            n r2 = r2.writeCompressed(r3)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r3 = r15
            r4 = 4
            r3 = r3[r4]     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            n r2 = r2.writeObjectStr(r3)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r3 = 530513(0x81851, float:7.43407E-40)
            n r2 = r2.writeCompressed(r3)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r3 = 3282875(0x3217bb, float:4.600288E-39)
            r11 = r3
            n r3 = new n     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r4 = r3
            r4.<init>()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r4 = r11
            n r3 = r3.writeCompressed(r4)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r4 = 58
            n r3 = r3.writeByte(r4)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r4 = r15
            r5 = 1
            r4 = r4[r5]     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            n r3 = r3.writeObjectStr(r4)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r4 = 58
            n r3 = r3.writeByte(r4)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r4 = r10
            r5 = 330583(0x50b57, float:4.63245E-40)
            av r4 = r4.findChildByKey(r5)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            java.lang.StringBuffer r4 = r4.textContent     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            java.lang.String r4 = p000.StringUtils.fromBuffer(r4)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r10 = r4
            r4 = 0
            r11 = r4
            r4 = r10
            n r3 = r3.writeRawString(r4)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r4 = 58
            n r3 = r3.writeByte(r4)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            n r4 = new n     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r5 = r4
            r5.<init>()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r5 = r15
            r6 = 4
            r5 = r5[r6]     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            n r4 = r4.writeObjectStr(r5)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r5 = 530521(0x81859, float:7.43418E-40)
            n r4 = r4.writeCompressed(r5)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r5 = r15
            r6 = 5
            r5 = r5[r6]     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            n r4 = r4.writeObjectStr(r5)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            n r4 = r4.encryptMD5()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            java.lang.String r4 = r4.toHexString()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            n r3 = r3.writeRawString(r4)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            n r3 = r3.encryptMD5()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            java.lang.String r3 = r3.toHexString()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            n r2 = r2.writeRawString(r3)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            java.lang.String r2 = r2.readAllByteStr()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r0[r1] = r2     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r0 = r15
            r1 = 3
            r2 = 1
            java.lang.Integer r2 = p000.ResourceManager.integerOf(r2)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r0[r1] = r2     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            z r0 = new z     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r1 = 34
            r2 = r15
            r0.<init>(r1, r2)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            goto La48
        La13:
            r0 = r8
            r1 = r15
            r10 = r1
            r1 = 0
            r0 = r0[r1]     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            bd r0 = (p000.XmppMailRuProtocol) r0     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r15 = r0
            r0 = 333441(0x51681, float:4.6725E-40)
            r1 = r10
            java.lang.String r1 = r1.tagName     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            boolean r0 = p000.StringUtils.matchesKey(r0, r1)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            if (r0 == 0) goto La32
            r0 = r15
            r0.handleComplete()     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            goto La48
        La32:
            r0 = r15
            r1 = 131550(0x201de, float:1.84341E-40)
            r2 = r10
            r3 = 131550(0x201de, float:1.84341E-40)
            av r2 = r2.findChildByKey(r3)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            java.lang.StringBuffer r2 = r2.textContent     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            java.lang.String r2 = p000.StringUtils.fromBuffer(r2)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            java.lang.String r1 = p000.StringUtils.concatKey(r1, r2)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r0.serverResourceId = r1     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
        La48:
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        La50:
            java.lang.Throwable r0 = new java.lang.Throwable     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r1 = r0
            r2 = r15
            java.lang.String r2 = java.lang.Integer.toString(r2)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            java.lang.String r2 = p000.StringUtils.intern(r2)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r1.<init>(r2)     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
            throw r0     // Catch: java.lang.Throwable -> La60 java.lang.Throwable -> La77 java.lang.Throwable -> La84
        La60:
            r15 = move-exception
            r0 = r8
            r1 = r15
            r10 = r1
            r1 = 0
            r0 = r0[r1]     // Catch: java.lang.Throwable -> La77 java.lang.Throwable -> La84
            ae r0 = (p000.XmppProtocol) r0     // Catch: java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r1 = r10
            r0.setException(r1)     // Catch: java.lang.Throwable -> La77 java.lang.Throwable -> La84
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            return
        La77:
            r16 = move-exception
            r0 = r9
            p000.HttpClient.closeAndUpdateStats(r0)     // Catch: java.lang.Throwable -> La84
            p000.NetworkLock.releaseNetworkLock()     // Catch: java.lang.Throwable -> La84
            r0 = r16
            throw r0     // Catch: java.lang.Throwable -> La84
        La83:
            return
        La84:
            return
        */
}
