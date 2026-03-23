package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
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
        String messageId = AppState.getString(StateKeys.SLOT_MESSAGE_ID);
        wrapInVector(messageId);
        int chatRoomId = AppState.getInt(StateKeys.INT_CHATROOM_ID);
        MrimAccount account = (MrimAccount) AppState.getAccount();
        Message message = account.findChatRoomById(chatRoomId).getMessage(messageId);
        String subject = message.getSubject();
        Vector toList = message.getToList();
        Vector ccList = message.getCcList();
        XmppMailRuProtocol.getFirstRecipient(toList);
        boolean needsAuth = AppState.getBool(StateKeys.SETTING_AUTH_REQUIRED);
        String replyPrefix = AppState.getString(StateKeys.STR_RES_HTTPS_PREFIX);
        String forwardPrefix = AppState.getString(StateKeys.STR_RES_HTTP_PREFIX);
        String body = AppState.emptyStr;
        if (i == 48) {
            ScreenBuilder.onScreenClosed();
            ScreenBuilder.onScreenClosed();
        }
        if (StringUtils.matchesKey(839, str)) {
            if (!needsAuth) {
                return ResourceManager.composeEmail(XmppMailRuProtocol.getFirstAddress(toList), new StringBuffer().append(replyPrefix).append(subject).toString(), body);
            }
            XmppMailRuProtocol.setMailAction(54, 0);
            return 0;
        }
        if (StringUtils.matchesKey(840, str)) {
            if (!needsAuth) {
                return ResourceManager.composeEmail(XmppMailRuProtocol.mergeAddressLists(XmppMailRuProtocol.copyAddressList(toList), ccList), new StringBuffer().append(replyPrefix).append(subject).toString(), body);
            }
            XmppMailRuProtocol.setMailAction(54, 1);
            return 0;
        }
        if (StringUtils.matchesKey(841, str)) {
            if (!needsAuth) {
                return ResourceManager.composeEmail(NetworkUtils.newVector(), new StringBuffer().append(forwardPrefix).append(subject).toString(), body);
            }
            XmppMailRuProtocol.setMailAction(54, 2);
            return 0;
        }
        if (StringUtils.matchesKey(855, str)) {
            AppState.setInt(StateKeys.INT_CHAT_VIEW_MODE, 2);
            return 0;
        }
        if (StringUtils.matchesKey(856, str)) {
            AppState.setInt(StateKeys.INT_CHAT_VIEW_MODE, 1);
            return 0;
        }
        if (!StringUtils.matchesKey(845, str)) {
            return 0;
        }
        AppState.setInt(StateKeys.INT_ACTIVE_CHATROOM_ID, account.findDefaultChatRoom().id);
        return 0;
    }

    /* renamed from: a */
    public static final ByteBuffer createSendMessageCmd(MmpProtocol protocol, MmpContact mmpContact, String str) {
        return ProtocolFactory.createMmpCommand(protocol, 4888, new ByteBuffer().writeByteLenStr(mmpContact.identifier).writeUTF(str).writeShortBE(0));
    }

    /* renamed from: a */
    public static final void playSound(int i) {
        stopSound();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new ByteBuffer().writeCompressed(590318).writeCompressed(i + 430).writeIntLE(3145472).toByteArray());
            Object resource = registerResource((Object) byteArrayInputStream);
            AppState.pool[StateKeys.RANGE_MEDIA_RESOURCES_START] = resource;
            if (null != resource) {
                Player playerCreatePlayer = Manager.createPlayer(byteArrayInputStream, AppState.getString(StateKeys.STR_RES_PROTOCOL_TAG_2));
                AppState.pool[StateKeys.OBJ_MEDIA_PLAYER] = registerResource(playerCreatePlayer);
                try {
                    playerCreatePlayer.realize();
                } catch (Throwable unused) {
                }
                if (AppState.getBool(StateKeys.SETTING_SOUND_ENABLED)) {
                    try {
                        ((javax.microedition.media.control.VolumeControl) playerCreatePlayer.getControl(AppState.getString(StateKeys.STR_RES_MEDIA_CONTROL))).setLevel(AppState.getInt(StateKeys.SETTING_VOLUME_LEVEL));
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
        Player player = (Player) AppState.pool[StateKeys.OBJ_MEDIA_PLAYER];
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
        closeInput((InputStream) AppState.pool[StateKeys.RANGE_MEDIA_RESOURCES_START]);
        AppState.clearRange(StateKeys.RANGE_MEDIA_RESOURCES_START, StateKeys.OBJ_MEDIA_PLAYER);
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
    public static final int getGroupCount(Account acct) {
        Vector vector = acct.groups;
        int count = Utils.vectorSize(vector);
        if (count > 0) {
            StringBuffer sb = NetworkUtils.newStringBuffer();
            for (int i = 0; i < count; i++) {
                sb.append(((ContactGroup) vector.elementAt(i)).name).append((char) 0);
            }
            AppState.setFromBuffer(StateKeys.SLOT_MENU_ITEM_1, sb);
            AppState.pool[StateKeys.VEC_GROUP_LIST] = vector;
            AppState.setInt(StateKeys.INT_GROUP_OPERATION_RESULT, 0);
        }
        return count;
    }

    /* renamed from: b */
    public static final void showAddContactScreen() {
        ContactInfo contactInfo = (ContactInfo) AppState.pool[StateKeys.SLOT_CONTACT_INFO];
        Account acctRef = contactInfo.getAccount();
        if (getGroupCount(acctRef) == 0) {
            postEvent((Object) AppState.getString(StateKeys.STR_NOTIFICATION_NEW_MSG));
            return;
        }
        if (AppState.getBool(StateKeys.FLAG_SHOW_PHOTO)) {
            AppState.setFromPool(StateKeys.SLOT_GROUP_ADD_GROUP, StateKeys.STR_SOFTKEY_OK);
            AppState.setInt(StateKeys.FLAG_SHOW_PHOTO, 0);
        } else {
            AppState.setFromPool(StateKeys.SLOT_GROUP_ADD_GROUP, StateKeys.STR_DEFAULT_GROUP_NAME);
        }
        if (acctRef.getType() == 1) {
            AppState.setObject(StateKeys.SLOT_GROUP_ADD_NAME, (Object) contactInfo.getString(60));
            AppState.setObject(StateKeys.SLOT_GROUP_ADD_DISPLAY, (Object) contactInfo.getDisplayNameOrId());
            ScreenManager.showScreen(ScreenManager.createScreen(3920));
            return;
        }
        if (((MrimAccount) acctRef).hasCustomDomain) {
            AppState.setInt(StateKeys.FLAG_GROUP_ADD_RESULT, 1);
            AppState.setInt(StateKeys.INT_ADD_CONTACT_MODE, 5);
        } else {
            AppState.setInt(StateKeys.FLAG_GROUP_ADD_RESULT, 0);
            AppState.setInt(StateKeys.INT_ADD_CONTACT_MODE, 4);
        }
        AppState.setObject(StateKeys.SLOT_GROUP_ADD_NAME, (Object) contactInfo.getEmailOrMmpId());
        AppState.setObject(StateKeys.SLOT_GROUP_ADD_DISPLAY, (Object) contactInfo.getFullName());
        ScreenManager.showScreen(ScreenManager.createScreen(3888));
    }

    /* renamed from: a */
    public static final void notifyNewMail(MrimAccount account, int i, String str, String str2) {
        boolean showPopup = AppState.getBool(StateKeys.SETTING_SHOW_POPUP);
        boolean showInList = AppState.getBool(StateKeys.SETTING_SHOW_IN_LIST);
        if (showInList || showPopup) {
            if (str != null) {
                int iLastIndexOf = str.lastIndexOf(60);
                if (str.length() > 30 && iLastIndexOf > 1) {
                    StringUtils.prefix(str, iLastIndexOf - 1);
                }
                ResourceManager.playNotificationSound(0);
            }
            if (showPopup && (AccountManager.getActiveScreenId() != 10 || !AppState.hasMemory())) {
                StringBuffer sb = NetworkUtils.newStringBuffer();
                if (str2 != null && str != null) {
                    postAccountNotification(account, NetworkUtils.bufToStringCached(sb.append(AppState.getString(StateKeys.STR_NEW_MAIL_FROM)).append(str).append(' ').append('\"').append(str2).append('\"').append('.').append('\n').append(new StringBuffer().append(i > 0 ? new StringBuffer().append(AppState.getString(StateKeys.STR_NEW_MAIL_COUNT)).append(i).append(AppState.getString(StateKeys.STR_NEW_MAIL_SUFFIX + Utils.pluralForm(i))).append('\n').toString() : AppState.emptyStr).append(AppState.getString(StateKeys.STR_MAIL_PREFIX)).toString())));
                } else if (i > 0) {
                    postAccountNotification(account, NetworkUtils.bufToStringCached(sb.append(AppState.getString(StateKeys.STR_NEW_MAIL_COUNT)).append(i).append(AppState.getString(StateKeys.STR_NEW_MAIL_SUFFIX + Utils.pluralForm(i))).append('\n').append(AppState.getString(StateKeys.STR_MAIL_PREFIX))));
                }
            }
            if (showInList) {
                if (i > 0 || !(str2 == null || str == null)) {
                    AppController.markScreenDirty();
                    AccountManager.markAccountHighlighted(account);
                    if (AppState.getBool(StateKeys.SETTING_SHOW_IN_LIST)) {
                        AppState.getVector(StateKeys.VEC_ACTIVE_CONNECTIONS).addElement(account);
                    }
                    TabBar.layout();
                }
            }
        }
    }

    /* renamed from: c */
    public static final void showChatRoomMessages() {
        ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(StateKeys.INT_CHATROOM_ID));
        Screen screen = ScreenManager.createScreen(4527);
        screen.setHeader(234, chatRoom.getDisplayName());
        Vector messages = NetworkUtils.newVector();
        Enumeration elements = chatRoom.messageIds.elements();
        while (elements.hasMoreElements()) {
            Hashtable hashtable = chatRoom.messages;
            Object key = elements.nextElement();
            if (hashtable.containsKey(key)) {
                messages.addElement(chatRoom.messages.get(key));
            }
        }
        Enumeration elements2 = messages.elements();
        while (elements2.hasMoreElements()) {
            screen.addItem(((Message) elements2.nextElement()).createMenuItem(chatRoom));
        }
        if (screen.menuItems.size() == 0) {
            screen.selectable = false;
            screen.addLabelById(835);
        } else {
            screen.scrollOffset = AppState.getInt(StateKeys.INT_SCROLL_OFFSET);
            screen.selectByTitle(AppState.getString(StateKeys.SLOT_MAP_POINT_2));
            screen.invalidateLayout();
        }
        screen.reverseScroll = true;
        ScreenManager.showScreen(screen);
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
        MrimAccount account = (MrimAccount) AppState.getAccount();
        photoUrlList = account.accountProfile.photoUrls;
        Vector candidates = NetworkUtils.newVector();
        Enumeration elements = account.contactMap.elements();
        while (elements.hasMoreElements()) {
            Contact contact = (Contact) elements.nextElement();
            if (!contact.isOffline() && !contact.isOnline()) {
                candidates.addElement(contact);
            }
        }
        int size = candidates.size();
        Screen screen = ScreenManager.createScreen(4248);
        contactIdList = NetworkUtils.newVector();
        for (int i = 0; i < size; i++) {
            MrimContact mrimContact = (MrimContact) candidates.elementAt(i);
            String identifier = mrimContact.getIdentifier();
            String str = mrimContact.displayName;
            String[] strArr = photoUrlList;
            int length = strArr.length;
            while (true) {
                length--;
                if (length >= 0) {
                    if (StringUtils.equals(identifier, strArr[length])) {
                        z = true;
                        break;
                    }
                } else {
                    z = false;
                    break;
                }
            }
            screen.addItem(MenuItem.createCheckbox(str, z));
            contactIdList.addElement(identifier);
        }
        selectionScreen = screen;
        ScreenManager.showScreen(screen);
    }

    /* renamed from: e */
    public static final int applyPhotoSelection() {
        Vector vector = selectionScreen.menuItems;
        Vector selected = NetworkUtils.newVector();
        int size = vector.size();
        for (int i = 0; i < size; i++) {
            if (((Boolean) ((MenuItem) vector.elementAt(i)).data).booleanValue()) {
                selected.addElement(contactIdList.elementAt(i));
            }
        }
        MrimAccount account = (MrimAccount) AppState.getAccount();
        VCard profile = account.accountProfile;
        profile.prevPhotoUrls = profile.photoUrls;
        int size2 = selected.size();
        profile.photoUrls = new String[size2];
        for (int i2 = 0; i2 < size2; i2++) {
            profile.photoUrls[i2] = (String) selected.elementAt(i2);
        }
        String[] strArr = account.accountProfile.photoUrls;
        XmlElement root = new XmlElement(114);
        XmlElement visibleEl = new XmlElement("visible", root, null);
        root.addChild(visibleEl);
        for (String str : strArr) {
            XmlElement userEl = new XmlElement("u", visibleEl, null);
            userEl.setAttrValue(328413, str);
            visibleEl.addChild(userEl);
        }
        account.trySendData(ProtocolFactory.createMrimPacket(account, 4181, new ByteBuffer().writeStringLatin1("geo-list").writeStringLatin1(root.toString())));
        if (account.accountProfile.gender != 3) {
            return 0;
        }
        account.setProfileGroups();
        return 0;
    }

    /* renamed from: a */
    public static final int handleMapSearch(int i, Object obj) {
        if (i == 6) {
            return handleMapPointAction(obj);
        }
        NetworkUtils.processScreenForm();
        String query = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SEARCH_QUERY));
        if (StringUtils.isEmpty(query)) {
            return NotificationHelper.showError(351);
        }
        boolean isCoordinate = true;
        int separatorScore = 0;
        int length = query.length();
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            char ch = query.charAt(length);
            if (ch == '.') {
                separatorScore += 10;
            } else if (ch == ',') {
                separatorScore++;
            } else {
                isCoordinate &= ch >= '0' && ch <= '9';
            }
        }
        if (isCoordinate && separatorScore == 21) {
            try {
                long lon = longitudeToPixel(extractLongitude(query));
                long lat = latitudeToPixel(extractLatitude(query));
                MapRenderer.setPosition(lon, lat);
                MapRenderer.setZoom(StringUtils.isInSavedRegion(lon, lat) ? 13 : 10);
            } catch (Throwable unused) {
            }
        } else {
            String encodedQuery = Conversation.replaceText(query, 1046, 199350);
            Image mapImage = AppState.getImage(StateKeys.OBJ_FONT_2);
            long currentLat = MapRenderer.currentLat;
            new AsyncTask(9, new ByteBuffer().writeCompressed(1442705).writeCompressed(1511760).writeRawString(Conversation.urlEncodeCyrillic((Object) encodedQuery)).writeCompressed(659815).writeLongAsString(currentLat).writeCompressed(659825).writeLongAsString(MapRenderer.currentLon).writeCompressed(659835).writeIntAsString(mapImage.getWidth()).writeCompressed(659845).writeIntAsString(mapImage.getHeight()).getStringAndClear());
        }
        return AppState.getBool(StateKeys.FLAG_LOADING) ? 161 : 6;
    }

    /* renamed from: c */
    public static final int handleMapPointAction(Object obj) {
        if (AppState.getBool(StateKeys.FLAG_NEW_MESSAGE)) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return 6;
        }
        if (!AppState.getBool(StateKeys.FLAG_LOADING)) {
            ConnectionThread.navigateToPoint((MapPoint) obj, true);
            return 6;
        }
        MrimAccount account = (MrimAccount) AppState.getAccount();
        account.setLocationProfile((MapPoint) obj);
        account.syncProfile();
        AppState.setInt(StateKeys.FLAG_LOADING, 0);
        return 160;
    }

    /* renamed from: f */
    public static final void requestNearbyPeople() {
        ByteBuffer c0043nM1310c = new ByteBuffer().writeCompressed(1901187).writeRawString(pixelToLatitude((int) MapUtils.pixelToCoord((int) (MapRenderer.currentPixelY - (MapRenderer.viewportHeight / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL)))).writeCompressed(393954).writeRawString(pixelToLongitude((int) MapUtils.pixelToCoord((int) (MapRenderer.currentPixelX - (MapRenderer.viewportWidth / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL)))).writeCompressed(393960).writeRawString(pixelToLatitude((int) MapUtils.pixelToCoord((int) (MapRenderer.currentPixelY + (MapRenderer.viewportHeight / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL)))).writeCompressed(393966).writeRawString(pixelToLongitude((int) MapUtils.pixelToCoord((int) (MapRenderer.currentPixelX + (MapRenderer.viewportWidth / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL)))).writeCompressed(1376928);
        long jM692d = SoftFloat.multiply(4612811918334230528L, SoftFloat.longToFloat(((MapRenderer.viewportHeight / 128) + 2) * ((MapRenderer.viewportWidth / 128) + 2)));
        int iM586d = AppState.getInt(StateKeys.MAP_ZOOM_LEVEL);
        long j = MapRenderer.currentPixelX;
        int i = MapRenderer.viewportWidth / 2;
        long jM318a = MapUtils.pixelToCoord((int) (j + i), iM586d) - MapUtils.pixelToCoord((int) (MapRenderer.currentPixelX - i), iM586d);
        long j2 = MapRenderer.currentPixelY;
        int i2 = MapRenderer.viewportHeight / 2;
        ByteBuffer c0043nM1314d = c0043nM1310c.writeRawString(SoftFloat.formatFloat(SoftFloat.divide(jM692d, SoftFloat.longToFloat(jM318a * (MapUtils.pixelToCoord((int) (j2 + i2), iM586d) - MapUtils.pixelToCoord((int) (MapRenderer.currentPixelY - i2), iM586d)))), 100));
        VCard.staticTs1 = (int) MapUtils.pixelToCoord((int) (MapRenderer.currentPixelX - (MapRenderer.viewportWidth / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL));
        VCard.staticTs2 = (int) MapUtils.pixelToCoord((int) (MapRenderer.currentPixelY - (MapRenderer.viewportHeight / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL));
        VCard.staticTs3 = (int) MapUtils.pixelToCoord((int) (MapRenderer.currentPixelX + (MapRenderer.viewportWidth / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL));
        VCard.staticTs4 = (int) MapUtils.pixelToCoord((int) (MapRenderer.currentPixelY + (MapRenderer.viewportHeight / 2)), AppState.getInt(StateKeys.MAP_ZOOM_LEVEL));
        VCard.staticTs5 = AppState.getInt(StateKeys.MAP_ZOOM_LEVEL);
        new AsyncTask(20, new Object[]{c0043nM1314d.getStringAndClear(), ResourceManager.integerOf(AppState.getInt(StateKeys.MAP_ZOOM_LEVEL))});
    }

    /* renamed from: g */
    public static final void postOkEvent() {
        postEvent(AppState.pool[StateKeys.ARR_EVENT_TYPE_1]);
    }

    /* renamed from: h */
    public static final void postCancelEvent() {
        postEvent(AppState.pool[StateKeys.ARR_EVENT_TYPE_2]);
    }

    /* renamed from: i */
    public static final void postSelectEvent() {
        postEvent(AppState.pool[StateKeys.ARR_EVENT_TYPE_3]);
    }

    /* renamed from: j */
    public static final void postBackEvent() {
        postEvent(AppState.pool[StateKeys.ARR_EVENT_TYPE_4]);
    }

    /* renamed from: a */
    public static final void postNavigationEvent(int i, int i2, int i3) {
        postEvent(new int[]{0, i, i2, i3});
    }

    /* renamed from: d */
    public static final void postEvent(Object obj) {
        Vector vectorM614m = AppState.getVector(StateKeys.VEC_EVENT_QUEUE);
        synchronized (vectorM614m) {
            vectorM614m.addElement(obj);
        }
    }

    /* renamed from: a */
    public static final void postRenameError(Object[] objArr, int i) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_REMOVED_FROM_LIST)).append(objArr[2]).append(AppState.getString(StateKeys.STR_MESSAGE_SEPARATOR)).append(i)));
    }

    /* renamed from: b */
    public static final void postAddGroupError(Object[] objArr, int i) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_TYPING_NOTIFICATION)).append(objArr[2]).append(AppState.getString(StateKeys.STR_MESSAGE_SEPARATOR)).append(i)));
    }

    /* renamed from: c */
    public static final void postDeleteError(Object[] objArr, int i) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_ADDED_TO_LIST)).append(objArr[2]).append(AppState.getString(StateKeys.STR_MESSAGE_SEPARATOR)).append(i)));
    }

    /* renamed from: b */
    public static final void postOperationError(int i) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_NETWORK_ERROR)).append(AppState.getString(StateKeys.STR_MESSAGE_SEPARATOR)).append(i)));
    }

    /* renamed from: a */
    public static final void postAccountError(Account acct, int i) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_ACCOUNT_CONNECTED)).append(acct).append(AppState.getString(StateKeys.STR_ACCOUNT_SEPARATOR)).append(AppState.getString(i))));
    }

    /* renamed from: a */
    public static final void postAccountMessage(Account acct, String str) {
        postEvent((Object) NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(AppState.getString(StateKeys.STR_ACCOUNT_CONNECTED)).append(acct).append(AppState.getString(StateKeys.STR_ACCOUNT_SEPARATOR)).append(str)));
    }

    /* renamed from: b */
    private static void postAccountNotification(Account acct, String str) {
        postEvent((Object) new Object[]{acct, str});
    }

    /* renamed from: a */
    public static final void postAccountEvent(MrimAccount account) {
        postEvent(new IOUtils(6, account));
    }

    /* renamed from: a */
    public static final int handleMailForwardAction(String str) {
        String strM584b = AppState.getString(StateKeys.SLOT_MESSAGE_ID);
        int iM586d = AppState.getInt(StateKeys.INT_CHATROOM_ID);
        MrimAccount account = (MrimAccount) AppState.getAccount();
        Message message = account.findChatRoomById(iM586d).getMessage(strM584b);
        Vector toList = message.getToList();
        Vector ccList = message.getCcList();
        String subject = message.getSubject();
        String strM584b2 = AppState.getString(StateKeys.STR_RES_HTTPS_PREFIX);
        String strM584b3 = AppState.getString(StateKeys.STR_RES_HTTP_PREFIX);
        String str2 = ((MrimAccount) AppState.getAccount()).login;
        wrapInVector(strM584b);
        if (StringUtils.matchesKey(839, str)) {
            ScreenBuilder.onScreenClosed();
            ResourceManager.composeEmail(XmppMailRuProtocol.getFirstAddress(toList), StringUtils.concat(strM584b2, subject), Utils.quoteText(message.body));
            return 0;
        }
        if (!StringUtils.matchesKey(840, str)) {
            if (StringUtils.matchesKey(841, str)) {
                ScreenBuilder.onScreenClosed();
                ResourceManager.composeEmail(NetworkUtils.newVector(), StringUtils.concat(strM584b3, subject), Utils.quoteText(message.body));
                return 0;
            }
            if (!StringUtils.matchesKey(845, str)) {
                return 0;
            }
            AppState.setInt(StateKeys.INT_ACTIVE_CHATROOM_ID, account.findDefaultChatRoom().id);
            return 0;
        }
        ScreenBuilder.onScreenClosed();
        Vector vectorM865a = XmppMailRuProtocol.mergeAddressLists(XmppMailRuProtocol.copyAddressList(ccList), toList);
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
        ResourceManager.composeEmail(vectorM865a, StringUtils.concat(strM584b2, subject), Utils.quoteText(message.body));
        return 0;
    }

    /* renamed from: a */
    private static final ByteBuffer createContactCommand(MmpProtocol protocol, MmpContact mmpContact, int i) {
        ByteBuffer c0043nM1357m = new ByteBuffer().writeShortString(mmpContact.identifier).writeShortBE(0);
        int iM920k = protocol.generateUniqueGroupId();
        return protocol.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(protocol, 4872, c0043nM1357m.writeShortBE(iM920k).writeShortBE(i).writeShortBE(0)), ResourceManager.integerOf(18), mmpContact, ResourceManager.integerOf(i), ResourceManager.integerOf(iM920k)});
    }

    /* renamed from: a */
    private static final ByteBuffer updateContactCommand(MmpProtocol protocol, MmpContact mmpContact, int i, int i2) {
        return protocol.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(protocol, 4874, new ByteBuffer().writeShortString(mmpContact.identifier).writeShortBE(0).writeShortBE(i).writeShortBE(i2).writeShortBE(0)), ResourceManager.integerOf(19), mmpContact, ResourceManager.integerOf(i2)});
    }

    /* renamed from: a */
    public static final ByteBuffer deleteContact(MmpProtocol protocol, MmpContact mmpContact) {
        return mmpContact.canDelete() ? updateContactCommand(protocol, mmpContact, mmpContact.canDelete, 2) : createContactCommand(protocol, mmpContact, 2);
    }

    /* renamed from: b */
    public static final ByteBuffer blockContact(MmpProtocol protocol, MmpContact mmpContact) {
        return mmpContact.canBlock() ? updateContactCommand(protocol, mmpContact, mmpContact.canBlock, 3) : createContactCommand(protocol, mmpContact, 3);
    }

    /* renamed from: c */
    public static final ByteBuffer unblockContact(MmpProtocol protocol, MmpContact mmpContact) {
        return mmpContact.canUnblock() ? updateContactCommand(protocol, mmpContact, mmpContact.canUnblock, 14) : createContactCommand(protocol, mmpContact, 14);
    }

    /* JADX WARN: Removed duplicated region for block: B:28:0x00a9  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final Screen buildContactListScreen(Screen screen, Account acct, Contact contact) {
        MenuItem menuItem = null;
        if (contact != null) {
            acct = contact.account;
        }
        Vector contacts = acct.getAllContacts();
        int size = contacts.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            MrimContact mrimContact = (MrimContact) contacts.elementAt(size);
            if (mrimContact.isSystem() || mrimContact.isOnline() || mrimContact.isOffline() || mrimContact.hasUnread()) {
                contacts.removeElementAt(size);
            }
        }
        AppController.sortContacts(contacts);
        for (int i = 0; i < contacts.size(); i++) {
            MrimContact mrimContact2 = (MrimContact) contacts.elementAt(i);
            String str = mrimContact2.simpleIdentifier;
            String str2 = mrimContact2.displayName;
            if (contact != null) {
                MrimContact mrimContact3 = (MrimContact) contact;
                menuItem = mrimContact3.groupsList != null && mrimContact3.groupsList.contains(str) ? new MenuItem(2, str2).setIconAndLabel(375, str2) : MenuItem.createCheckbox(str2, false);
            }
            menuItem.title = str;
            screen.addItem(menuItem);
        }
        NetworkUtils.releaseVector(contacts);
        return screen;
    }

    /* renamed from: a */
    public static final Vector getCheckedItems(Screen screen, int i) {
        Vector vectorM1213g = NetworkUtils.newVector();
        Vector vector = screen.menuItems;
        int size = vector.size();
        while (true) {
            size--;
            if (size < i) {
                return vectorM1213g;
            }
            MenuItem menuItem = (MenuItem) vector.elementAt(size);
            Object obj = menuItem.data;
            if (obj != null && ((Boolean) obj).booleanValue()) {
                vectorM1213g.addElement(menuItem.title);
            }
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v1, resolved type: l */
    /* JADX WARN: Multi-variable type inference failed */
    /* renamed from: b */
    public static final int handleContactMenuAction(String str, int i) {
        AppState.clearIndex(StateKeys.SLOT_CURRENT_ACCOUNT);
        Contact contact = AppState.getCurrentContact();
        if (i == 63 && !contact.account.isConnected()) {
            return NotificationHelper.showError(299);
        }
        if (i == 54 || i == 63 || i == 85) {
            ScreenBuilder.onScreenClosed();
        }
        if (StringUtils.matchesKey(717, str)) {
            int iM993f = ((MrimContact) contact).requestUserDetails();
            return 0 != iM993f ? NotificationHelper.showError(iM993f) : i;
        }
        if (i == 65) {
            ScreenBuilder.onScreenClosed();
            return ResourceManager.clearSmsFields();
        }
        if (i == 66) {
            if (contact instanceof XmppContact) {
                return ((XmppContact) contact).sendPresence(40);
            }
            AppState.pool[StateKeys.SLOT_CONTACT_INFO] = new ContactInfo(contact);
        } else if (i == 54) {
            AppState.setAccount(contact.account);
            ResourceManager.composeEmail(XmppMailRuProtocol.parseRecipientList(((MrimContact) contact).simpleIdentifier), (String) null, (String) null);
        } else if (i == 6) {
            ListItem item = (ListItem) contact;
            item.deselect();
            ConnectionThread.selectMapItem(item);
        }
        return i;
    }

    /* renamed from: a */
    private static final Object[] createHttpResult(int i, Object obj, int i2, ByteBuffer c0043n) {
        return new Object[]{ResourceManager.integerOf(i), ResourceManager.integerOf(i2), obj.toString(), c0043n};
    }

    /* renamed from: a */
    private static final Object[] createErrorResult(int i, int i2, Object obj) {
        return createHttpResult(i, NetworkUtils.newStringBuffer().append(AppState.getString(i2)).append(AppState.getString(StateKeys.STR_ERROR_SEPARATOR)).append(obj), 0, (ByteBuffer) null);
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
        AccountManager.markAccountHighlighted((MrimAccount) AppState.getAccount());
        AppState.pool[StateKeys.OBJ_REGISTRATION_DATA] = ConnectionThread.submitAsync(objArr);
    }

    /* renamed from: e */
    public static final void setSelectedItems(Object obj) {
        AppState.pool[StateKeys.SLOT_MEDIA_STREAM] = obj;
    }

    /* renamed from: g */
    private static void wrapInVector(String str) {
        Vector vectorM1213g = NetworkUtils.newVector();
        vectorM1213g.addElement(str);
        setSelectedItems(vectorM1213g);
    }

    /* renamed from: k */
    public static final Object[] pollAsyncResult() {
        Object[] objArrM609l = AppState.getObjectArray(StateKeys.OBJ_REGISTRATION_DATA);
        if (objArrM609l != null && ConnectionThread.getAsyncResult(objArrM609l) != null) {
            AppState.clearIndex(StateKeys.OBJ_REGISTRATION_DATA);
        }
        return objArrM609l;
    }

    /* renamed from: a */
    public static final StringBuffer appendAuthParams(StringBuffer stringBuffer, String str) {
        return stringBuffer.append(AppState.getString(StateKeys.SLOT_SESSION_HASH)).append(AppState.getString(StateKeys.STR_RES_STATUS_LABEL)).append(str);
    }

    /* renamed from: c */
    public static final int validateJsonResponse(Object[] objArr) {
        AppState.clearIndex(StateKeys.SLOT_MEDIA_PLAYER);
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
        AppState.pool[StateKeys.SLOT_MEDIA_PLAYER] = objM806e;
        return 0;
    }

    /* renamed from: l */
    public static final Object getJsonPayload() {
        Object obj = AppState.pool[StateKeys.SLOT_MEDIA_PLAYER];
        AppState.clearIndex(StateKeys.SLOT_MEDIA_PLAYER);
        return JsonParser.getVectorElement(obj, 2);
    }

    /* renamed from: c */
    public static final int loginXmpp(int i) {
        String strM522f = Utils.defaultStr(AppState.getString(StateKeys.SLOT_PASSWORD));
        String strM843u = XmppMailRuProtocol.getLoginLowerCase();
        String strM1215a = strM843u;
        if (StringUtils.isEmpty(strM843u)) {
            return NotificationHelper.showError(301);
        }
        int iM586d = AppState.getInt(StateKeys.INT_SERVER_INDEX);
        if (iM586d != 0 && strM1215a.indexOf(64) < 0) {
            strM1215a = NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(strM1215a).append(Utils.splitByNull(AppState.getString(StateKeys.STR_SERVER_LIST)).elementAt(iM586d)));
        }
        if (i == 2 && strM1215a.indexOf(64) < 0) {
            return NotificationHelper.showError(699);
        }
        int iM437a = AccountManager.validateCredentials(i, AppState.getAccount(), strM1215a, strM522f);
        if (0 != iM437a) {
            return NotificationHelper.showError(iM437a);
        }
        AccountManager.setCurrentAccount(AccountManager.createAccount(i, strM1215a).setDisplayName(Utils.defaultStr(AppState.getString(StateKeys.SLOT_DISPLAY_NAME))));
        return 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x0198  */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void handleFileTransfer(MmpProtocol protocol, ByteBuffer c0043n) {
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
                                protocol.trySendData(ProtocolFactory.createMmpCommand(protocol, 1035, new ByteBuffer().writeLong(jM1341m).writeShortBE(2).writeByteLenStr(strM1363z).writeCompressed(3213669).writeShortLE(protocol.getConnectionModeValue()).writeCompressed(3213718)));
                                break;
                            }
                        } else {
                            c0043n.skip(iM1353u9);
                        }
                    }
                    strM1368E = null;
                    strM1370r = strM1368E;
                    if (strM1368E != null) {
                        protocol.trySendData(ProtocolFactory.createMmpCommand(protocol, 1035, new ByteBuffer().writeLong(jM1341m).writeShortBE(2).writeByteLenStr(strM1363z).writeCompressed(3213669).writeShortLE(protocol.getConnectionModeValue()).writeCompressed(3213718)));
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
        protocol.onMessage(strM1363z, 0L, strM1370r);
    }

    /* renamed from: a */
    public static final void updateContactFlags(Contact contact) {
        AppState.setBool(StateKeys.FLAG_XMPP_CAN_EDIT, (contact instanceof XmppContact) && !((XmppProtocol) contact.account).mo83f());
    }

    /* renamed from: c */
    public static final int handleContactGroupAction(String str, int i) {
        AppState.clearIndex(StateKeys.SLOT_CURRENT_ACCOUNT);
        Object obj = AppState.pool[StateKeys.SLOT_CURRENT_ENTITY];
        if (i == 63 && !((Contact) obj).account.isConnected()) {
            return NotificationHelper.showError(299);
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
                return NotificationHelper.showError(iM993f);
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
            AppState.pool[StateKeys.SLOT_CONTACT_INFO] = new ContactInfo((Contact) obj);
        } else if (i == 54) {
            AppState.setAccount(((MrimContact) obj).account);
            ResourceManager.composeEmail(XmppMailRuProtocol.parseRecipientList(((MrimContact) obj).simpleIdentifier), (String) null, (String) null);
        } else if (i == 6) {
            ListItem item = (ListItem) obj;
            item.deselect();
            ConnectionThread.selectMapItem(item);
            AppController.applyViewMode(true, false, !AppState.getBool(StateKeys.FLAG_MAP_VIEW_ACTIVE));
            AppState.setInt(StateKeys.FLAG_REFRESH_CONTACTS, 1);
        }
        return i;
    }

    /* renamed from: d */
    public static final int handleStatusChange(int i) {
        Account acctM616i = AppState.getAccount();
        switch (acctM616i.getType()) {
            case 0:
                MrimAccount account = (MrimAccount) acctM616i;
                if (i == 6) {
                    return 17;
                }
                if (i == 5) {
                    int iMo120l = account.disconnect();
                    if (0 != iMo120l) {
                        return NotificationHelper.showError(iMo120l);
                    }
                    return 4;
                }
                int iM721d = account.setConfiguration(new int[]{1, 260, 2, 516, 3}[i]);
                if (0 != iM721d) {
                    return NotificationHelper.showError(iM721d);
                }
                return 4;
            case 1:
                MmpProtocol protocol = (MmpProtocol) acctM616i;
                if (i == 13) {
                    return 17;
                }
                if (i == 14) {
                    return 109;
                }
                if (i == 12) {
                    int iMo120l2 = protocol.disconnect();
                    if (0 != iMo120l2) {
                        return NotificationHelper.showError(iMo120l2);
                    }
                    return 4;
                }
                int iM918b = protocol.updateConnectionMode(new int[]{0, 32, 256, 2, 1, 4, 16, 24576, 20480, 16384, 12288, 8193}[i]);
                if (0 != iM918b) {
                    return NotificationHelper.showError(iM918b);
                }
                return 4;
            default:
                XmppProtocol c0005ae = (XmppProtocol) acctM616i;
                if (i == 0) {
                    int iMo120l3 = c0005ae.disconnect();
                    if (0 != iMo120l3) {
                        return NotificationHelper.showError(iMo120l3);
                    }
                    return 4;
                }
                int iM103b = c0005ae.setStatusMode(i);
                if (0 != iM103b) {
                    return NotificationHelper.showError(iM103b);
                }
                return 4;
        }
    }

    /* renamed from: d */
    public static final String transliterate(String str) {
        boolean zIsUpperCase = false;
        String str2 = null;
        String str3;
        Vector vectorM512e = Utils.splitByNull(AppState.getString(StateKeys.STR_RES_MEGA_URL_4));
        Vector vectorM512e2 = Utils.splitByNull(AppState.getString(StateKeys.STR_SOUND_LIST));
        Hashtable hashtable = new Hashtable();
        int size = vectorM512e.size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            }
            hashtable.put(vectorM512e.elementAt(size), vectorM512e2.elementAt(size));
        }
        String strM584b = AppState.getString(StateKeys.STR_SOUND_TYPE_1);
        String strM584b2 = AppState.getString(StateKeys.STR_SOUND_TYPE_2);
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
                NetworkLock.acquireNetworkLock();
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
                NetworkLock.releaseNetworkLock();
            } catch (Throwable th) {
                setAuthResult(objArr, th);
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
    private static final void setAuthResult(Object[] objArr, Object obj) {
        ((XmppProtocol) objArr[0]).authResult = obj;
    }
}
