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
import java.util.Calendar;
import java.util.Vector;
import javax.microedition.lcdui.Display;

/* renamed from: e */
/* loaded from: MobileAgent_3.9.jar:e.class */
public final class ResourceManager {

    /* renamed from: a */
    public final int tileType;

    /* renamed from: b */
    public final int zoomLevel;

    /* renamed from: c */
    public final int tileX;

    /* renamed from: d */
    public final int tileY;

    /* renamed from: e */
    public final String tileUrl;

    /* renamed from: m */
    private static int lastMinute;

    /* renamed from: f */
    public static int clockWidth;

    /* renamed from: g */
    public static long lastTileLoadTime;

    /* renamed from: h */
    public static Vector savedLocations;

    /* renamed from: i */
    public static Object syncObject;

    /* renamed from: j */
    public static Integer[] integerCache;

    /* renamed from: k */
    public static Boolean boolTrue;

    /* renamed from: l */
    public static Boolean boolFalse;

    public ResourceManager(int i, int i2, int i3, int i4) {
        this.tileType = i;
        ByteBuffer urlBuf = new ByteBuffer().writeUInt(4027430).writeUInt(i == 3 ? 1936548170 : 1936744781).writeUInt(4028966);
        this.zoomLevel = i2;
        ByteBuffer urlBuf2 = urlBuf.writeIntAsString(i2).writeUInt(4028454);
        this.tileX = i3;
        ByteBuffer urlBuf3 = urlBuf2.writeIntAsString(i3).writeUInt(4028710);
        this.tileY = i4;
        this.tileUrl = urlBuf3.writeIntAsString(i4).getStringAndClear();
    }

    public final boolean equals(Object obj) {
        return obj != null && (obj instanceof ResourceManager) && StringUtils.equals(this.tileUrl, ((ResourceManager) obj).tileUrl);
    }

    public final int hashCode() {
        return this.tileX ^ this.tileY;
    }

    /* renamed from: a */
    public static final void playNotificationSound(int i) {
        int i2 = 0;
        if (i == 1) {
            i2 = 2;
        } else if (i == 0) {
            i2 = 4;
        } else if (i == 3) {
            i2 = 6;
        } else if (i == 4) {
            i2 = 8;
        } else if (i == 5) {
            i2 = 10;
        } else if (i == 6) {
            i2 = 165;
        }
        playAlertIfEnabled(AppState.getInt(i2 + 75), AppState.getBool(i2 + 76));
    }

    /* renamed from: a */
    public static final void playAlertIfEnabled(int i, boolean z) {
        if (AppState.getBool(StateKeys.FLAG_MRIM_DATA_LOADED)) {
            if (z) {
                Display.getDisplay(AppState.getMidlet()).vibrate(250);
            }
            if (i == 0 || AppState.getBool(StateKeys.SETTING_NOTIFICATION_ENABLED) || !AppController.checkTimer(8, 1000L)) {
                return;
            }
            SoundPlayer.playSound(i);
        }
    }

    /* renamed from: a */
    public static final void resetClock() {
        AppController.timers[4] = 0;
        lastMinute = -1;
        clockWidth = 0;
        AppState.clearIndex(StateKeys.SLOT_CLOCK_STRING);
        updateClock();
    }

    /* renamed from: b */
    public static final void updateClock() {
        Calendar calendar;
        int i;
        if (!AppController.checkTimer(4, 1000L) || (i = (calendar = AppState.getCalendar()).get(12)) == lastMinute) {
            return;
        }
        String timeStr = ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Utils.zeroPad(calendar.get(11))).append(':').append(Utils.zeroPad(i)));
        AppState.setObject(StateKeys.SLOT_CLOCK_STRING, (Object) timeStr);
        clockWidth = AppState.getGfxContext(StateKeys.GFX_INDEX_DEFAULT).stringWidth(timeStr);
        lastMinute = i;
        AppController.needsRepaint = true;
    }

    /* renamed from: a */
    public static final void processXmppStream(Object[] objArr) {
        while (true) {
            XmppProtocol xmpp = (XmppProtocol) objArr[0];
            XmlElement element = ((XmlParser) objArr[2]).parse();
            synchronized (xmpp.elementQueue) {
                xmpp.elementQueue.addElement(element);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:40:0x0098, code lost:
    
        java.lang.Thread.sleep(255);
     */
    /* renamed from: b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final int readUtf8Char(Object[] objArr) {
        while (true) {
            ByteBuffer buffer = (ByteBuffer) objArr[1];
            synchronized (buffer) {
                int i = buffer.length;
                if (i > 0) {
                    int byte1 = buffer.peekUByteAt(0);
                    if ((byte1 & 128) == 0) {
                        return buffer.readUByte();
                    }
                    if (i != 1) {
                        int byte2 = buffer.peekUByteAt(1);
                        if (byte1 < 224) {
                            buffer.skip(2);
                            return ((byte1 & 31) << 6) | (byte2 & 63);
                        }
                        if (i != 2) {
                            buffer.skip(2);
                            return ((byte1 & 15) << 12) | ((byte2 & 63) << 6) | (buffer.readUByte() & 63);
                        }
                    }
                }
            }
        }
    }

    /* renamed from: a */
    public static final void dialPhoneContact(PhoneContact contact, int i) {
        dialPhoneUrl(VCard.formatPhoneContactUrl(contact, i), contact, i);
    }

    /* renamed from: a */
    public static final void dialPhoneUrl(String str, PhoneContact contact, int i) {
        new AsyncTask(AsyncTaskId.PARSE_CONTACTS_ASYNC, new Object[]{str, contact, integerOf(i)});
    }

    /* renamed from: a */
    public static final int handleDropdownSelect(String str, MenuItem menuItem) {
        Object[] objArr = (Object[]) menuItem.data;
        Object[] objArr2 = (Object[]) objArr[0];
        MenuItem targetItem = (MenuItem) objArr[1];
        ListView parentScreen = (ListView) objArr[2];
        String[] strArr = (String[]) objArr2[1];
        int i = 0;
        int length = strArr.length;
        while (true) {
            length--;
            if (length < 0) {
                targetItem.clear().setLabel(Utils.appendSpace(targetItem.title)).addText(strArr[i], 1, 7).setIcon(247).data = new Object[]{integerOf(i), strArr};
                parentScreen.rebuildItems();
                EventDispatcher.postEvent(new MenuItemEvent(targetItem));
                return 0;
            }
            if (str == strArr[length]) {
                i = length;
            }
        }
    }

    /* renamed from: a */
    public static final Object[] readAttachmentArray(ByteBuffer buffer) {
        try {
            int count = buffer.readInt();
            if (count == 0) {
                return null;
            }
            Object[] objArr = new Object[count];
            for (int i = 0; i < count; i++) {
                String[] strArr = new String[6];
                for (int i2 = 0; i2 < 6; i2++) {
                    strArr[i2] = buffer.readUTF8Str((String) null);
                }
                objArr[i] = strArr;
            }
            return objArr;
        } catch (Throwable unused) {
            return null;
        }
    }

    /* renamed from: c */
    public static final void showTrafficStats() {
        int i;
        int i2;
        int periodIndex = AppState.getInt(StateKeys.INT_PERIOD_INDEX);
        Account account = AppState.getAccount();
        if (account != null) {
            int sentBytes = account.getSyncValue(periodIndex, 0);
            i = sentBytes;
            formatTrafficItem(1326, sentBytes);
            int recvBytes = account.getSyncValue(periodIndex, 1);
            i2 = recvBytes;
            formatTrafficItem(1325, recvBytes);
            AppState.setInt(StateKeys.INT_STAT_ROWS, 8);
            AppState.setInt(StateKeys.INT_STAT_COLS, 3);
        } else {
            formatTrafficItem(1329, TrafficAccounting.getTrafficCount(0, periodIndex, 0));
            formatTrafficItem(1328, TrafficAccounting.getTrafficCount(0, periodIndex, 1));
            formatTrafficItem(1331, TrafficAccounting.getTrafficCount(1, periodIndex, 0));
            formatTrafficItem(1330, TrafficAccounting.getTrafficCount(1, periodIndex, 1));
            formatTrafficItem(1333, TrafficAccounting.getTrafficCount(2, periodIndex, 0));
            formatTrafficItem(1332, TrafficAccounting.getTrafficCount(2, periodIndex, 1));
            formatTrafficItem(1335, TrafficAccounting.getTrafficCount(3, periodIndex, 0));
            formatTrafficItem(1334, TrafficAccounting.getTrafficCount(3, periodIndex, 1));
            int totalSent = TrafficAccounting.getTotalTraffic(periodIndex, 0);
            i = totalSent;
            formatTrafficItem(1326, totalSent);
            int totalRecv = TrafficAccounting.getTotalTraffic(periodIndex, 1);
            i2 = totalRecv;
            formatTrafficItem(1325, totalRecv);
            AppState.setInt(StateKeys.INT_STAT_ROWS, 5);
            AppState.setInt(StateKeys.INT_STAT_COLS, 16);
        }
        long j = i + i2;
        int blockSize = AppState.getInt(StateKeys.SETTING_BLOCK_SIZE_KB) << 10;
        if (blockSize > 0) {
            long j2 = j % blockSize;
            if (j2 > 0) {
                j += blockSize - j2;
            }
        }
        int costCents = (int) ((j * AppState.getInt(StateKeys.SETTING_TRAFFIC_COST)) / 1048576);
        AppState.setFromBuffer(StateKeys.SLOT_TRAFFIC_COST_TEXT, ObjectPool.newStringBuffer().append(costCents / 100).append('.').append(Utils.zeroPad(costCents % 100)).append(' ').append(AppState.getString(StateKeys.STR_CURRENCY_SYMBOL)));
        AppState.setInt(StateKeys.INT_TRAFFIC_PERIOD_LABEL, periodIndex + 745);
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.TRAFFIC_STATS));
        AppState.clearRange(StateKeys.SLOT_GROUP_LIST_INDEX, StateKeys.RANGE_SEARCH_LABEL_END);
    }

    /* renamed from: a */
    private static final void formatTrafficItem(int i, int i2) {
        AppState.setObject(i, (Object) Utils.formatSize(i2));
    }

    /* renamed from: a */
    public static final ByteBuffer sendAddGroupCommand(MmpProtocol protocol, String str) {
        ByteBuffer groupBuffer = new ByteBuffer().writeUTF(str);
        int groupId = protocol.generateUniqueGroupId();
        return protocol.queueCommand(new Object[]{ProtocolFactory.createMmpCommand(protocol, 4872, groupBuffer.writeShortBE(groupId).writeShortBE(0).writeShortBE(1).writeShortBE(0)), integerOf(4), str, integerOf(groupId)});
    }

    /* renamed from: a */
    public static final int handleChatInputAction(String str) {
        int errorCode;
        String messageText = Utils.defaultStr(AppState.getString(StateKeys.SLOT_STATUS_TEXT));
        if (str != AppState.getString(StateKeys.STR_NOTIFICATION_SOUND)) {
            StringBuffer sb = Utils.getMessageBuffer();
            if (StringUtils.matchesKey(473, str)) {
                AppState.setFromBuffer(StateKeys.SLOT_STATUS_TEXT, sb.append(AppState.getString(StateKeys.SLOT_NOTIFICATION_TEXT)));
                return 0;
            }
            if (StringUtils.matchesKey(474, str)) {
                AppState.setObject(StateKeys.SLOT_NOTIFICATION_TEXT, (Object) messageText);
                AppState.setBool(StateKeys.FLAG_RESOURCE_LOADING, true);
                return 0;
            }
            if (!StringUtils.matchesKey(476, str)) {
                return 0;
            }
            AppState.setObject(StateKeys.SLOT_STATUS_TEXT, (Object) Conversation.transliterateRussian(messageText));
            return 0;
        }
        String phoneNumber = AppState.getString(StateKeys.SLOT_SELECTED_GROUP);
        MrimContact mrimContact = (MrimContact) AppState.pool[StateKeys.SLOT_CURRENT_ENTITY];
        MrimAccount mrimAccount = (MrimAccount) mrimContact.account;
        if (mrimAccount.isConnected()) {
            mrimContact.appendMessage(1, ObjectPool.toStringAndRelease(Utils.appendColon(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_FILE_TRANSFER_PREFIX)).append(Utils.formatPhone(phoneNumber))).append(messageText)), 0L, 0L);
            StringBuffer phoneSb = ObjectPool.newStringBuffer().append('+');
            if (phoneNumber.charAt(0) == '8') {
                phoneSb.append('7').append(StringUtils.suffix(phoneNumber, 1));
            } else {
                phoneSb.append(phoneNumber);
            }
            errorCode = mrimAccount.trySendData(mrimAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount, MrimCommand.CS_MESSAGE_EXT, new ByteBuffer().writeIntLE(0).writeStringLatin1(ObjectPool.toStringAndRelease(phoneSb)).writeStringUTF16(messageText)), integerOf(MrimAccount.RESP_XMPP_SERVICE), mrimContact, messageText, phoneNumber}));
        } else {
            errorCode = 299;
        }
        int i = errorCode;
        if (0 != errorCode) {
            return NotificationHelper.showError(i);
        }
        return 0;
    }

    /* renamed from: a */
    public static final void sendSmsRequest(Object obj) {
        if (!(obj instanceof String)) {
            return;
        }
        try {
            try {
                NetworkLock.acquireNetworkLock();
                HttpClient httpClient = HttpClient.createWithType3(obj);
                if (httpClient.getResponseCode() != 200) {
                    throw new Throwable();
                }
                String str = null;
                boolean z = false;
                Vector vector = new ByteBuffer(httpClient).parseXml().children;
                int size = vector.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        throw new RuntimeException();
                    }
                    XmlElement xmlElement = (XmlElement) vector.elementAt(size);
                    String str2 = xmlElement.tagName;
                    String textValue = StringUtils.fromBuffer(xmlElement.textContent);
                    if (StringUtils.matchesKey(PackedStringKeys.TAG_STATUS, str2) && StringUtils.matchesKey(PackedStringKeys.HTTP_STATUS_200, textValue)) {
                        z = true;
                    } else if (StringUtils.matchesKey(PackedStringKeys.TAG_LINK, str2)) {
                        str = textValue;
                    }
                    if (z && str != null) {
                        ObjectPool.releaseVector(vector);
                        EventDispatcher.postNotification(AppState.getString(StateKeys.STR_OPERATION_COMPLETE));
                        HttpClient.closeAndUpdateStats(httpClient);
                        NetworkLock.releaseNetworkLock();
                        return;
                    }
                }
            } catch (Throwable th) {
                EventDispatcher.postNotification(StringUtils.concatKeyObj(493, (Object) null));
                HttpClient.closeAndUpdateStats((HttpClient) null);
                NetworkLock.releaseNetworkLock();
            }
        } catch (Throwable th2) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            throw th2;
        }
    }

    /* renamed from: d */
    public static final void initMathTables() {
        AppState.pool[StateKeys.RES_LOG_BASE_TABLE] = readLongArray(986);
        AppState.pool[StateKeys.RES_POW_BASE_TABLE] = readLongArray(987);
        AppState.pool[StateKeys.RES_MULTIPLY_COEFFICIENTS] = readLongArray(990);
        AppState.pool[StateKeys.RES_LOOKUP_TABLE] = readLongArray(991);
        AppState.pool[StateKeys.RES_SHORT_INDEX_TABLE_2] = Utils.readShortArray(989);
        AppState.pool[StateKeys.RES_SHORT_INDEX_TABLE_1] = Utils.readShortArray(988);
        AppState.pool[StateKeys.RES_PALETTE_MAP_1] = Utils.bytesToInts(AppState.getBytes(StateKeys.RES_PALETTE_MAP_1));
        AppState.pool[StateKeys.RES_PALETTE_MAP_2] = Utils.bytesToInts(AppState.getBytes(StateKeys.RES_PALETTE_MAP_2));
        AppState.pool[StateKeys.RES_ICON_MAP] = Utils.bytesToInts(AppState.getBytes(StateKeys.RES_ICON_MAP));
    }

    /* renamed from: e */
    public static final void clearMathTables() {
        AppState.clearRange(StateKeys.STR_INFINITY, StateKeys.RES_PALETTE_MAP_2);
    }

    /* renamed from: f */
    private static final long[] readLongArray(int i) {
        byte[] bytes = AppState.getBytes(i);
        int length = bytes.length >> 3;
        long[] jArr = new long[length];
        int i2 = 0;
        int i3 = 0;
        while (i3 < length) {
            long j = 0;
            do {
                int i4 = i2;
                i2++;
                j = (j << 8) | (bytes[i4] & 255);
            } while ((i2 & 7) != 0);
            int i5 = i3;
            i3++;
            jArr[i5] = j;
        }
        ObjectPool.releaseBytes(bytes);
        return jArr;
    }

    /* renamed from: b */
    public static final long getTrigConstant(int i) {
        return ((long[]) AppState.pool[StateKeys.RES_LOOKUP_TABLE])[i];
    }

    /* renamed from: c */
    public static final int getPiMultiple(int i) {
        return ((int[]) AppState.pool[StateKeys.RES_PALETTE_MAP_1])[i];
    }

    /* renamed from: f */
    public static final int parseBalance() {
        ScreenManager.processScreenForm();
        String balanceStr = Utils.defaultStr(AppState.getString(StateKeys.SLOT_SCREEN_VALUE));
        int i = 0;
        int sepIdx = balanceStr.lastIndexOf(46);
        int dotIdx = sepIdx;
        if (sepIdx == -1) {
            dotIdx = balanceStr.lastIndexOf(44);
        }
        if (dotIdx != -1) {
            try {
                i = Integer.parseInt(StringUtils.prefix(balanceStr, dotIdx)) * 100;
            } catch (Throwable unused) {
            }
            try {
                String fraction = StringUtils.suffix(balanceStr, dotIdx + 1);
                int i2 = Integer.parseInt(fraction);
                i += fraction.length() == 1 ? i2 * 10 : i2;
            } catch (Throwable unused2) {
            }
        } else {
            try {
                i = Integer.parseInt(balanceStr) * 100;
            } catch (Throwable unused3) {
            }
        }
        AppState.setInt(StateKeys.SETTING_TRAFFIC_COST, i);
        return 0;
    }

    /* renamed from: g */
    public static final int clearSmsFields() {
        AppState.clearIndex(StateKeys.SLOT_SEARCH_LABEL_1);
        AppState.clearIndex(StateKeys.SLOT_STATUS_TEXT);
        AppState.clearIndex(StateKeys.SLOT_SELECTED_GROUP);
        return ScreenId.PHONE_GROUPS;
    }

    /* renamed from: h */
    public static final int syncAndReturn() {
        ((MrimAccount) AppState.getAccount()).profileManager.sync();
        if (AppState.getBool(StateKeys.FLAG_UPDATE_AVAILABLE)) {
            return AppState.getInt(StateKeys.INT_CONNECTION_STATE);
        }
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    /* renamed from: a */
    public static final void removeTileRequest(ResourceManager tile) {
        Vector requestQueue = AppState.getVector(StateKeys.VEC_CHATROOM_LIST);
        synchronized (requestQueue) {
            requestQueue.removeElement(tile);
        }
    }

    /* renamed from: i */
    public static final ResourceManager peekTileRequest() {
        ResourceManager tile;
        Vector requestQueue = AppState.getVector(StateKeys.VEC_CHATROOM_LIST);
        synchronized (requestQueue) {
            tile = (ResourceManager) (requestQueue.size() != 0 ? requestQueue.firstElement() : null);
        }
        return tile;
    }

    /* renamed from: b */
    public static final void enqueueTileRequest(ResourceManager tile) {
        Vector requestQueue = AppState.getVector(StateKeys.VEC_CHATROOM_LIST);
        synchronized (requestQueue) {
            if (!requestQueue.contains(tile)) {
                if (tile.tileType == 3) {
                    requestQueue.addElement(tile);
                } else {
                    int size = requestQueue.size();
                    while (size > 0 && ((ResourceManager) requestQueue.elementAt(size - 1)).tileType != 1) {
                        size--;
                    }
                    requestQueue.insertElementAt(tile, size);
                }
            }
        }
    }

    /* renamed from: j */
    public static final void showSavedLocations() {
        Vector vector = savedLocations;
        if (vector == null) {
            return;
        }
        ListView screen = ScreenManager.createScreen(ScreenDef.MAIL_ACCOUNT_LIST);
        int size = vector.size();
        while (true) {
            size--;
            if (size < 0) {
                ScreenManager.showScreen(screen);
                AppController.needsRepaint = true;
                return;
            } else {
                MapPoint mapPoint = (MapPoint) vector.elementAt(size);
                screen.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
            }
        }
    }

    /* renamed from: b */
    public static final int applyLocationProfile(Object obj) {
        MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
        MapPoint mapPoint = (MapPoint) obj;
        mrimAccount.profileManager.setMapLocation(mapPoint);
        XmppContactGroup.addMapPointIfNew(AppState.getVector(StateKeys.VEC_CONTACT_GROUPS), mapPoint, 0, 5);
        XmppContactGroup.saveMapPoints(AppState.getVector(StateKeys.VEC_CONTACT_GROUPS), 225);
        AppState.setInt(StateKeys.FLAG_LOADING, 0);
        mrimAccount.isHighlighted = true;
        return ScreenId.PROFILE_EDIT;
    }

    /* renamed from: a */
    public static final void startGeoSearch(String str, long j, long j2) {
        new AsyncTask(AsyncTaskId.FETCH_SAVED_LOCATIONS, new Object[]{str, new long[]{j, j2}});
    }

    /* renamed from: k */
    public static final void showMailAccountList() {
        AppState.clearIndex(StateKeys.SLOT_CURRENT_ACCOUNT);
        ListView screen = ScreenManager.createScreen(ScreenDef.GENERIC_LIST);
        Vector accounts = AccountManager.getMrimAccountList();
        int size = accounts.size();
        if (size > 0) {
            screen.addLabelById(832);
            for (int i = 0; i < size; i++) {
                screen.addItem(((MrimAccount) accounts.elementAt(i)).createMenuItem());
            }
        } else {
            screen.selectable = false;
            screen.addLabelById(551);
        }
        ObjectPool.releaseVector(accounts);
        ScreenManager.pushScreen(screen);
        TabBar.ensureSettingsTab();
        TabBar.findTab(36, (Account) null);
    }

    /* renamed from: c */
    public static final int selectMailAccount(Object obj) {
        if (obj == null) {
            return -1;
        }
        AppState.setInt(StateKeys.INT_SCREEN_ACTION, 38);
        AppState.setAccount(obj);
        return 0;
    }

    /* renamed from: d */
    public static final int handleSearchResultAction(int i) {
        Vector onlineAccounts = AccountManager.getOnlineMrimAccounts();
        switch (i) {
            case 0:
                if (onlineAccounts.size() <= 0) {
                    return NotificationHelper.showError(422);
                }
                ((MrimAccount) onlineAccounts.firstElement()).performUserSearch(new SearchEntry(AppController.getCurrentSearchResult().userId, 1));
                ScreenBuilder.onScreenClosed();
                return ScreenId.CONTACT_DELETE;
            case 1:
                if (onlineAccounts.size() <= 0) {
                    return NotificationHelper.showError(422);
                }
                ((MrimAccount) onlineAccounts.firstElement()).performUserSearch(new SearchEntry(AppController.getCurrentSearchResult().userId, 2));
                return ScreenId.MAP;
            case 2:
                return AppController.showPeopleNearby();
            case 3:
                return AppController.showPeopleSearch();
            default:
                AppState.setInt(StateKeys.INT_ASYNC_TASK_ID, 0);
                AppController.openUserProfile((MrimAccount) null, AppController.getCurrentSearchResult().userId);
                ScreenBuilder.onScreenClosed();
                return 0;
        }
    }

    /* renamed from: l */
    public static final void clearImageCache() {
        AppState.clearRange(StateKeys.SLOT_MEDIA_CALLBACK, StateKeys.OBJ_FONT_2);
    }

    /* renamed from: m */
    public static final void showWiFiNetworks() {
        Vector networks = ServiceRegistry.getActiveContactIds();
        int size = networks == null ? 0 : networks.size();
        int i = size;
        if (size == 0) {
            NotificationHelper.showMessageById(404);
            return;
        }
        ListView screen = ScreenManager.createScreen(ScreenDef.SAVED_LOCATIONS);
        while (true) {
            i--;
            if (i < 0) {
                ScreenManager.showScreen(screen);
                return;
            } else {
                Object networkObj = networks.elementAt(i);
                screen.addIconItemWithData(-1, ServiceRegistry.getPhotoHost(networkObj), 6, networkObj);
            }
        }
    }

    /* renamed from: d */
    public static final int setSelectedObject(Object obj) {
        AppState.pool[StateKeys.SLOT_MSG_ID_1] = obj;
        return 0;
    }

    /* renamed from: a */
    public static final int handleMessageInputAction(String str, int i) {
        String messageText = Utils.defaultStr(AppState.getString(StateKeys.SLOT_STATUS_TEXT));
        if (StringUtils.matchesKey(1060, str)) {
            int sendResult = AppState.getCurrentContact().sendMessage(messageText);
            if (0 != sendResult) {
                ScreenBuilder.onScreenClosed();
                return NotificationHelper.showError(sendResult);
            }
            AppState.setInt(StateKeys.FLAG_STATUS_TEXT_SET, 0);
            AppState.clearIndex(StateKeys.SLOT_STATUS_TEXT);
        } else if (StringUtils.matchesKey(473, str)) {
            AppState.setFromBuffer(StateKeys.SLOT_STATUS_TEXT, Utils.getMessageBuffer().append(AppState.getString(StateKeys.SLOT_NOTIFICATION_TEXT)));
        } else if (StringUtils.matchesKey(474, str)) {
            AppState.setObject(StateKeys.SLOT_NOTIFICATION_TEXT, (Object) messageText);
            AppState.setBool(StateKeys.FLAG_RESOURCE_LOADING, true);
        } else if (StringUtils.matchesKey(478, str)) {
            AppState.setObject(StateKeys.SLOT_STATUS_TEXT, (Object) StringUtils.transliterate(messageText));
        }
        if (i == 93 || i == 123 || i == 95 || i == 94) {
            return 0;
        }
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    /* renamed from: a */
    public static final ByteBuffer createGetContactsCmd(MmpProtocol protocol) {
        return ProtocolFactory.createMmpCommand(protocol, 4881, (ByteBuffer) null);
    }

    /* renamed from: b */
    public static final ByteBuffer createSyncContactsCmd(MmpProtocol protocol) {
        return ProtocolFactory.createMmpCommand(protocol, 4882, (ByteBuffer) null);
    }

    /* renamed from: c */
    public static final ByteBuffer createSyncGroupsCmd(MmpProtocol protocol) {
        Object[] objArr = new Object[2];
        ByteBuffer syncBuf = new ByteBuffer().writeIntLE(0).writeShortBE(0).writeShortBE(1);
        int size = protocol.groups.size();
        ByteBuffer syncBuf2 = syncBuf.writeShortBE((size << 1) + 4).writeShortBE(200).writeShortBE(size << 1);
        for (int i = 0; i < size; i++) {
            syncBuf2.writeShortBE(((MmpContactGroup) protocol.getGroup(i)).groupId);
        }
        objArr[0] = ProtocolFactory.createMmpCommand(protocol, 4873, syncBuf2);
        objArr[1] = integerOf(3);
        return protocol.queueCommand(objArr);
    }

    /* renamed from: n */
    public static final int updateMessageInput() {
        try {
            if (XmppContactGroup.getTextInputValue().length() != 0) {
                XmppContactGroup.setTextInputScreen(1055, 1060);
            } else {
                XmppContactGroup.setTextInputScreen(1060, 1055);
            }
            if (AppState.getBool(StateKeys.SETTING_EXTENDED_PRESENCE)) {
                int timestamp = AppState.getInt(StateKeys.INT_CURRENT_TIMESTAMP);
                if (Utils.abs(timestamp - AppState.getInt(StateKeys.INT_LAST_CHECK_TIMESTAMP)) > 5000) {
                    AppState.setInt(StateKeys.INT_LAST_CHECK_TIMESTAMP, timestamp);
                    int length = XmppContactGroup.getTextInputValue().length();
                    if (length != AppState.getInt(StateKeys.INT_LAST_LIST_SIZE) && Utils.abs(timestamp - AppState.getInt(StateKeys.INT_LAST_POLL_TIMESTAMP)) > 10000) {
                        Contact currentContact = AppState.getCurrentContact();
                        if (!currentContact.isOnline() && !currentContact.hasUnread() && !currentContact.isOffline()) {
                            currentContact.account.validateContactResend(currentContact);
                        }
                        AppState.setInt(StateKeys.INT_LAST_POLL_TIMESTAMP, timestamp);
                        AppState.setInt(StateKeys.INT_LAST_LIST_SIZE, length);
                    }
                }
            }
            return 0;
        } catch (Throwable unused) {
            return 0;
        }
    }

    /* renamed from: b */
    public static final void fetchSharedContacts(String str) {
        try {
            NetworkLock.acquireNetworkLock();
            HttpClient http = HttpClient.createWithType2((Object) str);
            if (http.getResponseCode() != 200) {
                throw new Throwable();
            }
            Vector lines = Utils.splitReplace(new ByteBuffer(http).readUTFWithLen(), '\n', '\r');
            XmppContactGroup.sharedContactList.removeAllElements();
            int size = lines.size();
            while (true) {
                size--;
                if (size < 0) {
                    ObjectPool.releaseVector(lines);
                    HttpClient.closeAndUpdateStats(http);
                    NetworkLock.releaseNetworkLock();
                    return;
                } else {
                    Vector fields = Utils.splitMerge((String) lines.elementAt(size), '|');
                    if (fields.size() == 5) {
                        XmppContactGroup.sharedContactList.addElement(new Object[]{fields.elementAt(0), new long[]{Long.parseLong((String) fields.elementAt(1)), Long.parseLong((String) fields.elementAt(2))}, fields.elementAt(4)});
                    }
                    ObjectPool.releaseVector(fields);
                }
            }
        } catch (RuntimeException th) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            throw th;
        } catch (Throwable th) {
            HttpClient.closeAndUpdateStats((HttpClient) null);
            NetworkLock.releaseNetworkLock();
            throw new RuntimeException(th);
        }
    }

    /* renamed from: a */
    public static final int composeEmail(Vector vector, String str, String str2) {
        StringBuffer recipientsSb = ObjectPool.newStringBuffer();
        String str3 = AppState.emptyStr;
        String separator = ObjectPool.unpackChars(8236);
        int i = 0;
        while (i < Utils.vectorSize(vector)) {
            recipientsSb.append(i > 0 ? separator : str3).append(((String[]) vector.elementAt(i))[0]);
            i++;
        }
        AppState.setObject(StateKeys.SLOT_MSG_EXTRA_2, (Object) ObjectPool.toStringAndRelease(recipientsSb));
        AppState.setObject(StateKeys.SLOT_MSG_EXTRA_3, (Object) Utils.defaultStr(str));
        String str4 = AppState.emptyStr;
        AppState.setFromBuffer(StateKeys.SLOT_TRAFFIC_STATUS_TEXT, ObjectPool.newStringBuffer().append(AppState.getBool(StateKeys.SETTING_TRAFFIC_INFO_ENABLED) ? ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_TRAFFIC_INFO_YES)).append('\n')) : str4).append(AppState.getBool(StateKeys.SETTING_TRAFFIC_INFO_TYPE) ? ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_TRAFFIC_INFO_NO)).append('\n')) : str4).append(Utils.defaultStr(str2)).append(AppState.getString(StateKeys.STR_TRAFFIC_LABEL)));
        return ScreenId.COMPOSE_MESSAGE;
    }

    /* renamed from: e */
    public static final Integer integerOf(int i) {
        return (i & 31) == i ? integerCache[i] : new Integer(i);
    }

    /* renamed from: a */
    public static final Boolean booleanOf(boolean z) {
        return z ? boolTrue : boolFalse;
    }

    /* renamed from: a */
    public static final int loadUserProfile(String str, Account targetAccount) {
        ByteBuffer urlBuffer;
        int atIndex = str.indexOf(64);
        String domain = StringUtils.suffix(str, atIndex + 1);
        Object[] objArr = new Object[3];
        if (targetAccount instanceof MmpProtocol) {
            urlBuffer = new ByteBuffer().writeCompressed(PackedStringKeys.URL_ICQ_BUDDY_ICON).writeRawString(str);
        } else {
            ByteBuffer profileBuf2 = new ByteBuffer().writeCompressed(PackedStringKeys.URL_OBRAZ_FOTO);
            int dotIndex = domain.indexOf(46);
            urlBuffer = profileBuf2.writeRawString(dotIndex < 0 ? ObjectPool.unpackChars(6775139) : StringUtils.prefix(domain, dotIndex)).writeByte(47).writeRawString(atIndex < 0 ? str : StringUtils.prefix(str, atIndex)).writeCompressed(467 + AppState.getInt(StateKeys.INT_ASYNC_TASK_ID));
        }
        objArr[0] = urlBuffer.getStringAndClear();
        objArr[1] = targetAccount;
        objArr[2] = null;
        AppState.pool[StateKeys.OBJ_REGISTRATION_DATA] = objArr;
        new AsyncTask(AsyncTaskId.DOWNLOAD_PHOTO, objArr);
        return 0;
    }

    /* renamed from: a */
    private static final void setUpdateFlag(byte b) {
        AppState.getBytes(StateKeys.SLOT_MEDIA_RESOURCE)[0] = b;
    }

    /* renamed from: u */
    private static final boolean isUpdatePending() {
        return AppState.getBytes(StateKeys.SLOT_MEDIA_RESOURCE)[0] != 0;
    }

    /* renamed from: o */
    public static final int checkForUpdates() {
        synchronized (AppState.pool[StateKeys.SLOT_MEDIA_RESOURCE]) {
            if (!isUpdatePending() && System.currentTimeMillis() > AppState.getLong(StateKeys.TIMESTAMP_LAST_UPDATE_CHECK) + 86400000) {
                AppState.setLong(StateKeys.TIMESTAMP_LAST_UPDATE_CHECK, System.currentTimeMillis());
                setUpdateFlag((byte) 1);
                new AsyncTask(AsyncTaskId.FETCH_UPDATE_STATUS);
            }
            if (isUpdatePending()) {
                return -1;
            }
            return AppState.getInt(StateKeys.SETTING_UPDATE_STATUS);
        }
    }

    /* JADX DEBUG: Finally have unexpected throw blocks count: 2, expect 1 */
    /* renamed from: p */
    public static final void fetchUpdateStatus() {
        try {
            NetworkLock.acquireNetworkLock();
            HttpClient httpConn = HttpClient.createHttpClient(AppState.getString(StateKeys.STR_RES_MEGA_URL_1), (Account) null, 3);
            if (httpConn.getResponseCode() != 200) {
                throw new Throwable();
            }
            ByteBuffer buffer = new ByteBuffer(httpConn);
            synchronized (AppState.pool[StateKeys.SLOT_MEDIA_RESOURCE]) {
                AppState.setInt(StateKeys.SETTING_UPDATE_STATUS, Integer.parseInt(buffer.parseXmlStr().getIntAttribute(PackedStringKeys.TAG_SNAP_LOGINS)) != 0 ? 1 : 0);
            }
            synchronized (AppState.pool[StateKeys.SLOT_MEDIA_RESOURCE]) {
                setUpdateFlag((byte) 0);
            }
            HttpClient.closeAndUpdateStats(httpConn);
            NetworkLock.releaseNetworkLock();
        } catch (Throwable unused) {
            synchronized (AppState.pool[StateKeys.SLOT_MEDIA_RESOURCE]) {
                setUpdateFlag((byte) 0);
                HttpClient.closeAndUpdateStats((HttpClient) null);
                NetworkLock.releaseNetworkLock();
            }
        }
    }

    /* renamed from: c */
    public static final int handleChatRoomAction(String str) {
        String messageId = AppState.getString(StateKeys.SLOT_MESSAGE_ID);
        int chatRoomId = AppState.getInt(StateKeys.INT_CHATROOM_ID);
        MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
        ChatRoom chatRoom = mrimAccount.chatRoomManager.findById(chatRoomId);
        if (StringUtils.matchesKey(848, str)) {
            chatRoom.readMessages.addElement(messageId);
            return 0;
        }
        if (StringUtils.matchesKey(847, str)) {
            chatRoom.markMessageRead(messageId);
            return 0;
        }
        if (StringUtils.matchesKey(846, str)) {
            ScreenBuilder.onScreenClosed();
            composeEmail((Vector) null, (String) null, (String) null);
            return 0;
        }
        if (StringUtils.matchesKey(1347, str)) {
            IOUtils.setSelectedItems(chatRoom.readMessages);
            return 0;
        }
        if (StringUtils.matchesKey(1061, str)) {
            ScreenBuilder.onScreenClosed();
            AppController.toggleOnlineMode(false);
            return 0;
        }
        if (!StringUtils.matchesKey(851, str)) {
            return 0;
        }
        AppState.setInt(StateKeys.INT_SCROLL_OFFSET, 0);
        AppState.clearIndex(StateKeys.SLOT_MAP_POINT_2);
        mrimAccount.chatRoomManager.loaded = true;
        chatRoom.setActive(false);
        AppState.setInt(StateKeys.INT_SCREEN_ACTION, 41);
        return 0;
    }

    /* renamed from: a */
    public static final String buildTileRequestUrl(long j, long j2, int i, String str) {
        String encodedQuery;
        ByteBuffer urlBuf = new ByteBuffer().writeCompressed(PackedStringKeys.URL_MAPS_MAIL_RU).writeUInt(1031283503);
        String longitude = MapUtils.pixelToLongitude(j);
        ByteBuffer urlBuf2 = urlBuf.writeRawString(longitude).writeUInt(4028710);
        String latitude = MapUtils.pixelToLatitude(j2);
        ByteBuffer urlBuffer = urlBuf2.writeRawString(latitude).writeUInt(4028966).writeIntAsString(i).writeCompressed(PackedStringKeys.PARAM_MAP_FULLSCREEN);
        if (str != null) {
            ByteBuffer urlBuf3 = urlBuffer.writeUInt(1031302438).writeRawString(longitude).writeUInt(1031367974).writeRawString(latitude).writeUInt(1031040294);
            if (StringUtils.isEmpty(str)) {
                encodedQuery = ObjectPool.unpackChars(1094795585);
            } else {
                ByteBuffer buffer = new ByteBuffer();
                int length = str.length();
                for (int i2 = 0; i2 < length; i2++) {
                    int ch = str.charAt(i2) & 65535;
                    if (ch < 128) {
                        buffer.writeByte(ch);
                    } else if (ch < 2048) {
                        buffer.writeByte(192 + (ch >> 6)).writeByte(128 + (ch & 63));
                    } else {
                        buffer.writeByte(224 + (ch >> 12)).writeByte(128 + ((ch >> 6) & 63)).writeByte(128 + (ch & 63));
                    }
                }
                encodedQuery = Conversation.replaceText(Conversation.replaceText(buffer.toBase64(), 65547, 200765), 65552, 200768);
            }
            urlBuf3.writeRawString(encodedQuery);
        }
        return urlBuffer.getStringAndClear();
    }

    /* renamed from: q */
    public static final int deleteSelectedEntity() {
        int groupError;
        Object obj = AppState.pool[StateKeys.SLOT_CURRENT_ENTITY];
        if ((obj instanceof ContactGroup) && 0 != (groupError = ((ContactGroup) obj).getSortIndex())) {
            return NotificationHelper.showError(groupError);
        }
        if (!(obj instanceof Contact)) {
            return ScreenId.CONTACT_LIST;
        }
        Contact selectedContact = (Contact) obj;
        int contactError = selectedContact.account.validateResend(selectedContact);
        if (0 != contactError) {
            return NotificationHelper.showError(contactError);
        }
        return ScreenId.CONTACT_LIST;
    }

    /* renamed from: r */
    public static final void processUpdateResult() {
        int charVal2;
        int charVal1;
        boolean showMessage = AppState.getBool(StateKeys.FLAG_SHOW_NOTIFICATION);
        Object obj = AppState.getObjectArray(StateKeys.OBJ_REGISTRATION_DATA)[0];
        if (obj instanceof Integer) {
            if (showMessage) {
                NotificationHelper.showMessageById(((Integer) obj).intValue());
                return;
            }
            return;
        }
        try {
            StringBuffer versionSb = ObjectPool.newStringBuffer();
            StringBuffer urlSb = ObjectPool.newStringBuffer();
            ByteBuffer buffer = (ByteBuffer) obj;
            while (buffer.length > 0 && 32 != (charVal1 = buffer.readUByte())) {
                versionSb.append((char) charVal1);
            }
            while (buffer.length > 0 && 32 != (charVal2 = buffer.readUByte())) {
                urlSb.append((char) charVal2);
            }
            AppState.setFromBuffer(StateKeys.SLOT_SCREEN_TITLE, versionSb);
            AppState.setFromBuffer(StateKeys.SLOT_SCREEN_SUBTITLE, urlSb);
            if (parseVersionNumber(AppState.getString(StateKeys.STR_APP_NAME)) >= parseVersionNumber(AppState.getString(StateKeys.SLOT_SCREEN_TITLE))) {
                throw new Throwable();
            }
            ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.PROFILE_LIST));
        } catch (Throwable unused) {
            if (showMessage) {
                NotificationHelper.showMessageById(731);
            }
        }
    }

    /* renamed from: s */
    public static final int applyVersionLabel() {
        AppState.setFromPool(StateKeys.SLOT_SAVED_STRING, StateKeys.SLOT_SCREEN_SUBTITLE);
        return 0;
    }

    /* renamed from: e */
    private static final int parseVersionNumber(String str) {
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < str.length(); i3++) {
            char ch1 = str.charAt(i3);
            if (ch1 == '.') {
                i = (i * 100) + i2;
                i2 = 0;
            } else if (ch1 >= '0' && ch1 <= '9') {
                i2 = ((i2 * 10) + ch1) - 48;
            }
        }
        return (i * 100) + i2;
    }

    /* renamed from: a */
    private static final ByteBuffer hashPassword(MrimAccount mrimAccount) {
        return new ByteBuffer().writeRawString(mrimAccount.password).encryptMD5();
    }

    /* renamed from: a */
    public static final ByteBuffer createAuthPacket(MrimAccount mrimAccount, Account targetAccount, int i, int i2, String str, boolean z, byte[] bArr) {
        ByteBuffer payload = new ByteBuffer().writeIntWithLen(266).writeIntWithLen(20200).writeIntLE(i).writeIntLE(i2).writeStringLatin1(str).writeIntLE(z ? 1 : 0).writeIntLE(bArr.length).writeBytes(bArr);
        while ((payload.length & 7) != 0) {
            payload.writeByte(0);
        }
        ByteBuffer buffer = new ByteBuffer();
        ByteBuffer passwordHash = hashPassword(mrimAccount);
        XmppContactGroup.encryptBlowfish(passwordHash.data, passwordHash.length, payload.data, payload.length);
        passwordHash.clear();
        return mrimAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount, MrimCommand.CS_AUTH_UPDATE, buffer.writeBufferIntLen(payload)), integerOf(MrimAccount.RESP_AUTH_RESPONSE), targetAccount});
    }

    /* renamed from: a */
    public static final void handleAuthResponse(MrimAccount mrimAccount, int i, Object[] objArr, ByteBuffer buffer) {
        if (i == 1) {
            buffer.readInt();
            buffer.ensureCapacity(0);
            ByteBuffer passwordHash = hashPassword(mrimAccount);
            XmppContactGroup.decryptBlowfish(passwordHash.data, passwordHash.length, buffer.data, buffer.length);
            passwordHash.clear();
            buffer.readInt();
            MmpProtocol protocol = (MmpProtocol) objArr[2];
            protocol.trySendData(ProtocolFactory.createMmpCommand(protocol, 288, new ByteBuffer().writeShortBE(16).writeIntLE(buffer.readInt()).writeIntLE(buffer.readInt()).writeIntLE(buffer.readInt()).writeIntLE(buffer.readInt())));
        }
    }

    /* renamed from: t */
    public static final void showTosScreen() {
        ScreenManager.showScreen(ScreenManager.createScreen(ScreenDef.WIFI_NETWORKS));
        NotificationHelper.showMessageById(1027);
    }

    /* renamed from: a */
    public static final int collectInvitees(ListView parentScreen) {
        ScreenManager.processScreenForm();
        String[] phoneNumbers = Utils.getPhoneNumbers(true);
        Vector invitees = ContactListManager.getCheckedItems(parentScreen, 1);
        int length = phoneNumbers.length;
        while (true) {
            length--;
            if (length < 0) {
                break;
            }
            invitees.addElement(phoneNumbers[length]);
        }
        if (invitees.size() == 0) {
            return NotificationHelper.showError(775);
        }
        invitees.addElement(((MrimAccount) AppState.getAccount()).login);
        AppState.pool[StateKeys.SLOT_SCREEN_TITLE] = invitees;
        return ScreenId.SEND_DATA;
    }

    /* renamed from: a */
    public static final ByteBuffer createMoveContactCmd(MrimAccount mrimAccount, MrimContact mrimContact, int i) {
        return mrimAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount, MrimCommand.CS_MODIFY_CONTACT, new ByteBuffer().writeIntLE(mrimContact.contactId).writeIntLE(i).writeIntLE(mrimContact.groupId).writeStringLatin1(mrimContact.simpleIdentifier).writeStringUTF16(mrimContact.displayName).writeStringLatin1(mrimContact.contactGroupsStr)), integerOf(MrimAccount.RESP_MOVE_FLAG), mrimContact, integerOf(i)});
    }

    /* renamed from: a */
    public static final ByteBuffer createAddToGroupCmd(MrimAccount mrimAccount, MrimContact mrimContact, MrimContactGroup group) {
        return mrimAccount.createAndQueueCommand(new Object[]{ProtocolFactory.createMrimPacket(mrimAccount, MrimCommand.CS_MODIFY_CONTACT, new ByteBuffer().writeIntLE(mrimContact.contactId).writeIntLE(mrimContact.statusFlags).writeIntLE(group.serverId).writeStringLatin1(mrimContact.simpleIdentifier).writeStringUTF16(mrimContact.displayName).writeStringLatin1(mrimContact.contactGroupsStr)), integerOf(MrimAccount.RESP_MOVE_TO_GROUP), mrimContact, group});
    }
}
