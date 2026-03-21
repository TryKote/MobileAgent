package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

/* renamed from: ao */
/* loaded from: MobileAgent_3.9.jar:ao.class */
public final class AppController {

    /* renamed from: a */
    public static long[] timers;

    /* renamed from: b */
    public static String pendingUrl;

    /* renamed from: c */
    public static MrimAccount pendingAccount;

    /* renamed from: d */
    public static Object appLock;

    /* renamed from: e */
    public static boolean isShuttingDown;

    /* renamed from: f */
    public static boolean needsLayoutUpdate;

    /* renamed from: g */
    public static boolean needsRepaint;

    /* renamed from: i */
    private static boolean isBackgrounded;

    /* renamed from: h */
    public static boolean saveOnExit;

    /* renamed from: j */
    private static MapPoint pendingMapPoint;

    /* renamed from: a */
    private static int showAccountList(Vector vector, int i, boolean z) {
        AppState.setBool(1467, z);
        AppState.clearIndex(1281);
        int size = vector.size();
        if (size == 0) {
            return showError(551);
        }
        if (size == 1) {
            AppState.setAccount(vector.firstElement());
            return i;
        }
        AppState.pool[1283] = vector;
        AppState.setInt(1466, i);
        return 39;
    }

    /* renamed from: a */
    public static final int handleAction(Object obj) {
        int targetState = AppState.getInt(1466);
        if (obj != null) {
            AppState.setAccount(obj);
            return targetState;
        }
        if (targetState != 152) {
            return 104;
        }
        AppState.clearIndex(1281);
        return 152;
    }

    /* renamed from: a */
    public static final int handleMenuAction(String str, Object obj) {
        if (StringUtils.matchesKey(548, str)) {
            rebuildAccountCaches();
            return 4;
        }
        Account account = (Account) obj;
        int errorCode = account.isConnecting() ? account.disconnect() : account.connect(0);
        if (errorCode != 0) {
            return showError(errorCode);
        }
        return 4;
    }

    /* renamed from: a */
    public static final int handleScreenAction(int i) {
        ScreenBuilder.onScreenClosed();
        AppState.setInt(4895, i);
        return AppState.getInt(3650);
    }

    /* renamed from: a */
    public static final int handleSoftKeyAction(String str) {
        int chatRoomId = AppState.getInt(1513);
        MrimAccount account = (MrimAccount) AppState.getAccount();
        ChatRoom chatRoom = account.findChatRoomById(chatRoomId);
        IOUtils.setSelectedItems(chatRoom.readMessages);
        if (StringUtils.matchesKey(852, str)) {
            chatRoom.readMessages.removeAllElements();
            return 0;
        }
        if (StringUtils.matchesKey(853, str)) {
            AppState.setInt(1525, 2);
            return 0;
        }
        if (StringUtils.matchesKey(854, str)) {
            AppState.setInt(1525, 1);
            return 0;
        }
        if (!StringUtils.matchesKey(845, str)) {
            return 0;
        }
        AppState.setInt(1527, account.findDefaultChatRoom().id);
        return 0;
    }

    /* renamed from: a */
    public static final void toggleOnlineMode(boolean z) {
        if (!z) {
            AppState.setInt(4778, 5);
        } else {
            AppState.setInt(4778, 4);
            AppState.setBool(1526, true);
        }
    }

    /* renamed from: a */
    public static final int handleEnterKey() {
        restoreState();
        return NetworkUtils.processScreenForm();
    }

    /* renamed from: b */
    public static final int handleBackKey() {
        restoreState();
        return 0;
    }

    /* renamed from: c */
    public static final void resetSearchResults() {
        AppState.clearRange(1348, 1351);
    }

    /* renamed from: ad */
    private static final void restoreState() {
        ((MrimAccount) AppState.getAccount()).getLastChatRoom().clear();
    }

    /* renamed from: d */
    public static final int handleLeftKey() {
        int errorCode = AppState.getCurrentContact().validateDelete();
        if (0 != errorCode) {
            return showError(errorCode);
        }
        return 4;
    }

    /* renamed from: b */
    public static final int handleItemAction(Object obj) {
        AppState.setInt(266, 1);
        if (obj != null) {
            AppState.pool[267] = obj;
        }
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    /* renamed from: e */
    public static final int handleRightKey() {
        AppState.setInt(285, 1);
        ConnectionThread.toggleScrollMode();
        return 6;
    }

    /* renamed from: f */
    public static final String getAppVersion() {
        return StringUtils.intern(Long.toString(Runtime.getRuntime().freeMemory()));
    }

    /* renamed from: g */
    public static final void clearPreviewState() {
        AppState.clearRange(1284, 1288);
    }

    /* renamed from: h */
    public static final void clearSearchState() {
        Contact contact = AppState.getCurrentContact();
        markContactUnread(contact);
        contact.flags = (byte) 0;
        contact.dirty = true;
        contact.updateRenderState();
        ScreenManager.showScreen(contact.showMessages().measureContent());
    }

    /* renamed from: b */
    public static final int mapKeyToAction(int i) {
        if (i == 4) {
            ScreenManager.handleScreenClose();
            return 0;
        }
        if (i != 137) {
            return 0;
        }
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    /* renamed from: a */
    public static final int[] resizeArray(int[] iArr, int i, int i2) {
        return growArray(growArray(iArr, i), i2);
    }

    /* renamed from: a */
    private static int[] growArray(int[] iArr, int i) {
        int[] result = iArr;
        int newLength = 1 + iArr[0];
        if (newLength == result.length) {
            int[] expanded = new int[newLength << 1];
            result = expanded;
            Utils.arraycopy(iArr, 0, expanded, 0, newLength);
        }
        result[newLength] = i;
        result[0] = result[0] + 1;
        return result;
    }

    /* renamed from: a */
    public static final void setTimer(int i, long j) {
        timers[i] = System.currentTimeMillis() + j;
    }

    /* renamed from: K */
    private static boolean isTimerType(int i) {
        return timers[i] < System.currentTimeMillis();
    }

    /* renamed from: a */
    public static final boolean isTimerExpired(long j) {
        return j != 0 && j < System.currentTimeMillis();
    }

    /* renamed from: b */
    public static final boolean checkTimer(int i, long j) {
        long[] timerArray = timers;
        long timerValue = timerArray[i];
        long currentTime = System.currentTimeMillis();
        if (timerValue >= timerValue) {
            return false;
        }
        timerArray[i] = currentTime + j;
        return true;
    }

    /* renamed from: i */
    public static final int handleHashKey() {
        if (ScreenManager.hasScreen(43)) {
            return 43;
        }
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return -1;
    }

    /* renamed from: c */
    public static final int handleStarAction(Object obj) {
        AppState.pool[1258] = obj;
        return 177;
    }

    /* renamed from: j */
    public static final int showPeopleNearby() {
        ResourceManager.dialPhoneContact((PhoneContact) AppState.pool[1256], AppState.getInt(1444) + 10);
        return 6;
    }

    /* renamed from: k */
    public static final int showPeopleSearch() {
        ResourceManager.dialPhoneContact((PhoneContact) AppState.pool[1256], AppState.getInt(1444) - 10);
        return 6;
    }

    /* renamed from: l */
    public static final UserSearchResult getCurrentSearchResult() {
        return (UserSearchResult) AppState.pool[1258];
    }

    /* renamed from: c */
    public static final int handleAccountOption(int i) {
        Account account = AppState.getAccount();
        if (!(account instanceof MmpProtocol)) {
            return interpolateColor((i + 161) - 4, (i + 155) - 4, 4);
        }
        ((MmpProtocol) account).reserved2 = i;
        if (i == 0) {
            return 3;
        }
        return interpolateColor(i + 268, i + 118, 3);
    }

    /* renamed from: m */
    public static final String getStatusText() {
        Object obj = AppState.pool[1336];
        if (obj == null) {
            return null;
        }
        return obj instanceof String ? (String) obj : ((MrimContact) obj).simpleIdentifier;
    }

    /* renamed from: d */
    public static final long getZoomNumerator(int i) {
        return (1 << (17 - i)) * ((i < 8 || i > 17) ? 119432 : 1194329);
    }

    /* renamed from: e */
    public static final long getZoomDenominator(int i) {
        return (i < 8 || i > 17) ? 100000L : 1000000L;
    }

    /* renamed from: a */
    public static long coordToPixel(long j, int i) {
        return (j * getZoomDenominator(i)) / getZoomNumerator(i);
    }

    /* renamed from: a */
    public static final long pixelToCoord(int i, int i2) {
        return (i * getZoomNumerator(i2)) / getZoomDenominator(i2);
    }

    /* renamed from: a */
    public static final int computeColor(int i, int i2, int i3, int i4) {
        return Utils.abs(i2 - i4) + Utils.abs(i - i3);
    }

    /* renamed from: b */
    public static final int processLoginField(String str) {
        if (AppState.getString(1251).equals(str)) {
            ScreenBuilder.onScreenClosed();
            if (ConnectionThread.hasRoutePoints()) {
                return 0;
            }
            return showError(354);
        }
        if (AppState.getString(376).equals(str)) {
            AppState.setInt(253, 1);
            XmppContactGroup.stopMapAnimation(AppState.getVector(1401));
            MapRenderer.needsRedraw = true;
            return 6;
        }
        if (!AppState.getString(377).equals(str)) {
            return 0;
        }
        AppState.setInt(253, 0);
        XmppContactGroup.startMapAnimation(AppState.getVector(1401));
        MapRenderer.needsRedraw = true;
        return 6;
    }

    /* renamed from: a */
    public static final ByteBuffer createMrimPacket(MrimAccount account, int command, ByteBuffer payload) {
        ByteBuffer header = new ByteBuffer().writeIntLE(-559038737).writeIntLE(65557);
        int sequenceNum = account.state;
        account.state = sequenceNum + 1;
        return header.writeIntLE(sequenceNum).writeIntLE(command).writeIntLE(payload != null ? payload.length : 0).writeZeros(24).writeBuffer(payload);
    }

    /* renamed from: f */
    public static final int handleGroupSelection(int i) {
        Message message = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513)).getMessage(AppState.getString(1346));
        String body = message.body;
        message.body = i == 0 ? Conversation.encodeAlternate(body) : Conversation.decodeAlternate(body);
        return 52;
    }

    /* renamed from: g */
    public static final int handleGroupRename(int i) {
        if (i != 147 && i != 133 && i != 89) {
            return 0;
        }
        Vector accounts = getMrimAccountList();
        int size = accounts.size();
        if (size == 0) {
            return showError(549);
        }
        if (size != 1) {
            return showAccountList(accounts, i, false);
        }
        AppState.setAccount(accounts.firstElement());
        return i;
    }

    /* renamed from: n */
    public static final void finishScreenBuild() {
        NetworkUtils.showConfirmDialog(180, 504);
        AppState.clearRange(1239, 1240);
        Conversation.createStatusReport(true, (MrimAccount) AppState.getAccount());
    }

    /* renamed from: h */
    public static final int handleContactOption(int i) {
        if (i != 6) {
            return 0;
        }
        MrimAccount account = (MrimAccount) AppState.getAccount();
        account.isHighlighted = true;
        if (!account.isSelected()) {
            return showError(667);
        }
        applyViewMode(true, false, !AppState.getBool(276));
        AppState.setInt(281, 1);
        ConnectionThread.selectMapItem((ListItem) account);
        return 0;
    }

    /* renamed from: a */
    public static final ByteBuffer createPingPacket(MmpProtocol protocol, int i) {
        ByteBuffer packet = new ByteBuffer().writeByte(42).writeByte(i);
        int sequenceNum = protocol.state + 1;
        protocol.state = sequenceNum;
        return packet.writeShortBE((sequenceNum & 0xFFFFFF) % 32768).writeShortBE(0);
    }

    /* renamed from: o */
    public static final int handleContactListKey() {
        MrimAccount account = (MrimAccount) AppState.pool[1282];
        ResourceManager.showMailAccountList();
        AppState.setAccount(account);
        AppState.setInt(1512, 38);
        return 37;
    }

    /* renamed from: a */
    public static final void setCurrentAccount(Account account) {
        Vector accounts = AppState.getVector(1291);
        if (accounts == null) {
            accounts = NetworkUtils.newVector();
            AppState.pool[1291] = accounts;
        }
        accounts.addElement(account);
    }

    /* renamed from: p */
    public static final int handlePresenceAction() {
        Vector accounts = AppState.getVector(1291);
        int size = accounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return 4;
            }
            ((Account) accounts.elementAt(size)).connect(0);
        }
    }

    /* renamed from: i */
    public static final int handleProfileAction(int i) {
        switch (i) {
            case 0:
                Conversation.createStatusReport(false, (MrimAccount) null);
                return 12;
            case 1:
                applyViewMode(false, true, true);
                return 12;
            case 2:
                applyViewMode(true, false, true);
                return 12;
            case 3:
                Conversation.setMapEnabled(true);
                return 12;
            case 4:
                Conversation.setMapEnabled(false);
                return 12;
            case 5:
                return NetworkUtils.getIconOffset();
            default:
                return 0;
        }
    }

    /* renamed from: a */
    public static final void applyViewMode(boolean showMap, boolean showList, boolean shouldInvalidate) {
        AppState.setBool(276, showMap);
        AppState.setBool(277, showList);
        if (!shouldInvalidate || !ConnectionThread.mapInitialized) {
            return;
        }
        int i = 11;
        while (true) {
            i--;
            if (i < 0) {
                MmpContact.clearLocationData();
                StringUtils.initTileCache();
                ConnectionThread.clearPhotoCache();
                MapRenderer.needsRedraw = true;
                return;
            }
            XmppContactGroup.invalidateCachedImage(i + 18);
        }
    }

    /* renamed from: c */
    public static final Object[] getUrlComponents(String str) {
        return new Object[]{ResourceManager.integerOf(20), str};
    }

    /* renamed from: d */
    public static final int openUrl(String str) {
        AppState.setObject(1279, (Object) new StringBuffer().append((Object) NetworkUtils.getMessageBuffer()).append(str).toString());
        return 0;
    }

    /* renamed from: q */
    public static final void refreshContactList() {
        AppState.clearRange(1016, 1021);
    }

    /* renamed from: j */
    public static final int handleSettingsOption(int i) {
        AppState.setInt(1510, i);
        return 34;
    }

    /* renamed from: k */
    public static final int handleExtSettingsOption(int i) {
        MrimAccount account = (MrimAccount) AppState.getAccount();
        switch (i) {
            case 0:
                if (account != null) {
                    account.markProfileForPublish();
                    break;
                } else {
                    Vector accounts = getMrimAccountList();
                    int size = accounts.size();
                    while (true) {
                        size--;
                        if (size < 0) {
                            NetworkUtils.releaseVector(accounts);
                            break;
                        } else {
                            findMrimAccount(accounts, size).markProfileForPublish();
                        }
                    }
                }
            case 1:
                if (account != null) {
                    account.markProfileForHide();
                    break;
                } else {
                    Vector accounts2 = getMrimAccountList();
                    int size2 = accounts2.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            NetworkUtils.releaseVector(accounts2);
                            break;
                        } else {
                            findMrimAccount(accounts2, size2).markProfileForHide();
                        }
                    }
                }
            case 2:
                if (account != null) {
                    account.setProfileGroups();
                    break;
                } else {
                    Vector accounts3 = getMrimAccountList();
                    int size3 = accounts3.size();
                    while (true) {
                        size3--;
                        if (size3 < 0) {
                            NetworkUtils.releaseVector(accounts3);
                            break;
                        } else {
                            findMrimAccount(accounts3, size3).setProfileGroups();
                        }
                    }
                }
            case 3:
                if (account != null) {
                    account.clearProfileGroups();
                    break;
                } else {
                    Vector accounts4 = getMrimAccountList();
                    int size4 = accounts4.size();
                    while (true) {
                        size4--;
                        if (size4 < 0) {
                            NetworkUtils.releaseVector(accounts4);
                            break;
                        } else {
                            findMrimAccount(accounts4, size4).clearProfileGroups();
                        }
                    }
                }
            case 4:
                return 156;
        }
        if (AppState.getBool(286)) {
            return AppState.getInt(1476);
        }
        ScreenBuilder.onScreenClosed();
        return 171;
    }

    /* renamed from: d */
    public static final int handleObjectAction(Object obj) {
        AppState.setAccount(obj);
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    /* renamed from: l */
    public static final int showError(int i) {
        if (ScreenManager.getCurrentScreen().screenType == 8) {
            ScreenBuilder.onScreenClosed();
        }
        AppState.setFromPool(1294, i);
        return 112;
    }

    /* renamed from: e */
    public static final void showNotification(String str) {
        AppState.setInt(3329, 112);
        AppState.setObject(1294, (Object) str);
        clearNotifications();
    }

    /* renamed from: m */
    public static final void showMessageById(int i) {
        AppState.setInt(3329, 112);
        AppState.setFromPool(1294, i);
        clearNotifications();
    }

    /* renamed from: r */
    public static final void clearNotifications() {
        ResourceManager.playNotificationSound(5);
        ScreenManager.showScreen(ScreenManager.createScreen(3328));
        AppState.clearIndex(1294);
    }

    /* renamed from: ae */
    private static final Object[] createSyncState() {
        return (Object[]) AppState.pool[1238];
    }

    /* renamed from: s */
    public static final void acquireNetworkLock() {
        Object[] syncState = createSyncState();
        while (true) {
            synchronized (syncState) {
                if (syncState[0] == null) {
                    syncState[0] = Thread.currentThread();
                    return;
                }
            }
            try {
                Thread.sleep(100);
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: t */
    public static final void releaseNetworkLock() {
        Object[] syncState = createSyncState();
        synchronized (syncState) {
            syncState[0] = null;
        }
    }

    /* renamed from: u */
    public static final boolean isNetworkBusy() {
        return createSyncState()[0] != null;
    }

    /* renamed from: n */
    public static final int handleConnectionOption(int i) {
        ((XmppContact) AppState.getCurrentContact()).setPresenceFeature(i);
        return 0;
    }

    /* renamed from: f */
    public static final int validateServerAddress(String str) {
        AppState.setFromBuffer(1279, NetworkUtils.getMessageBuffer().append(str));
        return 0;
    }

    /* renamed from: v */
    public static final int handleSendKey() {
        AppState.setAccount(getMrimAccountList().firstElement());
        return 168;
    }

    /* renamed from: a */
    public static final int handleServerAction(int i, String str) {
        switch (i) {
            case 0:
                return 155;
            case 1:
                return 156;
            case 2:
                return 157;
            case 3:
                return 154;
            default:
                if (AppState.getString(1225).equals(str)) {
                    return 160;
                }
                if (AppState.getString(1224).equals(str)) {
                    return 159;
                }
                int optionId = Integer.parseInt(StringUtils.suffix(str, 7));
                return (!(optionId >= 4 && optionId <= 53) || optionId == 25 || optionId == 31) ? (i & Integer.MIN_VALUE) != 0 ? 158 : 156 : optionId + 157;
        }
    }

    /* renamed from: w */
    public static final void processEventQueue() {
        Screen screen = ScreenManager.createScreen(4852);
        MrimAccount account = (MrimAccount) AppState.getAccount();
        Enumeration chatRooms = account.chatRoomsList.elements();
        while (chatRooms.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) chatRooms.nextElement();
            if (chatRoom != account.getLastChatRoom()) {
                MenuItem menuItem = MenuItem.createDefault().setIcon(234).setLabel(chatRoom.name);
                menuItem.data = chatRoom;
                screen.addItem(menuItem);
            }
        }
        ScreenManager.showScreen(screen);
    }

    /* renamed from: e */
    public static final int handleEventObject(Object obj) {
        AppState.setInt(1527, ((ChatRoom) obj).id);
        return 0;
    }

    /* renamed from: o */
    public static final int handleNotificationOption(int i) {
        if (i == 54) {
            ScreenBuilder.onScreenClosed();
            ResourceManager.composeEmail((Vector) null, (String) null, (String) null);
            return 0;
        }
        if (i == 68) {
            ScreenBuilder.onScreenClosed();
            toggleOnlineMode(true);
            return 0;
        }
        if (i != 37) {
            return 0;
        }
        ((MrimAccount) AppState.getAccount()).chatRoomsLoaded = true;
        return 0;
    }

    /* renamed from: a */
    public static final int sortContacts(Vector vector) {
        int size = vector.size();
        sortRange(vector, 0, size - 1);
        return size;
    }

    /* renamed from: a */
    private static final void sortRange(Vector vector, int left, int right) {
        if (left < right) {
            if (left + 1 == right) {
                if (((Sortable) vector.elementAt(left)).compareTo(vector.elementAt(right)) > 0) {
                    Utils.swapElements(vector, left, right);
                    return;
                }
                return;
            }
            int lo = left;
            int hi = right;
            boolean moveLow = true;
            while (lo < hi) {
                if (((Sortable) vector.elementAt(lo)).compareTo(vector.elementAt(hi)) > 0) {
                    Utils.swapElements(vector, lo, hi);
                    moveLow = !moveLow;
                }
                if (moveLow) {
                    lo++;
                } else {
                    hi--;
                }
            }
            sortRange(vector, left, lo - 1);
            sortRange(vector, hi + 1, right);
        }
    }

    /* renamed from: x */
    public static final void processBackgroundTasks() {
        updateTimerSlot(0);
    }

    /* renamed from: y */
    public static final void markScreenDirty() {
        if (AppState.getBool(270)) {
            updateTimerSlot(Integer.MAX_VALUE);
            setTimer(0, getSessionTimestamp());
        }
    }

    /* renamed from: z */
    public static final void processTimers() {
        updateTimerSlot(Integer.MAX_VALUE);
    }

    /* renamed from: L */
    private static final void updateTimerSlot(int i) {
        if (AppState.getBool(268)) {
            try {
                Display.getDisplay(AppState.getMidlet()).flashBacklight(i);
            } catch (Throwable unused) {
            }
        }
    }

    /* renamed from: M */
    private static int computeTimerValue(int i) {
        if (i == 0) {
            return 32;
        }
        if (i < 0) {
            return 0;
        }
        int count = 0;
        if ((i & (-65536)) == 0) {
            i <<= 16;
            count = 16;
        }
        if ((i & (-16777216)) == 0) {
            i <<= 8;
            count += 8;
        }
        while (i > 0) {
            count++;
            i <<= 1;
        }
        return count;
    }

    /* renamed from: b */
    public static final int countLeadingZeros(long j) {
        int highBits = computeTimerValue((int) (j >> 32));
        return highBits == 32 ? computeTimerValue((int) j) + 32 : highBits;
    }

    /* renamed from: b */
    public static final long shiftRightSticky(long j, int i) {
        return i >= 64 ? j == 0 ? 0L : 1L : (j << (64 - i)) == 0 ? j >>> i : (j >>> i) | 1;
    }

    /* renamed from: c */
    public static final long roundedShiftRight(long j, int i) {
        long shifted;
        long result;
        if (i > 64) {
            return 0L;
        }
        if (i == 64) {
            shifted = j;
            result = 0;
        } else {
            shifted = j << (64 - i);
            result = j >>> i;
        }
        return (shifted >= 0 || (shifted == Long.MIN_VALUE && (result & 1) != 1)) ? result : result + 1;
    }

    /* renamed from: p */
    public static final int getThemeColor(int i) {
        int size = AppState.getVector(1241).size();
        while (true) {
            size--;
            if (size < 0) {
                return 0;
            }
            getAccountByIndex(size).onError(i);
        }
    }

    /* renamed from: q */
    public static final int getThemeBackground(int i) {
        switch (i) {
            case 0:
                Conversation.incrementZoom();
                break;
            case 1:
                Conversation.decrementZoom();
                break;
            case 2:
                AppState.setInt(230, 1);
                break;
            case 3:
                AppState.setInt(230, 0);
                break;
            default:
                return 0;
        }
        MapRenderer.needsRedraw = true;
        return 6;
    }

    /* renamed from: A */
    public static final int getScreenMode1() {
        return computeLayoutParam(1004);
    }

    /* renamed from: B */
    public static final int getScreenMode2() {
        return computeLayoutParam(1005);
    }

    /* renamed from: C */
    public static final int getScreenMode3() {
        return Integer.parseInt(StringUtils.getSystemProp(1006));
    }

    /* renamed from: D */
    public static final int getScreenMode4() {
        return Integer.parseInt(StringUtils.getSystemProp(1007));
    }

    /* renamed from: N */
    private static final int computeLayoutParam(int i) {
        return Integer.parseInt(StringUtils.getSystemProp(i), 16);
    }

    /* renamed from: r */
    public static final int handleViewOption(int i) {
        if (i == 120) {
            if (!ConnectionThread.hasRoutePoints()) {
                return showError(354);
            }
            AppState.setInt(1443, 1);
            return 0;
        }
        if (i != 100) {
            return i == 0 ? 6 : 0;
        }
        AppState.setInt(1443, 1);
        return 0;
    }

    /* renamed from: s */
    public static final int handleThemeOption(int i) {
        if (i == 10) {
            return NetworkUtils.getIconOffset();
        }
        return 0;
    }

    /* renamed from: g */
    public static final int processInputText(String str) {
        AppState.setBool(1524, StringUtils.matchesKey(859, str));
        return 0;
    }

    /* renamed from: a */
    public static final int handleInputAction(int i, Object obj) {
        AppState.setAccount(obj);
        if (obj != null) {
            return 47;
        }
        if (i > 3) {
            return i;
        }
        AppState.setInt(1475, i);
        return 76;
    }

    /* renamed from: t */
    public static final int handleSoundOption(int i) {
        AppState.setFromBuffer(1279, NetworkUtils.getMessageBuffer().append(AppState.getString(i + (AppState.getCurrentContact() instanceof MmpContact ? 1141 : AppState.getCurrentContact() instanceof XmppContact ? 1184 : 1063))));
        return 63;
    }

    /* renamed from: a */
    public static final ByteBuffer createAuthData(MmpProtocol protocol) {
        ByteBuffer buffer = new ByteBuffer().writeShortBE(5);
        int authSlot = protocol.reserved2;
        buffer = buffer.writeShortBE(64 + (authSlot == 0 ? 0 : 16));
        int fieldIndex = 4;
        while (true) {
            fieldIndex--;
            if (fieldIndex < 0) {
                break;
            }
            buffer.writeCompressed(fieldIndex + 904);
        }
        if (authSlot != 0) {
            buffer.writeBytesAt(AppState.getBytes(908), (authSlot - 1) << 4, 16);
        }
        return createMmpCommand(protocol, 516, buffer);
    }

    /* renamed from: E */
    public static final long getSessionTimestamp() {
        switch (AppState.getInt(271)) {
            case 1:
                return 15000L;
            case 2:
                return 30000L;
            case 3:
                return 60000L;
            case 4:
                return 300000L;
            default:
                return 4294967295L;
        }
    }

    /* renamed from: a */
    public static final ByteBuffer createMrimAuthPacket(MrimAccount account) {
        return createMrimPacket(account, 4097, new ByteBuffer().writeIntLE(120));
    }

    /* renamed from: F */
    public static final void clearFormFields() {
        AppState.clearRange(1302, 1305);
    }

    /* renamed from: f */
    public static final int handleFormSubmit(Object obj) {
        XmppMailRuProtocol.mapContextItem = (ListItem) obj;
        return 0;
    }

    /* renamed from: a */
    public static final void waitForCompletion(Object[] objArr) throws InterruptedException {
        int remaining = 15000;
        do {
            remaining -= 500;
            if (remaining < 0) {
                IOUtils.postEvent((Object) objArr);
                return;
            }
            Thread.sleep(500L);
        } while (!isShuttingDown);
    }

    /* renamed from: u */
    public static final int handleChatOption(int i) {
        if (i != 54) {
            ScreenBuilder.onScreenClosed();
            ScreenBuilder.onScreenClosed();
            clearSearchState();
            return 0;
        }
        MrimContact contact = AppState.getCurrentMrimContact();
        AppState.setAccount(contact.account);
        ResourceManager.composeEmail(XmppMailRuProtocol.parseRecipientList(contact.simpleIdentifier), (String) null, (String) null);
        ScreenBuilder.onScreenClosed();
        ScreenBuilder.onScreenClosed();
        return 0;
    }

    /* renamed from: g */
    public static final int handleConversationAction(Object obj) {
        if (AppState.getBool(1443)) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return 0;
        }
        if (!AppState.getBool(1477)) {
            ConnectionThread.navigateToPoint((MapPoint) obj, true);
            return 0;
        }
        MapPoint mapPoint = (MapPoint) obj;
        ((MrimAccount) AppState.getAccount()).setLocationProfile(mapPoint);
        XmppContactGroup.addMapPointIfNew(AppState.getVector(1400), mapPoint, 0, 5);
        XmppContactGroup.saveMapPoints(AppState.getVector(1400), 225);
        AppState.setInt(1477, 0);
        return 160;
    }

    /* renamed from: c */
    private static int interpolateColor(int i, int i2, int i3) {
        AppState.setInt(4305, i);
        AppState.setInt(4313, i);
        AppState.setInt(4317, i2);
        AppState.setInt(4308, i3);
        return 49;
    }

    /* renamed from: v */
    public static final int handleChatRoomOption(int i) {
        if (i == 0) {
            ConnectionThread.setRouteStart();
            if (MmpContact.hasSecondToken()) {
                return 6;
            }
            AppState.setInt(1442, 1);
            return 158;
        }
        ConnectionThread.setRouteEnd();
        if (MmpContact.hasFirstToken()) {
            return 6;
        }
        AppState.setInt(1442, 0);
        return 158;
    }

    /* renamed from: w */
    public static final int handleMailboxOption(int i) {
        if (i != 0) {
            AppState.setInt(1477, 1);
            return 100;
        }
        AppState.setInt(1479, 1);
        ((MrimAccount) AppState.getAccount()).isHighlighted = false;
        return 12;
    }

    /* renamed from: a */
    public static final void handleMmpPacket(MmpProtocol protocol, ByteBuffer buffer) {
        buffer.skip(6);
        while (buffer.length > 0) {
            int tag = buffer.readShortBE();
            int length = buffer.readShortBE();
            if (tag == 9 && length == 2) {
                int statusCode = buffer.readShortBE();
                if (statusCode == 1) {
                    protocol.handleTimeout();
                    return;
                } else {
                    protocol.handleError(statusCode);
                    return;
                }
            }
            buffer.skip(length);
        }
        protocol.handleError(-1);
    }

    /* renamed from: x */
    public static final int handleChatListOption(int i) {
        MapPoint mapPoint = pendingMapPoint;
        if (mapPoint == null) {
            return showError(354);
        }
        if (i == 6) {
            MapRenderer.navigateToMapPoint(pendingMapPoint);
            return 0;
        }
        if (i == 118) {
            AppState.setObject(43, (Object) mapPoint.getResourceUrl());
            return 0;
        }
        if (i != 120) {
            return 0;
        }
        ConnectionThread.removeRoutePoint(mapPoint);
        return 0;
    }

    /* renamed from: y */
    public static final int handleChatDetailOption(int i) {
        ConnectionThread.showMapView();
        if (i == 6) {
            applyViewMode(true, false, !AppState.getBool(276));
            AppState.setInt(281, 1);
            AppState.setInt(1479, 1);
            return 0;
        }
        if (i == 100) {
            AppState.setInt(1477, 1);
            return 0;
        }
        if (!ConnectionThread.hasRoutePoints()) {
            return showError(354);
        }
        AppState.setInt(1478, 1);
        return 0;
    }

    /* renamed from: z */
    public static final int handleChatSettingsOption(int i) {
        if (i == 22 || i == 143 || i == 24 || i == 23) {
            return showAccountList(getMrimAccountList(), i, false);
        }
        if (i == 21 || i == 69 || i == 124) {
            return showAccountList(getXmppAccountList(), i, false);
        }
        return 0;
    }

    /* renamed from: G */
    public static final void showSettingsScreen() {
        AppState.setInt(217, 0);
        AppState.setInt(1511, 1);
        ScreenManager.pushScreen(ScreenManager.createScreen(4038));
    }

    /* renamed from: H */
    public static final void clearSearchResults2() {
        AppState.clearRange(1317, 1319);
    }

    /* renamed from: I */
    public static final void prepareFormData() {
        AppState.setInt(1506, 0);
        AppState.clearIndex(1318);
        AppState.pool[1317] = NetworkUtils.newVector();
    }

    /* renamed from: a */
    public static final void openUserProfile(MrimAccount account, String str) {
        pendingAccount = account;
        pendingUrl = str;
    }

    /* renamed from: J */
    public static final void clearMapPoints() {
        pendingAccount = null;
        pendingUrl = null;
    }

    /* renamed from: b */
    public static final ByteBuffer createPasswordAuthCmd(MrimAccount account, String str) {
        return createMrimPacket(account, 4128, new ByteBuffer().writeStringLatin1(str));
    }

    /* renamed from: h */
    public static final int processPhoneInput(String str) {
        String newValue = AppState.getString(1279);
        int i = 15;
        do {
            i--;
            if (i < 0) {
                return 0;
            }
        } while (AppState.getString(i + 48) != str);
        AppState.setObject(i + 48, (Object) newValue);
        return 0;
    }

    /* renamed from: h */
    public static final int handleSearchAction(Object obj) {
        ContactGroup group = (ContactGroup) obj;
        if (null == group) {
            return 4;
        }
        Contact contact = AppState.getCurrentContact();
        int errorCode = contact.isOnline() ? 310 : contact.account.validateMove(contact, contact.account.findGroup(contact), group);
        if (0 != errorCode) {
            return showError(errorCode);
        }
        return 4;
    }

    /* renamed from: i */
    public static final int handleSearchResultAction(Object obj) {
        MapRenderer.invalidate();
        GeoRegion region = (GeoRegion) obj;
        MapRenderer.setPosition(region.centerLat, region.centerLon);
        MapRenderer.setZoom(region == StringUtils.getGeoRegion() ? 3 : 11);
        return 0;
    }

    /* renamed from: K */
    public static final int handleInviteAction() {
        long lon;
        long lat;
        ListItem item = MapRenderer.tooltipItem;
        if (item != null) {
            lon = item.getWidth();
            lat = item.getBaseHeight();
        } else {
            lon = MapRenderer.currentLon;
            lat = MapRenderer.currentLat;
        }
        AppState.setInt(1479, 0);
        ResourceManager.startGeoSearch(VCard.formatLocationUrl(AppState.getInt(39), IOUtils.pixelToLongitude(lon), IOUtils.pixelToLatitude(lat)), lon, lat);
        return 6;
    }

    /* renamed from: L */
    public static final int handleInviteResult() {
        char errorCode;
        Account account = AppState.getAccount();
        if (account.isConnecting()) {
            errorCode = 300;
        } else {
            AppState.getVector(1241).removeElement(account);
            TabBar.initialize();
            saveAccountList();
            errorCode = 0;
        }
        if (0 != errorCode) {
            return showError(300);
        }
        return 25;
    }

    /* renamed from: A */
    public static final int handleAccountSwitchOption(int i) {
        Contact contact = AppState.getCurrentContact();
        switch (i) {
            case 0:
                int blockError = contact.validateBlock();
                if (0 != blockError) {
                    return showError(blockError);
                }
                return 4;
            case 1:
                int unblockError = contact.validateUnblock();
                if (0 != unblockError) {
                    return showError(unblockError);
                }
                return 4;
            default:
                return 0;
        }
    }

    /* renamed from: M */
    public static final void initChatRoomList() {
        Screen screen = ScreenManager.createScreen(4517);
        MrimAccount account = (MrimAccount) AppState.getAccount();
        Enumeration chatRooms = account.chatRoomsList.elements();
        while (chatRooms.hasMoreElements()) {
            ChatRoom chatRoom = (ChatRoom) chatRooms.nextElement();
            if (chatRoom != account.getLastChatRoom()) {
                MenuItem menuItem = MenuItem.createDefault().setIcon(234).setLabel(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(chatRoom.name).append(' ').append('['))).addText(StringUtils.intern(Integer.toString(chatRoom.unreadCount)), 1, 0).setLabel(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append('/').append(chatRoom.memberCount).append(']')));
                menuItem.data = chatRoom;
                screen.addItem(menuItem);
            }
        }
        ScreenManager.showScreen(screen);
    }

    /* renamed from: a */
    public static final ByteBuffer createChatRoomCmd(MrimAccount account, String str, int i) {
        ByteBuffer buffer = new ByteBuffer().writeIntLE(0);
        int atIndex = str.indexOf(64);
        return account.createAndQueueCommand(new Object[]{createMrimPacket(account, 4137, buffer.writeStringLatin1(StringUtils.prefix(str, atIndex)).writeIntLE(1).writeStringLatin1(StringUtils.suffix(str, atIndex + 1))), ResourceManager.integerOf(i)});
    }

    /* renamed from: j */
    public static final int handleMapSearchAction(Object obj) {
        long lon;
        long lat;
        Contact contact = (Contact) obj;
        String query = AppState.getString(1249);
        ListItem item = MapRenderer.tooltipItem;
        if (item == null || !item.isSelected()) {
            lon = MapRenderer.currentLon;
            lat = MapRenderer.currentLat;
        } else {
            lon = item.getWidth();
            lat = item.getBaseHeight();
        }
        int errorCode = contact.sendMessage(ResourceManager.buildTileRequestUrl(lon, lat, AppState.getInt(39), query));
        if (0 != errorCode) {
            return showError(errorCode);
        }
        return 0;
    }

    /* renamed from: k */
    public static final int handleMapResultAction(Object obj) {
        AppState.setObject(43, (Object) ((MapPoint) obj).getResourceUrl());
        return 0;
    }

    /* renamed from: i */
    public static final int processSearchQuery(String str) {
        if (!AppState.getString(402).equals(str)) {
            return 0;
        }
        if (MapRenderer.selectedMapPoint != null) {
            MapRenderer.selectedMapPoint.markInactive();
        }
        XmppContactGroup.startMapAnimation(AppState.getVector(1401));
        AppState.setInt(253, 0);
        MmpContact.clearLocationData();
        MapRenderer.needsRedraw = true;
        XmppContactGroup.lastCheckTs = System.currentTimeMillis();
        MapRenderer.needsRedraw = true;
        return 0;
    }

    /* renamed from: l */
    public static final int handleLocationAction(Object obj) {
        if (obj == null) {
            return 0;
        }
        AppState.setFromBuffer(1279, NetworkUtils.getMessageBuffer().append(obj));
        return 0;
    }

    /* renamed from: B */
    public static final int handleMapMenuOption(int i) {
        int activeCount = getActiveAccountCount();
        if (i == 15) {
            if (activeCount == 0) {
                return showError(551);
            }
            if (activeCount == 1) {
                rebuildAccountCaches();
                return 4;
            }
        } else {
            if (i == 3) {
                Vector accounts = getXmppAccountList();
                int result = showAccountList(accounts, 3, true);
                if (result != 39) {
                    return result;
                }
                accounts.insertElementAt(accounts, 0);
                return 39;
            }
            if (i == 152) {
                return showAccountList(getMrimAccountList(), 152, true);
            }
        }
        if (i == 10) {
            return NetworkUtils.getIconOffset();
        }
        if (i != 6) {
            return 0;
        }
        AppState.setInt(1414, 1);
        return 0;
    }

    /* renamed from: m */
    public static final int handleFileAction(Object obj) {
        int errorCode = ((Contact) obj).sendMessage(AppState.getString(43));
        if (0 != errorCode) {
            return showError(errorCode);
        }
        return 0;
    }

    /* renamed from: N */
    public static final boolean hasActiveConnection() {
        return AppState.getVector(1244).size() != 0;
    }

    /* renamed from: b */
    public static final void markAccountHighlighted(MrimAccount account) {
        AppState.getVector(1244).removeElement(account);
        TabBar.layout();
    }

    /* renamed from: O */
    public static final void updateTabBar() {
        AppState.getVector(1244).removeAllElements();
        TabBar.layout();
    }

    /* renamed from: P */
    public static final int handleTabAction() {
        int i = 0;
        Vector contacts = AppState.getVector(1243);
        int size = contacts.size();
        while (true) {
            size--;
            if (size < 0) {
                return i;
            }
            i |= ((Contact) contacts.elementAt(size)).flags;
        }
    }

    /* renamed from: a */
    public static final void markContactRead(Contact contact) {
        markScreenDirty();
        Vector contacts = AppState.getVector(1243);
        if (contacts.contains(contact)) {
            return;
        }
        contacts.addElement(contact);
        TabBar.layout();
    }

    /* renamed from: b */
    public static final void markContactUnread(Contact contact) {
        Vector contacts = AppState.getVector(1243);
        if (contacts.contains(contact)) {
            Utils.removeFrom(contacts, contact);
            TabBar.layout();
        }
    }

    /* renamed from: b */
    public static final boolean isAccountOnline(Account account) {
        if (account == null) {
            return false;
        }
        Vector contacts = AppState.getVector(1243);
        int size = contacts.size();
        do {
            size--;
            if (size < 0) {
                return false;
            }
        } while (((Contact) contacts.elementAt(size)).account != account);
        return true;
    }

    /* renamed from: c */
    public static final int getAccountStatus(Account account) {
        if (account == null) {
            return 16384;
        }
        Vector contacts = AppState.getVector(1243);
        int size = contacts.size();
        do {
            size--;
            if (size < 0) {
                return account.getIconId();
            }
        } while (((Contact) contacts.elementAt(size)).account != account);
        return 16384;
    }

    /* renamed from: c */
    public static final void deleteContact(Contact contact) {
        contact.clearStatus();
        AppState.getVector(1242).removeElement(contact);
        needsLayoutUpdate = true;
    }

    /* renamed from: a */
    public static final void updateAccountStatus(Account account, int i) {
        account.resetSyncIfChanged(initStartupState());
        int[] iArr = account.syncArray;
        iArr[0] = iArr[0] + i;
        iArr[2] = iArr[2] + i;
        iArr[4] = iArr[4] + i;
        iArr[6] = iArr[6] + i;
        AppState.addInt(2, i);
        AppState.addInt(4, i);
        AppState.addInt(6, i);
        AppState.addInt(8, i);
        AppState.addInt(293, i);
    }

    /* renamed from: b */
    public static final void setAccountOption(Account account, int i) {
        account.resetSyncIfChanged(initStartupState());
        int[] iArr = account.syncArray;
        iArr[1] = iArr[1] + i;
        iArr[3] = iArr[3] + i;
        iArr[5] = iArr[5] + i;
        iArr[7] = iArr[7] + i;
        AppState.addInt(3, i);
        AppState.addInt(5, i);
        AppState.addInt(7, i);
        AppState.addInt(9, i);
        AppState.addInt(294, i);
    }

    /* renamed from: a */
    public static final void processAccountData(Account account, ByteBuffer buffer) {
        updateAccountStatus(account, buffer.length);
    }

    /* renamed from: C */
    public static final void addSentBytes(int i) {
        initStartupState();
        AppState.addInt(10, i);
        AppState.addInt(12, i);
        AppState.addInt(14, i);
        AppState.addInt(16, i);
        AppState.addInt(293, i);
    }

    /* renamed from: D */
    public static final void addReceivedBytes(int i) {
        initStartupState();
        AppState.addInt(11, i);
        AppState.addInt(13, i);
        AppState.addInt(15, i);
        AppState.addInt(17, i);
        AppState.addInt(294, i);
    }

    /* renamed from: E */
    public static final void addDownloadBytes(int i) {
        initStartupState();
        AppState.addInt(18, i);
        AppState.addInt(20, i);
        AppState.addInt(22, i);
        AppState.addInt(24, i);
        AppState.addInt(293, i);
    }

    /* renamed from: F */
    public static final void addUploadBytes(int i) {
        initStartupState();
        AppState.addInt(19, i);
        AppState.addInt(21, i);
        AppState.addInt(23, i);
        AppState.addInt(25, i);
        AppState.addInt(294, i);
    }

    /* renamed from: G */
    public static final void addConnectionBytes(int i) {
        initStartupState();
        AppState.addInt(26, i);
        AppState.addInt(28, i);
        AppState.addInt(30, i);
        AppState.addInt(32, i);
        AppState.addInt(293, i);
    }

    /* renamed from: H */
    public static final void addProtocolBytes(int i) {
        initStartupState();
        AppState.addInt(27, i);
        AppState.addInt(29, i);
        AppState.addInt(31, i);
        AppState.addInt(33, i);
        AppState.addInt(294, i);
    }

    /* renamed from: a */
    public static final int getTrafficCount(int i, int i2, int i3) {
        return AppState.getInt(2 + (i << 3) + (i2 << 1) + i3);
    }

    /* renamed from: b */
    public static final int getTotalTraffic(int i, int i2) {
        return getTrafficCount(0, i, i2) + getTrafficCount(1, i, i2) + getTrafficCount(2, i, i2) + getTrafficCount(3, i, i2);
    }

    /* renamed from: b */
    public static final void addTrafficCount(int i, int i2, int i3) {
        AppState.setInt(2 + (i << 3) + (i2 << 1) + i3, 0);
    }

    /* renamed from: af */
    private static final int initStartupState() {
        int currentDate = AppState.getDateCode();
        int savedDate = AppState.getInt(1);
        if (currentDate != savedDate) {
            for (int i = 0; i < 4; i++) {
                int offset = i << 3;
                AppState.setInt(offset + 4, 0);
                AppState.setInt(offset + 5, 0);
                if ((currentDate >>> 8) != (savedDate >>> 8)) {
                    AppState.setInt(offset + 6, 0);
                    AppState.setInt(offset + 7, 0);
                }
            }
            AppState.setInt(1, currentDate);
        }
        return currentDate;
    }

    /* renamed from: ag */
    private static void loadSavedAccounts() {
        Vector accounts = NetworkUtils.newVector();
        ByteBuffer buffer = XmppMailRuProtocol.readChunkedRecord(NetworkUtils.longToHex(6513505));
        while (buffer.length > 0) {
            try {
                Account account = null;
                byte typeByte = buffer.readByte();
                switch (typeByte & 7) {
                    case 0:
                        MrimAccount mrimAccount = new MrimAccount(buffer);
                        account = mrimAccount;
                        accounts.addElement(mrimAccount);
                        break;
                    case 1:
                        MmpProtocol mmpProtocol = new MmpProtocol(buffer);
                        account = mmpProtocol;
                        accounts.addElement(mmpProtocol);
                        break;
                    case 2:
                        XmppProtocol xmppProtocol = new XmppProtocol(buffer);
                        account = xmppProtocol;
                        accounts.addElement(xmppProtocol);
                        break;
                    case 3:
                        XmppMailRuProtocol xmppMailRu = new XmppMailRuProtocol(buffer);
                        account = xmppMailRu;
                        accounts.addElement(xmppMailRu);
                        break;
                }
                if ((typeByte & 8) != 0) {
                    account.loadProperties(buffer);
                }
            } catch (Throwable unused) {
            }
        }
        AppState.pool[1241] = accounts;
    }

    /* renamed from: Q */
    public static final int getActiveAccountCount() {
        return AppState.getVector(1241).size();
    }

    /* renamed from: I */
    public static final Account getAccountByIndex(int i) {
        return (Account) AppState.getVector(1241).elementAt(i);
    }

    /* renamed from: ah */
    private static final void saveAccountList() {
        saveState(false, false);
        AppState.saveDelta(true);
    }

    /* renamed from: a */
    public static final void saveState(boolean z, boolean z2) {
        try {
            ByteBuffer buffer = new ByteBuffer();
            Vector accounts = AppState.getVector(1241);
            if (z2) {
                buffer.ensureCapacity(20480);
                while (accounts.size() > 0) {
                    ((Account) Utils.dequeue(accounts)).serializeAccount(buffer, z, true).saveProperties(buffer);
                }
            } else {
                buffer.ensureCapacity(3072);
                for (int i = 0; i < accounts.size(); i++) {
                    ((Account) accounts.elementAt(i)).serializeAccount(buffer, z, false).saveProperties(buffer);
                }
            }
            XmppMailRuProtocol.writeRecord(NetworkUtils.longToHex(6513505), buffer, z);
        } catch (Throwable unused) {
        }
    }

    /* renamed from: a */
    public static final int validateCredentials(int i, Account existingAccount, String str, String str2) {
        Account foundAccount;
        if (StringUtils.isEmpty(str)) {
            return 301;
        }
        if (StringUtils.isEmpty(str2)) {
            return 306;
        }
        Vector accounts = AppState.getVector(1241);
        int size = accounts.size();
        while (true) {
            size--;
            if (size >= 0) {
                Account candidate = (Account) accounts.elementAt(size);
                if (i == candidate.getType() && str.equals(candidate.login)) {
                    foundAccount = candidate;
                    break;
                }
            } else {
                foundAccount = null;
                break;
            }
        }
        if (existingAccount != null) {
            if (foundAccount == null || foundAccount == existingAccount) {
                return existingAccount.setCredentials(str, str2);
            }
            return 307;
        }
        if (foundAccount != null) {
            return 307;
        }
        Vector allAccounts = AppState.getVector(1241);
        int totalSize = allAccounts.size();
        int newId = 0;
        while (true) {
            boolean idTaken = false;
            int j = totalSize;
            while (true) {
                j--;
                if (j < 0) {
                    break;
                }
                if (((Account) allAccounts.elementAt(j)).accountId == newId) {
                    idTaken = true;
                    break;
                }
            }
            if (!idTaken) {
                break;
            }
            newId++;
        }
        if (i == 0) {
            allAccounts.addElement(new MrimAccount(newId, str, str2));
        } else if (i == 1) {
            allAccounts.addElement(new MmpProtocol(newId, str, str2));
        } else if (i == 2) {
            allAccounts.addElement(new XmppProtocol(newId, str, str2));
        } else if (i == 3) {
            allAccounts.addElement(new XmppMailRuProtocol(newId, str, str2));
        }
        TabBar.initialize();
        saveAccountList();
        return 0;
    }

    /* renamed from: b */
    public static final Account createAccount(int i, String str) {
        Vector accounts = AppState.getVector(1241);
        int size = accounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return null;
            }
            Account account = (Account) accounts.elementAt(size);
            if (str.equals(account.login) && account.getType() == i) {
                return account;
            }
        }
    }

    /* renamed from: R */
    public static final Vector getMrimAccountList() {
        Vector result = NetworkUtils.newVector();
        Vector allAccounts = AppState.getVector(1241);
        int size = allAccounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return result;
            }
            Object element = allAccounts.elementAt(size);
            if (element instanceof MrimAccount) {
                result.insertElementAt(element, 0);
            }
        }
    }

    /* renamed from: S */
    public static final Vector getOnlineMrimAccounts() {
        Vector accounts = getMrimAccountList();
        int size = accounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return accounts;
            }
            if (!findMrimAccount(accounts, size).isConnected()) {
                accounts.removeElementAt(size);
            }
        }
    }

    /* renamed from: T */
    public static final Vector getMmpAccountList() {
        Vector accounts = getMrimAccountList();
        int size = accounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return accounts;
            }
            if (findMrimAccount(accounts, size).syncSeq == 0) {
                accounts.removeElementAt(size);
            }
        }
    }

    /* renamed from: U */
    public static final int getActiveScreenId() {
        int i = 0;
        Vector accounts = getMrimAccountList();
        int size = accounts.size();
        while (true) {
            size--;
            if (size < 0) {
                NetworkUtils.releaseVector(accounts);
                return i;
            }
            i += findMrimAccount(accounts, size).syncSeq;
        }
    }

    /* renamed from: V */
    public static final Vector getXmppAccountList() {
        Vector result = NetworkUtils.newVector();
        Vector allAccounts = AppState.getVector(1241);
        int size = allAccounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return result;
            }
            result.insertElementAt(allAccounts.elementAt(size), 0);
        }
    }

    /* renamed from: ai */
    private static void rebuildAccountCaches() {
        boolean allDisconnected = true;
        int size = AppState.getVector(1241).size();
        while (true) {
            size--;
            if (size < 0) {
                break;
            } else if (getAccountByIndex(size).isConnecting()) {
                allDisconnected = false;
            }
        }
        int size2 = AppState.getVector(1241).size();
        while (true) {
            size2--;
            if (size2 < 0) {
                return;
            }
            Account account = getAccountByIndex(size2);
            if (account.isConnecting()) {
                if (!allDisconnected) {
                    account.disconnect();
                }
            } else if (allDisconnected) {
                account.connect(0);
            }
        }
    }

    /* renamed from: W */
    public static final Vector getAllAccountsList() {
        Vector result = NetworkUtils.newVector();
        Vector allAccounts = AppState.getVector(1241);
        int accountIdx = Utils.vectorSize(allAccounts);
        while (true) {
            accountIdx--;
            if (accountIdx < 0) {
                return result;
            }
            Vector contacts = ((Account) allAccounts.elementAt(accountIdx)).getAllContacts();
            int contactIdx = Utils.vectorSize(contacts);
            while (true) {
                contactIdx--;
                if (contactIdx < 0) {
                    break;
                }
                result.addElement(contacts.elementAt(contactIdx));
            }
            NetworkUtils.releaseVector(contacts);
        }
    }

    /* renamed from: d */
    public static final Vector getAccountConversations(Account targetAccount) {
        if (targetAccount == null) {
            Vector result = NetworkUtils.newVector();
            int count = getActiveAccountCount();
            while (true) {
                count--;
                if (count < 0) {
                    return result;
                }
                Account account = getAccountByIndex(count);
                int size = account.groups.size();
                while (true) {
                    size--;
                    if (size < 0) {
                        break;
                    }
                    result.addElement(account.getGroup(size));
                }
            }
        } else {
            Vector result2 = NetworkUtils.newVector();
            int count2 = getActiveAccountCount();
            while (true) {
                count2--;
                if (count2 < 0) {
                    return result2;
                }
                Account account2 = getAccountByIndex(count2);
                if (account2 == targetAccount) {
                    int size2 = account2.groups.size();
                    while (true) {
                        size2--;
                        if (size2 < 0) {
                            break;
                        }
                        ContactGroup group = account2.getGroup(size2);
                        if (group != account2.defaultGroup && group != account2.onlineGroup && group != account2.offlineGroup && group != account2.blockedGroup) {
                            result2.addElement(group);
                        }
                    }
                }
            }
        }
    }

    /* renamed from: a */
    private static final MrimAccount findMrimAccount(Vector vector, int i) {
        return (MrimAccount) vector.elementAt(i);
    }

    /* renamed from: X */
    public static final Vector getMapContacts() {
        Vector result = NetworkUtils.newVector();
        Vector mrimAccounts = getMrimAccountList();
        int size = mrimAccounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return result;
            }
            Vector contacts = findMrimAccount(mrimAccounts, size).getAllContacts();
            int size2 = contacts.size();
            while (true) {
                size2--;
                if (size2 < 0) {
                    break;
                }
                MrimContact contact = (MrimContact) contacts.elementAt(size2);
                if (contact.hasVCard()) {
                    result.addElement(contact);
                }
            }
            NetworkUtils.releaseVector(contacts);
        }
    }

    /* renamed from: Y */
    public static final Vector getMapProfiles() {
        Vector result = NetworkUtils.newVector();
        Vector mrimAccounts = getMrimAccountList();
        int size = mrimAccounts.size();
        while (true) {
            size--;
            if (size < 0) {
                return result;
            }
            MrimAccount account = findMrimAccount(mrimAccounts, size);
            if (account.accountProfile.hasCoordinates()) {
                result.addElement(account);
            }
        }
    }

    /* renamed from: a */
    public static final void setFormFields(String str, String str2, String str3, String str4, String str5) {
        AppState.setObject(1240, (Object) str5);
        AppState.setFromBuffer(1239, Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(Utils.appendParam(NetworkUtils.newStringBuffer(), 262572, str), 262576, str2), 524724, str3), 590268, str4), 524741, str5));
        setTimer(13, computeInitialState());
    }

    /* renamed from: aj */
    private static final int computeInitialState() {
        return AppState.getBytes(1004) != null ? 60000 : 300000;
    }

    /* renamed from: Z */
    public static final String[] getLanguageOptions() {
        if (!AppState.getBool(1468)) {
            setFormFields(null, null, null, null, null);
        } else if (checkTimer(13, computeInitialState())) {
            AppState.clearIndex(1239);
        }
        String formData = AppState.getString(1239);
        if (formData == null) {
            return null;
        }
        String[] strArr = new String[2];
        strArr[0] = formData;
        String langOption = AppState.getString(1240);
        strArr[1] = langOption != null ? langOption : AppState.getString(308);
        return strArr;
    }

    /* renamed from: a */
    public static final void dispatchCommand(Object obj, int i, int i2) {
        Object obj2 = new Object();
        appLock = obj2;
        synchronized (obj2) {
            AppState.init(obj);
            AppState.clearRange(1022, 1023);
            AppState.pool[1373] = NetworkUtils.newVector();
            AppState.pool[1272] = NetworkUtils.newVector();
            ScreenManager.initializeFonts();
            AppState.pool[1243] = NetworkUtils.newVector();
            AppState.pool[1244] = NetworkUtils.newVector();
            AppState.pool[1358] = NetworkUtils.newVector();
            AppState.pool[1359] = NetworkUtils.newVector();
            new AsyncTask(3);
            loadSavedAccounts();
            AppState.pool[1247] = NetworkUtils.newVector();
            processKeyRepeat();
            AppState.pool[1242] = NetworkUtils.newVector();
            ResourceManager.resetClock();
            ResourceManager.initMathTables();
            AppState.setInt(2, 0);
            AppState.setInt(3, 0);
            AppState.setInt(10, 0);
            AppState.setInt(11, 0);
            AppState.setInt(18, 0);
            AppState.setInt(19, 0);
            AppState.setInt(26, 0);
            AppState.setInt(27, 0);
            AppState.pool[1402] = NetworkUtils.newVector();
            XmppMailRuProtocol.calculateCacheSize();
            AppState.pool[1371] = new MainCanvas(i, i2);
            AppState.clearRange(332, 333);
            TabBar.initialize();
            AppState.pool[430] = Utils.bytesToInts(AppState.getBytes(430));
            AppState.pool[1357] = new byte[1];
            try {
                computeLayoutParam(1004);
                computeLayoutParam(1005);
                getScreenMode3();
                getScreenMode4();
            } catch (Throwable unused) {
                AppState.clearRange(1004, 1007);
            }
            setTimer(0, getSessionTimestamp());
            AppState.addInt(291, 1);
            AppState.saveDelta(true);
            if (AppState.getBool(217)) {
                showSettingsScreen();
            } else {
                int accountCount = getActiveAccountCount();
                if (accountCount == 0) {
                    ScreenManager.pushScreen(ScreenManager.createScreen(4381));
                    refreshContactList();
                } else {
                    while (true) {
                        accountCount--;
                        if (accountCount < 0) {
                            break;
                        } else {
                            setCurrentAccount(getAccountByIndex(accountCount));
                        }
                    }
                    ContactListManager.showContactList();
                    refreshContactList();
                }
            }
            new AsyncTask(13);
            new AsyncTask(0);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Removed duplicated region for block: B:286:0x0b19  */
    /* JADX WARN: Removed duplicated region for block: B:635:0x1396  */
    /* JADX WARN: Removed duplicated region for block: B:646:0x13cd  */
    /* JADX WARN: Removed duplicated region for block: B:714:0x151f  */
    /* JADX WARN: Removed duplicated region for block: B:755:0x15ff  */
    /* JADX WARN: Removed duplicated region for block: B:794:0x16cc  */
    /* JADX WARN: Removed duplicated region for block: B:797:0x16d3  */
    /* JADX WARN: Removed duplicated region for block: B:804:0x16e9 A[Catch: all -> 0x1e12, Throwable -> 0x1f03, TryCatch #0 {, blocks: (B:5:0x0009, B:7:0x000f, B:9:0x001b, B:11:0x0023, B:13:0x0031, B:14:0x0047, B:15:0x0048, B:16:0x0061, B:23:0x006d, B:20:0x0069, B:22:0x006c, B:24:0x0071, B:39:0x00ef, B:25:0x0082, B:26:0x008e, B:28:0x0096, B:34:0x00c2, B:36:0x00d7, B:37:0x00e1, B:30:0x00a0, B:32:0x00b5, B:38:0x00e9, B:41:0x00f7, B:45:0x0131, B:42:0x0110, B:44:0x012c, B:47:0x0139, B:49:0x013f, B:51:0x0149, B:53:0x0150, B:54:0x016b, B:56:0x0179, B:64:0x01a9, B:65:0x01b4, B:70:0x04a7, B:72:0x04b5, B:74:0x04c9, B:75:0x04d5, B:77:0x04dd, B:79:0x04e3, B:81:0x04ec, B:83:0x04f6, B:85:0x0500, B:87:0x050a, B:89:0x0514, B:96:0x0532, B:91:0x0521, B:97:0x0535, B:98:0x0539, B:100:0x0544, B:102:0x0550, B:104:0x0559, B:105:0x0589, B:106:0x05a8, B:107:0x05be, B:108:0x05d4, B:109:0x05ea, B:110:0x05fd, B:111:0x0617, B:113:0x0623, B:114:0x0626, B:116:0x062f, B:118:0x063a, B:122:0x066e, B:119:0x0655, B:121:0x0668, B:124:0x0676, B:125:0x0682, B:127:0x068b, B:128:0x068f, B:130:0x0695, B:134:0x06a6, B:142:0x06c5, B:144:0x06e0, B:145:0x06ee, B:147:0x06fc, B:151:0x070b, B:149:0x0704, B:164:0x0743, B:179:0x0783, B:181:0x0792, B:184:0x07aa, B:188:0x07c7, B:193:0x07db, B:201:0x07f6, B:203:0x0802, B:206:0x0812, B:208:0x0833, B:210:0x0871, B:209:0x0863, B:212:0x087b, B:214:0x08b5, B:213:0x0890, B:216:0x08bf, B:220:0x08ed, B:217:0x08cb, B:219:0x08e3, B:222:0x08f7, B:226:0x0925, B:223:0x0903, B:225:0x091b, B:228:0x092f, B:229:0x0937, B:231:0x096f, B:230:0x094a, B:235:0x0982, B:237:0x0991, B:240:0x09a1, B:241:0x09bc, B:261:0x0a55, B:242:0x09d0, B:244:0x09f7, B:248:0x0a08, B:250:0x0a10, B:252:0x0a1a, B:253:0x0a20, B:254:0x0a25, B:256:0x0a31, B:257:0x0a36, B:260:0x0a48, B:263:0x0a5f, B:268:0x0a70, B:270:0x0a89, B:272:0x0aab, B:274:0x0abb, B:278:0x0aee, B:275:0x0ad0, B:277:0x0ae3, B:282:0x0afc, B:285:0x0b0e, B:288:0x0b1f, B:290:0x0b2c, B:291:0x0b34, B:299:0x0b58, B:306:0x0b76, B:307:0x0b85, B:309:0x0b8f, B:311:0x0ba6, B:321:0x0bd0, B:323:0x0bda, B:325:0x0be5, B:327:0x0bee, B:329:0x0bfd, B:331:0x0c0c, B:332:0x0c15, B:346:0x0c5d, B:333:0x0c23, B:345:0x0c5a, B:348:0x0c65, B:350:0x0c6f, B:358:0x0c9c, B:360:0x0ca8, B:363:0x0cb8, B:379:0x0d42, B:364:0x0ccc, B:366:0x0d00, B:371:0x0d10, B:373:0x0d19, B:376:0x0d2d, B:378:0x0d36, B:388:0x0d67, B:390:0x0d76, B:393:0x0d86, B:395:0x0d9e, B:405:0x0de9, B:396:0x0dab, B:398:0x0dc5, B:400:0x0dd1, B:402:0x0dda, B:403:0x0ddf, B:404:0x0de4, B:411:0x0e02, B:413:0x0e0e, B:416:0x0e1e, B:417:0x0e45, B:418:0x0e73, B:421:0x0e9b, B:424:0x0ea4, B:426:0x0eb8, B:427:0x0ed6, B:429:0x0efc, B:431:0x0f1a, B:430:0x0f0b, B:434:0x0f24, B:435:0x0f47, B:437:0x0f52, B:438:0x0f5b, B:440:0x0f72, B:442:0x0f7e, B:445:0x0f8e, B:447:0x0f9a, B:452:0x0fb4, B:473:0x100c, B:482:0x1032, B:524:0x10dc, B:533:0x1104, B:547:0x1141, B:549:0x114f, B:551:0x1159, B:555:0x116b, B:557:0x117c, B:558:0x118b, B:561:0x1196, B:575:0x11d2, B:577:0x11e2, B:578:0x11f7, B:580:0x1206, B:581:0x1248, B:583:0x1255, B:585:0x125d, B:586:0x126f, B:588:0x1279, B:595:0x1292, B:986:0x1db5, B:988:0x1dbd, B:990:0x1dc6, B:991:0x1dd4, B:993:0x1de2, B:995:0x1deb, B:997:0x1df6, B:999:0x1dff, B:1000:0x1e06, B:598:0x129d, B:63:0x01a1, B:59:0x018e, B:600:0x12a5, B:602:0x12ac, B:603:0x12b4, B:604:0x12e8, B:606:0x12f0, B:608:0x12f9, B:609:0x12fd, B:615:0x1338, B:617:0x1341, B:619:0x134d, B:621:0x1358, B:623:0x1360, B:718:0x152c, B:730:0x156f, B:733:0x157e, B:736:0x158a, B:739:0x1598, B:742:0x15a5, B:744:0x15af, B:745:0x15b5, B:747:0x15bf, B:748:0x15c9, B:750:0x15d2, B:754:0x15fb, B:721:0x153f, B:726:0x1559, B:628:0x1374, B:630:0x137f, B:632:0x1387, B:637:0x139d, B:639:0x13a9, B:644:0x13c0, B:648:0x13d4, B:652:0x13e2, B:657:0x13f9, B:664:0x1415, B:667:0x1425, B:670:0x1433, B:673:0x1441, B:676:0x1456, B:679:0x1466, B:681:0x1471, B:687:0x1488, B:682:0x1478, B:690:0x149c, B:693:0x14b4, B:697:0x14c5, B:700:0x14d8, B:702:0x14de, B:704:0x14e7, B:708:0x14ff, B:710:0x1505, B:712:0x150e, B:756:0x1602, B:757:0x1608, B:758:0x160e, B:759:0x1618, B:761:0x1623, B:763:0x1631, B:765:0x1647, B:767:0x164f, B:769:0x165c, B:774:0x166d, B:778:0x1688, B:780:0x168e, B:784:0x16a6, B:801:0x16de, B:802:0x16e1, B:804:0x16e9, B:806:0x16f2, B:808:0x1727, B:810:0x1733, B:812:0x173d, B:818:0x1758, B:820:0x1762, B:822:0x1770, B:826:0x17a2, B:828:0x17ab, B:830:0x17b5, B:833:0x17c4, B:837:0x17d7, B:839:0x17e2, B:841:0x17ea, B:842:0x17f1, B:843:0x180c, B:844:0x1818, B:846:0x1824, B:848:0x1843, B:858:0x1863, B:857:0x185c, B:788:0x16b7, B:789:0x16ba, B:770:0x1662, B:860:0x187b, B:862:0x1891, B:864:0x18a2, B:866:0x18c4, B:868:0x18cc, B:869:0x18da, B:871:0x1906, B:872:0x1941, B:874:0x1961, B:875:0x1967, B:877:0x197a, B:878:0x198a, B:880:0x1992, B:881:0x1998, B:883:0x199f, B:889:0x19cf, B:891:0x19d6, B:893:0x19e6, B:895:0x1a13, B:898:0x1a2f, B:900:0x1a36, B:901:0x1a44, B:903:0x1a4b, B:905:0x1a57, B:906:0x1aa4, B:907:0x1abc, B:909:0x1ac3, B:910:0x1ad6, B:911:0x1af8, B:912:0x1afe, B:914:0x1b4b, B:915:0x1b5b, B:924:0x1ba1, B:916:0x1b64, B:923:0x1b95, B:919:0x1b83, B:926:0x1ba8, B:928:0x1bb8, B:929:0x1bc8, B:933:0x1bdd, B:937:0x1bf0, B:938:0x1bfc, B:939:0x1c09, B:940:0x1c10, B:941:0x1c17, B:943:0x1c2e, B:945:0x1c35, B:949:0x1c4d, B:951:0x1c65, B:955:0x1c7e, B:957:0x1c8d, B:961:0x1c9e, B:967:0x1d04, B:962:0x1cdb, B:964:0x1cef, B:969:0x1d0b, B:973:0x1d23, B:975:0x1d34, B:974:0x1d2f, B:976:0x1d53, B:979:0x1d61, B:981:0x1d84, B:985:0x1d9c, B:1002:0x1e0e), top: B:1058:0x0009, outer: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:833:0x17c4 A[Catch: all -> 0x1e12, Throwable -> 0x1f03, TryCatch #0 {, blocks: (B:5:0x0009, B:7:0x000f, B:9:0x001b, B:11:0x0023, B:13:0x0031, B:14:0x0047, B:15:0x0048, B:16:0x0061, B:23:0x006d, B:20:0x0069, B:22:0x006c, B:24:0x0071, B:39:0x00ef, B:25:0x0082, B:26:0x008e, B:28:0x0096, B:34:0x00c2, B:36:0x00d7, B:37:0x00e1, B:30:0x00a0, B:32:0x00b5, B:38:0x00e9, B:41:0x00f7, B:45:0x0131, B:42:0x0110, B:44:0x012c, B:47:0x0139, B:49:0x013f, B:51:0x0149, B:53:0x0150, B:54:0x016b, B:56:0x0179, B:64:0x01a9, B:65:0x01b4, B:70:0x04a7, B:72:0x04b5, B:74:0x04c9, B:75:0x04d5, B:77:0x04dd, B:79:0x04e3, B:81:0x04ec, B:83:0x04f6, B:85:0x0500, B:87:0x050a, B:89:0x0514, B:96:0x0532, B:91:0x0521, B:97:0x0535, B:98:0x0539, B:100:0x0544, B:102:0x0550, B:104:0x0559, B:105:0x0589, B:106:0x05a8, B:107:0x05be, B:108:0x05d4, B:109:0x05ea, B:110:0x05fd, B:111:0x0617, B:113:0x0623, B:114:0x0626, B:116:0x062f, B:118:0x063a, B:122:0x066e, B:119:0x0655, B:121:0x0668, B:124:0x0676, B:125:0x0682, B:127:0x068b, B:128:0x068f, B:130:0x0695, B:134:0x06a6, B:142:0x06c5, B:144:0x06e0, B:145:0x06ee, B:147:0x06fc, B:151:0x070b, B:149:0x0704, B:164:0x0743, B:179:0x0783, B:181:0x0792, B:184:0x07aa, B:188:0x07c7, B:193:0x07db, B:201:0x07f6, B:203:0x0802, B:206:0x0812, B:208:0x0833, B:210:0x0871, B:209:0x0863, B:212:0x087b, B:214:0x08b5, B:213:0x0890, B:216:0x08bf, B:220:0x08ed, B:217:0x08cb, B:219:0x08e3, B:222:0x08f7, B:226:0x0925, B:223:0x0903, B:225:0x091b, B:228:0x092f, B:229:0x0937, B:231:0x096f, B:230:0x094a, B:235:0x0982, B:237:0x0991, B:240:0x09a1, B:241:0x09bc, B:261:0x0a55, B:242:0x09d0, B:244:0x09f7, B:248:0x0a08, B:250:0x0a10, B:252:0x0a1a, B:253:0x0a20, B:254:0x0a25, B:256:0x0a31, B:257:0x0a36, B:260:0x0a48, B:263:0x0a5f, B:268:0x0a70, B:270:0x0a89, B:272:0x0aab, B:274:0x0abb, B:278:0x0aee, B:275:0x0ad0, B:277:0x0ae3, B:282:0x0afc, B:285:0x0b0e, B:288:0x0b1f, B:290:0x0b2c, B:291:0x0b34, B:299:0x0b58, B:306:0x0b76, B:307:0x0b85, B:309:0x0b8f, B:311:0x0ba6, B:321:0x0bd0, B:323:0x0bda, B:325:0x0be5, B:327:0x0bee, B:329:0x0bfd, B:331:0x0c0c, B:332:0x0c15, B:346:0x0c5d, B:333:0x0c23, B:345:0x0c5a, B:348:0x0c65, B:350:0x0c6f, B:358:0x0c9c, B:360:0x0ca8, B:363:0x0cb8, B:379:0x0d42, B:364:0x0ccc, B:366:0x0d00, B:371:0x0d10, B:373:0x0d19, B:376:0x0d2d, B:378:0x0d36, B:388:0x0d67, B:390:0x0d76, B:393:0x0d86, B:395:0x0d9e, B:405:0x0de9, B:396:0x0dab, B:398:0x0dc5, B:400:0x0dd1, B:402:0x0dda, B:403:0x0ddf, B:404:0x0de4, B:411:0x0e02, B:413:0x0e0e, B:416:0x0e1e, B:417:0x0e45, B:418:0x0e73, B:421:0x0e9b, B:424:0x0ea4, B:426:0x0eb8, B:427:0x0ed6, B:429:0x0efc, B:431:0x0f1a, B:430:0x0f0b, B:434:0x0f24, B:435:0x0f47, B:437:0x0f52, B:438:0x0f5b, B:440:0x0f72, B:442:0x0f7e, B:445:0x0f8e, B:447:0x0f9a, B:452:0x0fb4, B:473:0x100c, B:482:0x1032, B:524:0x10dc, B:533:0x1104, B:547:0x1141, B:549:0x114f, B:551:0x1159, B:555:0x116b, B:557:0x117c, B:558:0x118b, B:561:0x1196, B:575:0x11d2, B:577:0x11e2, B:578:0x11f7, B:580:0x1206, B:581:0x1248, B:583:0x1255, B:585:0x125d, B:586:0x126f, B:588:0x1279, B:595:0x1292, B:986:0x1db5, B:988:0x1dbd, B:990:0x1dc6, B:991:0x1dd4, B:993:0x1de2, B:995:0x1deb, B:997:0x1df6, B:999:0x1dff, B:1000:0x1e06, B:598:0x129d, B:63:0x01a1, B:59:0x018e, B:600:0x12a5, B:602:0x12ac, B:603:0x12b4, B:604:0x12e8, B:606:0x12f0, B:608:0x12f9, B:609:0x12fd, B:615:0x1338, B:617:0x1341, B:619:0x134d, B:621:0x1358, B:623:0x1360, B:718:0x152c, B:730:0x156f, B:733:0x157e, B:736:0x158a, B:739:0x1598, B:742:0x15a5, B:744:0x15af, B:745:0x15b5, B:747:0x15bf, B:748:0x15c9, B:750:0x15d2, B:754:0x15fb, B:721:0x153f, B:726:0x1559, B:628:0x1374, B:630:0x137f, B:632:0x1387, B:637:0x139d, B:639:0x13a9, B:644:0x13c0, B:648:0x13d4, B:652:0x13e2, B:657:0x13f9, B:664:0x1415, B:667:0x1425, B:670:0x1433, B:673:0x1441, B:676:0x1456, B:679:0x1466, B:681:0x1471, B:687:0x1488, B:682:0x1478, B:690:0x149c, B:693:0x14b4, B:697:0x14c5, B:700:0x14d8, B:702:0x14de, B:704:0x14e7, B:708:0x14ff, B:710:0x1505, B:712:0x150e, B:756:0x1602, B:757:0x1608, B:758:0x160e, B:759:0x1618, B:761:0x1623, B:763:0x1631, B:765:0x1647, B:767:0x164f, B:769:0x165c, B:774:0x166d, B:778:0x1688, B:780:0x168e, B:784:0x16a6, B:801:0x16de, B:802:0x16e1, B:804:0x16e9, B:806:0x16f2, B:808:0x1727, B:810:0x1733, B:812:0x173d, B:818:0x1758, B:820:0x1762, B:822:0x1770, B:826:0x17a2, B:828:0x17ab, B:830:0x17b5, B:833:0x17c4, B:837:0x17d7, B:839:0x17e2, B:841:0x17ea, B:842:0x17f1, B:843:0x180c, B:844:0x1818, B:846:0x1824, B:848:0x1843, B:858:0x1863, B:857:0x185c, B:788:0x16b7, B:789:0x16ba, B:770:0x1662, B:860:0x187b, B:862:0x1891, B:864:0x18a2, B:866:0x18c4, B:868:0x18cc, B:869:0x18da, B:871:0x1906, B:872:0x1941, B:874:0x1961, B:875:0x1967, B:877:0x197a, B:878:0x198a, B:880:0x1992, B:881:0x1998, B:883:0x199f, B:889:0x19cf, B:891:0x19d6, B:893:0x19e6, B:895:0x1a13, B:898:0x1a2f, B:900:0x1a36, B:901:0x1a44, B:903:0x1a4b, B:905:0x1a57, B:906:0x1aa4, B:907:0x1abc, B:909:0x1ac3, B:910:0x1ad6, B:911:0x1af8, B:912:0x1afe, B:914:0x1b4b, B:915:0x1b5b, B:924:0x1ba1, B:916:0x1b64, B:923:0x1b95, B:919:0x1b83, B:926:0x1ba8, B:928:0x1bb8, B:929:0x1bc8, B:933:0x1bdd, B:937:0x1bf0, B:938:0x1bfc, B:939:0x1c09, B:940:0x1c10, B:941:0x1c17, B:943:0x1c2e, B:945:0x1c35, B:949:0x1c4d, B:951:0x1c65, B:955:0x1c7e, B:957:0x1c8d, B:961:0x1c9e, B:967:0x1d04, B:962:0x1cdb, B:964:0x1cef, B:969:0x1d0b, B:973:0x1d23, B:975:0x1d34, B:974:0x1d2f, B:976:0x1d53, B:979:0x1d61, B:981:0x1d84, B:985:0x1d9c, B:1002:0x1e0e), top: B:1058:0x0009, outer: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:859:0x1878  */
    /* JADX WARN: Removed duplicated region for block: B:953:0x1c78  */
    /* renamed from: aa */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final void onSoftKeyPressed() {
        boolean z;
        boolean z2;
        int i;
        int i2 = 0;
        Object objM179a;
        boolean z3;
        boolean z4 = false;
        int[] iArrM190r;
        int[] iArrM191s;
        Screen c0013amM66b;
        int iM1181a = 0;
        Message c0026azM1415b;
        TextBox textBoxM1028h;
        int i3 = 0;
        MrimAccount c0028ba;
        ChatRoom c0052wM745h;
        Message c0026azM1415b2;
        boolean z5;
        while (!isShuttingDown) {
            synchronized (appLock) {
                if (!isShuttingDown) {
                    AppState.updateTime();
                    ResourceManager.updateClock();
                    if (!MainCanvas.pointerDragged && MainCanvas.pointerDownTime != 0 && System.currentTimeMillis() - MainCanvas.pointerDownTime > 600) {
                        int i4 = MainCanvas.pointerDownX;
                        int i5 = MainCanvas.pointerDownY;
                        Vector vectorM614m = AppState.getVector(1266);
                        synchronized (vectorM614m) {
                            vectorM614m.addElement(new int[]{8, i4, i5});
                        }
                        MainCanvas.pointerDownTime = 0L;
                    }
                    Vector vectorM614m2 = AppState.getVector(1241);
                    int size = vectorM614m2.size();
                    while (true) {
                        size--;
                        if (size < 0) {
                            int iM586d = AppState.getInt(1531);
                            Vector vectorM614m3 = AppState.getVector(1242);
                            int size2 = vectorM614m3.size();
                            while (true) {
                                size2--;
                                if (size2 < 0) {
                                    if (needsLayoutUpdate && ScreenManager.getCurrentScreen().screenId == 4 && isTimerType(1)) {
                                        needsLayoutUpdate = false;
                                        AppState.getString(1237);
                                        ContactListManager.refreshList();
                                        AppState.clearIndex(1237);
                                        setTimer(1, 1000L);
                                    }
                                    Object objM524a = Utils.dequeue(AppState.getVector(1266));
                                    if (objM524a == null) {
                                        Screen c0013amM66b2 = ScreenManager.getCurrentScreen();
                                        MenuItem c0032cM69e = ScreenManager.getCurrentMenuItem();
                                        Object obj = c0032cM69e == null ? null : c0032cM69e.data;
                                        String str = c0032cM69e == null ? null : c0032cM69e.title;
                                        int iM338l = 0;
                                        switch (ScreenManager.getCurrentScreen().screenId) {
                                            case 1:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 2:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 3:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 4:
                                                iM1181a = ContactListManager.updateContextMenu(c0013amM66b2, obj);
                                                iM338l = iM1181a;
                                                break;
                                            case 5:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 6:
                                                long jCurrentTimeMillis = System.currentTimeMillis();
                                                if (jCurrentTimeMillis - AppState.getLong(1556) > 45) {
                                                    AppState.setLong(1556, jCurrentTimeMillis);
                                                }
                                                if (isTimerType(10) && MapRenderer.crosshairVisible) {
                                                    if (AppState.getBool(276)) {
                                                        if ((MapRenderer.currentLon < VCard.staticTs1 || MapRenderer.currentLon > VCard.staticTs3 || MapRenderer.currentLat > VCard.staticTs4 || MapRenderer.currentLat < VCard.staticTs2 || ((long) AppState.getInt(39)) != VCard.staticTs5) && AppState.getBool(280)) {
                                                            IOUtils.requestNearbyPeople();
                                                        }
                                                    }
                                                    MapRenderer.setCrosshairVisible(false);
                                                }
                                                int iM586d2 = AppState.getInt(1564);
                                                if (iM586d2 >= 0 && AppState.getLong(1556) == jCurrentTimeMillis && !AppState.getBool(1553)) {
                                                    AppState.setLong(1558, MapRenderer.currentLon);
                                                    AppState.setLong(1560, MapRenderer.currentLat);
                                                    int iM586d3 = AppState.getInt(39);
                                                    long jM315d = (getZoomNumerator(iM586d3) / getZoomDenominator(iM586d3)) * 9;
                                                    switch (iM586d2) {
                                                        case 0:
                                                            AppState.setLong(1558, AppState.getLong(1558) + jM315d);
                                                            break;
                                                        case 1:
                                                            AppState.setLong(1558, AppState.getLong(1558) - jM315d);
                                                            break;
                                                        case 2:
                                                            AppState.setLong(1560, AppState.getLong(1560) + jM315d);
                                                            break;
                                                        case 3:
                                                            AppState.setLong(1560, AppState.getLong(1560) - jM315d);
                                                            break;
                                                    }
                                                    MapRenderer.setPosition(AppState.getLong(1558), AppState.getLong(1560));
                                                    setTimer(10, 500L);
                                                    MapRenderer.resetInteraction();
                                                }
                                                if (AppState.getLong(1556) == jCurrentTimeMillis) {
                                                    MapRenderer.render();
                                                }
                                                if (AppState.getBool(277) && checkTimer(7, 300000L)) {
                                                    setTimer(7, 300000L);
                                                    StringUtils.clearSatelliteTiles();
                                                    Vector vectorM614m4 = AppState.getVector(1383);
                                                    int size3 = vectorM614m4.size();
                                                    while (true) {
                                                        size3--;
                                                        if (size3 < 0) {
                                                            MapRenderer.needsRedraw = true;
                                                            new AsyncTask(6);
                                                        } else if (3 == ((ResourceManager) vectorM614m4.elementAt(size3)).tileType) {
                                                            vectorM614m4.removeElementAt(size3);
                                                        }
                                                    }
                                                }
                                                if (AppState.getBool(1553)) {
                                                    needsRepaint = true;
                                                }
                                                if (MapRenderer.tapConsumed) {
                                                    MapRenderer.tapConsumed = false;
                                                    z5 = true;
                                                } else {
                                                    z5 = false;
                                                }
                                                iM338l = z5 ? 113 : ConnectionThread.showMapSearchResults();
                                                break;
                                            case 7:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 8:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 9:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 10:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 11:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 13:
                                                Object[] objArr = (Object[]) AppState.pool[1271];
                                                Object obj2 = objArr[0];
                                                if (obj2 == null) {
                                                    String str2 = (String) objArr[20];
                                                    if ((str2 != null && Utils.parseInt((Object) str2) == 0) || objArr[3] != null) {
                                                        iM1181a = NetworkUtils.handleRegSubmit(objArr);
                                                    }
                                                    iM338l = iM1181a;
                                                    break;
                                                } else {
                                                    showNotification(StringUtils.concatKeyObj(506, obj2));
                                                }
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 14:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 15:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 16:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 17:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 18:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 19:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 20:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 21:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 22:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 23:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 24:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 25:
                                                iM338l = AppState.pool[1291] != null ? 122 : 0;
                                                break;
                                            case 26:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 27:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 28:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 29:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 30:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 32:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 33:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 34:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 35:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 36:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 37:
                                                Object[] objArrM1156c = ConnectionThread.getAsyncResult(IOUtils.pollAsyncResult());
                                                if (objArrM1156c != null) {
                                                    int iM586d4 = AppState.getInt(1512);
                                                    int iM818c = IOUtils.validateJsonResponse(objArrM1156c);
                                                    if (iM818c != 0) {
                                                        iM1181a = iM818c;
                                                    } else {
                                                        ((MrimAccount) AppState.getAccount()).parseChatRoomsFromJson(IOUtils.getJsonPayload());
                                                        iM1181a = iM586d4;
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 38:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 39:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 40:
                                                Contact abstractC0041lM611g = AppState.getCurrentContact();
                                                iM338l = (abstractC0041lM611g.flags != 0) || abstractC0041lM611g.dirty ? 40 : 0;
                                                break;
                                            case 41:
                                                Object[] objArrM1156c2 = ConnectionThread.getAsyncResult(IOUtils.pollAsyncResult());
                                                if (objArrM1156c2 != null) {
                                                    int iM818c2 = IOUtils.validateJsonResponse(objArrM1156c2);
                                                    if (iM818c2 != 0) {
                                                        iM1181a = iM818c2;
                                                    } else {
                                                        Object objM819l = IOUtils.getJsonPayload();
                                                        MrimAccount c0028ba2 = (MrimAccount) AppState.getAccount();
                                                        ChatRoom c0052wM745h2 = c0028ba2.findChatRoomById(AppState.getInt(1513));
                                                        if (c0052wM745h2 != c0028ba2.getLastChatRoom()) {
                                                            c0052wM745h2.subject = JsonParser.getStringValue(objM819l, AppState.getString(591768));
                                                            c0052wM745h2.messageIds.removeAllElements();
                                                            Enumeration enumerationElements = ((Vector) JsonParser.getValue(objM819l, AppState.getString(526244))).elements();
                                                            while (enumerationElements.hasMoreElements()) {
                                                                c0052wM745h2.messageIds.addElement(enumerationElements.nextElement());
                                                            }
                                                            Enumeration enumerationElements2 = ((Vector) JsonParser.getValue(objM819l, AppState.getString(329636))).elements();
                                                            while (enumerationElements2.hasMoreElements()) {
                                                                Message c0026az = new Message((Hashtable) enumerationElements2.nextElement());
                                                                c0052wM745h2.messages.put(c0026az.from, c0026az);
                                                            }
                                                            Enumeration enumerationKeys = c0052wM745h2.messages.keys();
                                                            while (enumerationKeys.hasMoreElements()) {
                                                                String str3 = (String) enumerationKeys.nextElement();
                                                                if (!c0052wM745h2.messageIds.contains(str3)) {
                                                                    c0052wM745h2.messages.remove(str3);
                                                                }
                                                            }
                                                            Enumeration enumerationElements3 = c0052wM745h2.readMessages.elements();
                                                            while (enumerationElements3.hasMoreElements()) {
                                                                String str4 = (String) enumerationElements3.nextElement();
                                                                if (!c0052wM745h2.messageIds.contains(str4)) {
                                                                    c0052wM745h2.readMessages.removeElement(str4);
                                                                }
                                                            }
                                                            c0052wM745h2.isInitialized = false;
                                                        } else {
                                                            Enumeration enumerationElements4 = ((Vector) objM819l).elements();
                                                            while (enumerationElements4.hasMoreElements()) {
                                                                Message c0026az2 = new Message((Hashtable) enumerationElements4.nextElement());
                                                                c0052wM745h2.messages.put(c0026az2.from, c0026az2);
                                                            }
                                                        }
                                                        iM1181a = 43;
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 42:
                                                Object[] objArrM1156c3 = ConnectionThread.getAsyncResult(IOUtils.pollAsyncResult());
                                                if (objArrM1156c3 != null) {
                                                    int iM818c3 = IOUtils.validateJsonResponse(objArrM1156c3);
                                                    if (iM818c3 != 0) {
                                                        iM1181a = iM818c3;
                                                    } else {
                                                        Object objM819l2 = IOUtils.getJsonPayload();
                                                        MrimAccount c0028ba3 = (MrimAccount) AppState.getAccount();
                                                        int size4 = ((Vector) objM819l2).size();
                                                        for (int i6 = 0; i6 < size4; i6++) {
                                                            Enumeration enumerationKeys2 = ((Hashtable) JsonParser.getVectorElement(objM819l2, i6)).keys();
                                                            while (enumerationKeys2.hasMoreElements()) {
                                                                String str5 = (String) enumerationKeys2.nextElement();
                                                                ChatRoom c0052wM747i = c0028ba3.findChatRoomByName(str5);
                                                                ChatRoom c0052wM745h3 = c0028ba3.findChatRoomById(AppState.getInt(1527));
                                                                if (c0052wM747i != null && (c0026azM1415b2 = c0052wM747i.getMessage(str5)) != null && c0052wM745h3 != null) {
                                                                    if (c0026azM1415b2.hasFlag(4)) {
                                                                        if (c0052wM745h3 == c0028ba3.findDefaultChatRoom()) {
                                                                            c0026azM1415b2.setFlag(4, false);
                                                                        }
                                                                        c0052wM747i.decrementUnread();
                                                                    }
                                                                    c0052wM747i.decrementMembers();
                                                                    if (!c0026azM1415b2.isRead()) {
                                                                        c0052wM745h3.incrementUnread();
                                                                    }
                                                                    c0052wM745h3.memberCount++;
                                                                }
                                                                if (c0052wM747i != c0052wM745h3) {
                                                                    c0028ba3.removeUserFromChatRooms(str5);
                                                                    c0052wM745h3.setActive(false);
                                                                }
                                                            }
                                                        }
                                                        iM1181a = 43;
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 43:
                                                AppState.setInt(1514, c0013amM66b2.scrollOffset);
                                                AppState.setObject(1345, (Object) str);
                                                if (str == null || (c0052wM745h = (c0028ba = (MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513))) == c0028ba.getLastChatRoom() || str.equals(c0052wM745h.subject)) {
                                                    i3 = 0;
                                                    iM338l = i3;
                                                    break;
                                                } else {
                                                    Object obj3 = null;
                                                    Enumeration enumerationElements5 = c0052wM745h.messageIds.elements();
                                                    while (enumerationElements5.hasMoreElements()) {
                                                        Hashtable hashtable = c0052wM745h.messages;
                                                        Object objNextElement = enumerationElements5.nextElement();
                                                        if (hashtable.containsKey(objNextElement)) {
                                                            obj3 = c0052wM745h.messages.get(objNextElement);
                                                        }
                                                    }
                                                    if (str == (obj3 != null ? ((Message) obj3).from : null)) {
                                                        c0052wM745h.setActive(true);
                                                        i3 = 41;
                                                    }
                                                    iM338l = i3;
                                                }
                                                break;
                                            case 44:
                                                int iM586d5 = AppState.getInt(1506);
                                                iM338l = 0 != iM586d5 ? showError(iM586d5) : AppState.pool[1318] == null ? 0 : 73;
                                                break;
                                            case 45:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 46:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 47:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 48:
                                                iM1181a = XmppMailRuProtocol.processMailResponse();
                                                iM338l = iM1181a;
                                                break;
                                            case 49:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 50:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 51:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 52:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 53:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 54:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 55:
                                                NetworkUtils.closeAllConnections();
                                                ResourceManager.clearImageCache();
                                                ResourceManager.clearMathTables();
                                                System.gc();
                                                try {
                                                    Thread.sleep(50);
                                                } catch (Throwable unused) {
                                                }
                                                AppState.addInt(292, 1);
                                                saveOnExit = true;
                                                isBackgrounded = true;
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 56:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 57:
                                                iM338l = AppState.getObjectArray(1271)[0] == null ? 0 : 59;
                                                break;
                                            case 58:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 59:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 60:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 61:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 62:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 63:
                                                iM1181a = ResourceManager.updateMessageInput();
                                                iM338l = iM1181a;
                                                break;
                                            case 64:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 65:
                                                if (checkTimer(9, 3000L) && (textBoxM1028h = XmppContactGroup.getTextInputBox()) != null) {
                                                    String strM16a = StringUtils.getTextBoxString(textBoxM1028h);
                                                    if (AppState.getBool(106)) {
                                                        String strM1123k = Conversation.transliterateRussian(strM16a);
                                                        if (!StringUtils.equals(strM1123k, strM16a)) {
                                                            textBoxM1028h.setString(strM1123k);
                                                        }
                                                    } else {
                                                        int length = strM16a.length();
                                                        int i7 = length;
                                                        int i8 = length;
                                                        while (true) {
                                                            i8--;
                                                            if (i8 < 0) {
                                                                int i9 = i7 - 160;
                                                                if (i9 > 0) {
                                                                    textBoxM1028h.setString(StringUtils.prefix(strM16a, strM16a.length() - i9));
                                                                }
                                                            } else {
                                                                char cCharAt = strM16a.charAt(i8);
                                                                if ((cCharAt >= 1040 && cCharAt <= 1071) || ((cCharAt >= 1072 && cCharAt <= 1103) || cCharAt == 1105 || cCharAt == 1025)) {
                                                                    i7 += 2;
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 66:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 67:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 68:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 69:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 70:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 71:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 72:
                                                Object[] objArrM1156c4 = ConnectionThread.getAsyncResult(IOUtils.pollAsyncResult());
                                                if (objArrM1156c4 != null) {
                                                    int iM818c4 = IOUtils.validateJsonResponse(objArrM1156c4);
                                                    if (iM818c4 != 0) {
                                                        iM1181a = iM818c4;
                                                    } else {
                                                        Object objM819l3 = IOUtils.getJsonPayload();
                                                        int size5 = ((Vector) objM819l3).size();
                                                        while (true) {
                                                            size5--;
                                                            if (size5 < 0) {
                                                                iM1181a = 43;
                                                            } else {
                                                                Object objM482e = JsonParser.getVectorElement(objM819l3, size5);
                                                                int iM510a = Utils.parseInt((Object) JsonParser.getStringByInt(objM482e, 263673));
                                                                String strM480c = JsonParser.getStringByInt(objM482e, 329240);
                                                                ChatRoom c0052wM747i2 = ((MrimAccount) AppState.getAccount()).findChatRoomByName(strM480c);
                                                                Message c0026azM1415b3 = c0052wM747i2.getMessage(strM480c);
                                                                if (c0052wM747i2 != null) {
                                                                    c0052wM747i2.markMessageRead(strM480c);
                                                                }
                                                                if (iM510a == 1) {
                                                                    if (c0026azM1415b3 != null && !c0026azM1415b3.hasFlag(4)) {
                                                                        c0026azM1415b3.setFlag(4, true);
                                                                        c0052wM747i2.incrementUnread();
                                                                    }
                                                                } else if (c0026azM1415b3 != null && c0026azM1415b3.hasFlag(4)) {
                                                                    c0026azM1415b3.setFlag(4, false);
                                                                    c0052wM747i2.decrementUnread();
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 73:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 74:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 75:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 76:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 77:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 78:
                                                Object[] objArrM1156c5 = ConnectionThread.getAsyncResult(IOUtils.pollAsyncResult());
                                                if (objArrM1156c5 != null) {
                                                    int iM818c5 = IOUtils.validateJsonResponse(objArrM1156c5);
                                                    if (iM818c5 != 0) {
                                                        iM1181a = iM818c5;
                                                    } else {
                                                        Object objM819l4 = IOUtils.getJsonPayload();
                                                        Object objM476a = JsonParser.getValueByInt(objM819l4, 329636);
                                                        if (JsonParser.getIntByInt(objM819l4, 198543) == 1) {
                                                            int size6 = ((Vector) objM476a).size();
                                                            while (true) {
                                                                size6--;
                                                                if (size6 >= 0) {
                                                                    String strM483f = JsonParser.getVectorString(objM476a, size6);
                                                                    MrimAccount c0028ba4 = (MrimAccount) AppState.getAccount();
                                                                    ChatRoom c0052wM747i3 = c0028ba4.findChatRoomByName(strM483f);
                                                                    if (c0052wM747i3 != null && (c0026azM1415b = c0052wM747i3.getMessage(strM483f)) != null) {
                                                                        if (c0026azM1415b.hasFlag(4)) {
                                                                            c0052wM747i3.decrementUnread();
                                                                        }
                                                                        c0052wM747i3.decrementMembers();
                                                                    }
                                                                    c0028ba4.removeUserFromChatRooms(strM483f);
                                                                }
                                                            }
                                                        }
                                                        iM1181a = 43;
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 79:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 80:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 81:
                                                Object[] objArrM1156c6 = ConnectionThread.getAsyncResult(IOUtils.pollAsyncResult());
                                                if (objArrM1156c6 != null) {
                                                    int iM818c6 = IOUtils.validateJsonResponse(objArrM1156c6);
                                                    if (iM818c6 != 0) {
                                                        iM1181a = iM818c6;
                                                    } else {
                                                        ChatRoom c0052wM746W = ((MrimAccount) AppState.getAccount()).getLastChatRoom();
                                                        Vector vector = (Vector) IOUtils.getJsonPayload();
                                                        c0052wM746W.clear();
                                                        int size7 = vector.size();
                                                        for (int i10 = 0; i10 < size7; i10++) {
                                                            Hashtable hashtable2 = (Hashtable) vector.elementAt(i10);
                                                            Vector vector2 = (Vector) JsonParser.getValue(hashtable2, AppState.getString(329636));
                                                            String strM480c2 = JsonParser.getStringByInt(hashtable2, 198561);
                                                            int size8 = vector2.size();
                                                            for (int i11 = 0; i11 < size8; i11++) {
                                                                Vector vector3 = c0052wM746W.messageIds;
                                                                Object objElementAt = vector2.elementAt(i11);
                                                                vector3.addElement(objElementAt);
                                                                c0052wM746W.metadata.put(objElementAt, strM480c2);
                                                            }
                                                        }
                                                        c0052wM746W.isInitialized = false;
                                                        int iM541c = Utils.vectorSize(c0052wM746W.messageIds);
                                                        if (iM541c > 0) {
                                                            c0052wM746W.subject = (String) c0052wM746W.messageIds.lastElement();
                                                            MrimAccount c0028ba5 = (MrimAccount) AppState.getAccount();
                                                            for (int i12 = 0; i12 < iM541c; i12++) {
                                                                String strM521a = Utils.getVectorString(c0052wM746W.messageIds, i12);
                                                                Message c0026azM1415b4 = c0028ba5.findChatRoomById(Utils.parseInt(c0052wM746W.metadata.get(strM521a))).getMessage(strM521a);
                                                                if (c0026azM1415b4 != null) {
                                                                    c0052wM746W.messages.put(strM521a, c0026azM1415b4);
                                                                } else {
                                                                    c0052wM746W.participants.addElement(strM521a);
                                                                    c0052wM746W.isInitialized = true;
                                                                }
                                                            }
                                                            c0052wM746W.name = new StringBuffer().append(AppState.getString(901)).append(c0052wM746W.messageIds.size()).toString();
                                                        }
                                                        if (c0052wM746W.messageIds.size() == 0) {
                                                            iM1181a = showError(736);
                                                        } else {
                                                            AppState.setInt(1513, c0052wM746W.id);
                                                            iM1181a = 41;
                                                        }
                                                    }
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 82:
                                                Object[] objArrM1156c7 = ConnectionThread.getAsyncResult(IOUtils.pollAsyncResult());
                                                if (objArrM1156c7 != null) {
                                                    int iM818c7 = IOUtils.validateJsonResponse(objArrM1156c7);
                                                    iM1181a = iM818c7 != 0 ? iM818c7 : StringUtils.isEmpty((String) IOUtils.getJsonPayload()) ? showError(878) : 83;
                                                } else {
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 83:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 84:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 85:
                                                iM338l = AppState.pool[1315] == null ? 0 : 96;
                                                break;
                                            case 86:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 87:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 88:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 89:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 90:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 91:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 92:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 93:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 94:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 95:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 96:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 97:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 98:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 99:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 100:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 101:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 102:
                                                iM338l = AppState.getObjectArray(1271)[2] == null ? 0 : 106;
                                                break;
                                            case 103:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 104:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 105:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 106:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 107:
                                                iM338l = AppState.getObjectArray(1271)[2] == null ? 0 : 106;
                                                break;
                                            case 108:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 109:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 110:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 111:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 112:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 113:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 114:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 115:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 116:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 117:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 118:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 119:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 120:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 121:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 122:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 123:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 124:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 125:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 126:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 127:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 128:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 129:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 130:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 131:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 132:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 133:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 134:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 135:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 136:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 137:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 138:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 139:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 140:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 141:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 142:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 143:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 144:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 145:
                                                iM338l = AppState.pool[1315] == null ? 0 : 96;
                                                break;
                                            case 146:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 147:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 148:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 149:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 150:
                                                iM338l = AppState.pool[1318] == null ? 0 : 142;
                                                break;
                                            case 151:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 152:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 153:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 154:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 155:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 156:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 157:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 158:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 159:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 160:
                                                iM1181a = System.currentTimeMillis() - ResourceManager.lastTileLoadTime > 5000 ? ResourceManager.syncAndReturn() : 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 161:
                                                iM1181a = ConnectionThread.showMapSearchResults();
                                                iM338l = iM1181a;
                                                break;
                                            case 162:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 163:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 164:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 165:
                                                Object[] objArrM609l = AppState.getObjectArray(1271);
                                                Object obj4 = objArrM609l[0];
                                                if (obj4 != null) {
                                                    showNotification(StringUtils.concatKeyObj(506, obj4));
                                                    iM1181a = 0;
                                                } else {
                                                    iM1181a = objArrM609l[3] == null ? 0 : NetworkUtils.handleRegSubmit(objArrM609l);
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 166:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 167:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 168:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 169:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 170:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 171:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 172:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 173:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 174:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 175:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 176:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 177:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 178:
                                                iM1181a = 0;
                                                iM338l = iM1181a;
                                                break;
                                            case 179:
                                                Vector vectorM614m5 = AppState.getVector(1284);
                                                if (Utils.vectorSize(vectorM614m5) <= 1) {
                                                    NetworkUtils.releaseVector(vectorM614m5);
                                                    IOUtils.postEvent((Object) AppState.getString(1029));
                                                    iM1181a = 4;
                                                } else {
                                                    Object objElementAt2 = vectorM614m5.elementAt(0);
                                                    if (objElementAt2 instanceof String) {
                                                        Object[] objArr2 = {(String) objElementAt2, StringUtils.concatKey(5510023, Conversation.percentEncode((String) vectorM614m5.lastElement())), null};
                                                        new AsyncTask(26, objArr2);
                                                        vectorM614m5.setElementAt(objArr2, 0);
                                                    } else {
                                                        Object obj5 = ((Object[]) objElementAt2)[2];
                                                        if (obj5 != null) {
                                                            if (obj5 instanceof Throwable) {
                                                                IOUtils.postEvent((Object) StringUtils.concatKeyObj(1030, obj5));
                                                            } else {
                                                                Utils.dequeue(vectorM614m5);
                                                            }
                                                        }
                                                    }
                                                    iM1181a = 0;
                                                }
                                                iM338l = iM1181a;
                                                break;
                                            case 180:
                                                iM1181a = AppState.getString(1239) == null ? 0 : 147;
                                                iM338l = iM1181a;
                                                break;
                                        }
                                        if (iM338l == 12) {
                                            ScreenBuilder.onScreenClosed();
                                        } else if (iM338l != 0) {
                                            ScreenBuilder.openScreen(iM338l);
                                        }
                                    } else if (objM524a instanceof int[]) {
                                        int[] iArr = (int[]) objM524a;
                                        switch (iArr[0]) {
                                            case 0:
                                                Screen c0013amM66b3 = ScreenManager.getCurrentScreen();
                                                if (c0013amM66b3 == null) {
                                                    break;
                                                } else {
                                                    if (c0013amM66b3.screenId != 6) {
                                                        needsRepaint = true;
                                                    }
                                                    int i13 = iArr[1];
                                                    int i14 = iArr[2];
                                                    int i15 = ScreenManager.getCurrentScreen().screenId;
                                                    int i16 = TabBar.currentIndex;
                                                    int size9 = AppState.getVector(1246).size();
                                                    boolean z6 = i16 == size9 - 1;
                                                    if (i15 == 4) {
                                                        ContactListManager.clearState();
                                                        if (size9 > 1) {
                                                            AppState.setInt(1414, 0);
                                                            if (i14 == 2) {
                                                                if (c0013amM66b3.isAtStart()) {
                                                                    TabBar c0008ahM168d = TabBar.getPreviousTab();
                                                                    if (c0008ahM168d != null) {
                                                                        ScreenBuilder.openScreen(c0008ahM168d.selectTab());
                                                                    }
                                                                    z4 = true;
                                                                } else {
                                                                    z4 = false;
                                                                }
                                                            } else if (i14 == 5) {
                                                                if (c0013amM66b3.isAtEnd()) {
                                                                    TabBar c0008ahM167c = TabBar.getNextTab();
                                                                    if (c0008ahM167c != null) {
                                                                        ScreenBuilder.openScreen(c0008ahM167c.selectTab());
                                                                    }
                                                                    z4 = true;
                                                                } else {
                                                                    z4 = false;
                                                                }
                                                            } else if (i15 == 36) {
                                                                AppState.setInt(1414, 0);
                                                                if (i14 == 2) {
                                                                    ScreenBuilder.openScreen(TabBar.getPreviousTab().selectTab());
                                                                    z4 = true;
                                                                } else if (i14 == 5) {
                                                                    if (!z6) {
                                                                        ScreenBuilder.openScreen(TabBar.getNextTab().selectTab());
                                                                    }
                                                                    z4 = true;
                                                                } else if (i15 != 6) {
                                                                    z4 = false;
                                                                } else if (AppState.getBool(1414)) {
                                                                    if (i13 == 42) {
                                                                        Conversation.incrementZoom();
                                                                        z4 = true;
                                                                    } else if (i13 == 35) {
                                                                        Conversation.decrementZoom();
                                                                        z4 = true;
                                                                    } else if (i13 == 48) {
                                                                        Conversation.createStatusReport(false, (MrimAccount) null);
                                                                        ScreenBuilder.openScreen(6);
                                                                        z4 = true;
                                                                    } else if (i13 == 49) {
                                                                        ScreenBuilder.openScreen(100);
                                                                        z4 = true;
                                                                    } else if (i13 == 50) {
                                                                        boolean zM587e = AppState.getBool(41);
                                                                        if (zM587e) {
                                                                            Conversation.setMapEnabled(false);
                                                                        } else {
                                                                            Conversation.setMapEnabled(true);
                                                                        }
                                                                        AppState.setBool(41, !zM587e);
                                                                        ScreenBuilder.openScreen(6);
                                                                        z4 = true;
                                                                    } else if (i13 == 51) {
                                                                        IOUtils.postEvent(new IOUtils(7, null));
                                                                        z4 = true;
                                                                    } else if (i13 == 53) {
                                                                        AppState.setBool(230, !AppState.getBool(230));
                                                                        MapRenderer.needsRedraw = true;
                                                                        z4 = true;
                                                                    } else if (i13 == 55) {
                                                                        if (MmpContact.locationEnabled && (iArrM191s = MmpContact.getPrevRoutePoint()) != null) {
                                                                            MapRenderer.animateTo(iArrM191s[0], iArrM191s[1]);
                                                                        }
                                                                        z4 = true;
                                                                    } else if (i13 == 57) {
                                                                        if (MmpContact.locationEnabled && (iArrM190r = MmpContact.getNextRoutePoint()) != null) {
                                                                            MapRenderer.animateTo(iArrM190r[0], iArrM190r[1]);
                                                                        }
                                                                        z4 = true;
                                                                    }
                                                                } else if (i14 == 2) {
                                                                    ScreenBuilder.openScreen(TabBar.getPreviousTab().selectTab());
                                                                    z4 = true;
                                                                } else if (i14 == 5) {
                                                                    if (!z6) {
                                                                        ScreenBuilder.openScreen(TabBar.getNextTab().selectTab());
                                                                    }
                                                                    z4 = true;
                                                                } else if (i14 == 1) {
                                                                    z4 = true;
                                                                } else if (i14 == 6) {
                                                                    ConnectionThread.handleMapSwitch(c0013amM66b3);
                                                                    z4 = true;
                                                                }
                                                            }
                                                            if (!z4) {
                                                                int iM456O = i13 == 42 ? getKeyAction(AppState.getInt(205)) : i13 == 35 ? getKeyAction(AppState.getInt(206)) : (i13 < 48 || i13 > 57) ? 0 : getKeyAction(AppState.getInt(i13 + 159));
                                                                int i17 = iM456O;
                                                                if (iM456O != 0) {
                                                                    ScreenBuilder.openScreen(i17);
                                                                    break;
                                                                } else if (i14 == 8) {
                                                                    onItemSelected();
                                                                    break;
                                                                } else if (i14 == 1) {
                                                                    c0013amM66b3.scrollUp();
                                                                    break;
                                                                } else if (i14 == 6) {
                                                                    c0013amM66b3.scrollDown();
                                                                    break;
                                                                } else if (i14 == 2) {
                                                                    if (c0013amM66b3.showCheckboxes) {
                                                                        ScreenBuilder.onScreenClosed();
                                                                        break;
                                                                    } else if (c0013amM66b3.screenId == 6) {
                                                                        AppState.setInt(1564, 1);
                                                                        break;
                                                                    } else {
                                                                        if (c0013amM66b3.layoutMode == 1) {
                                                                            int i18 = c0013amM66b3.selectedIndex;
                                                                            int size10 = c0013amM66b3.menuItems.size();
                                                                            c0013amM66b3.selectedIndex = ((i18 + size10) - 1) % size10;
                                                                            c0013amM66b3.invalidateLayout();
                                                                        }
                                                                        break;
                                                                    }
                                                                } else if (i14 == 5) {
                                                                    c0013amM66b3.onActionKey();
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            case 1:
                                                ScreenBuilder.onMenuItemSelected();
                                                break;
                                            case 2:
                                                ScreenBuilder.onMenuItemAction();
                                                break;
                                            case 3:
                                                needsRepaint = true;
                                                onItemSelected();
                                                break;
                                            case 4:
                                                if (ScreenManager.getCurrentScreen().screenId == 6) {
                                                    needsRepaint = true;
                                                    AppState.setInt(1564, -1);
                                                }
                                                break;
                                            case 5:
                                                int i19 = iArr[1];
                                                int i20 = iArr[2];
                                                if (!AppState.getBool(71) || i20 <= AppState.getHeight()) {
                                                    z2 = false;
                                                } else {
                                                    if (i19 < (AppState.getInt(1528) >> 1)) {
                                                        ScreenBuilder.onMenuItemSelected();
                                                    } else {
                                                        ScreenBuilder.onMenuItemAction();
                                                    }
                                                    z2 = true;
                                                }
                                                if (z2 || (i = ScreenManager.getCurrentScreen().screenId) == 137) {
                                                    break;
                                                } else if (i20 > 17 || !ScreenManager.hasModal()) {
                                                    i2 = 0;
                                                    int i21 = i2;
                                                    if (i2 <= 0) {
                                                        if (i != i21) {
                                                            if (i == 4) {
                                                                ContactListManager.clearState();
                                                            }
                                                            ScreenBuilder.openScreen(i21);
                                                        }
                                                        break;
                                                    } else {
                                                        Screen c0013amM66b4 = ScreenManager.getCurrentScreen();
                                                        if (c0013amM66b4 != null) {
                                                            c0013amM66b4.touchConsumed = true;
                                                            c0013amM66b4.marginLeft = 0;
                                                            c0013amM66b4.marginTop = 0;
                                                            int i22 = i19 - c0013amM66b4.offsetX;
                                                            int i23 = i20 - c0013amM66b4.offsetY;
                                                            boolean z7 = i22 >= 2 && i22 < 2 + c0013amM66b4.contentWidth && i23 >= c0013amM66b4.contentTop && i23 < c0013amM66b4.contentTop + c0013amM66b4.contentHeight;
                                                            boolean z8 = z7;
                                                            if (z7 && c0013amM66b4.screenId == 6) {
                                                                int i24 = i23 - c0013amM66b4.contentTop;
                                                                if (i24 > 0) {
                                                                    ConnectionThread.toggleMapControls(c0013amM66b4);
                                                                    MapRenderer.dragActive = false;
                                                                    MapRenderer.rippleTimestamp = System.currentTimeMillis();
                                                                    MapRenderer.rippleX = i22;
                                                                    MapRenderer.rippleY = i24;
                                                                    MapRenderer.needsRedraw = true;
                                                                    z3 = true;
                                                                } else {
                                                                    z3 = false;
                                                                }
                                                            } else if (z8 || c0013amM66b4.screenType == 1 || c0013amM66b4.screenType == 12) {
                                                                z3 = false;
                                                            } else {
                                                                ScreenBuilder.onScreenClosed();
                                                                needsRepaint = true;
                                                                z3 = true;
                                                            }
                                                            if (!z3) {
                                                                int i25 = c0013amM66b4.screenType;
                                                                if ((i25 == 1 || i25 == 12) && (objM179a = TabBar.hitTest(i19, i20)) != null) {
                                                                    if (!(objM179a instanceof int[])) {
                                                                        int i26 = ((TabBar) objM179a).type;
                                                                        Account abstractC0037h = ((TabBar) objM179a).account;
                                                                        AppState.setInt(1414, 0);
                                                                        if (i == 4) {
                                                                            ContactListManager.clearState();
                                                                        }
                                                                        if (i26 != 6 && i26 != 36 && abstractC0037h != null) {
                                                                            TabBar.findTab(4, ((TabBar) objM179a).account);
                                                                            ScreenBuilder.openScreen(4);
                                                                        } else if (i != i26) {
                                                                            ScreenBuilder.openScreen(i26);
                                                                            break;
                                                                        }
                                                                    } else {
                                                                        switch (((int[]) objM179a)[1]) {
                                                                            case 246:
                                                                                ScreenBuilder.openScreen(TabBar.getNextTab().selectTab());
                                                                                break;
                                                                            case 248:
                                                                                ScreenBuilder.openScreen(TabBar.getPreviousTab().selectTab());
                                                                                break;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                } else {
                                                    int iM586d6 = AppState.getInt(1528) - 17;
                                                    if (handleTabAction() == 0) {
                                                        if (!hasActiveConnection() && i19 > iM586d6) {
                                                            i2 = 36;
                                                        }
                                                        int i212 = i2;
                                                        if (i2 <= 0) {
                                                        }
                                                    } else if (i19 > iM586d6) {
                                                        i2 = !AppState.getBool(243) ? 4 : 0;
                                                        int i2122 = i2;
                                                        if (i2 <= 0) {
                                                        }
                                                    } else {
                                                        iM586d6 -= 17;
                                                        if (!hasActiveConnection()) {
                                                            i2 = 0;
                                                            int i21222 = i2;
                                                            if (i2 <= 0) {
                                                            }
                                                        }
                                                    }
                                                }
                                                break;
                                            case 6:
                                                int i27 = iArr[1];
                                                int i28 = iArr[2];
                                                Screen c0013amM66b5 = ScreenManager.getCurrentScreen();
                                                if (c0013amM66b5 == null || !c0013amM66b5.touchConsumed) {
                                                    break;
                                                } else {
                                                    int i29 = i27 - c0013amM66b5.offsetX;
                                                    int i30 = i28 - (c0013amM66b5.offsetY + c0013amM66b5.contentTop);
                                                    if (c0013amM66b5.marginLeft == 0 && c0013amM66b5.marginTop == 0) {
                                                        c0013amM66b5.marginLeft = i29;
                                                        c0013amM66b5.marginTop = i30;
                                                    }
                                                    int i31 = i29 - c0013amM66b5.marginLeft;
                                                    int i32 = i30 - c0013amM66b5.marginTop;
                                                    c0013amM66b5.marginLeft = i29;
                                                    c0013amM66b5.marginTop = i30;
                                                    if (c0013amM66b5.screenId == 6) {
                                                        ConnectionThread.toggleMapControls(c0013amM66b5);
                                                        MapRenderer.dragActive = true;
                                                        MapRenderer.rippleTimestamp = 0L;
                                                        int iM586d7 = AppState.getInt(39);
                                                        MapRenderer.setPosition(MapRenderer.currentLon - ((int) pixelToCoord(i31, iM586d7)), MapRenderer.currentLat + ((int) pixelToCoord(i32, iM586d7)));
                                                        MapRenderer.needsRedraw = true;
                                                        break;
                                                    } else {
                                                        c0013amM66b5.scrollOffset -= i32;
                                                        if (c0013amM66b5.totalHeight < c0013amM66b5.contentHeight) {
                                                            c0013amM66b5.scrollOffset = 0;
                                                        }
                                                        if (c0013amM66b5.scrollOffset > c0013amM66b5.totalHeight - c0013amM66b5.contentHeight) {
                                                            c0013amM66b5.scrollOffset = c0013amM66b5.totalHeight - c0013amM66b5.contentHeight;
                                                        }
                                                        if (c0013amM66b5.scrollOffset < 0) {
                                                            c0013amM66b5.scrollOffset = 0;
                                                        }
                                                        needsRepaint = true;
                                                    }
                                                }
                                                break;
                                            case 7:
                                                int i33 = iArr[1];
                                                int i34 = iArr[2];
                                                int i35 = iArr[3];
                                                int i36 = iArr[4];
                                                int i37 = iArr[5];
                                                Screen c0013amM66b6 = ScreenManager.getCurrentScreen();
                                                if (c0013amM66b6 != null) {
                                                    c0013amM66b6.onPointerEvent(i33, i34, i35, i36, i37 != 0);
                                                }
                                                break;
                                            case 8:
                                                int i38 = iArr[1];
                                                int i39 = iArr[2];
                                                Screen c0013amM66b7 = ScreenManager.getCurrentScreen();
                                                if (c0013amM66b7 != null) {
                                                    int i40 = i38 - c0013amM66b7.offsetX;
                                                    int i41 = i39 - c0013amM66b7.offsetY;
                                                    c0013amM66b7.touchConsumed = false;
                                                    if (c0013amM66b7.screenId == 6) {
                                                        int i42 = i41 - c0013amM66b7.contentTop;
                                                        ConnectionThread.toggleMapControls(c0013amM66b7);
                                                        MapRenderer.onDrag(i40, i42);
                                                    }
                                                }
                                                break;
                                        }
                                    } else if (objM524a instanceof String) {
                                        showNotification((String) objM524a);
                                        needsRepaint = true;
                                    } else if (objM524a instanceof Object[]) {
                                        if (((Object[]) objM524a)[0] instanceof MrimAccount) {
                                            AppState.setInt(4486, 108);
                                            AppState.setObject(1344, ((Object[]) objM524a)[1]);
                                            MrimAccount c0028ba6 = (MrimAccount) ((Object[]) objM524a)[0];
                                            c0028ba6.chatRoomsLoaded = true;
                                            AppState.pool[1282] = c0028ba6;
                                            ScreenManager.showScreen(ScreenManager.createScreen(4485));
                                            AppState.clearIndex(1344);
                                            needsRepaint = true;
                                        } else {
                                            ((MrimAccount) ((Object[]) objM524a)[1]).addOfflineContact((String) ((Object[]) objM524a)[0]);
                                        }
                                    } else if (objM524a instanceof IOUtils) {
                                        IOUtils c0029bb = (IOUtils) objM524a;
                                        int i43 = c0029bb.eventType;
                                        Object obj6 = c0029bb.eventData;
                                        switch (i43) {
                                            case 3:
                                                ResourceManager.showSavedLocations();
                                                break;
                                            case 4:
                                                Object[] objArr3 = (Object[]) obj6;
                                                PhoneContact c0020at = (PhoneContact) objArr3[0];
                                                AppState.pool[1256] = c0020at;
                                                Vector vector4 = (Vector) objArr3[1];
                                                AppState.pool[1257] = vector4;
                                                int iIntValue = ((Integer) objArr3[2]).intValue();
                                                AppState.setInt(1444, iIntValue);
                                                Screen c0013amM75b = ScreenManager.createScreen(2237);
                                                if (iIntValue >= 10) {
                                                    c0013amM75b.addIconItemWithData(6, AppState.getString(421), 1, null);
                                                }
                                                int size11 = vector4.size();
                                                while (true) {
                                                    size11--;
                                                    if (size11 < 0) {
                                                        if (iIntValue < c0020at.userCount - 10) {
                                                            c0013amM75b.addIconItemWithData(6, AppState.getString(420), 2, null);
                                                        }
                                                        AppState.setBool(1445, iIntValue < c0020at.userCount - 10);
                                                        AppState.setBool(1446, iIntValue >= 10);
                                                        ScreenManager.showScreen(c0013amM75b);
                                                        break;
                                                    } else {
                                                        UserSearchResult c0045p = (UserSearchResult) vector4.elementAt(size11);
                                                        c0013amM75b.addIconItemWithData(c0045p.gender == 1 ? 377 : c0045p.gender == 2 ? 378 : 379, c0045p.getText(), 0, c0045p);
                                                    }
                                                }
                                            case 5:
                                                AppState.setInt(1508, 1);
                                                IOUtils.showAddContactScreen();
                                                break;
                                            case 6:
                                                ((MrimAccount) obj6).syncProfile();
                                                break;
                                        }
                                        needsRepaint = true;
                                    } else {
                                        needsRepaint = true;
                                        needsLayoutUpdate = true;
                                        Screen c0013amM66b8 = ScreenManager.getCurrentScreen();
                                        int i44 = ScreenManager.getCurrentScreen().screenId;
                                        if (objM524a != null && (objM524a instanceof MenuItem)) {
                                            MenuItem c0032c = (MenuItem) objM524a;
                                            if (c0032c.id == 2) {
                                                if (i44 == 147 && AppState.setBool(1468, ((Boolean) c0032c.data).booleanValue())) {
                                                    NetworkUtils.processScreenForm();
                                                    AppState.setFromPool(1289, 1286);
                                                    finishScreenBuild();
                                                }
                                            }
                                        } else if (i44 == 21) {
                                            if (AppState.getAccount().getType() == 0) {
                                                StringUtils.updateRegDropdowns(c0013amM66b8, objM524a);
                                            }
                                        } else if (i44 == 164) {
                                            MenuItem c0032c2 = (MenuItem) objM524a;
                                            Object[] objArr4 = (Object[]) c0032c2.data;
                                            int iIntValue2 = ((Integer) objArr4[0]).intValue();
                                            String[] strArr = (String[]) objArr4[1];
                                            MenuItem c0032c3 = null;
                                            Vector vector5 = c0013amM66b8.menuItems;
                                            int size12 = vector5.size();
                                            while (true) {
                                                size12--;
                                                if (size12 < 0) {
                                                    if (c0032c2.title.equals(AppState.getString(809))) {
                                                        MenuItem c0032c4 = c0032c3;
                                                        String strM522f = iIntValue2 == 0 ? Utils.defaultStr(AppState.getString(1287)) : strArr[iIntValue2];
                                                        Object[] objArr5 = (Object[]) c0032c4.data;
                                                        c0032c4.clear().setAction(objArr5[4], strM522f, objArr5[1], objArr5[2], objArr5[3]);
                                                    }
                                                    c0013amM66b8.rebuildItems();
                                                } else {
                                                    MenuItem c0032c5 = (MenuItem) vector5.elementAt(size12);
                                                    if (c0032c5.id == 15 && c0032c5.title.startsWith(AppState.getString(811))) {
                                                        c0032c3 = c0032c5;
                                                    }
                                                }
                                            }
                                        } else if (i44 == 26) {
                                            MenuItem c0032c6 = (MenuItem) objM524a;
                                            Object[] objArr6 = (Object[]) c0032c6.data;
                                            if (AppState.getString(560).equals(c0032c6.title)) {
                                                AppState.setInt(72, ((Integer) objArr6[0]).intValue());
                                            }
                                        } else if (i44 == 28) {
                                            ResourceManager.playAlertIfEnabled(((Integer) ((Object[]) ((MenuItem) objM524a).data)[0]).intValue(), false);
                                        }
                                    }
                                    if (!AppState.getBool(71) && null != (c0013amM66b = ScreenManager.getCurrentScreen())) {
                                        AppState.getCanvas().setCommands(c0013amM66b.titleLeft, c0013amM66b.titleRight);
                                    }
                                    IOUtils.checkSoundTimer();
                                    if (isTimerExpired(timers[0]) && (!AppState.getBool(272) || ScreenManager.getCurrentScreen().screenId != 6)) {
                                        if (AppState.getCanvas().isShown()) {
                                            updateTimerSlot(0);
                                        } else {
                                            setTimer(0, getSessionTimestamp());
                                        }
                                    }
                                } else {
                                    Contact abstractC0041l = (Contact) vectorM614m3.elementAt(size2);
                                    if (Utils.abs(iM586d - abstractC0041l.statusCode) > 10000) {
                                        deleteContact(abstractC0041l);
                                    }
                                }
                            }
                        } else {
                            Account abstractC0037h2 = (Account) vectorM614m2.elementAt(size);
                            try {
                                if (abstractC0037h2.progress <= 0 || abstractC0037h2.progress == 100) {
                                    Vector vectorM614m6 = AppState.getVector(1247);
                                    if (vectorM614m6.contains(abstractC0037h2)) {
                                        Utils.removeFrom(vectorM614m6, abstractC0037h2);
                                        processKeyRepeat();
                                    }
                                } else {
                                    Vector vectorM614m7 = AppState.getVector(1247);
                                    if (!vectorM614m7.contains(abstractC0037h2)) {
                                        vectorM614m7.addElement(abstractC0037h2);
                                        processKeyRepeat();
                                    }
                                }
                                abstractC0037h2.loadData();
                            } catch (Throwable unused2) {
                                abstractC0037h2.handleConnError();
                            }
                        }
                    }
                }
            }
            String strM584b = AppState.getString(1236);
            if (strM584b != null) {
                try {
                    isBackgrounded = true;
                    AppState.getMidlet().platformRequest(strM584b);
                    throw new Throwable();
                } catch (Throwable unused3) {
                    AppState.clearIndex(1236);
                }
            }
            if (isBackgrounded) {
                AppState.getMidlet().destroyApp(true);
                isShuttingDown = true;
                throw new RuntimeException();
            }
            if ((handleTabAction() != 0 || hasActiveConnection()) && isTimerType(5)) {
                needsRepaint = true;
            }
            MainCanvas c0011akM582c = AppState.getCanvas();
            if (!isShuttingDown && needsRepaint) {
                Object obj7 = AppState.currentScreen;
                if (null != obj7) {
                    if (obj7 == AppState.getCanvas()) {
                        AppState.getCanvas().updateFullScreenMode();
                    }
                    Display.getDisplay(AppState.getMidlet()).setCurrent(obj7 instanceof Displayable ? (Displayable) obj7 : null);
                    setTimer(0, getSessionTimestamp());
                    AppState.currentScreen = null;
                    z = true;
                } else {
                    z = false;
                }
                if (!z) {
                    if (c0011akM582c.isShown()) {
                        c0011akM582c.repaint();
                        setTimer(5, 1000L);
                    } else {
                        try {
                            Thread.sleep(200);
                        } catch (Throwable unused4) {
                        }
                    }
                }
            }
            try {
                Thread.sleep(isTimerType(3) ? 500 : 25);
            } catch (Throwable unused5) {
            }
        }
    }

    /* renamed from: ab */
    public static final void onItemSelected() {
        int i;
        int iM338l;
        int i2;
        MenuItem c0032cM69e = ScreenManager.getCurrentMenuItem();
        if (c0032cM69e == null || c0032cM69e.execute(ScreenManager.getCurrentScreen()) == -1) {
            Screen c0013amM66b = ScreenManager.getCurrentScreen();
            String strM67c = ScreenManager.getCurrentTitle();
            int iM68d = ScreenManager.getCurrentWidth();
            MenuItem c0032cM69e2 = ScreenManager.getCurrentMenuItem();
            MenuItem c0032cM223e = AppState.getVector(1272).size() > 0 ? ScreenManager.getCurrentScreen().getHeaderItem() : null;
            Object obj = c0032cM69e2 == null ? null : c0032cM69e2.data;
            Object obj2 = c0032cM223e == null ? null : c0032cM223e.data;
            int iM460J = 0;
            switch (ScreenManager.getCurrentScreen().screenId) {
                case 1:
                    iM460J = handleMapMenuOption(iM68d);
                    break;
                case 2:
                    iM460J = 0;
                    break;
                case 3:
                    iM460J = IOUtils.handleStatusChange(iM68d);
                    break;
                case 4:
                    iM460J = ContactListManager.onContactSelected(strM67c, obj);
                    break;
                case 5:
                    iM460J = handleChatSettingsOption(iM68d);
                    break;
                case 6:
                    if (!AppState.getBool(1414)) {
                        ConnectionThread.toggleMapControls(c0013amM66b);
                        i2 = -1;
                    } else if (AppState.getBool(1479)) {
                        String strM809a = IOUtils.pixelToLongitude(MapRenderer.currentLon);
                        String strM810b = IOUtils.pixelToLatitude(MapRenderer.currentLat);
                        AppState.setInt(1479, 0);
                        ResourceManager.startGeoSearch(VCard.formatLocationUrl(AppState.getInt(39), strM809a, strM810b), MapRenderer.currentLon, MapRenderer.currentLat);
                        i2 = 0;
                    } else {
                        i2 = 113;
                    }
                    iM460J = i2;
                    break;
                case 7:
                    iM460J = 0;
                    break;
                case 8:
                    iM460J = handleSettingsOption(iM68d);
                    break;
                case 9:
                    iM460J = 0;
                    break;
                case 10:
                    iM460J = 55;
                    break;
                case 11:
                    iM460J = handleLeftKey();
                    break;
                case 13:
                    iM460J = -1;
                    break;
                case 14:
                    iM460J = 0;
                    break;
                case 15:
                    iM460J = handleMenuAction(strM67c, obj);
                    break;
                case 16:
                    iM460J = 0;
                    break;
                case 17:
                    iM460J = handleAccountOption(iM68d);
                    break;
                case 18:
                    iM460J = 0;
                    break;
                case 19:
                    iM460J = 0;
                    break;
                case 20:
                    iM460J = handleProfileAction(iM68d);
                    break;
                case 21:
                    iM460J = 0;
                    break;
                case 22:
                    iM460J = 0;
                    break;
                case 23:
                    iM460J = 0;
                    break;
                case 24:
                    iM460J = 0;
                    break;
                case 25:
                    iM460J = handleInputAction(iM68d, obj);
                    break;
                case 26:
                    iM460J = 0;
                    break;
                case 27:
                    iM460J = 0;
                    break;
                case 28:
                    iM460J = 0;
                    break;
                case 29:
                    iM460J = 0;
                    break;
                case 30:
                    iM460J = IOUtils.handleContactGroupAction(strM67c, iM68d);
                    break;
                case 32:
                    iM460J = ResourceManager.handleDropdownSelect(strM67c, c0032cM69e2);
                    break;
                case 33:
                    iM460J = 0;
                    break;
                case 34:
                    iM460J = 0;
                    break;
                case 35:
                    iM460J = handleConnectionOption(iM68d);
                    break;
                case 36:
                    iM460J = ResourceManager.selectMailAccount(obj);
                    break;
                case 37:
                    iM460J = -1;
                    break;
                case 38:
                    AppState.setInt(1513, ((ChatRoom) obj).id);
                    iM460J = 0;
                    break;
                case 39:
                    iM460J = handleAction(obj);
                    break;
                case 40:
                    if (obj2 != null) {
                        Object[] objArr = (Object[]) obj2;
                        if (((Integer) objArr[0]).intValue() == 0) {
                            MapPoint c0014an = new MapPoint((String) objArr[1]);
                            c0014an.height = 2;
                            ConnectionThread.navigateToPoint(c0014an, false);
                            AppState.setInt(1414, 1);
                            iM338l = 6;
                        } else {
                            String str = (String) objArr[1];
                            String str2 = (String) objArr[2];
                            long jLongValue = ((Long) objArr[3]).longValue();
                            clearPreviewState();
                            AppState.setInt(1507, 0);
                            AppState.setObject(1287, (Object) str);
                            AppState.setFromBuffer(1284, NetworkUtils.newStringBuffer().append(str2).append(':'));
                            AppState.setLong(1469, jLongValue);
                            iM338l = 115;
                        }
                    } else {
                        AppState.clearIndex(1279);
                        Contact abstractC0041lM611g = AppState.getCurrentContact();
                        iM338l = !abstractC0041lM611g.account.isConnected() ? showError(299) : abstractC0041lM611g.isOffline() ? ResourceManager.clearSmsFields() : 63;
                    }
                    iM460J = iM338l;
                    break;
                case 41:
                    iM460J = -1;
                    break;
                case 42:
                    iM460J = -1;
                    break;
                case 43:
                    AppState.setInt(1514, c0013amM66b.scrollOffset);
                    AppState.setObject(1345, (Object) strM67c);
                    Message c0026az = (Message) obj;
                    if (c0026az == null) {
                        i = -1;
                    } else {
                        AppState.setObject(1346, (Object) c0026az.from);
                        ChatRoom c0052wM745h = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513));
                        if (StringUtils.matchesKey(894, c0052wM745h.name) || StringUtils.matchesKey(899, c0052wM745h.name)) {
                            XmppMailRuProtocol.setMailAction(54, 3);
                        } else {
                            XmppMailRuProtocol.setMailAction(52, 0);
                        }
                        i = 0;
                    }
                    iM460J = i;
                    break;
                case 44:
                    iM460J = -1;
                    break;
                case 45:
                    iM460J = -1;
                    break;
                case 46:
                    iM460J = 0;
                    break;
                case 47:
                    iM460J = 0;
                    break;
                case 48:
                    iM460J = -1;
                    break;
                case 49:
                    iM460J = 0;
                    break;
                case 50:
                    iM460J = 0;
                    break;
                case 51:
                    ResourceManager.handleChatRoomAction(strM67c);
                case 52:
                    iM460J = 0;
                    break;
                case 53:
                    iM460J = IOUtils.handleMailForwardAction(strM67c);
                    break;
                case 54:
                    iM460J = 0;
                    break;
                case 55:
                    iM460J = -1;
                    break;
                case 56:
                    iM460J = 0;
                    break;
                case 57:
                    iM460J = -1;
                    break;
                case 58:
                    iM460J = handleGroupSelection(iM68d);
                    break;
                case 59:
                    iM460J = ResourceManager.applyVersionLabel();
                    break;
                case 60:
                    iM460J = processInputText(strM67c);
                    break;
                case 61:
                    iM460J = 42;
                    break;
                case 62:
                    iM460J = IOUtils.handleMailMenuAction(strM67c, iM68d);
                    break;
                case 63:
                    iM460J = 0;
                    break;
                case 64:
                    iM460J = handleAccountSwitchOption(iM68d);
                    break;
                case 65:
                    iM460J = 0;
                    break;
                case 66:
                    iM460J = 0;
                    break;
                case 67:
                    iM460J = handleSoftKeyAction(strM67c);
                    break;
                case 68:
                    iM460J = 0;
                    break;
                case 69:
                    iM460J = 0;
                    break;
                case 70:
                    iM460J = 0;
                    break;
                case 71:
                    iM460J = ResourceManager.deleteSelectedEntity();
                    break;
                case 72:
                    iM460J = -1;
                    break;
                case 73:
                    AppState.pool[1319] = obj;
                    iM460J = 0;
                    break;
                case 74:
                    iM460J = -1;
                    break;
                case 75:
                    iM460J = -1;
                    break;
                case 76:
                    iM460J = 0;
                    break;
                case 77:
                    iM460J = handleInviteResult();
                    break;
                case 78:
                    iM460J = -1;
                    break;
                case 79:
                    ScreenBuilder.onScreenClosed();
                    ScreenBuilder.onScreenClosed();
                    iM460J = 0;
                    break;
                case 80:
                    iM460J = handleNotificationOption(iM68d);
                    break;
                case 81:
                    iM460J = -1;
                    break;
                case 82:
                    iM460J = -1;
                    break;
                case 83:
                    iM460J = handleHashKey();
                    break;
                case 84:
                    iM460J = ResourceManager.handleMessageInputAction(strM67c, iM68d);
                    break;
                case 85:
                    iM460J = -1;
                    break;
                case 86:
                    iM460J = handleSearchAction(obj);
                    break;
                case 87:
                    iM460J = ResourceManager.handleChatInputAction(strM67c);
                    break;
                case 88:
                    iM460J = handleThemeOption(iM68d);
                    break;
                case 89:
                    iM460J = -1;
                    break;
                case 90:
                    iM460J = handleEventObject(obj);
                    break;
                case 91:
                    iM460J = getThemeBackground(iM68d);
                    break;
                case 92:
                    iM460J = IOUtils.handleContactMenuAction(strM67c, iM68d);
                    break;
                case 93:
                    iM460J = handleSoundOption(iM68d);
                    break;
                case 94:
                    iM460J = processPhoneInput(strM67c);
                    break;
                case 95:
                    iM460J = validateServerAddress(strM67c);
                    break;
                case 96:
                    iM460J = 0;
                    break;
                case 97:
                    iM460J = handleSearchResultAction(obj);
                    break;
                case 98:
                    iM460J = processPhoneInput(strM67c);
                    break;
                case 99:
                    iM460J = openUrl(strM67c);
                    break;
                case 100:
                    iM460J = IOUtils.handleMapPointAction(obj);
                    break;
                case 101:
                    iM460J = handleConversationAction(obj);
                    break;
                case 102:
                    iM460J = -1;
                    break;
                case 103:
                    iM460J = 0;
                    break;
                case 104:
                    iM460J = getThemeColor(iM68d);
                    break;
                case 105:
                    iM460J = 0;
                    break;
                case 106:
                    iM460J = 0;
                    break;
                case 107:
                    iM460J = -1;
                    break;
                case 108:
                    iM460J = handleContactListKey();
                    break;
                case 109:
                    iM460J = ((MmpProtocol) AppState.getAccount()).scheduleVersionUpdate(iM68d);
                    break;
                case 110:
                    iM460J = 0;
                    break;
                case 111:
                    iM460J = handleMapSearchAction(obj);
                    break;
                case 112:
                    iM460J = 0;
                    break;
                case 113:
                    iM460J = XmppMailRuProtocol.handleMapAction(iM68d);
                    break;
                case 114:
                    iM460J = 0;
                    break;
                case 115:
                    iM460J = 0;
                    break;
                case 116:
                    iM460J = handleMapResultAction(obj);
                    break;
                case 117:
                    iM460J = processLoginField(strM67c);
                    break;
                case 118:
                    iM460J = handleFileAction(obj);
                    break;
                case 119:
                    iM460J = handleChatRoomOption(iM68d);
                    break;
                case 120:
                    iM460J = handleIncomingCall(obj);
                    break;
                case 121:
                    iM460J = handleChatListOption(iM68d);
                    break;
                case 122:
                    iM460J = handlePresenceAction();
                    break;
                case 123:
                    iM460J = handleLocationAction(obj);
                    break;
                case 124:
                    iM460J = 0;
                    break;
                case 125:
                    iM460J = -1;
                    break;
                case 126:
                    iM460J = -1;
                    break;
                case 127:
                    iM460J = -1;
                    break;
                case 128:
                    AppState.getCurrentContact().initMessageBuffer();
                    iM460J = 4;
                    break;
                case 129:
                    iM460J = 0;
                    break;
                case 130:
                    iM460J = handleScreenAction(iM68d);
                    break;
                case 131:
                    iM460J = processSearchQuery(strM67c);
                    break;
                case 132:
                    iM460J = mapKeyToAction(iM68d);
                    break;
                case 133:
                    iM460J = 0;
                    break;
                case 134:
                    iM460J = 0;
                    break;
                case 135:
                    iM460J = 0;
                    break;
                case 136:
                    iM460J = 0;
                    break;
                case 137:
                    iM460J = -1;
                    break;
                case 138:
                    iM460J = -1;
                    break;
                case 139:
                    iM460J = 129;
                    break;
                case 140:
                    iM460J = 0;
                    break;
                case 141:
                    iM460J = -1;
                    break;
                case 142:
                    AppState.pool[1336] = obj;
                    iM460J = obj != null ? 0 : -1;
                    break;
                case 143:
                    iM460J = 0;
                    break;
                case 144:
                    iM460J = 0;
                    break;
                case 145:
                    iM460J = -1;
                    break;
                case 146:
                    iM460J = handleGroupRename(iM68d);
                    break;
                case 147:
                    iM460J = 0;
                    break;
                case 148:
                    iM460J = 0;
                    break;
                case 149:
                    iM460J = 0;
                    break;
                case 150:
                    iM460J = -1;
                    break;
                case 151:
                    iM460J = handleExtSettingsOption(iM68d);
                    break;
                case 152:
                    iM460J = handleContactOption(iM68d);
                    break;
                case 153:
                    iM460J = ResourceManager.setSelectedObject(obj);
                    break;
                case 154:
                    iM460J = 0;
                    break;
                case 155:
                    iM460J = 0;
                    break;
                case 156:
                    iM460J = IOUtils.applyPhotoSelection();
                    break;
                case 157:
                    iM460J = 0;
                    break;
                case 158:
                    iM460J = handleViewOption(iM68d);
                    break;
                case 159:
                    iM460J = handleItemAction(obj);
                    break;
                case 160:
                    iM460J = ResourceManager.syncAndReturn();
                    break;
                case 161:
                    iM460J = -1;
                    break;
                case 162:
                    iM460J = handleChatDetailOption(iM68d);
                    break;
                case 163:
                    iM460J = handleSendKey();
                    break;
                case 164:
                    iM460J = 0;
                    break;
                case 165:
                    iM460J = -1;
                    break;
                case 166:
                    iM460J = handleChatOption(iM68d);
                    break;
                case 167:
                    iM460J = handleMailboxOption(iM68d);
                    break;
                case 168:
                    iM460J = 0;
                    break;
                case 169:
                    iM460J = ResourceManager.applyLocationProfile(obj);
                    break;
                case 170:
                    iM460J = handleFormSubmit(obj);
                    break;
                case 171:
                    iM460J = handleRightKey();
                    break;
                case 172:
                    iM460J = handleObjectAction(obj);
                    break;
                case 173:
                    iM460J = handleInviteAction();
                    break;
                case 174:
                    iM460J = 0;
                    break;
                case 175:
                    iM460J = 0;
                    break;
                case 176:
                    iM460J = iM68d == 1 ? showPeopleSearch() : iM68d == 2 ? showPeopleNearby() : handleStarAction(obj);
                    break;
                case 177:
                    iM460J = ResourceManager.handleSearchResultAction(iM68d);
                    break;
                case 178:
                    iM460J = handleEditAction(iM68d);
                    break;
                case 179:
                    iM460J = -1;
                    break;
                case 180:
                    iM460J = -1;
                    break;
            }
            if (iM460J != -1) {
                if (iM460J == 12) {
                    ScreenBuilder.onScreenClosed();
                    return;
                }
                if (iM460J != 0) {
                    ScreenBuilder.openScreen(iM460J);
                    return;
                }
                int i3 = c0013amM66b.softKeyRight;
                if (i3 != 200) {
                    int i4 = i3 == 199 ? iM68d : i3;
                    int i5 = i4;
                    if (i4 == 12) {
                        ScreenBuilder.onScreenClosed();
                    } else if (i5 != 0) {
                        ScreenBuilder.openScreen(i5);
                    }
                }
            }
        }
    }

    /* renamed from: O */
    private static final int getKeyAction(int i) {
        if (ScreenManager.getCurrentScreen().screenId == 137) {
            return 0;
        }
        Screen c0013amM66b = ScreenManager.getCurrentScreen();
        int i2 = ScreenManager.getCurrentScreen().screenId;
        switch (i) {
            case 4:
                break;
            case 8:
                if (c0013amM66b.selectable) {
                    c0013amM66b.selectedIndex = c0013amM66b.menuItems.size() - 1;
                    c0013amM66b.scrollOffset = c0013amM66b.totalHeight - c0013amM66b.contentHeight;
                    if (c0013amM66b.scrollOffset < 0) {
                        c0013amM66b.scrollOffset = 0;
                    }
                } else if (c0013amM66b.totalHeight < c0013amM66b.contentHeight) {
                    c0013amM66b.scrollOffset = 0;
                } else if (((MenuItem) c0013amM66b.menuItems.lastElement()).getTotalHeight() < c0013amM66b.contentHeight) {
                    c0013amM66b.scrollOffset = c0013amM66b.totalHeight - c0013amM66b.contentHeight;
                } else {
                    int[] iArr = c0013amM66b.layoutCache;
                    c0013amM66b.scrollOffset = iArr[iArr[0]];
                }
                c0013amM66b.invalidateLayout();
                break;
            case 11:
                AppState.toggleBool(98);
                needsLayoutUpdate = true;
                break;
            case 12:
                if (i2 != 73) {
                    if (i2 != 4) {
                        if (i2 == 30 || i2 == 92 || i2 == 40) {
                            break;
                        }
                    } else {
                        break;
                    }
                } else {
                    AppState.pool[1319] = c0013amM66b.getSelectedItem().data;
                    break;
                }
                break;
        }
        return 0;
    }

    /* renamed from: ac */
    public static final int handleGameAction() {
        Object obj = AppState.pool[1365];
        if (obj == null || !(obj instanceof Contact)) {
            return 0;
        }
        Contact abstractC0041l = (Contact) obj;
        if (!abstractC0041l.account.isConnected()) {
            return showError(299);
        }
        AppState.clearIndex(1281);
        return (abstractC0041l.isSystem() || abstractC0041l.isOffline()) ? 0 : 85;
    }

    /* renamed from: ak */
    private static final void processKeyRepeat() {
        AppState.setInt(1408, AppState.getVector(1247).size() * Utils.max(16, AppState.getInt(1450)));
        needsRepaint = true;
    }

    /* renamed from: a */
    public static final String[] createAddressPair(String str, String str2) {
        return new String[]{str, str2};
    }

    /* renamed from: J */
    public static final int handleEditAction(int i) {
        applyViewMode(i == 0, i != 0, true);
        AppState.setInt(281, 1);
        return 0;
    }

    /* renamed from: a */
    public static final void handleMrimMailNotify(MrimAccount c0028ba, ByteBuffer c0043n) {
        c0043n.readInt();
        switch (c0043n.readInt() & 255) {
            case 65:
                processMrimMailData(c0028ba, 490);
                break;
            case 66:
                processMrimMailData(c0028ba, 491);
                break;
            case 67:
            case 69:
            case 70:
            case 71:
            case 72:
            default:
                c0028ba.handleError(0);
                NetworkUtils.checkCrashReport();
                break;
            case 68:
                processMrimMailData(c0028ba, 492);
                break;
            case 73:
                c0028ba.handleComplete();
                break;
        }
    }

    /* renamed from: a */
    private static final void processMrimMailData(MrimAccount c0028ba, int i) {
        IOUtils.postAccountError(c0028ba, i);
        c0028ba.closeConnection();
        c0028ba.lastError = c0028ba.getDefaultError();
    }

    /* renamed from: n */
    public static final int handleIncomingCall(Object obj) {
        if (AppState.getBool(1443)) {
            MapRenderer.confirmMapPoint((MapPoint) obj);
            return 6;
        }
        if (!AppState.getBool(1478)) {
            pendingMapPoint = (MapPoint) obj;
            return 121;
        }
        MrimAccount c0028ba = (MrimAccount) AppState.getAccount();
        MapPoint c0014an = (MapPoint) obj;
        c0028ba.setSimpleProfile(IOUtils.pixelToLongitude(c0014an.longitude), IOUtils.pixelToLatitude(c0014an.latitude));
        c0028ba.syncProfile();
        AppState.setInt(1478, 0);
        return 160;
    }

    /* renamed from: a */
    public static final ByteBuffer createMmpCommand(MmpProtocol c0033d, int i, ByteBuffer c0043n) {
        ByteBuffer c0043nM1357m = createPingPacket(c0033d, 2).writeShortBE(i >> 8).writeShortBE(i & 255).writeShortBE(0);
        int i2 = c0033d.messageSequence + 1;
        c0033d.messageSequence = i2;
        return c0043nM1357m.writeIntBE(i2).writeBuffer(c0043n).updateLength();
    }
}
