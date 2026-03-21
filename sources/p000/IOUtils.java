package p000;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.io.Connection;
import javax.microedition.lcdui.Image;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

/* renamed from: bb */
/* loaded from: MobileAgent_3.9.jar:bb.class */
public final class IOUtils {

    /* renamed from: a */
    public int eventType;

    /* renamed from: b */
    public Object eventData;

    /* renamed from: c */
    public static Vector openResources;

    /* renamed from: d */
    public static Vector contactIdList;

    /* renamed from: e */
    public static String[] photoUrlList;

    /* renamed from: f */
    private static Screen selectionScreen;

    public IOUtils(int i, Object obj) {
        this.eventType = i;
        this.eventData = obj;
    }

    /* renamed from: a */
    public static final int handleMailMenuAction(String str, int i) {
        String strM584b = AppState.getString(1346);
        wrapInVector(strM584b);
        int iM586d = AppState.getInt(1513);
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        Message c0026azM1415b = c0028ba.findChatRoomById(iM586d).getMessage(strM584b);
        String strM673d = c0026azM1415b.getSubject();
        Vector vectorM668b = c0026azM1415b.getToList();
        Vector vectorM669c = c0026azM1415b.getCcList();
        XmppMailRuProtocol.getFirstRecipient(vectorM668b);
        boolean zM587e = AppState.getBool(96);
        String strM584b2 = AppState.getString(198549);
        String strM584b3 = AppState.getString(198546);
        String str2 = AppState.emptyStr;
        if (i == 48) {
            ScreenBuilder.onScreenClosed();
            ScreenBuilder.onScreenClosed();
        }
        if (StringUtils.matchesKey(839, str)) {
            if (!zM587e) {
                return ResourceManager.composeEmail(XmppMailRuProtocol.getFirstAddress(vectorM668b), new StringBuffer().append(strM584b2).append(strM673d).toString(), str2);
            }
            XmppMailRuProtocol.setMailAction(54, 0);
            return 0;
        }
        if (StringUtils.matchesKey(840, str)) {
            if (!zM587e) {
                return ResourceManager.composeEmail(XmppMailRuProtocol.mergeAddressLists(XmppMailRuProtocol.copyAddressList(vectorM668b), vectorM669c), new StringBuffer().append(strM584b2).append(strM673d).toString(), str2);
            }
            XmppMailRuProtocol.setMailAction(54, 1);
            return 0;
        }
        if (StringUtils.matchesKey(841, str)) {
            if (!zM587e) {
                return ResourceManager.composeEmail(NetworkUtils.newVector(), new StringBuffer().append(strM584b3).append(strM673d).toString(), str2);
            }
            XmppMailRuProtocol.setMailAction(54, 2);
            return 0;
        }
        if (StringUtils.matchesKey(855, str)) {
            AppState.setInt(1525, 2);
            return 0;
        }
        if (StringUtils.matchesKey(856, str)) {
            AppState.setInt(1525, 1);
            return 0;
        }
        if (!StringUtils.matchesKey(845, str)) {
            return 0;
        }
        AppState.setInt(1527, c0028ba.findDefaultChatRoom().id);
        return 0;
    }

    /* renamed from: a */
    public static final ByteBuffer createSendMessageCmd(MmpProtocol c0033d, MmpContact c0009ai, String str) {
        return AppController.createMmpCommand(c0033d, 4888, new ByteBuffer().writeByteLenStr(c0009ai.identifier).writeUTF(str).writeShortBE(0));
    }

    /* renamed from: a */
    public static final void playSound(int i) {
        stopSound();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new ByteBuffer().writeCompressed(590318).writeCompressed(i + 430).writeIntLE(3145472).toByteArray());
            Object objM761a = registerResource((Object) byteArrayInputStream);
            AppState.pool[1264] = objM761a;
            if (null != objM761a) {
                Player playerCreatePlayer = Manager.createPlayer(byteArrayInputStream, AppState.getString(655831));
                AppState.pool[1265] = registerResource(playerCreatePlayer);
                try {
                    playerCreatePlayer.realize();
                } catch (Throwable unused) {
                }
                if (AppState.getBool(87)) {
                    try {
                        ((javax.microedition.media.control.VolumeControl) playerCreatePlayer.getControl(AppState.getString(852449))).setLevel(AppState.getInt(88));
                    } catch (Throwable unused2) {
                    }
                }
                try {
                    playerCreatePlayer.prefetch();
                } catch (Throwable unused3) {
                }
                try {
                    playerCreatePlayer.start();
                } catch (Throwable unused4) {
                }
                AppController.setTimer(6, 10000L);
            }
        } catch (Throwable unused5) {
        }
    }

    /* renamed from: m */
    private static final void stopSound() {
        Player player = (Player) AppState.pool[1265];
        if (player != null) {
            unregisterResource(player);
            try {
                player.stop();
            } catch (Throwable unused) {
            }
            try {
                player.close();
            } catch (Throwable unused2) {
            }
        }
        closeInput((InputStream) AppState.pool[1264]);
        AppState.clearRange(1264, 1265);
    }

    /* renamed from: a */
    public static final void checkSoundTimer() {
        boolean z;
        long[] jArr = AppController.timers;
        long j = jArr[6];
        if (j == 0 || j >= System.currentTimeMillis()) {
            z = false;
        } else {
            jArr[6] = 0;
            z = true;
        }
        if (z) {
            stopSound();
        }
    }

    /* renamed from: a */
    public static final int getGroupCount(Account abstractC0037h) {
        Vector vector = abstractC0037h.groups;
        int iM541c = Utils.vectorSize(vector);
        if (iM541c > 0) {
            StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
            for (int i = 0; i < iM541c; i++) {
                stringBufferM1217h.append(((ContactGroup) vector.elementAt(i)).name).append((char) 0);
            }
            AppState.setFromBuffer(1323, stringBufferM1217h);
            AppState.pool[1324] = vector;
            AppState.setInt(1507, 0);
        }
        return iM541c;
    }

    /* renamed from: b */
    public static final void showAddContactScreen() {
        ContactInfo c0042m = (ContactInfo) AppState.pool[1319];
        Account abstractC0037hM1255c = c0042m.getAccount();
        if (getGroupCount(abstractC0037hM1255c) == 0) {
            postEvent((Object) AppState.getString(743));
            return;
        }
        if (AppState.getBool(1508)) {
            AppState.setFromPool(1322, 331);
            AppState.setInt(1508, 0);
        } else {
            AppState.setFromPool(1322, 741);
        }
        if (abstractC0037hM1255c.getType() == 1) {
            AppState.setObject(1320, (Object) c0042m.getString(60));
            AppState.setObject(1321, (Object) c0042m.getDisplayNameOrId());
            ScreenManager.showScreen(ScreenManager.createScreen(3920));
            return;
        }
        if (((MrimAccount) abstractC0037hM1255c).hasCustomDomain) {
            AppState.setInt(1509, 1);
            AppState.setInt(3897, 5);
        } else {
            AppState.setInt(1509, 0);
            AppState.setInt(3897, 4);
        }
        AppState.setObject(1320, (Object) c0042m.getEmailOrMmpId());
        AppState.setObject(1321, (Object) c0042m.getFullName());
        ScreenManager.showScreen(ScreenManager.createScreen(3888));
    }

    /* renamed from: a */
    public static final void notifyNewMail(MrimAccount c0028ba, int i, String str, String str2) {
        boolean zM587e = AppState.getBool(91);
        boolean zM587e2 = AppState.getBool(90);
        if (zM587e2 || zM587e) {
            if (str != null) {
                int iLastIndexOf = str.lastIndexOf(60);
                if (str.length() > 30 && iLastIndexOf > 1) {
                    StringUtils.prefix(str, iLastIndexOf - 1);
                }
                ResourceManager.playNotificationSound(0);
            }
            if (zM587e && (AppController.getActiveScreenId() != 10 || !AppState.hasMemory())) {
                StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
                if (str2 != null && str != null) {
                    postAccountNotification(c0028ba, NetworkUtils.bufToStringCached(stringBufferM1217h.append(AppState.getString(917)).append(str).append(' ').append('\"').append(str2).append('\"').append('.').append('\n').append(new StringBuffer().append(i > 0 ? new StringBuffer().append(AppState.getString(918)).append(i).append(AppState.getString(919 + Utils.pluralForm(i))).append('\n').toString() : AppState.emptyStr).append(AppState.getString(916)).toString())));
                } else if (i > 0) {
                    postAccountNotification(c0028ba, NetworkUtils.bufToStringCached(stringBufferM1217h.append(AppState.getString(918)).append(i).append(AppState.getString(919 + Utils.pluralForm(i))).append('\n').append(AppState.getString(916))));
                }
            }
            if (zM587e2) {
                if (i > 0 || !(str2 == null || str == null)) {
                    AppController.markScreenDirty();
                    AppController.markAccountHighlighted(c0028ba);
                    if (AppState.getBool(90)) {
                        AppState.getVector(1244).addElement(c0028ba);
                    }
                    TabBar.layout();
                }
            }
        }
    }

    /* renamed from: c */
    public static final void showChatRoomMessages() {
        ChatRoom c0052wM745h = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513));
        Screen c0013amM75b = ScreenManager.createScreen(4527);
        c0013amM75b.setHeader(234, c0052wM745h.getDisplayName());
        Vector vectorM1213g = NetworkUtils.newVector();
        Enumeration enumerationElements = c0052wM745h.messageIds.elements();
        while (enumerationElements.hasMoreElements()) {
            Hashtable hashtable = c0052wM745h.messages;
            Object objNextElement = enumerationElements.nextElement();
            if (hashtable.containsKey(objNextElement)) {
                vectorM1213g.addElement(c0052wM745h.messages.get(objNextElement));
            }
        }
        Enumeration enumerationElements2 = vectorM1213g.elements();
        while (enumerationElements2.hasMoreElements()) {
            c0013amM75b.addItem(((Message) enumerationElements2.nextElement()).createMenuItem(c0052wM745h));
        }
        if (c0013amM75b.menuItems.size() == 0) {
            c0013amM75b.selectable = false;
            c0013amM75b.addLabelById(835);
        } else {
            c0013amM75b.scrollOffset = AppState.getInt(1514);
            c0013amM75b.selectByTitle(AppState.getString(1345));
            c0013amM75b.invalidateLayout();
        }
        c0013amM75b.reverseScroll = true;
        ScreenManager.showScreen(c0013amM75b);
    }

    /* renamed from: a */
    public static final Object registerResource(Object obj) {
        if (obj != null) {
            openResources.addElement(obj);
        }
        return obj;
    }

    /* renamed from: b */
    public static final void unregisterResource(Object obj) {
        if (obj != null) {
            Utils.removeFrom(openResources, obj);
        }
    }

    /* renamed from: a */
    public static final void closeInput(InputStream inputStream) {
        if (inputStream != null) {
            try {
                unregisterResource(inputStream);
                inputStream.close();
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: a */
    public static final void closeOutput(OutputStream outputStream) {
        if (outputStream != null) {
            try {
                unregisterResource(outputStream);
                outputStream.close();
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: a */
    public static final void closeConn(Connection connection) {
        if (connection != null) {
            try {
                unregisterResource(connection);
                connection.close();
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: a */
    public static final void closeRecordStore(RecordStore recordStore) {
        if (recordStore != null) {
            try {
                unregisterResource(recordStore);
                recordStore.closeRecordStore();
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: a */
    public static final RecordStore openRecordStore(String str, boolean z) throws RecordStoreException {
        return (RecordStore) registerResource((Object) RecordStore.openRecordStore(str, z));
    }

    /* renamed from: d */
    public static final void showPhotoSelector() {
        boolean z;
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        photoUrlList = c0028ba.accountProfile.photoUrls;
        Vector vectorM1213g = NetworkUtils.newVector();
        Enumeration enumerationElements = c0028ba.contactMap.elements();
        while (enumerationElements.hasMoreElements()) {
            Contact abstractC0041l = (Contact) enumerationElements.nextElement();
            if (!abstractC0041l.isOffline() && !abstractC0041l.isOnline()) {
                vectorM1213g.addElement(abstractC0041l);
            }
        }
        int size = vectorM1213g.size();
        Screen c0013amM75b = ScreenManager.createScreen(4248);
        contactIdList = NetworkUtils.newVector();
        for (int i = 0; i < size; i++) {
            MrimContact c0035f = (MrimContact) vectorM1213g.elementAt(i);
            String strMo135a = c0035f.getIdentifier();
            String str = c0035f.displayName;
            String[] strArr = photoUrlList;
            int length = strArr.length;
            while (true) {
                length--;
                if (length >= 0) {
                    if (StringUtils.equals(strMo135a, strArr[length])) {
                        z = true;
                        break;
                    }
                } else {
                    z = false;
                    break;
                }
            }
            c0013amM75b.addItem(MenuItem.createCheckbox(str, z));
            contactIdList.addElement(strMo135a);
        }
        selectionScreen = c0013amM75b;
        ScreenManager.showScreen(c0013amM75b);
    }

    /* renamed from: e */
    public static final int applyPhotoSelection() {
        Vector vector = selectionScreen.menuItems;
        Vector vectorM1213g = NetworkUtils.newVector();
        int size = vector.size();
        for (int i = 0; i < size; i++) {
            if (((Boolean) ((MenuItem) vector.elementAt(i)).data).booleanValue()) {
                vectorM1213g.addElement(contactIdList.elementAt(i));
            }
        }
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        VCard c0003ac = c0028ba.accountProfile;
        c0003ac.prevPhotoUrls = c0003ac.photoUrls;
        int size2 = vectorM1213g.size();
        c0003ac.photoUrls = new String[size2];
        for (int i2 = 0; i2 < size2; i2++) {
            c0003ac.photoUrls[i2] = (String) vectorM1213g.elementAt(i2);
        }
        String[] strArr = c0028ba.accountProfile.photoUrls;
        XmlElement c0022av = new XmlElement(114);
        XmlElement c0022av2 = new XmlElement("visible", c0022av, null);
        c0022av.addChild(c0022av2);
        for (String str : strArr) {
            XmlElement c0022av3 = new XmlElement("u", c0022av2, null);
            c0022av3.setAttrValue(328413, str);
            c0022av2.addChild(c0022av3);
        }
        c0028ba.trySendData(AppController.createMrimPacket(c0028ba, 4181, new ByteBuffer().writeStringLatin1("geo-list").writeStringLatin1(c0022av.toString())));
        if (c0028ba.accountProfile.gender != 3) {
            return 0;
        }
        c0028ba.setProfileGroups();
        return 0;
    }

    /* renamed from: a */
    public static final int handleMapSearch(int i, Object obj) {
        if (i == 6) {
            return handleMapPointAction(obj);
        }
        NetworkUtils.m1195d();
        String strM522f = Utils.defaultStr(AppState.getString(1248));
        if (StringUtils.isEmpty(strM522f)) {
            return AppController.showError(351);
        }
        boolean z = true;
        int i2 = 0;
        int length = strM522f.length();
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            char cCharAt = strM522f.charAt(length);
            if (cCharAt == '.') {
                i2 += 10;
            } else if (cCharAt == ',') {
                i2++;
            } else {
                z &= cCharAt >= '0' && cCharAt <= '9';
            }
        }
        if (z && i2 == 21) {
            try {
                long jM807b = longitudeToPixel(extractLongitude(strM522f));
                long jM808c = latitudeToPixel(extractLatitude(strM522f));
                MapRenderer.setPosition(jM807b, jM808c);
                MapRenderer.setZoom(StringUtils.isInSavedRegion(jM807b, jM808c) ? 13 : 10);
            } catch (Throwable unused) {
            }
        } else {
            String strM1109a = Conversation.replaceText(strM522f, 1046, 199350);
            Image imageM615n = AppState.getImage(1364);
            long j = MapRenderer.currentLat;
            new AsyncTask(9, new ByteBuffer().writeCompressed(1442705).writeCompressed(1511760).writeRawString(Conversation.urlEncodeCyrillic((Object) strM1109a)).writeCompressed(659815).writeLongAsString(j).writeCompressed(659825).writeLongAsString(MapRenderer.currentLon).writeCompressed(659835).writeIntAsString(imageM615n.getWidth()).writeCompressed(659845).writeIntAsString(imageM615n.getHeight()).getStringAndClear());
        }
        return AppState.getBool(1477) ? 161 : 6;
    }

    /* renamed from: c */
    public static final int handleMapPointAction(Object obj) {
        if (AppState.getBool(1443)) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return 6;
        }
        if (!AppState.getBool(1477)) {
            ConnectionThread.m1165a((MapPoint) obj, true);
            return 6;
        }
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        c0028ba.setLocationProfile((MapPoint) obj);
        c0028ba.syncProfile();
        AppState.setInt(1477, 0);
        return 160;
    }

    /* renamed from: f */
    public static final void requestNearbyPeople() {
        ByteBuffer c0043nM1310c = new ByteBuffer().writeCompressed(1901187).writeRawString(pixelToLatitude((int) AppController.pixelToCoord((int) (MapRenderer.currentPixelY - (MapRenderer.viewportHeight / 2)), AppState.getInt(39)))).writeCompressed(393954).writeRawString(pixelToLongitude((int) AppController.pixelToCoord((int) (MapRenderer.currentPixelX - (MapRenderer.viewportWidth / 2)), AppState.getInt(39)))).writeCompressed(393960).writeRawString(pixelToLatitude((int) AppController.pixelToCoord((int) (MapRenderer.currentPixelY + (MapRenderer.viewportHeight / 2)), AppState.getInt(39)))).writeCompressed(393966).writeRawString(pixelToLongitude((int) AppController.pixelToCoord((int) (MapRenderer.currentPixelX + (MapRenderer.viewportWidth / 2)), AppState.getInt(39)))).writeCompressed(1376928);
        long jM692d = SoftFloat.multiply(4612811918334230528L, SoftFloat.longToFloat(((MapRenderer.viewportHeight / 128) + 2) * ((MapRenderer.viewportWidth / 128) + 2)));
        int iM586d = AppState.getInt(39);
        long j = MapRenderer.currentPixelX;
        int i = MapRenderer.viewportWidth / 2;
        long jM318a = AppController.pixelToCoord((int) (j + i), iM586d) - AppController.pixelToCoord((int) (MapRenderer.currentPixelX - i), iM586d);
        long j2 = MapRenderer.currentPixelY;
        int i2 = MapRenderer.viewportHeight / 2;
        ByteBuffer c0043nM1314d = c0043nM1310c.writeRawString(SoftFloat.formatFloat(SoftFloat.divide(jM692d, SoftFloat.longToFloat(jM318a * (AppController.pixelToCoord((int) (j2 + i2), iM586d) - AppController.pixelToCoord((int) (MapRenderer.currentPixelY - i2), iM586d)))), 100));
        VCard.staticTs1 = (int) AppController.pixelToCoord((int) (MapRenderer.currentPixelX - (MapRenderer.viewportWidth / 2)), AppState.getInt(39));
        VCard.staticTs2 = (int) AppController.pixelToCoord((int) (MapRenderer.currentPixelY - (MapRenderer.viewportHeight / 2)), AppState.getInt(39));
        VCard.staticTs3 = (int) AppController.pixelToCoord((int) (MapRenderer.currentPixelX + (MapRenderer.viewportWidth / 2)), AppState.getInt(39));
        VCard.staticTs4 = (int) AppController.pixelToCoord((int) (MapRenderer.currentPixelY + (MapRenderer.viewportHeight / 2)), AppState.getInt(39));
        VCard.staticTs5 = AppState.getInt(39);
        new AsyncTask(20, new Object[]{c0043nM1314d.getStringAndClear(), ResourceManager.integerOf(AppState.getInt(39))});
    }

    /* renamed from: g */
    public static final void postOkEvent() {
        postEvent(AppState.pool[1267]);
    }

    /* renamed from: h */
    public static final void postCancelEvent() {
        postEvent(AppState.pool[1268]);
    }

    /* renamed from: i */
    public static final void postSelectEvent() {
        postEvent(AppState.pool[1269]);
    }

    /* renamed from: j */
    public static final void postBackEvent() {
        postEvent(AppState.pool[1270]);
    }

    /* renamed from: a */
    public static final void postNavigationEvent(int i, int i2, int i3) {
        postEvent(new int[]{0, i, i2, i3});
    }

    /* renamed from: d */
    public static final void postEvent(Object obj) {
        Vector vectorM614m = AppState.getVector(1266);
        synchronized (vectorM614m) {
            vectorM614m.addElement(obj);
        }
    }

    /* renamed from: a */
    public static final void postRenameError(Object[] objArr, int i) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(455)).append(objArr[2]).append(AppState.getString(457)).append(i)));
    }

    /* renamed from: b */
    public static final void postAddGroupError(Object[] objArr, int i) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(456)).append(objArr[2]).append(AppState.getString(457)).append(i)));
    }

    /* renamed from: c */
    public static final void postDeleteError(Object[] objArr, int i) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(454)).append(objArr[2]).append(AppState.getString(457)).append(i)));
    }

    /* renamed from: b */
    public static final void postOperationError(int i) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(464)).append(AppState.getString(457)).append(i)));
    }

    /* renamed from: a */
    public static final void postAccountError(Account abstractC0037h, int i) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(459)).append(abstractC0037h).append(AppState.getString(460)).append(AppState.getString(i))));
    }

    /* renamed from: a */
    public static final void postAccountMessage(Account abstractC0037h, String str) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(459)).append(abstractC0037h).append(AppState.getString(460)).append(str)));
    }

    /* renamed from: b */
    private static void postAccountNotification(Account abstractC0037h, String str) {
        postEvent((Object) new Object[]{abstractC0037h, str});
    }

    /* renamed from: a */
    public static final void postAccountEvent(MrimAccount c0028ba) {
        postEvent(new IOUtils(6, c0028ba));
    }

    /* renamed from: a */
    public static final int handleMailForwardAction(String str) {
        String strM584b = AppState.getString(1346);
        int iM586d = AppState.getInt(1513);
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        Message c0026azM1415b = c0028ba.findChatRoomById(iM586d).getMessage(strM584b);
        Vector vectorM668b = c0026azM1415b.getToList();
        Vector vectorM669c = c0026azM1415b.getCcList();
        String strM673d = c0026azM1415b.getSubject();
        String strM584b2 = AppState.getString(198549);
        String strM584b3 = AppState.getString(198546);
        String str2 = ((MrimAccount) AppState.getAccount()).login;
        wrapInVector(strM584b);
        if (StringUtils.matchesKey(839, str)) {
            ScreenBuilder.onScreenClosed();
            ResourceManager.composeEmail(XmppMailRuProtocol.getFirstAddress(vectorM668b), StringUtils.concat(strM584b2, strM673d), Utils.quoteText(c0026azM1415b.body));
            return 0;
        }
        if (!StringUtils.matchesKey(840, str)) {
            if (StringUtils.matchesKey(841, str)) {
                ScreenBuilder.onScreenClosed();
                ResourceManager.composeEmail(NetworkUtils.newVector(), StringUtils.concat(strM584b3, strM673d), Utils.quoteText(c0026azM1415b.body));
                return 0;
            }
            if (!StringUtils.matchesKey(845, str)) {
                return 0;
            }
            AppState.setInt(1527, c0028ba.findDefaultChatRoom().id);
            return 0;
        }
        ScreenBuilder.onScreenClosed();
        Vector vectorM865a = XmppMailRuProtocol.mergeAddressLists(XmppMailRuProtocol.copyAddressList(vectorM669c), vectorM668b);
        int iM541c = Utils.vectorSize(vectorM865a);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                break;
            }
            Object objElementAt = vectorM865a.elementAt(iM541c);
            if (StringUtils.equals(str2, ((String[]) objElementAt)[0])) {
                vectorM865a.removeElement(objElementAt);
                break;
            }
        }
        ResourceManager.composeEmail(vectorM865a, StringUtils.concat(strM584b2, strM673d), Utils.quoteText(c0026azM1415b.body));
        return 0;
    }

    /* renamed from: a */
    private static final ByteBuffer createContactCommand(MmpProtocol c0033d, MmpContact c0009ai, int i) {
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortString(c0009ai.identifier).writeShortBE(0);
        int iM920k = c0033d.generateUniqueGroupId();
        return c0033d.queueCommand(new Object[]{AppController.createMmpCommand(c0033d, 4872, c0043nM1357m.writeShortBE(iM920k).writeShortBE(i).writeShortBE(0)), ResourceManager.integerOf(18), c0009ai, ResourceManager.integerOf(i), ResourceManager.integerOf(iM920k)});
    }

    /* renamed from: a */
    private static final ByteBuffer updateContactCommand(MmpProtocol c0033d, MmpContact c0009ai, int i, int i2) {
        return c0033d.queueCommand(new Object[]{AppController.createMmpCommand(c0033d, 4874, new ByteBuffer().writeShortString(c0009ai.identifier).writeShortBE(0).writeShortBE(i).writeShortBE(i2).writeShortBE(0)), ResourceManager.integerOf(19), c0009ai, ResourceManager.integerOf(i2)});
    }

    /* renamed from: a */
    public static final ByteBuffer deleteContact(MmpProtocol c0033d, MmpContact c0009ai) {
        return c0009ai.canDelete() ? updateContactCommand(c0033d, c0009ai, c0009ai.canDelete, 2) : createContactCommand(c0033d, c0009ai, 2);
    }

    /* renamed from: b */
    public static final ByteBuffer blockContact(MmpProtocol c0033d, MmpContact c0009ai) {
        return c0009ai.canBlock() ? updateContactCommand(c0033d, c0009ai, c0009ai.canBlock, 3) : createContactCommand(c0033d, c0009ai, 3);
    }

    /* renamed from: c */
    public static final ByteBuffer unblockContact(MmpProtocol c0033d, MmpContact c0009ai) {
        return c0009ai.canUnblock() ? updateContactCommand(c0033d, c0009ai, c0009ai.canUnblock, 14) : createContactCommand(c0033d, c0009ai, 14);
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00a9  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final Screen buildContactListScreen(Screen c0013am, Account abstractC0037h, Contact abstractC0041l) {
        MenuItem c0032cM899a = null;
        if (abstractC0041l != null) {
            abstractC0037h = abstractC0041l.account;
        }
        Vector vectorM1078P = abstractC0037h.getAllContacts();
        int size = vectorM1078P.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            MrimContact c0035f = (MrimContact) vectorM1078P.elementAt(size);
            if (c0035f.isSystem() || c0035f.isOnline() || c0035f.isOffline() || c0035f.hasUnread()) {
                vectorM1078P.removeElementAt(size);
            }
        }
        AppController.sortContacts(vectorM1078P);
        for (int i = 0; i < vectorM1078P.size(); i++) {
            MrimContact c0035f2 = (MrimContact) vectorM1078P.elementAt(i);
            String str = c0035f2.simpleIdentifier;
            String str2 = c0035f2.displayName;
            if (abstractC0041l != null) {
                MrimContact c0035f3 = (MrimContact) abstractC0041l;
                c0032cM899a = c0035f3.groupsList != null && c0035f3.groupsList.contains(str) ? new MenuItem(2, str2).setIconAndLabel(375, str2) : MenuItem.createCheckbox(str2, false);
            }
            c0032cM899a.title = str;
            c0013am.addItem(c0032cM899a);
        }
        NetworkUtils.releaseVector(vectorM1078P);
        return c0013am;
    }

    /* renamed from: a */
    public static final Vector getCheckedItems(Screen c0013am, int i) {
        Vector vectorM1213g = NetworkUtils.newVector();
        Vector vector = c0013am.menuItems;
        int size = vector.size();
        while (true) {
            size--;
            if (size < i) {
                return vectorM1213g;
            }
            MenuItem c0032c = (MenuItem) vector.elementAt(size);
            Object obj = c0032c.data;
            if (obj != null && ((Boolean) obj).booleanValue()) {
                vectorM1213g.addElement(c0032c.title);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: l */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: b */
    public static final int handleContactMenuAction(String str, int i) {
        AppState.clearIndex(1281);
        Contact abstractC0041lM611g = AppState.getCurrentContact();
        if (i == 63 && !abstractC0041lM611g.account.isConnected()) {
            return AppController.showError(299);
        }
        if (i == 54 || i == 63 || i == 85) {
            ScreenBuilder.onScreenClosed();
        }
        if (StringUtils.matchesKey(717, str)) {
            int iM993f = ((MrimContact) abstractC0041lM611g).requestUserDetails();
            return 0 != iM993f ? AppController.showError(iM993f) : i;
        }
        if (i == 65) {
            ScreenBuilder.onScreenClosed();
            return ResourceManager.clearSmsFields();
        }
        if (i == 66) {
            if (abstractC0041lM611g instanceof XmppContact) {
                return ((XmppContact) abstractC0041lM611g).sendPresence(40);
            }
            AppState.pool[1319] = new ContactInfo(abstractC0041lM611g);
        } else if (i == 54) {
            AppState.setAccount(abstractC0041lM611g.account);
            ResourceManager.composeEmail(XmppMailRuProtocol.parseRecipientList(((MrimContact) abstractC0041lM611g).simpleIdentifier), (String) null, (String) null);
        } else if (i == 6) {
            ListItem interfaceC0044o = (ListItem) abstractC0041lM611g;
            interfaceC0044o.deselect();
            ConnectionThread.m1172a(interfaceC0044o);
        }
        return i;
    }

    /* renamed from: a */
    private static final Object[] createHttpResult(int i, Object obj, int i2, ByteBuffer c0043n) {
        return new Object[]{ResourceManager.integerOf(i), ResourceManager.integerOf(i2), obj.toString(), c0043n};
    }

    /* renamed from: a */
    private static final Object[] createErrorResult(int i, int i2, Object obj) {
        return createHttpResult(i, NetworkUtils.newStringBuffer().append(AppState.getString(i2)).append(AppState.getString(946)).append(obj), 0, (ByteBuffer) null);
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
    public static final Object[] createHttpRequest(int i, String str, ByteBuffer c0043n) {
        return createHttpResult(0, str, i, c0043n);
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

    /* renamed from: b */
    public static final long longitudeToPixel(String str) {
        return SoftFloat.floatToLong(SoftFloat.multiply(4708606483430899712L, SoftFloat.multiply(SoftFloat.parseFloat(str), 4580687790476533044L)));
    }

    /* renamed from: c */
    public static final long latitudeToPixel(String str) {
        long jM697a = SoftFloat.parseFloat(str);
        long j = jM697a;
        if (jM697a > 4635963235168681984L) {
            j = 4635963235168681984L;
        }
        if (j < -4587408801686093824L) {
            j = -4587408801686093824L;
        }
        long jM692d = SoftFloat.multiply(j, 4580687790476533044L);
        long jM692d2 = SoftFloat.multiply(4590560114707566468L, SoftFloat.sin(jM692d));
        return SoftFloat.floatToLong(SoftFloat.subtract(0L, SoftFloat.multiply(4708606483430899712L, SoftFloat.log(SoftFloat.divide(SoftFloat.cos(SoftFloat.divide(SoftFloat.subtract(4609753056924675352L, jM692d), 4611686018427387904L)), SoftFloat.pow(SoftFloat.divide(SoftFloat.subtract(4607182418800017408L, jM692d2), SoftFloat.add(4607182418800017408L, jM692d2)), 4586056515080195972L))))));
    }

    /* renamed from: a */
    public static final String pixelToLongitude(long j) {
        return SoftFloat.formatFloat(SoftFloat.divide(SoftFloat.divide(SoftFloat.longToFloat(j), 4708606483430899712L), 4580687790476533044L), 9);
    }

    /* renamed from: b */
    public static final String pixelToLatitude(long j) {
        long jM703f = SoftFloat.exp(SoftFloat.divide(SoftFloat.negate(SoftFloat.longToFloat(j)), 4708606483430899712L));
        long jM693e = SoftFloat.divide(4590560114707566468L, 4611686018427387904L);
        long jM691c = SoftFloat.subtract(4609753056924675352L, SoftFloat.multiply(SoftFloat.atan(jM703f), 4611686018427387904L));
        int i = 15;
        long jM691c2 = 4591870180066957722L;
        while (true) {
            i--;
            if (i <= 0 || SoftFloat.compare(jM691c2 & Long.MAX_VALUE, 4502148214488346440L) <= 0) {
                break;
            }
            long jM692d = SoftFloat.multiply(4590560114707566468L, SoftFloat.sin(jM691c));
            jM691c2 = SoftFloat.subtract(SoftFloat.subtract(4609753056924675352L, SoftFloat.multiply(SoftFloat.atan(SoftFloat.multiply(jM703f, SoftFloat.pow(SoftFloat.divide(SoftFloat.subtract(4607182418800017408L, jM692d), SoftFloat.add(4607182418800017408L, jM692d)), jM693e))), 4611686018427387904L)), jM691c);
            jM691c = SoftFloat.add(jM691c, jM691c2);
        }
        return SoftFloat.formatFloat(SoftFloat.divide(jM691c, 4580687790476533044L), 9);
    }

    /* renamed from: e */
    private static String extractLatitude(String str) {
        try {
            return StringUtils.prefix(str, Utils.removeChar(str, ' ').indexOf(44));
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: f */
    private static String extractLongitude(String str) {
        try {
            return StringUtils.suffix(str, Utils.removeChar(str, ' ').indexOf(44) + 1);
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: b */
    public static final void sendChatRoomRequest(Object[] objArr) {
        AppController.markAccountHighlighted((MrimAccount) AppState.getAccount());
        AppState.pool[1271] = ConnectionThread.m1149a(objArr);
    }

    /* renamed from: e */
    public static final void setSelectedItems(Object obj) {
        AppState.pool[1356] = obj;
    }

    /* renamed from: g */
    private static void wrapInVector(String str) {
        Vector vectorM1213g = NetworkUtils.newVector();
        vectorM1213g.addElement(str);
        setSelectedItems(vectorM1213g);
    }

    /* renamed from: k */
    public static final Object[] pollAsyncResult() {
        Object[] objArrM609l = AppState.getObjectArray(1271);
        if (objArrM609l != null && ConnectionThread.m1156c(objArrM609l) != null) {
            AppState.clearIndex(1271);
        }
        return objArrM609l;
    }

    /* renamed from: a */
    public static final StringBuffer appendAuthParams(StringBuffer stringBuffer, String str) {
        return stringBuffer.append(AppState.getString(1381)).append(AppState.getString(395134)).append(str);
    }

    /* renamed from: c */
    public static final int validateJsonResponse(Object[] objArr) {
        AppState.clearIndex(1355);
        if (!isHttpSuccess(objArr)) {
            return AppController.showError(888);
        }
        Object objM806e = parseJsonResponse(objArr);
        if (objM806e == null) {
            return AppController.showError(889);
        }
        if (!JsonParser.isSuccess(objM806e)) {
            return AppController.showError(890);
        }
        AppState.pool[1355] = objM806e;
        return 0;
    }

    /* renamed from: l */
    public static final Object getJsonPayload() {
        Object obj = AppState.pool[1355];
        AppState.clearIndex(1355);
        return JsonParser.getVectorElement(obj, 2);
    }

    /* renamed from: c */
    public static final int loginXmpp(int i) {
        String strM522f = Utils.defaultStr(AppState.getString(1293));
        String strM843u = XmppMailRuProtocol.getLoginLowerCase();
        String strM1215a = strM843u;
        if (StringUtils.isEmpty(strM843u)) {
            return AppController.showError(301);
        }
        int iM586d = AppState.getInt(1474);
        if (iM586d != 0 && strM1215a.indexOf(64) < 0) {
            strM1215a = NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(strM1215a).append(Utils.splitByNull(AppState.getString(696)).elementAt(iM586d)));
        }
        if (i == 2 && strM1215a.indexOf(64) < 0) {
            return AppController.showError(699);
        }
        int iM437a = AppController.validateCredentials(i, AppState.getAccount(), strM1215a, strM522f);
        if (0 != iM437a) {
            return AppController.showError(iM437a);
        }
        AppController.setCurrentAccount(AppController.createAccount(i, strM1215a).setDisplayName(Utils.defaultStr(AppState.getString(1297))));
        return 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x0198  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void handleFileTransfer(MmpProtocol c0033d, ByteBuffer c0043n) {
        int iM1353u;
        String strM1370r;
        String strM1368E;
        String strM1369q;
        long jM1341m = c0043n.readLong();
        int iM1353u2 = c0043n.readShortBE();
        String strM1363z = c0043n.readLenPrefixStr();
        c0043n.readShortBE();
        int iM1353u3 = c0043n.readShortBE();
        while (true) {
            iM1353u3--;
            if (iM1353u3 < 0) {
                break;
            }
            c0043n.readShortBE();
            c0043n.skip(c0043n.readShortBE());
        }
        while (true) {
            int iM1353u4 = c0043n.readShortBE();
            iM1353u = c0043n.readShortBE();
            if (iM1353u4 == 2 || iM1353u4 == 5) {
                break;
            } else {
                c0043n.skip(iM1353u);
            }
        }
        switch (iM1353u2) {
            case 1:
                int i = iM1353u;
                while (true) {
                    if (i <= 0) {
                        strM1369q = null;
                        break;
                    } else {
                        int iM1353u5 = c0043n.readShortBE();
                        int iM1353u6 = c0043n.readShortBE();
                        int i2 = i - 4;
                        if (iM1353u5 == 257) {
                            int iM1353u7 = c0043n.readShortBE();
                            c0043n.readShortBE();
                            strM1369q = iM1353u7 == 2 ? c0043n.readUnicodeChars(iM1353u6 - 4) : c0043n.readByteChars(iM1353u6 - 4);
                            break;
                        } else {
                            c0043n.skip(iM1353u6);
                            i = i2 - iM1353u6;
                        }
                    }
                }
                strM1370r = strM1369q;
                break;
            case 2:
                if (c0043n.readShortBE() == 0) {
                    c0043n.skip(24);
                    int i3 = iM1353u - 26;
                    while (i3 > 0) {
                        int iM1353u8 = c0043n.readShortBE();
                        int iM1353u9 = c0043n.readShortBE();
                        i3 -= iM1353u9 + 4;
                        if (iM1353u8 == 10001) {
                            c0043n.readShortLE();
                            c0043n.readShortLE();
                            int iM1355w = c0043n.readIntBE();
                            int iM1355w2 = c0043n.readIntBE();
                            int iM1355w3 = c0043n.readIntBE();
                            int iM1355w4 = c0043n.readIntBE();
                            c0043n.readShortBE();
                            c0043n.readInt();
                            c0043n.readByte();
                            c0043n.readShortBE();
                            int iM1354v = c0043n.readShortLE();
                            c0043n.readShortBE();
                            c0043n.skip(iM1354v - 2);
                            if ((iM1355w | iM1355w2 | iM1355w3 | iM1355w4) == 0) {
                                c0043n.readShortBE();
                                c0043n.readShortLE();
                                c0043n.readShortLE();
                                strM1368E = c0043n.readModifiedStrTrim();
                            } else {
                                strM1368E = null;
                            }
                            strM1370r = strM1368E;
                            if (strM1368E != null && strM1370r.length() > 0) {
                                c0033d.trySendData(AppController.createMmpCommand(c0033d, 1035, new ByteBuffer().writeLong(jM1341m).writeShortBE(2).writeByteLenStr(strM1363z).writeCompressed(3213669).writeShortLE(c0033d.getConnectionModeValue()).writeCompressed(3213718)));
                                break;
                            }
                        } else {
                            c0043n.skip(iM1353u9);
                        }
                    }
                    strM1368E = null;
                    strM1370r = strM1368E;
                    if (strM1368E != null) {
                        c0033d.trySendData(AppController.createMmpCommand(c0033d, 1035, new ByteBuffer().writeLong(jM1341m).writeShortBE(2).writeByteLenStr(strM1363z).writeCompressed(3213669).writeShortLE(c0033d.getConnectionModeValue()).writeCompressed(3213718)));
                    }
                } else {
                    strM1368E = null;
                    strM1370r = strM1368E;
                    if (strM1368E != null) {
                    }
                }
                break;
            case 3:
            default:
                strM1370r = null;
                break;
            case 4:
                c0043n.readIntBE();
                int iM1353u10 = c0043n.readShortBE();
                strM1370r = (iM1353u10 == 1 || iM1353u10 == 4) ? c0043n.readByteChars(c0043n.readShortLE() - 1) : null;
                break;
        }
        if (!Utils.nonEmpty(strM1370r) || StringUtils.matchesEncoded(strM1363z, 875573297)) {
            return;
        }
        if (StringUtils.matchesEncoded(strM1363z, 49)) {
            throw new RuntimeException();
        }
        c0033d.onMessage(strM1363z, 0L, strM1370r);
    }

    /* renamed from: a */
    public static final void updateContactFlags(Contact abstractC0041l) {
        AppState.setBool(1504, (abstractC0041l instanceof XmppContact) && !((XmppProtocol) abstractC0041l.account).mo83f());
    }

    /* renamed from: c */
    public static final int handleContactGroupAction(String str, int i) {
        AppState.clearIndex(1281);
        Object obj = AppState.pool[1365];
        if (i == 63 && !((Contact) obj).account.isConnected()) {
            return AppController.showError(299);
        }
        if (i == 40 || i == 63 || i == 85) {
            ScreenBuilder.onScreenClosed();
            if (i != 85) {
                AppController.clearSearchState();
            }
        }
        if (StringUtils.matchesKey(717, str)) {
            int iM993f = ((MrimContact) obj).requestUserDetails();
            if (0 != iM993f) {
                return AppController.showError(iM993f);
            }
            return 40;
        }
        if (i == 65) {
            ScreenBuilder.onScreenClosed();
            AppController.clearSearchState();
            return ResourceManager.clearSmsFields();
        }
        if (i == 66) {
            if (obj instanceof XmppContact) {
                return ((XmppContact) obj).sendPresence(4);
            }
            AppState.pool[1319] = new ContactInfo((Contact) obj);
        } else if (i == 54) {
            AppState.setAccount(((MrimContact) obj).account);
            ResourceManager.composeEmail(XmppMailRuProtocol.parseRecipientList(((MrimContact) obj).simpleIdentifier), (String) null, (String) null);
        } else if (i == 6) {
            ListItem interfaceC0044o = (ListItem) obj;
            interfaceC0044o.deselect();
            ConnectionThread.m1172a(interfaceC0044o);
            AppController.applyViewMode(true, false, !AppState.getBool(276));
            AppState.setInt(281, 1);
        }
        return i;
    }

    /* renamed from: d */
    public static final int handleStatusChange(int i) {
        Account abstractC0037hM616i = AppState.getAccount();
        switch (abstractC0037hM616i.getType()) {
            case 0:
                MrimAccount c0028ba = (MrimAccount) abstractC0037hM616i;
                if (i == 6) {
                    return 17;
                }
                if (i == 5) {
                    int iMo120l = c0028ba.disconnect();
                    if (0 != iMo120l) {
                        return AppController.showError(iMo120l);
                    }
                    return 4;
                }
                int iM721d = c0028ba.setConfiguration(new int[]{1, 260, 2, 516, 3}[i]);
                if (0 != iM721d) {
                    return AppController.showError(iM721d);
                }
                return 4;
            case 1:
                MmpProtocol c0033d = (MmpProtocol) abstractC0037hM616i;
                if (i == 13) {
                    return 17;
                }
                if (i == 14) {
                    return 109;
                }
                if (i == 12) {
                    int iMo120l2 = c0033d.disconnect();
                    if (0 != iMo120l2) {
                        return AppController.showError(iMo120l2);
                    }
                    return 4;
                }
                int iM918b = c0033d.updateConnectionMode(new int[]{0, 32, 256, 2, 1, 4, 16, 24576, 20480, 16384, 12288, 8193}[i]);
                if (0 != iM918b) {
                    return AppController.showError(iM918b);
                }
                return 4;
            default:
                XmppProtocol c0005ae = (XmppProtocol) abstractC0037hM616i;
                if (i == 0) {
                    int iMo120l3 = c0005ae.disconnect();
                    if (0 != iMo120l3) {
                        return AppController.showError(iMo120l3);
                    }
                    return 4;
                }
                int iM103b = c0005ae.setStatusMode(i);
                if (0 != iM103b) {
                    return AppController.showError(iM103b);
                }
                return 4;
        }
    }

    /* renamed from: d */
    public static final String transliterate(String str) {
        boolean zIsUpperCase = false;
        String str2 = null;
        String str3;
        Vector vectorM512e = Utils.splitByNull(AppState.getString(14290598));
        Vector vectorM512e2 = Utils.splitByNull(AppState.getString(958));
        Hashtable hashtable = new Hashtable();
        int size = vectorM512e.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            hashtable.put(vectorM512e.elementAt(size), vectorM512e2.elementAt(size));
        }
        String strM584b = AppState.getString(956);
        String strM584b2 = AppState.getString(957);
        Hashtable hashtable2 = new Hashtable();
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        int length = strM584b.length();
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            hashtable2.put(StringUtils.extractBuffer(stringBufferM1217h.append(strM584b.charAt(length))), StringUtils.extractBuffer(stringBufferM1217h.append(strM584b2.charAt(length))));
        }
        int length2 = str.length();
        int i = 0;
        while (i < length2) {
            String strM9b = null;
            int i2 = 3;
            while (true) {
                if (i2 < 1) {
                    break;
                }
                try {
                    String strM12a = StringUtils.substring(str, i, i + i2);
                    zIsUpperCase = Character.isUpperCase(strM12a.charAt(0));
                    str2 = (String) hashtable.get(StringUtils.intern(strM12a.toLowerCase()));
                    strM9b = str2;
                } catch (Throwable unused) {
                }
                if (str2 != null) {
                    if (zIsUpperCase && (str3 = (String) hashtable2.get(StringUtils.prefix(strM9b, 1))) != null) {
                        strM9b = strM9b.length() == 1 ? str3 : StringUtils.concat(str3, StringUtils.suffix(strM9b, 1));
                    }
                    i += i2 - 1;
                    stringBufferM1217h.append(strM9b);
                } else {
                    i2--;
                }
            }
            if (strM9b == null) {
                stringBufferM1217h.append(str.charAt(i));
            }
            i++;
        }
        NetworkUtils.releaseVector(vectorM512e);
        NetworkUtils.releaseVector(vectorM512e2);
        return NetworkUtils.bufToStringCached(stringBufferM1217h);
    }

    /* renamed from: d */
    public static final void performXmppAuth(Object[] objArr) {
        try {
            try {
                AppController.acquireNetworkLock();
                HttpClient c0024axM629a = HttpClient.createHttpClient((String) objArr[1], (Account) objArr[0], 0);
                int iM634a = c0024axM629a.getResponseCode();
                if (iM634a == 200) {
                    Vector vectorM516c = Utils.splitNonEmpty(new ByteBuffer(c0024axM629a).getStringAndClear(), '\n');
                    if (((Integer) objArr[2]).intValue() == 0) {
                        objArr[2] = ResourceManager.integerOf(1);
                        objArr[1] = new ByteBuffer().writeCompressed(2365173).writeCompressed(2692947).writeObjectStr(vectorM516c.elementAt(0)).writeByte(38).writeObjectStr(vectorM516c.elementAt(1)).readAllByteStr();
                        new AsyncTask(30, objArr);
                    } else {
                        setAuthResult(objArr, vectorM516c.elementAt(0));
                    }
                    NetworkUtils.releaseVector(vectorM516c);
                } else {
                    if (iM634a != 403) {
                        throw new Throwable(StringUtils.intern(Integer.toString(iM634a)));
                    }
                    ((XmppProtocol) objArr[0]).handleComplete();
                }
                HttpClient.closeAndUpdateStats(c0024axM629a);
                AppController.releaseNetworkLock();
            } catch (Throwable th) {
                setAuthResult(objArr, th);
                HttpClient.closeAndUpdateStats((HttpClient) null);
                AppController.releaseNetworkLock();
            }
        } catch (Throwable th2) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            AppController.releaseNetworkLock();
            throw th2;
        }
    }

    /* renamed from: a */
    private static final void setAuthResult(Object[] objArr, Object obj) {
        ((XmppProtocol) objArr[0]).authResult = obj;
    }
}
