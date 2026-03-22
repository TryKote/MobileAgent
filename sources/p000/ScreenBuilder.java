package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;

/* renamed from: au */
/* loaded from: MobileAgent_3.9.jar:au.class */
public final class ScreenBuilder {
    /* renamed from: a */
    public static final void openScreen(int i) {
        boolean z;
        String str;
        String[] regData;
        Object[] request;
        int i2;
        AppController.markScreenDirty();
        AppController.needsRepaint = true;
        while (true) {
            if (!ScreenManager.hasScreen(i)) {
                Vector screenStack = AppState.getVector(1272);
                int size = screenStack.size();
                z = false;
                while (--size >= 0) {
                    i2 = ((Screen) screenStack.elementAt(size)).screenType;
                    if (i2 == 7 || i2 == 8) {
                        z = true;
                        break;
                    }
                }
                if (!z) {
                    break;
                }
            }
            onScreenClosed();
        }
        switch (i) {
            case 0:
                return;
            case 1:
                int size2 = AppState.getVector(1241).size();
                AppState.setBool(1463, size2 > 0);
                AppState.setBool(1462, size2 > 1);
                AppState.setBool(1464, AppState.getBool(1463));
                AppState.setBool(1465, size2 > 0);
                ScreenManager.showScreen(ScreenManager.createScreen(2361));
                return;
            case 2:
                AppController.showSettingsScreen();
                return;
            case 3:
                Screen dialogScreen = ScreenManager.createDialogScreen(3);
                Account account = AppState.getAccount();
                switch (account.getType()) {
                    case 0:
                        dialogScreen.addIconById(156, 642, 0).addIconById(159, 643, 1).addIconById(157, 644, 2).addIconById(160, 645, 3).addIconById(158, 646, 4).addIconById(155, 647, 5).addActionById(-1, 718, 6);
                        break;
                    case 1:
                        MmpProtocol mmpProtocol = (MmpProtocol) account;
                        int iconResId = mmpProtocol.getIconResourceId();
                        dialogScreen.addIconById(iconResId, 642, 0).addIconById(iconResId | 16384000, 643, 1).addIconById(iconResId | 16449536, 645, 3).addIconById(iconResId | 16318464, 644, 4).addIconById(iconResId | 16580608, 648, 5).addIconById(iconResId | 16646144, 654, 6).addIconById(iconResId | 17039360, 649, 7).addIconById(iconResId | 16973824, 650, 8).addIconById(iconResId | 16908288, 651, 9).addIconById(iconResId | 16842752, 652, 10).addIconById(iconResId | 17104896, 653, 11).addIconById(iconResId | 16515072, 646, 2).addIconById(255, 647, 12).addActionById(mmpProtocol.getExtType(), 655, 14).addActionById(-1, 718, 13);
                        break;
                    default:
                        if (((XmppProtocol) account).mo83f()) {
                            dialogScreen.addIconById(387, 642, 1).addIconById(385, 647, 0);
                            break;
                        } else {
                            dialogScreen.addIconById(383, 642, 1).addIconById(16384383, 643, 4).addIconById(16318847, 644, 2).addIconById(16449919, 645, 5).addIconById(16580991, 648, 6).addIconById(16515455, 646, 3).addIconById(381, 647, 0);
                            break;
                        }
                }
                ScreenManager.showScreen(dialogScreen.setSoftKeys(AppState.getString(1048), AppState.getString(1050), 199, 12, 199));
                return;
            case 4:
                ContactListManager.showContactList();
                return;
            case 5:
                AppState.setBool(1472, AppController.getMrimAccountList().size() > 1);
                AppState.setBool(1473, AppController.getXmppAccountList().size() > 1);
                ScreenManager.showScreen(ScreenManager.createScreen(2733));
                return;
            case 6:
                ConnectionThread.showMapScreen();
                return;
            case 7:
                ScreenManager.showScreen(ScreenManager.createScreen(2641));
                return;
            case 8:
                ScreenManager.showScreen(ScreenManager.createScreen(3959));
                return;
            case 9:
                AppState.setInt(1505, 1);
                AppState.setObject(1284, (Object) StringUtils.intern(Long.toString(Runtime.getRuntime().totalMemory())));
                AppState.setObject(1285, (Object) AppController.getAppVersion());
                AppState.setFromBuffer(1288, NetworkUtils.newStringBuffer().append(AppState.getString(1375)).append(AppState.getString(511)));
                AppState.setObject(1287, (Object) new ByteBuffer().writeLongBytes(7234309766870429269L).writeByte(44).writeRawString(AppState.getAppProperty(519)).getStringAndClear());
                ScreenManager.showScreen(ScreenManager.createScreen(2448));
                return;
            case 10:
                NetworkUtils.showAlertById(10, 710);
                return;
            case 11:
                NetworkUtils.showAlertBuffer(11, NetworkUtils.newStringBuffer().append(AppState.getString(768)).append(AppState.getCurrentContact().displayName).append(NetworkUtils.longToHex(16167)));
                return;
            case 12:
                return;
            case 13:
                NetworkUtils.showConfirmDialog(13, 505);
                return;
            case 14:
                StringBuffer sb = NetworkUtils.newStringBuffer();
                int intVal = AppState.getInt(113);
                AppState.setFromBuffer(1286, sb.append(intVal / 100).append('.').append(Utils.zeroPad(intVal % 100)));
                ScreenManager.showScreen(ScreenManager.createScreen(3183));
                return;
            case 15:
                Screen contactListScreen = NetworkUtils.addContactItems(ScreenManager.createScreen(2719), AppState.getVector(1241));
                Account currentAccount = TabBar.currentAccount;
                if (currentAccount != null) {
                    contactListScreen.selectByTitle(currentAccount.getSignature());
                }
                ScreenManager.showScreen(contactListScreen);
                return;
            case 16:
                AppState.setInt(1475, 0);
                ScreenManager.showScreen(ScreenManager.createScreen(4467));
                return;
            case 17:
                Screen dialogScreen2 = ScreenManager.createDialogScreen(17);
                if (AppState.getAccount() instanceof MmpProtocol) {
                    for (int i3 = 0; i3 <= 36; i3++) {
                        dialogScreen2.addIconById(i3 + 268, i3 + 118, i3);
                    }
                } else {
                    for (int i4 = 0; i4 <= 49; i4++) {
                        if (i4 != 21 && i4 != 27) {
                            dialogScreen2.addIconById(i4 + 161, i4 + 155, i4 + 4);
                        }
                    }
                }
                ScreenManager.showScreen(dialogScreen2.setSoftKeys(AppState.getString(1048), AppState.getString(1050), 199, 12, 199));
                return;
            case 18:
                return;
            case 19:
                AppController.clearFormFields();
                Contact contact = AppState.getCurrentContact();
                AppState.setObject(1302, (Object) contact.displayName);
                Vector nameParts = Utils.splitNonEmpty(contact.getDefaultName(), ',');
                for (int i5 = 0; i5 < 3; i5++) {
                    if (i5 < nameParts.size()) {
                        AppState.pool[i5 + 1303] = nameParts.elementAt(i5);
                    }
                }
                NetworkUtils.releaseVector(nameParts);
                AppState.setInt(3510, (!(contact instanceof MrimContact) || contact.isSystem()) ? 1 : 4);
                ScreenManager.showScreen(ScreenManager.createScreen(3501));
                return;
            case 20:
                boolean flag = AppState.getBool(41);
                boolean flag2 = AppState.getBool(277);
                AppState.setBool(1422, !flag && flag2);
                AppState.setBool(1423, flag && flag2);
                ScreenManager.showScreen(ScreenManager.createScreen(1632));
                return;
            case 21:
                int accountType = AppState.getAccount().getType();
                if (accountType == 0) {
                    StringUtils.showRegionSelector();
                    return;
                }
                if (accountType == 1) {
                    AppState.setInt(1491, -1);
                    ScreenManager.showScreen(ScreenManager.createScreen(3569));
                    return;
                }
                StringUtils.resetRegForm();
                if (IOUtils.getGroupCount(AppState.getAccount()) == 0) {
                    IOUtils.postEvent((Object) AppState.getString(743));
                    return;
                } else {
                    ScreenManager.showScreen(ScreenManager.createScreen(3939));
                    return;
                }
            case 22:
                ScreenManager.showScreen(ScreenManager.createScreen(3535));
                return;
            case 23:
                return;
            case 24:
                return;
            case 25:
                Screen screen = ScreenManager.createScreen(2581);
                Vector accounts = AppState.getVector(1241);
                int size3 = accounts.size();
                for (int i6 = 0; i6 < size3; i6++) {
                    screen.addItem(((Account) accounts.elementAt(i6)).createFlagMenuItem());
                }
                ScreenManager.showScreen(screen.addActionById(-1, 531, 16).addIconById(-1, 532, 1).addIconById(-1, 533, 3).addIconById(-1, 534, 2));
                return;
            case 26:
                AppState.setInt(4305, AppState.getInt(72));
                ScreenManager.showScreen(ScreenManager.createScreen(2917));
                return;
            case 27:
                ScreenManager.showScreen(ScreenManager.createScreen(3214));
                return;
            case 28:
                ScreenManager.showScreen(ScreenManager.createScreen(2978));
                return;
            case 29:
                AppState.setInt(4305, AppState.getInt(243));
                ScreenManager.showScreen(ScreenManager.createScreen(3102));
                return;
            case 30:
                AppState.setInt(3707, 1);
                Object obj = AppState.pool[1365];
                if (obj instanceof ContactGroup) {
                    AppState.setInt(1494, 1);
                    ScreenManager.showScreen(ScreenManager.createScreen(3686));
                    return;
                }
                Contact selectedContact = (Contact) obj;
                if (selectedContact.isSystem()) {
                    AppState.setInt(3784, 30);
                    AppState.setInt(3785, 4);
                    ScreenManager.showScreen(ScreenManager.createScreen(3783));
                    return;
                }
                IOUtils.updateContactFlags(selectedContact);
                AppState.setInt(1494, 0);
                boolean z2 = selectedContact instanceof MrimContact;
                boolean z3 = z2;
                AppState.setBool(1495, z2);
                boolean z4 = z3 && selectedContact.isOffline();
                AppState.setBool(1496, z4);
                AppState.setBool(1497, z3 && !z4);
                AppState.setBool(1498, selectedContact.isOnline());
                AppState.setBool(1499, selectedContact.hasUnread() && !selectedContact.isOnline());
                AppState.setBool(1501, z3 && !z4 && ((MrimContact) selectedContact).hasVCard());
                AppState.setInt(3706, 4);
                AppState.setInt(3705, 30);
                ScreenManager.showScreen(ScreenManager.createScreen(3704));
                return;
            case 31:
            default:
                return;
            case 32:
                return;
            case 33:
                ScreenManager.showScreen(ScreenManager.createScreen(2817));
                return;
            case 34:
                ResourceManager.showTrafficStats();
                return;
            case 35:
                ScreenManager.showScreen(ScreenManager.createScreen(5157));
                return;
            case 36:
                ResourceManager.showMailAccountList();
                return;
            case 37:
                AppState.clearIndex(1271);
                MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
                if (mrimAccount.getChatRoomCount() != 0 && !mrimAccount.chatRoomsLoaded) {
                    AppController.initChatRoomList();
                    return;
                }
                NetworkUtils.showConfirmDialog(37, 833);
                Vector params = NetworkUtils.newVector();
                JsonParser.addIntToVector(params, 0);
                params.addElement(AppState.emptyStr);
                JsonParser.addIntToVector(params, 1);
                IOUtils.sendChatRoomRequest(ConnectionThread.createAuthRequest(NetworkUtils.newStringBuffer().append(AppState.getString(1050207)).append('?').append(AppState.getString(722608)).append(AppState.getString(2098635)).append(AppState.getString(1381)).append(AppState.getString(395134)).append(Conversation.urlEncode((Object) JsonParser.toJson(params)))));
                return;
            case 38:
                AppController.initChatRoomList();
                return;
            case 39:
                Vector accountList = AppState.getVector(1283);
                Screen screen2 = ScreenManager.createScreen(2581);
                screen2.screenId = 39;
                screen2.showCheckboxes = true;
                int size4 = accountList.size();
                boolean showFlags = AppState.getBool(1467);
                for (int i7 = 0; i7 < size4; i7++) {
                    Object element = accountList.elementAt(i7);
                    if (!(element instanceof Account)) {
                        screen2.addActionById(11, 548, 0);
                    } else if (showFlags) {
                        screen2.addItem(((Account) element).createFlagMenuItem());
                    } else {
                        screen2.addItem(((Account) element).createMenuItem());
                    }
                }
                Account currentAccount2 = TabBar.currentAccount;
                if (currentAccount2 != null) {
                    screen2.selectByTitle(currentAccount2.getSignature());
                }
                ScreenManager.showScreen(screen2);
                NetworkUtils.releaseVector(accountList);
                return;
            case 40:
                AppController.clearSearchState();
                return;
            case 41:
                AppState.clearIndex(1271);
                ChatRoom chatRoom = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513));
                if (!chatRoom.isInitialized) {
                    IOUtils.showChatRoomMessages();
                    return;
                }
                NetworkUtils.showConfirmDialog(41, 836);
                MrimAccount mrimAccount2 = (MrimAccount) AppState.getAccount();
                Vector params2 = NetworkUtils.newVector();
                if (chatRoom == mrimAccount2.getLastChatRoom()) {
                    params2.addElement(StringUtils.intern(Integer.toString(0)));
                    params2.addElement(chatRoom.participants);
                    request = ConnectionThread.createUploadRequest(AppState.getString(1050207), NetworkUtils.appendFromState(722608).append(AppState.getString(2950868)).append(AppState.getString(1381)).append(AppState.getString(395134)).append(Conversation.urlEncode((Object) JsonParser.toJson(params2))));
                } else {
                    params2.addElement(StringUtils.intern(Integer.toString(chatRoom.id)));
                    int intVal2 = AppState.getInt(97);
                    params2.addElement(StringUtils.intern(Integer.toString(Utils.max(intVal2, chatRoom.messageIds.size() + (chatRoom.isActive ? intVal2 : 0)))));
                    params2.addElement(StringUtils.intern(Integer.toString(1)));
                    params2.addElement(AppState.emptyStr);
                    Vector messageIdParams = NetworkUtils.newVector();
                    Enumeration contactEnum = chatRoom.messageIds.elements();
                    while (contactEnum.hasMoreElements()) {
                        Hashtable hashtable = chatRoom.messages;
                        Object msgIdObj = contactEnum.nextElement();
                        if (hashtable.containsKey(msgIdObj)) {
                            messageIdParams.addElement(msgIdObj);
                        }
                    }
                    params2.addElement(messageIdParams);
                    request = ConnectionThread.createAuthRequest(NetworkUtils.appendFromState(1050207).append('?').append(AppState.getString(722608)).append(AppState.getString(1640218)).append(AppState.getString(1381)).append(AppState.getString(395134)).append(Conversation.urlEncode((Object) JsonParser.toJson(params2))));
                }
                IOUtils.sendChatRoomRequest(request);
                return;
            case 42:
                NetworkUtils.showConfirmDialog(42, 862);
                Vector params4 = NetworkUtils.newVector();
                JsonParser.addIntToVector(params4, AppState.getInt(1527));
                params4.addElement(AppState.getVector(1356));
                IOUtils.sendChatRoomRequest(ConnectionThread.createUploadRequest(AppState.getString(1050207), NetworkUtils.newStringBuffer().append(AppState.getString(722608)).append(AppState.getString(1640193)).append(AppState.getString(1381)).append(AppState.getString(395134)).append(Conversation.urlEncode((Object) JsonParser.toJson(params4)))));
                return;
            case 43:
                IOUtils.showChatRoomMessages();
                return;
            case 44:
                AppController.prepareFormData();
                Account account2 = AppState.getAccount();
                if (AppState.getAccount().getType() == 0) {
                    regData = StringUtils.buildRegData();
                } else {
                    regData = new String[8];
                    int intVal3 = AppState.getInt(1491);
                    regData[0] = intVal3 > 0 ? StringUtils.intern(Integer.toString(intVal3)) : AppState.emptyStr;
                    regData[1] = AppState.getString(AppState.getBool(1492) ? 1046 : 1038);
                    regData[2] = Utils.defaultStr(AppState.getString(1307));
                    regData[3] = Utils.defaultStr(AppState.getString(1308));
                    regData[4] = Utils.defaultStr(AppState.getString(1309));
                    regData[5] = Utils.defaultStr(AppState.getString(1310));
                    regData[6] = Utils.defaultStr(AppState.getString(1311));
                    regData[7] = Utils.defaultStr(AppState.getString(1312));
                }
                NetworkUtils.showErrorOrConfirm(44, 729, account2.validateObject(regData));
                return;
            case 45:
                return;
            case 46:
                return;
            case 47:
                ScreenManager.showScreen(ScreenManager.createScreen(2697));
                return;
            case 48:
                AppState.clearIndex(1271);
                String msgId = AppState.getString(1346);
                Message message = (Message) ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513)).messages.get(msgId);
                Message messageWithBody = message.body != null ? message : null;
                NetworkUtils.showConfirmDialog(48, 837);
                if (messageWithBody == null) {
                    Vector params5 = NetworkUtils.newVector();
                    params5.addElement(msgId);
                    params5.addElement(AppState.emptyStr);
                    params5.addElement(NetworkUtils.longToHex(6775156));
                    IOUtils.sendChatRoomRequest(ConnectionThread.createAuthRequest(NetworkUtils.newStringBuffer().append(AppState.getString(1377926)).append('?').append(AppState.getString(722608)).append(AppState.getString(1836851)).append(AppState.getString(1381)).append(AppState.getString(395134)).append(Conversation.urlEncode((Object) JsonParser.toJson(params5)))));
                    return;
                }
                return;
            case 49:
                ScreenManager.showScreen(ScreenManager.createScreen(4302));
                return;
            case 50:
                ScreenManager.showScreen(ScreenManager.createScreen(3170));
                return;
            case 51:
                String msgId2 = AppState.getString(1346);
                int chatRoomId = AppState.getInt(1513);
                MrimAccount mrimAccount3 = (MrimAccount) AppState.getAccount();
                boolean z5 = msgId2 != null;
                boolean z6 = z5;
                AppState.setBool(1521, z5);
                ChatRoom chatRoom2 = mrimAccount3.findChatRoomById(chatRoomId);
                boolean isRead = chatRoom2.isMessageRead(msgId2);
                AppState.setBool(1518, z6 && isRead);
                AppState.setBool(1519, z6 && !isRead);
                Message message3 = chatRoom2.getMessage(msgId2);
                AppState.setBool(1522, z6 && !message3.isRead());
                AppState.setBool(1523, z6 && message3.isRead());
                int size5 = chatRoom2.readMessages.size();
                AppState.setBool(1520, size5 != 0);
                AppState.setBool(1517, chatRoom2 != mrimAccount3.getLastChatRoom());
                AppState.setFromBuffer(1347, NetworkUtils.newStringBuffer().append(AppState.getString(849)).append(size5).append(')'));
                ScreenManager.showScreen(ScreenManager.createScreen(4589));
                return;
            case 52:
                String msgId3 = AppState.getString(1346);
                ChatRoom chatRoom3 = ((MrimAccount) AppState.getAccount()).findChatRoomById(AppState.getInt(1513));
                int roomType = chatRoom3.getType();
                Message message4 = chatRoom3.getMessage(msgId3);
                if (roomType == 2) {
                    String[] ccRecipient = XmppMailRuProtocol.getFirstRecipient(message4.ccList);
                    str = ccRecipient != null ? ccRecipient[1] : AppState.emptyStr;
                } else {
                    String[] toRecipient = XmppMailRuProtocol.getFirstRecipient(message4.toList);
                    str = toRecipient != null ? toRecipient[1] : AppState.emptyStr;
                }
                AppState.setObject(1284, (Object) str);
                AppState.setObject(1285, (Object) Utils.normalizeSpaces(message4.getSubject()));
                AppState.setObject(1286, (Object) Utils.normalizeSpaces(message4.body));
                Screen screen3 = ScreenManager.createScreen(4537);
                Object[] objArr = message4.attachments;
                if (objArr != null) {
                    for (Object obj2 : objArr) {
                        screen3.addItem(MenuItem.createSeparator().setIcon(221).addText(((String[]) obj2)[1], 1, 0));
                    }
                }
                ScreenManager.showScreen(screen3);
                AppController.clearPreviewState();
                return;
            case 53:
                ScreenManager.showScreen(ScreenManager.createScreen(4551));
                return;
            case 54:
                ScreenManager.showScreen(ScreenManager.createScreen(4806));
                return;
            case 55:
                NetworkUtils.showConfirmDialog(55, 761);
                return;
            case 56:
                ScreenManager.showScreen(ScreenManager.createScreen(3052));
                return;
            case 57:
                NetworkUtils.showConfirmDialog(57, 730);
                AppState.setLong(219, System.currentTimeMillis());
                Object[] objArr2 = new Object[1];
                AppState.pool[1271] = objArr2;
                new AsyncTask(2, objArr2);
                NetworkUtils.checkCrashReport();
                return;
            case 58:
                ScreenManager.showScreen(ScreenManager.createScreen(4729));
                return;
            case 59:
                ResourceManager.processUpdateResult();
                return;
            case 60:
                ScreenManager.showScreen(ScreenManager.createScreen(4711));
                return;
            case 61:
                NetworkUtils.showAlertById(61, 857);
                return;
            case 62:
                ScreenManager.showScreen(ScreenManager.createScreen(4667));
                return;
            case 63:
                XmppContactGroup.showTextInputDialog(AppState.getCurrentContact().displayName, AppState.getString(1279), 1000, StringUtils.isKnownDevice2 ? 2097152 : 0, AppState.getString(424), 1059, 1055, new AsyncTask());
                AppState.setInt(1457, 0);
                AppState.setInt(1458, 0);
                AppState.setInt(1459, 0);
                ScreenManager.showScreen(new Screen());
                return;
            case 64:
                Contact contact2 = AppState.getCurrentContact();
                AppState.setInt(4113, contact2.canDelete() ? 25 : 24);
                AppState.setInt(4118, contact2.canBlock() ? 25 : 24);
                ScreenManager.showScreen(ScreenManager.createScreen(4100));
                return;
            case 65:
                Vector phoneGroups = Utils.splitNonEmpty(AppState.getCurrentMrimContact().contactGroupsStr, ',');
                int size6 = phoneGroups.size();
                if (size6 <= 0) {
                    AppController.showMessageById(713);
                    return;
                }
                StringBuffer sb2 = NetworkUtils.newStringBuffer();
                for (int i8 = 0; i8 < size6; i8++) {
                    sb2.append(Utils.formatPhone((String) phoneGroups.elementAt(i8))).append((char) 0);
                }
                AppState.setFromBuffer(1313, sb2);
                AppState.setInt(1493, 0);
                ScreenManager.showScreen(ScreenManager.createScreen(3627));
                return;
            case 66:
                IOUtils.showAddContactScreen();
                return;
            case 67:
                ScreenManager.showScreen(ScreenManager.createScreen(4633));
                return;
            case 68:
                AppController.resetSearchResults();
                ScreenManager.showScreen(ScreenManager.createScreen(4769));
                return;
            case 69:
                ScreenManager.showScreen(ScreenManager.createScreen(3340));
                return;
            case 70:
                AppState.setObject(1306, (Object) AppState.getCurrentGroup().name);
                ScreenManager.showScreen(ScreenManager.createScreen(3553));
                return;
            case 71:
                StringBuffer sbAlert = NetworkUtils.newStringBuffer().append(AppState.getString(672));
                Object obj3 = AppState.pool[1365];
                NetworkUtils.showAlertBuffer(71, sbAlert.append(obj3 instanceof ContactGroup ? ((ContactGroup) obj3).name : ((Contact) obj3).displayName).append(NetworkUtils.longToHex(16167)));
                return;
            case 72:
                NetworkUtils.showConfirmDialog(72, 866);
                Vector selectedItems = AppState.getVector(1356);
                Vector itemsParams = NetworkUtils.newVector();
                int size7 = selectedItems.size();
                while (true) {
                    size7--;
                    if (size7 < 0) {
                        Vector outerParams = NetworkUtils.newVector();
                        outerParams.addElement(itemsParams);
                        IOUtils.sendChatRoomRequest(ConnectionThread.createUploadRequest(AppState.getString(1377771), NetworkUtils.newStringBuffer().append(AppState.getString(722608)).append(AppState.getString(1574400)).append(AppState.getString(1381)).append(AppState.getString(395134)).append(Conversation.urlEncode((Object) JsonParser.toJson(outerParams)))));
                        return;
                    } else {
                        Hashtable hashtable2 = new Hashtable();
                        JsonParser.putIntKey(hashtable2, 329240, JsonParser.getVectorElement(selectedItems, size7));
                        JsonParser.putIntKey(hashtable2, 263673, ResourceManager.integerOf(AppState.getInt(1525)));
                        itemsParams.addElement(hashtable2);
                    }
                }
            case 73:
                int errorMsgId = AppState.getInt(1506);
                if (0 != errorMsgId) {
                    AppController.clearSearchResults2();
                    AppController.showMessageById(errorMsgId);
                    return;
                }
                Vector searchResults = AppState.getVector(1318);
                if (0 != searchResults.size()) {
                    ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(3868), searchResults));
                    return;
                } else {
                    AppController.clearSearchResults2();
                    AppController.showMessageById(736);
                    return;
                }
            case 74:
                return;
            case 75:
                return;
            case 76:
                XmppMailRuProtocol.showLoginScreen();
                return;
            case 77:
                NetworkUtils.showAlertBuffer(77, NetworkUtils.newStringBuffer().append(AppState.getString(672)).append(AppState.getAccount().login).append(NetworkUtils.longToHex(16167)));
                return;
            case 78:
                NetworkUtils.showConfirmDialog(78, 861);
                Vector params8 = NetworkUtils.newVector();
                params8.addElement(AppState.getVector(1356));
                JsonParser.addIntToVector(params8, AppState.getBool(1524) ? 1 : 0);
                IOUtils.sendChatRoomRequest(ConnectionThread.createUploadRequest(AppState.getString(1508975), IOUtils.appendAuthParams(NetworkUtils.newStringBuffer().append(AppState.getString(722608)).append(AppState.getString(1640123)), Conversation.urlEncode((Object) JsonParser.toJson(params8)))));
                return;
            case 79:
                NetworkUtils.showAlertById(79, 863);
                return;
            case 80:
                ScreenManager.showScreen(ScreenManager.createScreen(4747));
                return;
            case 81:
                NetworkUtils.showConfirmDialog(81, 872);
                Vector params9 = NetworkUtils.newVector();
                params9.addElement(AppState.getString(AppState.getBool(1526) ? 264068 : 1038));
                JsonParser.addIntToVector(params9, AppState.getInt(1513));
                params9.addElement(Utils.defaultStr(AppState.getString(1348)));
                params9.addElement(Utils.defaultStr(AppState.getString(1349)));
                params9.addElement(Utils.defaultStr(AppState.getString(1350)));
                params9.addElement(Utils.defaultStr(AppState.getString(1351)));
                IOUtils.sendChatRoomRequest(ConnectionThread.createUploadRequest(AppState.getString(1050207), NetworkUtils.newStringBuffer().append(AppState.getString(722608)).append(AppState.getString(1509223)).append(AppState.getString(1381)).append(AppState.getString(395134)).append(Conversation.urlEncodeCyrillic((Object) JsonParser.toJson(params9)))));
                return;
            case 82:
                NetworkUtils.showConfirmDialog(82, 877);
                Message newMessage = new Message(XmppMailRuProtocol.parseRecipientList(Utils.defaultStr(AppState.getString(1352))), Utils.defaultStr(AppState.getString(1353)), Utils.defaultStr(AppState.getString(1354)));
                Vector params10 = NetworkUtils.newVector();
                params10.addElement(newMessage.toHashtable());
                IOUtils.sendChatRoomRequest(ConnectionThread.createUploadRequest(AppState.getString(1377947), IOUtils.appendAuthParams(NetworkUtils.newStringBuffer().append(AppState.getString(722608)).append(AppState.getString(1574735)), Conversation.urlEncodeCyrillic((Object) JsonParser.toJson(params10)))));
                return;
            case 83:
                ResourceManager.playNotificationSound(4);
                AppState.setInt(3329, 83);
                AppState.setFromPool(1294, 879);
                AppController.clearNotifications();
                return;
            case 84:
                String inputText = XmppContactGroup.getTextInputValue();
                AppState.setObject(1279, (Object) inputText);
                AppState.setBool(1456, !StringUtils.isEmpty(inputText));
                ScreenManager.showScreen(ScreenManager.createScreen(2299));
                return;
            case 85:
                AppState.clearIndex(1315);
                if (AppState.getCurrentContact() == null) {
                    NetworkUtils.showErrorOrConfirm(85, 727, 0);
                    return;
                } else {
                    Contact contact3 = AppState.getCurrentContact();
                    NetworkUtils.showErrorOrConfirm(85, 727, contact3.account.validateDelete(contact3));
                    return;
                }
            case 86:
                Screen screen4 = ScreenManager.createScreen(4238);
                Contact contact4 = AppState.getCurrentContact();
                Vector vector = contact4.account.groups;
                int size8 = vector.size();
                for (int i9 = 0; i9 < size8; i9++) {
                    ContactGroup group = (ContactGroup) vector.elementAt(i9);
                    screen4.addItem(group.createMenuItem(-1));
                    if (group.containsContact(contact4)) {
                        screen4.selectedIndex = i9;
                    }
                }
                ScreenManager.showScreen(screen4);
                return;
            case 87:
                AppState.setBool(1456, Utils.nonEmpty(AppState.getString(1279)));
                ScreenManager.showScreen(ScreenManager.createScreen(3647));
                return;
            case 88:
                ScreenManager.showScreen(ScreenManager.createScreen(4836));
                return;
            case 89:
                ResourceManager.showTosScreen();
                return;
            case 90:
                AppController.processEventQueue();
                return;
            case 91:
                boolean isOnline4 = AppState.getBool(277);
                boolean isCustom = AppState.getBool(230);
                AppState.setBool(1418, isOnline4 && !isCustom);
                AppState.setBool(1419, isOnline4 && isCustom);
                ScreenManager.showScreen(ScreenManager.createScreen(1600));
                return;
            case 92:
                AppState.setInt(3707, 0);
                Contact contact5 = AppState.getCurrentContact();
                if (contact5.isSystem()) {
                    AppState.setInt(3784, 92);
                    AppState.setInt(3785, 3);
                    ScreenManager.showScreen(ScreenManager.createScreen(3783));
                    return;
                }
                IOUtils.updateContactFlags(contact5);
                boolean z7 = contact5 instanceof MrimContact;
                boolean z8 = z7;
                AppState.setBool(1495, z7);
                boolean z9 = z8 && contact5.isOffline();
                AppState.setBool(1496, z9);
                AppState.setBool(1497, z8 && !z9);
                AppState.setBool(1498, contact5.isOnline());
                AppState.setBool(1499, contact5.hasUnread() && !contact5.isOnline());
                AppState.setBool(1501, z8 && !z9 && ((MrimContact) contact5).hasVCard());
                AppState.setInt(3706, 3);
                AppState.setInt(3705, 92);
                ScreenManager.showScreen(ScreenManager.createScreen(3704));
                return;
            case 93:
                Screen screen5 = ScreenManager.createScreen(2621);
                if (AppState.getCurrentContact() instanceof MmpContact) {
                    for (int i10 = 0; i10 < 43; i10++) {
                        if (AppState.getString(i10 + 1141) != null) {
                            screen5.addIconTextItem(i10 + 110, StringUtils.intern(Integer.toString(i10)), i10);
                        }
                    }
                } else if (AppState.getCurrentContact() instanceof XmppContact) {
                    for (int i11 = 0; i11 < 37; i11++) {
                        if (AppState.getString(i11 + 1184) != null) {
                            screen5.addIconTextItem(i11 + 318, StringUtils.intern(Integer.toString(i11)), i11);
                        }
                    }
                } else {
                    for (int i12 = 10; i12 < 74; i12++) {
                        screen5.addIconTextItem(i12 + 36, StringUtils.intern(Integer.toString(i12)), i12);
                    }
                    for (int i13 = 0; i13 < 10; i13++) {
                        screen5.addIconTextItem(i13 + 36, StringUtils.intern(Integer.toString(i13)), i13);
                    }
                    screen5.addIconTextItem(142, StringUtils.intern(Integer.toString(74)), 74);
                    screen5.addIconTextItem(137, StringUtils.intern(Integer.toString(75)), 75);
                    screen5.addIconTextItem(210, StringUtils.intern(Integer.toString(76)), 76);
                    screen5.addIconTextItem(205, StringUtils.intern(Integer.toString(77)), 77);
                }
                ScreenManager.showScreen(screen5);
                return;
            case 94:
                Screen screen6 = ScreenManager.createScreen(2611);
                for (int i14 = 0; i14 < 15; i14++) {
                    screen6.addTextItem(AppState.getString(i14 + 48));
                }
                ScreenManager.showScreen(screen6);
                return;
            case 95:
                Screen screen7 = ScreenManager.createScreen(2601);
                for (int i15 = 0; i15 < 15; i15++) {
                    screen7.addTextItem(AppState.getString(i15 + 48));
                }
                ScreenManager.showScreen(screen7);
                return;
            case 96:
                ContactInfo contactInfo = (ContactInfo) AppState.pool[1315];
                String str2 = (String) contactInfo.get(ResourceManager.integerOf(-1));
                if (null != str2) {
                    AppController.showNotification(str2);
                } else {
                    AppState.setInt(3834, contactInfo.isXmppContact() ? 0 : 503);
                    ScreenManager.showScreen(contactInfo.buildContactScreen(3830));
                }
                AppState.setInt(3650, 102);
                return;
            case 97:
                Vector regions = AppState.getVector(1389);
                int size9 = regions.size();
                if (size9 == 0) {
                    AppController.showMessageById(397);
                    return;
                }
                Screen screen8 = ScreenManager.createScreen(1691);
                for (int i16 = 0; i16 < size9; i16++) {
                    GeoRegion region = (GeoRegion) regions.elementAt(i16);
                    screen8.addIconItemWithData(-1, region.name, 6, region);
                }
                GeoRegion currentRegion = StringUtils.getGeoRegion();
                screen8.addIconItemWithData(-1, currentRegion.name, 6, currentRegion);
                ScreenManager.showScreen(screen8);
                return;
            case 98:
                Screen screen9 = ScreenManager.createScreen(4080);
                for (int i17 = 0; i17 < 15; i17++) {
                    screen9.addTextItem(AppState.getString(i17 + 48));
                }
                ScreenManager.showScreen(screen9);
                return;
            case 99:
                Screen screen10 = ScreenManager.createScreen(4090);
                for (int i18 = 0; i18 < 15; i18++) {
                    screen10.addTextItem(AppState.getString(i18 + 48));
                }
                ScreenManager.showScreen(screen10);
                return;
            case 100:
                Screen screen11 = ScreenManager.createScreen(1701);
                Vector mapPoints = AppState.getVector(1400);
                for (int i19 = 0; i19 < mapPoints.size(); i19++) {
                    MapPoint mapPoint = (MapPoint) mapPoints.elementAt(i19);
                    screen11.addIconItemWithData(-1, mapPoint.name, 6, mapPoint);
                }
                ScreenManager.showScreen(screen11);
                return;
            case 101:
                return;
            case 102:
                if (AppController.pendingAccount == null && AppController.pendingUrl == null) {
                    Contact contact6 = AppState.getCurrentContact();
                    String statusText = AppController.getStatusText();
                    NetworkUtils.showErrorOrConfirm(102, 728, statusText != null ? contact6.account.getResourceId((Object) statusText) : ResourceManager.loadUserProfile(contact6.getIdentifier(), contact6.account));
                    return;
                } else {
                    NetworkUtils.showErrorOrConfirm(102, 728, 0);
                    ResourceManager.loadUserProfile(AppController.pendingUrl, AppController.pendingAccount);
                    AppController.clearMapPoints();
                    return;
                }
            case 103:
                ScreenManager.showScreen(((ContactInfo) AppState.pool[1319]).buildContactScreen(3878));
                AppState.clearIndex(1315);
                AppState.setInt(3650, 107);
                return;
            case 104:
                ScreenManager.showScreen(ScreenManager.createScreen(4204));
                return;
            case 105:
                XmppMailRuProtocol.showLoginScreen();
                ScreenManager.getCurrentScreen().screenId = 105;
                return;
            case 106:
                Screen screen12 = ScreenManager.createScreen(3840);
                Object obj4 = ((Object[]) AppState.pool[1271])[2];
                if (obj4 instanceof Image) {
                    screen12.addItem(MenuItem.createGraphics(new GraphicsContext((Image) obj4)));
                } else {
                    screen12.addLabelById(((Integer) obj4).intValue());
                }
                ScreenManager.showScreen(screen12);
                AppState.clearIndex(1271);
                return;
            case 107:
                ContactInfo contactInfo2 = (ContactInfo) AppState.pool[1319];
                NetworkUtils.showErrorOrConfirm(107, 728, contactInfo2.getAccount().getResourceId((Object) contactInfo2.getEmailOrMmpId()));
                return;
            case 108:
                return;
            case 109:
                ScreenManager.showScreen(ScreenManager.createScreen(4862).selectByTitle(AppState.getString(879 + ((MmpProtocol) AppState.getAccount()).getPendingVersion())));
                return;
            case 110:
                AppState.setObject(1249, (Object) AppState.emptyStr);
                String tooltipText = MapRenderer.getTooltipText();
                if (tooltipText != null) {
                    AppState.setObject(1249, (Object) tooltipText);
                }
                ScreenManager.showScreen(ScreenManager.createScreen(1727));
                return;
            case 111:
                Vector allContacts = AppController.getAllAccountsList();
                int size10 = allContacts.size();
                while (true) {
                    size10--;
                    if (size10 < 0) {
                        if (allContacts.size() == 0) {
                            AppController.showMessageById(762);
                        } else {
                            AppController.sortContacts(allContacts);
                            ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(1743), allContacts));
                        }
                        NetworkUtils.releaseVector(allContacts);
                        return;
                    }
                    if (((Contact) allContacts.elementAt(size10)).isOffline()) {
                        allContacts.removeElementAt(size10);
                    }
                }
            case 112:
                AppController.clearNotifications();
                return;
            case 113:
                XmppMailRuProtocol.showMapContextMenu();
                return;
            case 114:
                AppState.setObject(1250, (Object) AppState.emptyStr);
                String tooltipText2 = MapRenderer.getTooltipText();
                if (tooltipText2 != null) {
                    AppState.setObject(1250, (Object) tooltipText2);
                }
                ScreenManager.showScreen(ScreenManager.createScreen(1876));
                return;
            case 115:
                ScreenManager.showScreen(ScreenManager.createScreen(2501));
                AppController.clearPreviewState();
                return;
            case 116:
                Screen screen13 = ScreenManager.createScreen(1892);
                Enumeration routeEnum = ConnectionThread.getRouteElements();
                while (routeEnum.hasMoreElements()) {
                    MapPoint mapPoint2 = (MapPoint) routeEnum.nextElement();
                    screen13.addIconItemWithData(-1, mapPoint2.name, 118, mapPoint2);
                }
                ScreenManager.showScreen(screen13);
                return;
            case 117:
                Conversation.updateStatusText(375);
                ScreenManager.showScreen(ScreenManager.createScreen(1902));
                return;
            case 118:
                Vector allContacts2 = AppController.getAllAccountsList();
                int size11 = allContacts2.size();
                while (true) {
                    size11--;
                    if (size11 < 0) {
                        if (allContacts2.size() == 0) {
                            AppController.showMessageById(762);
                        } else {
                            AppController.sortContacts(allContacts2);
                            ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(1930), allContacts2));
                        }
                        NetworkUtils.releaseVector(allContacts2);
                        return;
                    }
                    if (((Contact) allContacts2.elementAt(size11)).isOffline()) {
                        allContacts2.removeElementAt(size11);
                    }
                }
            case 119:
                ScreenManager.showScreen(ScreenManager.createScreen(1940));
                return;
            case 120:
                Screen screen14 = ScreenManager.createScreen(1958);
                Enumeration routeEnum2 = ConnectionThread.getRouteElements();
                while (routeEnum2.hasMoreElements()) {
                    MapPoint mapPoint3 = (MapPoint) routeEnum2.nextElement();
                    screen14.addIconItemWithData(-1, mapPoint3.name, 6, mapPoint3);
                }
                ScreenManager.showScreen(screen14);
                return;
            case 121:
                ScreenManager.showScreen(ScreenManager.createScreen(1968));
                return;
            case 122:
                NetworkUtils.showAlertById(122, 535);
                return;
            case 123:
                ScreenManager.showScreen(AppState.getCurrentContact().showMessageSummary());
                return;
            case 124:
                ScreenManager.showScreen(ScreenManager.createScreen(3479));
                return;
            case 125:
                Account account3 = AppState.getAccount();
                Vector contacts11 = NetworkUtils.newVector();
                Enumeration contactEnum2 = account3.contactMap.elements();
                while (contactEnum2.hasMoreElements()) {
                    Contact contactToDelete = (Contact) contactEnum2.nextElement();
                    if (contactToDelete.canDelete()) {
                        contacts11.addElement(contactToDelete);
                    }
                }
                if (contacts11.size() > 0) {
                    ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(4070), contacts11));
                } else {
                    AppController.showMessageById(762);
                }
                NetworkUtils.releaseVector(contacts11);
                return;
            case 126:
                Account account4 = AppState.getAccount();
                Vector contacts12 = NetworkUtils.newVector();
                Enumeration contactEnum3 = account4.contactMap.elements();
                while (contactEnum3.hasMoreElements()) {
                    Contact contactToBlock = (Contact) contactEnum3.nextElement();
                    if (contactToBlock.canBlock()) {
                        contacts12.addElement(contactToBlock);
                    }
                }
                if (contacts12.size() > 0) {
                    ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(4060), contacts12));
                } else {
                    AppController.showMessageById(762);
                }
                NetworkUtils.releaseVector(contacts12);
                return;
            case 127:
                Account account5 = AppState.getAccount();
                Vector contacts13 = NetworkUtils.newVector();
                Enumeration contactEnum4 = account5.contactMap.elements();
                while (contactEnum4.hasMoreElements()) {
                    Contact contactToUnblock = (Contact) contactEnum4.nextElement();
                    if (contactToUnblock.canUnblock()) {
                        contacts13.addElement(contactToUnblock);
                    }
                }
                if (contacts13.size() > 0) {
                    ScreenManager.showScreen(NetworkUtils.addContactItems(ScreenManager.createScreen(4050), contacts13));
                } else {
                    AppController.showMessageById(762);
                }
                NetworkUtils.releaseVector(contacts13);
                return;
            case 128:
                NetworkUtils.showAlertBuffer(128, NetworkUtils.newStringBuffer().append(AppState.getString(760)).append(AppState.getCurrentContact().displayName).append(NetworkUtils.longToHex(16167)));
                return;
            case 129:
                StringBuffer sbAccounts = NetworkUtils.newStringBuffer().append(AppState.getString(396));
                Vector mmpAccounts = AppController.getMmpAccountList();
                int i20 = 0;
                int size12 = mmpAccounts.size();
                int i21 = size12;
                while (true) {
                    i21--;
                    if (i21 < 0) {
                        AppState.setInt(1438, i20);
                        AppState.setObject(1252, (Object) NetworkUtils.bufToStringCached(sbAccounts));
                        ScreenManager.showScreen(ScreenManager.createScreen(1990));
                        return;
                    } else {
                        String str3 = ((MrimAccount) mmpAccounts.elementAt(i21)).login;
                        sbAccounts.append(str3);
                        if (i21 != 0) {
                            sbAccounts.append((char) 0);
                        }
                        if (str3.equals(AppState.getString(267))) {
                            i20 = size12 - i21;
                        }
                    }
                }
            case 130:
                ScreenManager.showScreen(ScreenManager.createScreen(4892));
                return;
            case 131:
                ScreenManager.showScreen(ScreenManager.createScreen(2043));
                return;
            case 132:
                ScreenManager.showScreen(ScreenManager.createScreen(2421));
                return;
            case 133:
                return;
            case 134:
                return;
            case 135:
                return;
            case 136:
                return;
            case 137:
                ScreenManager.showScreen(ScreenManager.createScreen(4369));
                if (AppState.getBool(269)) {
                    AppController.processBackgroundTasks();
                    return;
                }
                return;
            case 138:
                return;
            case 139:
                return;
            case 140:
                ScreenManager.showScreen(ScreenManager.createScreen(5090));
                return;
            case 141:
                return;
            case 142:
                Screen screen15 = ScreenManager.createScreen(4159);
                MrimContact mrimContact = (MrimContact) AppState.getCurrentContact();
                MrimAccount mrimAccount4 = (MrimAccount) mrimContact.account;
                Vector groupMembers = AppState.getVector(1318);
                mrimContact.setGroupsList(groupMembers);
                for (int i22 = 0; i22 < groupMembers.size(); i22++) {
                    String memberLogin = Utils.getVectorString(groupMembers, i22);
                    if (!StringUtils.equals(memberLogin, mrimAccount4.login)) {
                        MrimContact mrimContact2 = (MrimContact) mrimAccount4.getContact((Object) memberLogin);
                        if (mrimContact2 != null) {
                            screen15.addItem(mrimContact2.createMenuItem());
                        } else {
                            screen15.addIconItemWithData(154, memberLogin, 0, memberLogin);
                        }
                    }
                }
                if (screen15.menuItems.size() == 0) {
                    screen15.selectable = false;
                    Screen labelScreen = screen15.addLabelById(772);
                    labelScreen.setSoftKeys(AppState.getString(1038), AppState.getString(1050), labelScreen.softKeyLeft, labelScreen.softKeyCenter, labelScreen.softKeyRight);
                }
                ScreenManager.showScreen(screen15);
                AppState.clearIndex(1318);
                return;
            case 143:
                AppState.setInt(2722, 0);
                AppState.setFromBuffer(1292, NetworkUtils.newStringBuffer().append(AppState.getString(771)).append(1 + (AppState.getInt(63) % 1000)));
                ScreenManager.showScreen(IOUtils.buildContactListScreen(ScreenManager.createScreen(4138), (MrimAccount) AppState.getAccount(), (Contact) null));
                return;
            case 144:
                ScreenManager.showScreen(IOUtils.buildContactListScreen(ScreenManager.createScreen(4169), (Account) null, AppState.getCurrentContact()));
                return;
            case 145:
                AppState.clearIndex(1315);
                NetworkUtils.showErrorOrConfirm(145, 727, ((MrimAccount) AppState.getCurrentContact().account).sendDeleteCommand(AppController.getStatusText()));
                return;
            case 146:
                AppState.setBool(1462, AppController.getMrimAccountList().size() > 1);
                ScreenManager.showScreen(ScreenManager.createScreen(2523));
                return;
            case 147:
                String[] langOptions = AppController.getLanguageOptions();
                if (langOptions != null) {
                    AppState.setObject(1285, (Object) langOptions[0]);
                    AppState.setFromBuffer(1284, NetworkUtils.newStringBuffer().append(AppState.getString(522)).append(langOptions[1]));
                    if (AppState.getInt(282) != 0) {
                        AppState.clearIndex(524);
                        AppState.clearIndex(525);
                    }
                    AppState.setFromPool(1286, 524);
                    if (AppState.getString(1289) != null) {
                        AppState.setFromPool(1286, 1289);
                        AppState.clearIndex(1289);
                    }
                    ScreenManager.showScreen(ScreenManager.createScreen(2482));
                    return;
                }
                break;
            case 148:
                return;
            case 149:
                return;
            case 150:
                AppController.prepareFormData();
                MrimContact mrimContact3 = (MrimContact) AppState.getCurrentContact();
                MrimAccount mrimAccount5 = (MrimAccount) mrimContact3.account;
                NetworkUtils.showErrorOrConfirm(150, 504, mrimAccount5.trySendData(mrimAccount5.createAndQueueCommand(new Object[]{AppController.createMrimPacket(mrimAccount5, 4104, new ByteBuffer().writeIntLE(4194304).writeStringLatin1(mrimContact3.simpleIdentifier).writeIntLE(0).writeIntLE(0).writeIntLE(4).writeIntLE(1)), ResourceManager.integerOf(10), mrimContact3, new Long(1L)})));
                return;
            case 151:
                ScreenManager.showScreen(ScreenManager.createScreen(3272));
                return;
            case 152:
                ConnectionThread.showMapView();
                ScreenManager.showScreen(ScreenManager.createScreen(3302));
                return;
            case 153:
                ResourceManager.showWiFiNetworks();
                return;
            case 154:
                ScreenManager.showScreen(ScreenManager.createScreen(2085));
                return;
            case 155:
                Vector contactIds = ConnectionThread.getAllContactIds();
                int count = Utils.vectorSize(contactIds);
                if (count == 0) {
                    AppController.showMessageById(404);
                    return;
                }
                Screen screen16 = ScreenManager.createScreen(2101);
                for (int i23 = 0; i23 < count; i23++) {
                    Object contactId = contactIds.elementAt(i23);
                    screen16.addItem(MenuItem.createCheckbox(ConnectionThread.getPhotoHost(contactId), !ConnectionThread.hiddenContacts.contains(contactId)));
                }
                ScreenManager.showScreen(screen16);
                return;
            case 156:
                IOUtils.showPhotoSelector();
                return;
            case 157:
                ScreenManager.pushScreen(ScreenManager.createScreen(4381));
                return;
            case 158:
                AppState.setInt(2122, AppState.getBool(1442) ? 407 : 408);
                Conversation.updateStatusText(411);
                ScreenManager.showScreen(ScreenManager.createScreen(2111));
                return;
            case 159:
                if (!AppState.getBool(266)) {
                    Vector mmpAccounts2 = AppController.getMmpAccountList();
                    int size13 = mmpAccounts2.size();
                    int i24 = size13;
                    if (size13 > 0) {
                        Screen screen17 = ScreenManager.createScreen(2140);
                        while (true) {
                            i24--;
                            if (i24 < 0) {
                                ScreenManager.showScreen(screen17);
                                return;
                            }
                            MrimAccount mrimAccount6 = (MrimAccount) mmpAccounts2.elementAt(i24);
                            int iconId = mrimAccount6.getIconId();
                            String str4 = mrimAccount6.login;
                            screen17.addIconItemWithData(iconId, str4, 153, str4);
                        }
                    }
                }
                ResourceManager.showWiFiNetworks();
                return;
            case 160:
                StringBuffer stringBuffer = new StringBuffer(AppState.getString(780));
                stringBuffer.append(AppState.getString(((MrimAccount) AppState.getAccount()).accountProfile.gender + 780));
                AppState.setFromBuffer(1337, stringBuffer);
                ResourceManager.lastTileLoadTime = System.currentTimeMillis();
                ScreenManager.showScreen(ScreenManager.createScreen(4258));
                return;
            case 161:
                NetworkUtils.showConfirmDialog(161, 872);
                return;
            case 162:
                Conversation.updateStatusText(411);
                ScreenManager.showScreen(ScreenManager.createScreen(4270));
                return;
            case 163:
                AppState.setInt(1577, 0);
                NetworkUtils.showAlertBuffer(163, NetworkUtils.newStringBuffer().append(AppState.getString(1028)));
                return;
            case 164:
                NetworkUtils.processRegForm();
                return;
            case 165:
                NetworkUtils.showConfirmDialog(165, 505);
                AppState.pool[1271] = NetworkUtils.newRequest();
                return;
            case 166:
                ScreenManager.showScreen(ScreenManager.createScreen(4179));
                return;
            case 167:
                ScreenManager.showScreen(ScreenManager.createScreen(2158));
                return;
            case 168:
                ResourceManager.showTosScreen();
                return;
            case 169:
                ResourceManager.showSavedLocations();
                return;
            case 170:
                Screen screen18 = ScreenManager.createScreen(2176);
                Vector vector2 = ((Conversation) AppState.pool[1255]).items;
                int size14 = vector2.size();
                while (true) {
                    size14--;
                    if (size14 < 0) {
                        ScreenManager.showScreen(screen18);
                        AppState.clearIndex(1255);
                        return;
                    } else {
                        ListItem listItem = (ListItem) vector2.elementAt(size14);
                        screen18.addIconItemWithData(-1, listItem.getText(), 0, listItem);
                    }
                }
            case 171:
                NetworkUtils.showAlertById(171, 787);
                AppState.setInt(286, 1);
                return;
            case 172:
                Screen screen19 = ScreenManager.createScreen(2186);
                Vector onlineAccounts = AppController.getOnlineMrimAccounts();
                int size15 = onlineAccounts.size();
                while (true) {
                    size15--;
                    if (size15 < 0) {
                        ScreenManager.showScreen(screen19);
                        return;
                    } else {
                        MrimAccount mrimAccount7 = (MrimAccount) onlineAccounts.elementAt(size15);
                        screen19.addIconItemWithData(156, mrimAccount7.login, 0, mrimAccount7);
                    }
                }
            case 173:
                NetworkUtils.showAlertById(173, 416);
                return;
            case 174:
                ScreenManager.showScreen(ScreenManager.createScreen(2198));
                return;
            case 175:
                ScreenManager.showScreen(ScreenManager.createScreen(2218));
                return;
            case 176:
                return;
            case 177:
                AppState.setCurrentEntity((Object) null);
                ScreenManager.showScreen(ScreenManager.createScreen(2247));
                return;
            case 178:
                ScreenManager.showScreen(ScreenManager.createScreen(2279));
                return;
            case 179:
                NetworkUtils.showConfirmDialog(179, 504);
                return;
            case 180:
                break;
        }
        AppController.finishScreenBuild();
    }

    /* renamed from: a */
    public static final void onMenuItemSelected() {
        int errorCode;
        int errorCode2;
        int errorCode3;
        int errorCode4;
        int errorCode5;
        int deleteResult;
        int unblockResult;
        int blockResult;
        int sendResult;
        int errorCode6;
        int errorCode7;
        int errorCode8;
        int configResult;
        int sendResult2;
        int modifyResult;
        AppController.needsRepaint = true;
        AppController.needsLayoutUpdate = true;
        Screen currentScreen = ScreenManager.getCurrentScreen();
        String title = ScreenManager.getCurrentTitle();
        int action = ScreenManager.getCurrentWidth();
        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
        Object obj = menuItem == null ? null : menuItem.data;
        int nextScreen = 0;
        switch (ScreenManager.getCurrentScreen().screenId) {
            case 1:
                nextScreen = AppController.handleMapMenuOption(action);
                break;
            case 2:
                nextScreen = 0;
                break;
            case 3:
                nextScreen = IOUtils.handleStatusChange(action);
                break;
            case 4:
                nextScreen = ContactListManager.getSelectedContact();
                break;
            case 5:
                nextScreen = AppController.handleChatSettingsOption(action);
                break;
            case 6:
                if (!AppState.getBool(1547)) {
                    ConnectionThread.toggleMapControls(currentScreen);
                }
                nextScreen = 0;
                break;
            case 7:
                nextScreen = 0;
                break;
            case 8:
                nextScreen = AppController.handleSettingsOption(action);
                break;
            case 9:
                nextScreen = 0;
                break;
            case 10:
                nextScreen = 55;
                break;
            case 11:
                nextScreen = AppController.handleLeftKey();
                break;
            case 13:
                nextScreen = -1;
                break;
            case 14:
                nextScreen = ResourceManager.parseBalance();
                break;
            case 15:
                nextScreen = AppController.handleMenuAction(title, obj);
                break;
            case 16:
                nextScreen = 0;
                break;
            case 17:
                nextScreen = AppController.handleAccountOption(action);
                break;
            case 18:
                nextScreen = 0;
                break;
            case 19:
                NetworkUtils.processScreenForm();
                String[] phoneNumbers = Utils.getPhoneNumbers(false);
                Object[] objArr = new Object[phoneNumbers.length + 1];
                objArr[0] = Utils.defaultStr(AppState.getString(1302));
                for (int i = 0; i < phoneNumbers.length; i++) {
                    objArr[i + 1] = phoneNumbers[i];
                }
                Contact contact = AppState.getCurrentContact();
                if (contact.isOnline()) {
                    contact.setDisplayName((String) objArr[0]);
                    AppController.needsLayoutUpdate = true;
                    modifyResult = 0;
                } else {
                    modifyResult = contact.account.validateModify(contact, objArr);
                }
                nextScreen = 0 != modifyResult ? AppController.showError(modifyResult) : 0;
                break;
            case 20:
                nextScreen = AppController.handleProfileAction(action);
                break;
            case 21:
                NetworkUtils.processScreenForm();
                nextScreen = AppState.getAccount() instanceof XmppProtocol ? ((XmppProtocol) AppState.getAccount()).addNewContact() : 0;
                break;
            case 22:
                NetworkUtils.processScreenForm();
                MrimAccount mrimAccount = (MrimAccount) AppState.getAccount();
                String displayName = Utils.defaultStr(AppState.getString(1302));
                String[] phoneNumbers2 = Utils.getPhoneNumbers(false);
                if (!mrimAccount.isConnected()) {
                    sendResult2 = 299;
                } else if (Utils.nonEmpty(displayName)) {
                    int length = phoneNumbers2.length;
                    if (length == 0) {
                        sendResult2 = 709;
                    } else {
                        Enumeration contactEnum = mrimAccount.contactMap.elements();
                        while (true) {
                            if (contactEnum.hasMoreElements()) {
                                MrimContact mrimContact = (MrimContact) contactEnum.nextElement();
                                int i2 = length;
                                do {
                                    i2--;
                                    if (i2 < 0) {
                                        break;
                                    }
                                } while (!mrimContact.isInGroup(phoneNumbers2[i2]));
                                sendResult2 = 486;
                            } else {
                                MrimContactGroup contactGroup = mrimAccount.getFirstContactGroup();
                                ByteBuffer packetBuf = new ByteBuffer().writeIntLE(1048576).writeIntLE(103).writeStringLatin1(AppState.getString(1233)).writeStringUTF16(displayName);
                                String emailsJoined = Utils.joinComma(phoneNumbers2);
                                sendResult2 = mrimAccount.trySendData(mrimAccount.createAndQueueCommand(new Object[]{AppController.createMrimPacket(mrimAccount, 4121, packetBuf.writeStringLatin1(emailsJoined).writeZeros(8)), ResourceManager.integerOf(5), displayName, emailsJoined, contactGroup}));
                            }
                        }
                    }
                } else {
                    sendResult2 = 708;
                }
                nextScreen = 0 != sendResult2 ? AppController.showError(sendResult2) : 0;
                break;
            case 23:
                nextScreen = 0;
                break;
            case 24:
                nextScreen = 0;
                break;
            case 25:
                nextScreen = AppController.handleInputAction(action, obj);
                break;
            case 26:
                NetworkUtils.processScreenForm();
                AppState.setInt(4305, AppState.getInt(72));
                ScreenManager.initializeFonts();
                AppState.getCanvas().updateFullScreenMode();
                TabBar.initialize();
                ResourceManager.resetClock();
                nextScreen = 0;
                break;
            case 27:
                nextScreen = NetworkUtils.processScreenForm();
                break;
            case 28:
                nextScreen = NetworkUtils.processScreenForm();
                break;
            case 29:
                NetworkUtils.processScreenForm();
                if (AppState.getInt(4305) != AppState.getInt(243)) {
                    TabBar.initialize();
                }
                nextScreen = 0;
                break;
            case 30:
                nextScreen = IOUtils.handleContactGroupAction(title, action);
                break;
            case 32:
                nextScreen = ResourceManager.handleDropdownSelect(title, menuItem);
                break;
            case 33:
                nextScreen = NetworkUtils.processScreenForm();
                break;
            case 34:
                int intVal = AppState.getInt(1510);
                Account account = AppState.getAccount();
                if (account != null) {
                    account.syncArray[intVal + intVal + 1] = 0;
                    account.syncArray[intVal + intVal] = 0;
                } else {
                    for (int i3 = 0; i3 < 4; i3++) {
                        AppController.addTrafficCount(i3, intVal, 0);
                        AppController.addTrafficCount(i3, intVal, 1);
                    }
                }
                nextScreen = 0;
                break;
            case 35:
                nextScreen = AppController.handleConnectionOption(action);
                break;
            case 36:
                nextScreen = 0;
                break;
            case 37:
                nextScreen = -1;
                break;
            case 38:
                nextScreen = 0;
                break;
            case 39:
                nextScreen = AppController.handleAction(obj);
                break;
            case 40:
                nextScreen = 0;
                break;
            case 41:
                nextScreen = -1;
                break;
            case 42:
                nextScreen = -1;
                break;
            case 43:
                AppState.setInt(1514, currentScreen.scrollOffset);
                AppState.setObject(1345, (Object) title);
                Message message = (Message) obj;
                AppState.setObject(1346, (Object) (message != null ? message.from : null));
                nextScreen = 0;
                break;
            case 44:
                nextScreen = -1;
                break;
            case 45:
                nextScreen = -1;
                break;
            case 46:
                nextScreen = 0;
                break;
            case 47:
                nextScreen = 0;
                break;
            case 48:
                nextScreen = -1;
                break;
            case 49:
                NetworkUtils.processScreenForm();
                nextScreen = (AppState.getInt(4308) != 4 || 0 == (configResult = ((MrimAccount) AppState.getAccount()).setConfiguration(((AppState.getInt(4305) - 157) << 8) + 4))) ? 0 : AppController.showError(configResult);
                break;
            case 50:
                NetworkUtils.processScreenForm();
                nextScreen = 0;
                break;
            case 51:
                nextScreen = ResourceManager.handleChatRoomAction(title);
                break;
            case 52:
                nextScreen = 0;
                break;
            case 53:
                nextScreen = IOUtils.handleMailForwardAction(title);
                break;
            case 54:
                NetworkUtils.processScreenForm();
                Vector params = NetworkUtils.newVector();
                StringBuffer sb = NetworkUtils.newStringBuffer();
                String recipientStr = Utils.defaultStr(AppState.getString(1352));
                int length2 = recipientStr.length();
                int i4 = 0;
                while (i4 <= length2) {
                    char ch = i4 == length2 ? ';' : recipientStr.charAt(i4);
                    char c = ch;
                    if (ch == ';' || c == ',' || c == ' ') {
                        String token = StringUtils.extractBuffer(sb);
                        if (!StringUtils.isEmpty(token)) {
                            params.addElement(token);
                        }
                    } else {
                        sb.append(c);
                    }
                    i4++;
                }
                if (Utils.vectorSize(params) == 0) {
                    errorCode8 = AppController.showError(873);
                } else {
                    boolean z = false;
                    int count = Utils.vectorSize(params);
                    while (true) {
                        count--;
                        if (count < 0) {
                            errorCode8 = z ? AppController.showError(876) : 0;
                            break;
                        } else {
                            String str = (String) params.elementAt(count);
                            int atIdx = str.indexOf(64);
                            if (atIdx <= 0 || str.indexOf(46) <= 0 || str.indexOf(32) >= 0 || atIdx != str.lastIndexOf(64) || str.indexOf(44) >= 0) {
                                z = true;
                            }
                        }
                    }
                }
                nextScreen = errorCode8;
                break;
            case 55:
                nextScreen = -1;
                break;
            case 56:
                NetworkUtils.processScreenForm();
                if (AppState.getBool(90)) {
                    AppController.updateTabBar();
                }
                nextScreen = 0;
                break;
            case 57:
                nextScreen = -1;
                break;
            case 58:
                nextScreen = AppController.handleGroupSelection(action);
                break;
            case 59:
                nextScreen = ResourceManager.applyVersionLabel();
                break;
            case 60:
                nextScreen = AppController.processInputText(title);
                break;
            case 61:
                nextScreen = 42;
                break;
            case 62:
                nextScreen = IOUtils.handleMailMenuAction(title, action);
                break;
            case 63:
                AppController.needsLayoutUpdate = true;
                AppState.setScreen(AppState.getCanvas().updateCommands());
                onScreenClosed();
                nextScreen = 84;
                break;
            case 64:
                nextScreen = AppController.handleAccountSwitchOption(action);
                break;
            case 65:
                NetworkUtils.processScreenForm();
                AppState.pool[1314] = Utils.splitNonEmpty(AppState.getCurrentMrimContact().contactGroupsStr, ',').elementAt(AppState.getInt(1493));
                nextScreen = 0;
                break;
            case 66:
                NetworkUtils.processScreenForm();
                int addResult = ((ContactInfo) AppState.pool[1319]).getAccount().validateGroupAdd(Utils.defaultStr(AppState.getString(1320)), Utils.defaultStr(AppState.getString(1321)), Utils.defaultStr(AppState.getString(1322)), (ContactGroup) AppState.getVector(1324).elementAt(AppState.getInt(1507)), AppState.getBool(1509));
                nextScreen = 0 != addResult ? AppController.showError(addResult) : 0;
                break;
            case 67:
                nextScreen = AppController.handleSoftKeyAction(title);
                break;
            case 68:
                nextScreen = AppController.handleEnterKey();
                break;
            case 69:
                NetworkUtils.processScreenForm();
                int createResult = AppState.getAccount().validateGroupCreate(Utils.defaultStr(AppState.getString(1295)));
                nextScreen = 0 != createResult ? AppController.showError(createResult) : 0;
                break;
            case 70:
                NetworkUtils.processScreenForm();
                int renameResult = AppState.getCurrentGroup().rename(Utils.defaultStr(AppState.getString(1306)));
                nextScreen = 0 != renameResult ? AppController.showError(renameResult) : 0;
                break;
            case 71:
                nextScreen = ResourceManager.deleteSelectedEntity();
                break;
            case 72:
                nextScreen = -1;
                break;
            case 73:
                AppState.pool[1319] = obj;
                nextScreen = 0;
                break;
            case 74:
                nextScreen = -1;
                break;
            case 75:
                nextScreen = -1;
                break;
            case 76:
                nextScreen = XmppMailRuProtocol.performLogin();
                break;
            case 77:
                nextScreen = AppController.handleInviteResult();
                break;
            case 78:
                nextScreen = -1;
                break;
            case 79:
                onScreenClosed();
                onScreenClosed();
                nextScreen = 0;
                break;
            case 80:
                nextScreen = AppController.handleNotificationOption(action);
                break;
            case 81:
                nextScreen = -1;
                break;
            case 82:
                nextScreen = -1;
                break;
            case 83:
                nextScreen = AppController.handleHashKey();
                break;
            case 84:
                nextScreen = ResourceManager.handleMessageInputAction(title, action);
                break;
            case 85:
                nextScreen = -1;
                break;
            case 86:
                nextScreen = AppController.handleSearchAction(obj);
                break;
            case 87:
                nextScreen = ResourceManager.handleChatInputAction(title);
                break;
            case 88:
                nextScreen = AppController.handleThemeOption(action);
                break;
            case 89:
                nextScreen = -1;
                break;
            case 90:
                nextScreen = AppController.handleEventObject(obj);
                break;
            case 91:
                nextScreen = AppController.getThemeBackground(action);
                break;
            case 92:
                nextScreen = IOUtils.handleContactMenuAction(title, action);
                break;
            case 93:
                nextScreen = AppController.handleSoundOption(action);
                break;
            case 94:
                nextScreen = AppController.processPhoneInput(title);
                break;
            case 95:
                nextScreen = AppController.validateServerAddress(title);
                break;
            case 96:
                nextScreen = ((ContactInfo) AppState.pool[1315]).isMrimContact() ? 130 : ((ContactInfo) AppState.pool[1315]).isXmppContact() ? -1 : AppState.getInt(3650);
                break;
            case 97:
                nextScreen = AppController.handleSearchResultAction(obj);
                break;
            case 98:
                nextScreen = AppController.processPhoneInput(title);
                break;
            case 99:
                nextScreen = AppController.openUrl(title);
                break;
            case 100:
                nextScreen = IOUtils.handleMapSearch(action, obj);
                break;
            case 101:
                nextScreen = AppController.handleConversationAction(obj);
                break;
            case 102:
                nextScreen = -1;
                break;
            case 103:
                nextScreen = ((ContactInfo) AppState.pool[1319]).isMrimContact() ? 130 : AppState.getInt(3650);
                break;
            case 104:
                nextScreen = AppController.getThemeColor(action);
                break;
            case 105:
                int loginResult = XmppMailRuProtocol.performLogin();
                nextScreen = 0 == loginResult ? 4 : loginResult;
                break;
            case 106:
                nextScreen = 0;
                break;
            case 107:
                nextScreen = -1;
                break;
            case 108:
                nextScreen = AppController.handleContactListKey();
                break;
            case 109:
                nextScreen = ((MmpProtocol) AppState.getAccount()).scheduleVersionUpdate(action);
                break;
            case 110:
                NetworkUtils.processScreenForm();
                nextScreen = StringUtils.isEmpty(Utils.defaultStr(AppState.getString(1249))) ? AppController.showError(352) : 0;
                break;
            case 111:
                nextScreen = AppController.handleMapSearchAction(obj);
                break;
            case 112:
                nextScreen = -1;
                break;
            case 113:
                nextScreen = XmppMailRuProtocol.handleMapAction(action);
                break;
            case 114:
                NetworkUtils.processScreenForm();
                String locationName = Utils.defaultStr(AppState.getString(1250));
                if (StringUtils.isEmpty(locationName)) {
                    errorCode7 = AppController.showError(372);
                } else {
                    long lon = MapRenderer.currentLon;
                    long lat = MapRenderer.currentLat;
                    ListItem listItem = MapRenderer.tooltipItem;
                    if (listItem != null && listItem.isSelected()) {
                        lon = listItem.getWidth();
                        lat = listItem.getBaseHeight();
                        listItem.select();
                    }
                    MapPoint mapPoint = new MapPoint(locationName, 0L, 0L, 0L, 0L, lon, lat, AppState.getInt(39));
                    mapPoint.height = 4;
                    Vector screenStack = AppState.getVector(1401);
                    XmppContactGroup.addMapPointIfNew(screenStack, mapPoint, 0, 50);
                    XmppContactGroup.saveMapPoints(screenStack, 226);
                    MapRenderer.navigateToMapPoint(mapPoint);
                    errorCode7 = 0;
                }
                nextScreen = errorCode7;
                break;
            case 115:
                NetworkUtils.processScreenForm();
                String messageText4 = Utils.defaultStr(AppState.getString(1286));
                if (StringUtils.isEmpty(messageText4)) {
                    errorCode6 = AppController.showError(523);
                } else {
                    MrimAccount mrimAccount2 = (MrimAccount) AppState.getCurrentContact().account;
                    boolean flag = AppState.getBool(1507);
                    long timestamp = AppState.getLong(1469);
                    if (mrimAccount2.isConnected()) {
                        IOUtils.postEvent((Object) AppState.getString(494));
                        sendResult = mrimAccount2.trySendData(AppController.createMrimPacket(mrimAccount2, 4196, new ByteBuffer().writeIntLE(flag ? 5 : 20).writeStringUTF16(messageText4).writeLong(timestamp)));
                    } else {
                        sendResult = 299;
                    }
                    errorCode6 = 0 != sendResult ? AppController.showError(sendResult) : 0;
                }
                nextScreen = errorCode6;
                break;
            case 116:
                nextScreen = AppController.handleMapResultAction(obj);
                break;
            case 117:
                nextScreen = AppController.processLoginField(title);
                break;
            case 118:
                nextScreen = AppController.handleFileAction(obj);
                break;
            case 119:
                nextScreen = AppController.handleChatRoomOption(action);
                break;
            case 120:
                nextScreen = AppController.handleIncomingCall(obj);
                break;
            case 121:
                nextScreen = AppController.handleChatListOption(action);
                break;
            case 122:
                nextScreen = AppController.handlePresenceAction();
                break;
            case 123:
                nextScreen = AppController.handleLocationAction(obj);
                break;
            case 124:
                nextScreen = 0;
                break;
            case 125:
                Contact selectedContact = (Contact) obj;
                nextScreen = (null == selectedContact || 0 == (blockResult = selectedContact.validateBlock())) ? 0 : AppController.showError(blockResult);
                break;
            case 126:
                Contact contactToDelete = (Contact) obj;
                nextScreen = (null == contactToDelete || 0 == (unblockResult = contactToDelete.validateUnblock())) ? 0 : AppController.showError(unblockResult);
                break;
            case 127:
                Contact contactToBlock = (Contact) obj;
                nextScreen = (null == contactToBlock || 0 == (deleteResult = contactToBlock.validateDelete())) ? 0 : AppController.showError(deleteResult);
                break;
            case 128:
                AppState.getCurrentContact().initMessageBuffer();
                nextScreen = 4;
                break;
            case 129:
                NetworkUtils.processScreenForm();
                AppState.setInt(44, 1);
                int intVal2 = AppState.getInt(1438);
                if (intVal2 > 0) {
                    AppState.setInt(266, 1);
                    AppState.setObject(267, (Object) Utils.splitAndGet(1252, intVal2));
                } else {
                    AppState.setObject(267, (Object) AppState.emptyStr);
                }
                nextScreen = 0;
                break;
            case 130:
                nextScreen = AppController.handleScreenAction(action);
                break;
            case 131:
                nextScreen = AppController.processSearchQuery(title);
                break;
            case 132:
                nextScreen = AppController.mapKeyToAction(action);
                break;
            case 133:
                nextScreen = 0;
                break;
            case 134:
                nextScreen = 0;
                break;
            case 135:
                nextScreen = 0;
                break;
            case 136:
                nextScreen = 0;
                break;
            case 137:
                nextScreen = -1;
                break;
            case 138:
                nextScreen = 0;
                break;
            case 139:
                nextScreen = 129;
                break;
            case 140:
                nextScreen = NetworkUtils.processScreenForm();
                break;
            case 141:
                nextScreen = -1;
                break;
            case 142:
                if (obj == null) {
                    errorCode5 = -1;
                } else if (obj instanceof String) {
                    AppState.pool[1319] = ContactInfo.createForAccount(AppState.getCurrentContact().account).setEmailAddress((String) obj).setDisplayName((String) obj);
                    errorCode5 = 66;
                } else {
                    errorCode5 = AppController.showError(773);
                }
                nextScreen = errorCode5;
                break;
            case 143:
                NetworkUtils.processScreenForm();
                String chatName = Utils.defaultStr(AppState.getString(1292));
                if (StringUtils.isEmpty(chatName)) {
                    errorCode4 = AppController.showError(301);
                } else {
                    Vector checkedItems = IOUtils.getCheckedItems(currentScreen, 3);
                    if (checkedItems.size() == 0) {
                        errorCode4 = AppController.showError(775);
                    } else {
                        MrimAccount mrimAccount3 = (MrimAccount) AppState.getAccount();
                        boolean flag2 = AppState.getBool(2722);
                        ByteBuffer buffer = new ByteBuffer();
                        int size = checkedItems.size();
                        int i5 = size;
                        ByteBuffer membersBuf = buffer.writeIntLE(size);
                        while (true) {
                            i5--;
                            if (i5 < 0) {
                                ByteBuffer wrappedBuf = new ByteBuffer().writeBufferIntLen(membersBuf);
                                Object[] objArr2 = new Object[3];
                                objArr2[0] = AppController.createMrimPacket(mrimAccount3, 4121, new ByteBuffer().writeIntLE(128).writeZeros(8).writeStringUTF16(chatName).writeZeros(12).writeBufferIntLen(flag2 ? wrappedBuf.writeStringLatin1(mrimAccount3.login) : wrappedBuf));
                                objArr2[1] = ResourceManager.integerOf(15);
                                objArr2[2] = chatName;
                                int sendResult3 = mrimAccount3.trySendData(mrimAccount3.createAndQueueCommand(objArr2));
                                if (0 != sendResult3) {
                                    errorCode4 = AppController.showError(sendResult3);
                                } else {
                                    AppState.addInt(63, 1);
                                    errorCode4 = 0;
                                }
                            } else {
                                membersBuf.writeStringLatin1((String) checkedItems.elementAt(i5));
                            }
                        }
                    }
                }
                nextScreen = errorCode4;
                break;
            case 144:
                Vector checkedItems2 = IOUtils.getCheckedItems(currentScreen, 0);
                if (checkedItems2.size() == 0) {
                    errorCode3 = AppController.showError(775);
                } else {
                    MrimContact mrimContact2 = (MrimContact) AppState.getCurrentContact();
                    MrimAccount mrimAccount4 = (MrimAccount) mrimContact2.account;
                    ByteBuffer buffer2 = new ByteBuffer();
                    int size2 = checkedItems2.size();
                    int i6 = size2;
                    ByteBuffer membersBuf2 = buffer2.writeIntLE(size2);
                    while (true) {
                        i6--;
                        if (i6 < 0) {
                            int sendResult4 = mrimAccount4.trySendData(mrimAccount4.createAndQueueCommand(new Object[]{AppController.createMrimPacket(mrimAccount4, 4104, new ByteBuffer().writeIntLE(4194304).writeStringLatin1(mrimContact2.simpleIdentifier).writeIntLE(0).writeIntLE(0).writeBufferIntLen(new ByteBuffer().writeIntLE(3).writeBufferIntLen(membersBuf2))), ResourceManager.integerOf(10), mrimContact2, new Long(2L)}));
                            errorCode3 = 0 != sendResult4 ? AppController.showError(sendResult4) : 0;
                        } else {
                            membersBuf2.writeStringLatin1((String) checkedItems2.elementAt(i6));
                        }
                    }
                }
                nextScreen = errorCode3;
                break;
            case 145:
                nextScreen = -1;
                break;
            case 146:
                nextScreen = AppController.handleGroupRename(action);
                break;
            case 147:
                NetworkUtils.processScreenForm();
                String messageText6 = Utils.defaultStr(AppState.getString(1286));
                if (StringUtils.isEmpty(messageText6)) {
                    errorCode2 = AppController.showError(523);
                } else {
                    MrimAccount mrimAccount5 = (MrimAccount) AppState.getAccount();
                    new AsyncTask(17, new ByteBuffer().writeCompressed(1442705).writeCompressed(1049531).writeUInt(4022591).writeRawString(mrimAccount5.login).writeUInt(4022822).writeRawString(mrimAccount5.password).writeCompressed(459757).writeCompressed(459750).writeRawString(Conversation.urlEncodeCyrillic((Object) messageText6)).writeRawString(Utils.defaultStr(AppState.getBool(1468) ? AppState.getString(1285) : null)).getStringAndClear());
                    AppState.addInt(282, 1);
                    errorCode2 = 0;
                }
                nextScreen = errorCode2;
                break;
            case 148:
                nextScreen = 0;
                break;
            case 149:
                nextScreen = 0;
                break;
            case 150:
                nextScreen = -1;
                break;
            case 151:
                nextScreen = AppController.handleExtSettingsOption(action);
                break;
            case 152:
                nextScreen = AppController.handleContactOption(action);
                break;
            case 153:
                nextScreen = ResourceManager.setSelectedObject(obj);
                break;
            case 154:
                NetworkUtils.processScreenForm();
                String msgId = AppState.getString(1253);
                long lon2 = MapRenderer.currentLon;
                long lat2 = MapRenderer.currentLat;
                ListItem tooltipItem2 = MapRenderer.tooltipItem;
                if (tooltipItem2 != null && tooltipItem2.isSelected()) {
                    lon2 = tooltipItem2.getWidth();
                    lat2 = tooltipItem2.getBaseHeight();
                    tooltipItem2.select();
                }
                String msgId2 = AppState.getString(1254);
                long j = lon2;
                long j2 = lat2;
                if (msgId != null) {
                    XmppContactGroup.sharedContactList.addElement(new Object[]{msgId, new long[]{j, j2}, msgId2});
                }
                long j3 = lon2;
                long j4 = lat2;
                if (msgId != null) {
                    String sessionKey = Utils.defaultStr(AppState.getString(223));
                    ByteBuffer requestBuf = new ByteBuffer().writeCompressed(3150648).writeUInt(15713).writeRawString(msgId).writeUInt(4022822).writeLongAsString(j3).writeUInt(4023078).writeLongAsString(j4).writeUInt(4023334).writeRawString(sessionKey).writeUInt(4023590).writeRawString(new ByteBuffer().writeRawString(sessionKey).writeCompressed(396139).writeLongAsString(j3).encryptMD5().toHexString());
                    if (msgId2 != null) {
                        requestBuf.writeUInt(4023846).writeEncodedString(msgId2);
                    }
                    if (AppState.getBool(266)) {
                        String msgId3 = AppState.getString(267);
                        if (Utils.nonEmpty(msgId3)) {
                            requestBuf.writeUInt(4024102).writeEncodedString(msgId3);
                        }
                    }
                    new AsyncTask(16, requestBuf.getStringAndClear());
                }
                MapRenderer.needsRedraw = true;
                nextScreen = 0;
                break;
            case 155:
                Vector contactIds = ConnectionThread.getAllContactIds();
                StringBuffer sb2 = NetworkUtils.newStringBuffer();
                Vector vector = currentScreen.menuItems;
                int size3 = vector.size();
                for (int i7 = 0; i7 < size3; i7++) {
                    if (!((Boolean) ((MenuItem) vector.elementAt(i7)).data).booleanValue()) {
                        sb2.append(contactIds.elementAt(i7)).append((char) 0);
                    }
                }
                String hiddenStr = NetworkUtils.bufToStringCached(sb2);
                ConnectionThread.hiddenContacts = Utils.split(hiddenStr, (char) 0);
                AppState.setObject(264, (Object) hiddenStr);
                nextScreen = 0;
                break;
            case 156:
                nextScreen = IOUtils.applyPhotoSelection();
                break;
            case 157:
                nextScreen = 0;
                break;
            case 158:
                nextScreen = AppController.handleViewOption(action);
                break;
            case 159:
                nextScreen = AppController.handleItemAction(obj);
                break;
            case 160:
                nextScreen = ResourceManager.syncAndReturn();
                break;
            case 161:
                nextScreen = -1;
                break;
            case 162:
                nextScreen = AppController.handleChatDetailOption(action);
                break;
            case 163:
                nextScreen = AppController.handleSendKey();
                break;
            case 164:
                NetworkUtils.processScreenForm();
                String loginLower = XmppMailRuProtocol.getLoginLowerCase();
                String fullLogin = loginLower;
                if (!XmppMailRuProtocol.isMailRuDomain(loginLower)) {
                    fullLogin = StringUtils.concat(fullLogin, Utils.splitAndGet(694, AppState.getInt(1474)));
                }
                if (XmppMailRuProtocol.isValidUsername(fullLogin)) {
                    String str2 = fullLogin;
                    String password = Utils.defaultStr(AppState.getString(1293));
                    String firstName = Utils.defaultStr(AppState.getString(1284));
                    int intVal3 = AppState.getInt(4305);
                    AppState.pool[1271] = NetworkUtils.createRegRequest(str2, 0, password, firstName, 0 == intVal3 ? Utils.defaultStr(AppState.getString(1287)) : (String) Utils.splitNonEmpty(AppState.getString(810), (char) 0).elementAt(intVal3), Utils.defaultStr(AppState.getString(1288)), Utils.defaultStr(AppState.getString(1298)), Utils.defaultStr(AppState.getString(1299)), AppState.getInt(1489), AppState.getInt(1488), AppState.getInt(1491), AppState.getInt(1481), AppState.getInt(1480), AppState.getString(1342), AppState.getString(1343));
                    errorCode = 13;
                } else {
                    errorCode = AppController.showError(559);
                }
                nextScreen = errorCode;
                break;
            case 165:
                nextScreen = -1;
                break;
            case 166:
                nextScreen = AppController.handleChatOption(action);
                break;
            case 167:
                nextScreen = AppController.handleMailboxOption(action);
                break;
            case 168:
                nextScreen = ResourceManager.collectInvitees(currentScreen);
                break;
            case 169:
                nextScreen = ResourceManager.applyLocationProfile(obj);
                break;
            case 170:
                nextScreen = AppController.handleFormSubmit(obj);
                break;
            case 171:
                nextScreen = AppController.handleRightKey();
                break;
            case 172:
                nextScreen = AppController.handleObjectAction(obj);
                break;
            case 173:
                nextScreen = AppController.handleInviteAction();
                break;
            case 174:
                nextScreen = 0;
                break;
            case 175:
                NetworkUtils.processScreenForm();
                if (AppState.getBool(280)) {
                    IOUtils.requestNearbyPeople();
                }
                nextScreen = 0;
                break;
            case 176:
                nextScreen = AppController.handleStarAction(obj);
                break;
            case 177:
                nextScreen = ResourceManager.handleSearchResultAction(action);
                break;
            case 178:
                nextScreen = AppController.handleEditAction(action);
                break;
            case 179:
                nextScreen = -1;
                break;
            case 180:
                nextScreen = -1;
                break;
        }
        System.out.println("[DEBUG] onMenuItemSelected: screenId=" + ScreenManager.getCurrentScreen().screenId + " nextScreen=" + nextScreen + " softKeyLeft=" + currentScreen.softKeyLeft);
        if (nextScreen != -1) {
            if (nextScreen == 12) {
                onScreenClosed();
                return;
            }
            if (nextScreen != 0) {
                openScreen(nextScreen);
                return;
            }
            int i8 = currentScreen.softKeyLeft;
            if (i8 != 200) {
                int i9 = i8 == 199 ? action : i8;
                int i10 = i9;
                System.out.println("[DEBUG] onMenuItemSelected: softKeyLeft action=" + i10);
                if (i9 == 12) {
                    onScreenClosed();
                } else if (i10 != 0) {
                    openScreen(i10);
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* renamed from: b */
    public static final void onMenuItemAction() {
        int sendMsgResult;
        AppController.needsRepaint = true;
        AppController.needsLayoutUpdate = true;
        Screen currentScreen = ScreenManager.getCurrentScreen();
        int i = ScreenManager.getCurrentScreen().screenId;
        ScreenManager.getCurrentTitle();
        MenuItem menuItem = ScreenManager.getCurrentMenuItem();
        Object obj = menuItem == null ? null : menuItem.data;
        int result = 0;
        switch (i) {
            case 1:
                result = 0;
                break;
            case 2:
                result = 0;
                break;
            case 3:
                result = 0;
                break;
            case 4:
                result = ContactListManager.onContactAction(obj);
                break;
            case 5:
                result = 0;
                break;
            case 6:
                result = ConnectionThread.handleMapBack(currentScreen);
                break;
            case 7:
                result = 0;
                break;
            case 8:
                result = 0;
                break;
            case 9:
                result = 0;
                break;
            case 10:
                result = 0;
                break;
            case 11:
                result = 0;
                break;
            case 13:
                result = 0;
                break;
            case 14:
                result = 0;
                break;
            case 15:
                result = 0;
                break;
            case 16:
                result = 0;
                break;
            case 17:
                result = 0;
                break;
            case 18:
                result = 0;
                break;
            case 19:
                result = 0;
                break;
            case 20:
                result = 0;
                break;
            case 21:
                result = 0;
                break;
            case 22:
                result = 0;
                break;
            case 23:
                result = 0;
                break;
            case 24:
                result = 0;
                break;
            case 25:
                result = 0;
                break;
            case 26:
                AppController.needsLayoutUpdate = true;
                result = 0;
                break;
            case 27:
                result = 0;
                break;
            case 28:
                result = 0;
                break;
            case 29:
                result = 0;
                break;
            case 30:
                result = 0;
                break;
            case 32:
                result = 0;
                break;
            case 33:
                result = 0;
                break;
            case 34:
                result = 0;
                break;
            case 35:
                result = 0;
                break;
            case 36:
                result = ResourceManager.selectMailAccount(obj);
                break;
            case 37:
                result = 0;
                break;
            case 38:
                result = 0;
                break;
            case 39:
                result = 0;
                break;
            case 40:
                result = 0;
                break;
            case 41:
                AppState.clearIndex(1271);
                result = 0;
                break;
            case 42:
                result = 0;
                break;
            case 43:
                result = 0;
                break;
            case 44:
                result = 0;
                break;
            case 45:
                result = 0;
                break;
            case 46:
                result = 0;
                break;
            case 47:
                result = 0;
                break;
            case 48:
                AppState.clearIndex(1271);
                result = 0;
                break;
            case 49:
                result = 0;
                break;
            case 50:
                result = 0;
                break;
            case 51:
                result = 0;
                break;
            case 52:
                result = 0;
                break;
            case 53:
                result = 0;
                break;
            case 54:
                result = 0;
                break;
            case 55:
                result = -1;
                break;
            case 56:
                result = 0;
                break;
            case 57:
                result = 0;
                break;
            case 58:
                result = 0;
                break;
            case 59:
                result = 0;
                break;
            case 60:
                result = 0;
                break;
            case 61:
                result = 12;
                break;
            case 62:
                result = 0;
                break;
            case 63:
                String inputText = XmppContactGroup.getTextInputValue();
                if (!StringUtils.isEmpty(inputText) && 0 != (sendMsgResult = AppState.getCurrentContact().sendMessage(inputText))) {
                    onScreenClosed();
                    IOUtils.postEvent((Object) AppState.getString(sendMsgResult));
                }
                AppState.setInt(1456, 0);
                AppState.clearIndex(1279);
                AppController.needsLayoutUpdate = true;
                AppState.setScreen(AppState.getCanvas());
                onScreenClosed();
                result = 40;
                break;
            case 64:
                result = 0;
                break;
            case 65:
                result = 0;
                break;
            case 66:
                result = 0;
                break;
            case 67:
                result = 0;
                break;
            case 68:
                result = AppController.handleBackKey();
                break;
            case 69:
                result = 0;
                break;
            case 70:
                result = 0;
                break;
            case 71:
                result = 0;
                break;
            case 72:
                result = 0;
                break;
            case 73:
                result = 0;
                break;
            case 74:
                result = 0;
                break;
            case 75:
                result = 0;
                break;
            case 76:
                result = 0;
                break;
            case 77:
                result = 0;
                break;
            case 78:
                result = 0;
                break;
            case 79:
                result = 0;
                break;
            case 80:
                result = 0;
                break;
            case 81:
                result = 0;
                break;
            case 82:
                result = 0;
                break;
            case 83:
                result = AppController.handleHashKey();
                break;
            case 84:
                onScreenClosed();
                result = 0;
                break;
            case 85:
                AppController.clearMapPoints();
                result = 0;
                break;
            case 86:
                result = 0;
                break;
            case 87:
                result = 0;
                break;
            case 88:
                result = 0;
                break;
            case 89:
                result = 0;
                break;
            case 90:
                result = 0;
                break;
            case 91:
                result = 0;
                break;
            case 92:
                result = 0;
                break;
            case 93:
                result = 0;
                break;
            case 94:
                result = 0;
                break;
            case 95:
                result = 0;
                break;
            case 96:
                AppController.clearMapPoints();
                result = 0;
                break;
            case 97:
                result = 0;
                break;
            case 98:
                result = 0;
                break;
            case 99:
                result = 0;
                break;
            case 100:
                AppState.setInt(1443, 0);
                AppState.setInt(1477, 0);
                result = 0;
                break;
            case 101:
                AppState.setInt(1478, 0);
                AppState.setInt(1443, 0);
                result = 0;
                break;
            case 102:
                AppState.clearIndex(1271);
                result = 0;
                break;
            case 103:
                result = 0;
                break;
            case 104:
                result = 0;
                break;
            case 105:
                result = 12;
                break;
            case 106:
                result = 0;
                break;
            case 107:
                AppState.clearIndex(1271);
                result = 0;
                break;
            case 108:
                result = 0;
                break;
            case 109:
                result = 0;
                break;
            case 110:
                result = 0;
                break;
            case 111:
                result = 0;
                break;
            case 112:
                result = 0;
                break;
            case 113:
                result = 0;
                break;
            case 114:
                result = 0;
                break;
            case 115:
                result = 0;
                break;
            case 116:
                AppState.setInt(1443, 0);
                result = 0;
                break;
            case 117:
                result = 0;
                break;
            case 118:
                result = 0;
                break;
            case 119:
                result = 0;
                break;
            case 120:
                AppState.setInt(1478, 0);
                result = 0;
                break;
            case 121:
                result = 0;
                break;
            case 122:
                result = 0;
                break;
            case 123:
                result = 0;
                break;
            case 124:
                result = 0;
                break;
            case 125:
                result = 0;
                break;
            case 126:
                result = 0;
                break;
            case 127:
                result = 0;
                break;
            case 128:
                result = 0;
                break;
            case 129:
                result = 0;
                break;
            case 130:
                result = 0;
                break;
            case 131:
                result = 0;
                break;
            case 132:
                result = 0;
                break;
            case 133:
                result = 0;
                break;
            case 134:
                result = 0;
                break;
            case 135:
                result = 0;
                break;
            case 136:
                result = 0;
                break;
            case 137:
                result = 12;
                break;
            case 138:
                result = 0;
                break;
            case 139:
                result = 6;
                break;
            case 140:
                result = 0;
                break;
            case 141:
                result = 0;
                break;
            case 142:
                result = 0;
                break;
            case 143:
                result = 0;
                break;
            case 144:
                result = 0;
                break;
            case 145:
                result = 0;
                break;
            case 146:
                result = 0;
                break;
            case 147:
                result = 0;
                break;
            case 148:
                result = 0;
                break;
            case 149:
                result = 0;
                break;
            case 150:
                result = 12;
                break;
            case 151:
                result = 0;
                break;
            case 152:
                result = 0;
                break;
            case 153:
                result = 0;
                break;
            case 154:
                result = 0;
                break;
            case 155:
                result = 0;
                break;
            case 156:
                result = 0;
                break;
            case 157:
                result = 0;
                break;
            case 158:
                result = 0;
                break;
            case 159:
                result = 0;
                break;
            case 160:
                onScreenClosed();
                result = 0;
                break;
            case 161:
                result = 4;
                break;
            case 162:
                result = 0;
                break;
            case 163:
                result = 0;
                break;
            case 164:
                result = 0;
                break;
            case 165:
                result = 0;
                break;
            case 166:
                result = 0;
                break;
            case 167:
                result = 0;
                break;
            case 168:
                result = 0;
                break;
            case 169:
                ((MrimAccount) AppState.getAccount()).isHighlighted = true;
                result = 0;
                break;
            case 170:
                result = 0;
                break;
            case 171:
                AppState.setInt(285, 0);
                ConnectionThread.toggleScrollMode();
                result = 6;
                break;
            case 172:
                result = 0;
                break;
            case 173:
                AppState.setAccount((Object) null);
                result = 12;
                break;
            case 174:
                result = 0;
                break;
            case 175:
                result = 0;
                break;
            case 176:
                result = 12;
                break;
            case 177:
                result = 0;
                break;
            case 178:
                result = 0;
                break;
            case 179:
                result = 0;
                break;
            case 180:
                result = 0;
                break;
        }
        System.out.println("[DEBUG] onMenuItemAction: screenId=" + ScreenManager.getCurrentScreen().screenId + " result=" + result + " softKeyCenter=" + currentScreen.softKeyCenter);
        if (result != -1) {
            if (result == 12) {
                onScreenClosed();
                return;
            }
            if (result != 0) {
                openScreen(result);
                return;
            }
            int i2 = currentScreen.softKeyCenter;
            if (i2 != 200) {
                if (i2 == 12) {
                    onScreenClosed();
                } else if (i2 != 0) {
                    openScreen(i2);
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* renamed from: c */
    public static final void onScreenClosed() {
        AppController.needsRepaint = true;
        switch (ScreenManager.getCurrentScreen().screenId) {
            case 2:
                AppState.setBool(71, AppState.getBool(218));
                AppState.getCanvas().updateFullScreenMode();
                AppState.setInt(1511, 0);
                break;
            case 6:
                TabBar.scrollEnabled = false;
                TabBar.removeSearchTab();
                break;
            case 9:
                AppController.clearPreviewState();
                break;
            case 14:
                AppState.clearIndex(1286);
                break;
            case 19:
                AppController.clearFormFields();
                break;
            case 21:
                int accountType = AppState.getAccount().getType();
                if (accountType != 0 && accountType == 1) {
                    AppState.clearIndex(1307);
                    AppState.clearIndex(1308);
                    AppState.clearIndex(1309);
                    AppState.clearIndex(1310);
                    AppState.clearIndex(1311);
                    AppState.clearIndex(1312);
                    AppState.setInt(1492, 0);
                    break;
                } else {
                    StringUtils.resetRegForm();
                    break;
                }
            case 22:
                AppController.clearFormFields();
                break;
            case 25:
                AppState.clearIndex(1281);
                break;
            case 26:
                AppState.setInt(72, AppState.getInt(4305));
                break;
            case 36:
                TabBar.removeSettingsTab();
                break;
            case 37:
                AppState.clearIndex(1271);
                break;
            case 39:
                AppState.clearIndex(1283);
                break;
            case 40:
                AppState.clearIndex(1290);
                AppState.clearIndex(1279);
                break;
            case 41:
                AppState.clearIndex(1271);
                break;
            case 42:
                AppState.clearIndex(1271);
                break;
            case 48:
                AppState.clearIndex(1271);
                break;
            case 51:
                AppState.clearIndex(1347);
                break;
            case 54:
                AppState.clearRange(1352, 1354);
                break;
            case 59:
                AppState.clearIndex(1284);
                AppState.clearIndex(1285);
                AppState.clearIndex(1271);
                break;
            case 66:
                AppState.clearIndex(1319);
                AppState.clearRange(1320, 1324);
                break;
            case 67:
                IOUtils.setSelectedItems((Object) null);
                break;
            case 68:
                AppController.resetSearchResults();
                break;
            case 69:
                AppState.clearIndex(1295);
                break;
            case 70:
                AppState.clearIndex(1306);
                break;
            case 72:
                AppState.clearIndex(1271);
                break;
            case 73:
                AppController.clearSearchResults2();
                break;
            case 76:
                XmppMailRuProtocol.clearLoginFields();
                break;
            case 78:
                AppState.clearIndex(1271);
                break;
            case 81:
                AppState.clearIndex(1271);
                break;
            case 82:
                AppState.clearIndex(1271);
                break;
            case 85:
                if (AppState.getCurrentContact() != null) {
                    AppState.getCurrentContact().mo148L();
                    break;
                }
                break;
            case 96:
                AppState.clearIndex(1315);
                break;
            case 100:
                AppState.clearIndex(1248);
                break;
            case 101:
                AppState.setInt(1443, 0);
                AppState.setInt(1477, 0);
                break;
            case 105:
                XmppMailRuProtocol.clearLoginFields();
                break;
            case 108:
                AppController.updateTabBar();
                AppState.clearIndex(1282);
                break;
            case 113:
                XmppMailRuProtocol.mapContextItem = null;
                break;
            case 120:
                AppState.setInt(1443, 0);
                break;
            case 122:
                NetworkUtils.releaseVector(AppState.getVector(1291));
                AppState.clearIndex(1291);
                break;
            case 138:
                AppController.refreshContactList();
                break;
            case 142:
                AppState.clearIndex(1336);
                break;
            case 143:
                AppState.clearIndex(1292);
                break;
            case 147:
                AppController.clearPreviewState();
                break;
            case 154:
                AppState.clearIndex(1253);
                AppState.clearIndex(1254);
                break;
            case 156:
                IOUtils.photoUrlList = null;
                IOUtils.contactIdList = null;
                break;
            case 164:
                AppState.clearIndex(1292);
                AppState.clearIndex(1293);
                AppState.clearIndex(1284);
                AppState.setInt(1474, 0);
                AppState.setInt(4305, 0);
                AppState.clearRange(1341, 1343);
                AppController.clearPreviewState();
                StringUtils.resetRegForm();
                break;
            case 168:
                AppController.clearFormFields();
                break;
            case 176:
                AppState.clearRange(1256, 1258);
                break;
            case 179:
                AppState.clearIndex(1284);
                break;
            case 180:
                AppState.clearIndex(1271);
                break;
        }
        Vector screenStack = AppState.getVector(1272);
        int size = screenStack.size() - 1;
        Screen closedScreen = (Screen) screenStack.elementAt(size);
        NetworkUtils.releaseVector(closedScreen.tabItems);
        NetworkUtils.releaseVector(closedScreen.menuItems);
        screenStack.removeElementAt(size);
        Utils.trimIfEmpty(screenStack);
    }
}
