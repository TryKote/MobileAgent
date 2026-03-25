package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

/* renamed from: ag */
/* loaded from: MobileAgent_3.9.jar:ag.class */
public abstract class ContactListManager {
    /* renamed from: a */
    public static final void showContactList() {
        RemoteLogger.log("CL", "showContactList called");
        AppState.clearIndex(StateKeys.SLOT_CURRENT_ACCOUNT);
        AppState.clearIndex(StateKeys.SLOT_CURRENT_ENTITY);
        AppState.setInt(StateKeys.INT_CONNECTION_STATE, 4);
        TabBar.findTab(4, TabBar.currentAccount);
        Screen c0013amM161g = buildContactList();
        TabBar c0008ahM175i = TabBar.getCurrentTab();
        Screen c0013amM257b = c0013amM161g.selectByTitle(c0008ahM175i.selectedTitle);
        ScreenManager.pushScreen(c0013amM257b);
        c0013amM257b.scrollOffset = c0008ahM175i.selectedIndex;
        c0013amM257b.invalidateLayout();
    }

    /* renamed from: b */
    public static final int selectContact() {
        updateState();
        MenuItem c0032cM69e = ScreenManager.getCurrentMenuItem();
        AppState.setCurrentEntity(c0032cM69e == null ? null : c0032cM69e.data);
        return AppController.handleGameAction();
    }

    /* renamed from: f */
    private static final void updateState() {
        TabBar c0008ahM175i = TabBar.getCurrentTab();
        Screen c0013amM66b = ScreenManager.getCurrentScreen();
        c0008ahM175i.selectedIndex = c0013amM66b.scrollOffset;
        c0008ahM175i.selectedTitle = c0013amM66b.getSelectedTitle();
    }

    /* renamed from: c */
    public static final void clearState() {
        AppState.clearIndex(StateKeys.SLOT_CURRENT_ACCOUNT);
        AppState.clearIndex(StateKeys.SLOT_CURRENT_ENTITY);
        updateState();
    }

    /* renamed from: d */
    public static final void refreshList() {
        RemoteLogger.log("CL", "refreshList called");
        clearState();
        TabBar c0008ahM175i = TabBar.getCurrentTab();
        Screen c0013amM257b = buildContactList().selectByTitle(c0008ahM175i.selectedTitle);
        ScreenManager.pushScreen(c0013amM257b);
        c0013amM257b.scrollOffset = c0008ahM175i.selectedIndex;
        c0013amM257b.invalidateLayout();
        TabBar.findTab(4, TabBar.currentAccount);
        AppController.needsRepaint = true;
    }

    /* renamed from: e */
    public static final int getSelectedContact() {
        updateState();
        return 0;
    }

    /* renamed from: a */
    public static final int onContactSelected(String str, Object obj) {
        if (str == null) {
            return -1;
        }
        updateState();
        AppState.setCurrentEntity(obj);
        if (obj == null) {
            return 0;
        }
        if (obj instanceof ContactGroup) {
            AppController.needsLayoutUpdate = true;
            return ((ContactGroup) obj).toggleSpecial();
        }
        if (!(obj instanceof Contact)) {
            return 0;
        }
        AppState.clearIndex(StateKeys.SLOT_STATUS_TEXT);
        AppController.clearSearchState();
        return ((Contact) obj).getDefaultAction();
    }

    /* renamed from: a */
    public static final int onContactAction(Object obj) {
        updateState();
        AppState.setCurrentEntity(obj);
        return obj != null ? 30 : -1;
    }

    /* JADX WARN: Removed duplicated region for block: B:31:0x00bb A[Catch: Throwable -> 0x0124, PHI: r12
      0x00bb: PHI (r12v1 int) = (r12v0 int), (r12v3 int) binds: [B:27:0x009f, B:29:0x00b2] A[DONT_GENERATE, DONT_INLINE], TryCatch #0 {Throwable -> 0x0124, blocks: (B:26:0x0098, B:28:0x00a2, B:31:0x00bb, B:36:0x00d8, B:39:0x00ee, B:41:0x00f8, B:44:0x0112), top: B:75:0x0098 }] */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00d4 A[PHI: r12
      0x00d4: PHI (r12v4 int) = (r12v0 int), (r12v2 int) binds: [B:25:0x0095, B:32:0x00cb] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x00ee A[Catch: Throwable -> 0x0124, PHI: r12
      0x00ee: PHI (r12v5 int) = (r12v4 int), (r12v8 int) binds: [B:35:0x00d5, B:37:0x00e5] A[DONT_GENERATE, DONT_INLINE], TryCatch #0 {Throwable -> 0x0124, blocks: (B:26:0x0098, B:28:0x00a2, B:31:0x00bb, B:36:0x00d8, B:39:0x00ee, B:41:0x00f8, B:44:0x0112), top: B:75:0x0098 }] */
    /* JADX WARN: Removed duplicated region for block: B:44:0x0112 A[Catch: Throwable -> 0x0124, PHI: r12
      0x0112: PHI (r12v6 int) = (r12v5 int), (r12v7 int) binds: [B:40:0x00f5, B:42:0x0109] A[DONT_GENERATE, DONT_INLINE], TryCatch #0 {Throwable -> 0x0124, blocks: (B:26:0x0098, B:28:0x00a2, B:31:0x00bb, B:36:0x00d8, B:39:0x00ee, B:41:0x00f8, B:44:0x0112), top: B:75:0x0098 }] */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static final int updateContextMenu(Screen c0013am, Object obj) {
        Account abstractC0037h;
        int iM1250M = -1;
        if (AppState.pool[StateKeys.VEC_ACCOUNT_SELECTION] != null) {
            return ScreenId.PRESENCE_ACTION;
        }
        if (!AppState.getBool(StateKeys.FLAG_CLEANUP_DONE)) {
            AppState.setInt(StateKeys.FLAG_CLEANUP_DONE, 1);
            if (System.currentTimeMillis() - AppState.getLong(StateKeys.TIMESTAMP_FIRST_RUN) > 604800000) {
                AppState.setInt(StateKeys.FLAG_SHOW_NOTIFICATION, 0);
                return ScreenId.FIRST_RUN;
            }
        }
        updateState();
        Vector vector = c0013am.tabItems;
        if (obj != null) {
            Contact abstractC0041l = null;
            ContactGroup abstractC0046q = null;
            if (obj instanceof ContactGroup) {
                ContactGroup abstractC0046q2 = (ContactGroup) obj;
                abstractC0046q = abstractC0046q2;
                abstractC0037h = abstractC0046q2.account;
            } else {
                Contact abstractC0041l2 = (Contact) obj;
                abstractC0041l = abstractC0041l2;
                abstractC0037h = abstractC0041l2.account;
            }
            Account abstractC0037h2 = abstractC0037h;
            int iMo108h = abstractC0037h.getIconId();
            String str = abstractC0037h2.shortName;
            if (!AppState.getBool(StateKeys.SETTING_MULTI_ACCOUNT)) {
                TabBar.updateTitle(iMo108h, str);
            }
            if (vector != null) {
                boolean z = false;
                String str2 = AppState.emptyStr;
                int i = 0;
                if (abstractC0041l != null) {
                    try {
                        iM1250M = abstractC0041l.getContextAction();
                    } catch (Throwable unused) {
                        z = true;
                    }
                    if (iM1250M >= 0) {
                        i = 0 + 1;
                        if (vector.size() < i || ((Integer) vector.elementAt(0)).intValue() != iM1250M) {
                            z = true;
                        } else {
                            int i2 = i;
                            i++;
                            if (vector.size() <= i2 || !vector.elementAt(i2).equals(abstractC0041l.extra)) {
                                z = true;
                            } else if (abstractC0046q != null) {
                                i++;
                                if (vector.elementAt(0).equals(str2)) {
                                    int iMo922n = abstractC0037h2.getExtType();
                                    if (iMo922n >= 0) {
                                        int i3 = i;
                                        i++;
                                        if (vector.size() <= i3 || ((Integer) vector.elementAt(i3)).intValue() != iMo922n) {
                                            z = true;
                                        } else if (i != vector.size()) {
                                            z = true;
                                        }
                                    }
                                } else {
                                    z = true;
                                }
                            }
                        }
                        if (z) {
                            vector.removeAllElements();
                            if (abstractC0041l != null) {
                                int iM1250M2 = abstractC0041l.getContextAction();
                                if (iM1250M2 >= 0) {
                                    vector.addElement(ResourceManager.integerOf(iM1250M2));
                                }
                                vector.addElement(abstractC0041l.extra);
                            }
                            if (abstractC0046q != null) {
                                vector.addElement(str2);
                            }
                            int iMo922n2 = abstractC0037h2.getExtType();
                            if (iMo922n2 >= 0) {
                                vector.addElement(ResourceManager.integerOf(iMo922n2));
                            }
                            AppController.needsRepaint = true;
                        }
                    }
                }
            }
        } else if (vector != null && vector.size() > 0) {
            vector.removeAllElements();
            AppController.needsRepaint = true;
        }
        return AppState.getBool(StateKeys.FLAG_CONVERSATION_ACTIVE) ? 163 : 0;
    }

    /* renamed from: g */
    private static final Screen buildContactList() {
        RemoteLogger.log("CL", "buildContactList: currentAccount=" + (TabBar.currentAccount != null ? TabBar.currentAccount.login : "null"));
        boolean zM1056C;
        MergedContactGroup c0054y;
        int iM586d = 1 + AppState.getInt(StateKeys.SETTING_CONTACT_SORT_MODE);
        AppState.setInt(StateKeys.INT_CONTACT_ICON_SIZE, iM586d == 1 ? 1 : 12);
        Screen c0013amM75b = ScreenManager.createScreen(ScreenDef.CONTACT_LIST_TEMPLATE);
        int i = c0013amM75b.contentWidth - 1;
        if (!AppState.getBool(StateKeys.SETTING_SHOW_OFFLINE)) {
            boolean z = !AppState.getBool(StateKeys.SETTING_SORT_ORDER);
            Account abstractC0037h = TabBar.currentAccount;
            Vector vectorM445W = abstractC0037h == null ? AccountManager.getAllAccountsList() : abstractC0037h.getAllContacts();
            Vector vector = vectorM445W;
            int iM353a = AppController.sortContacts(vectorM445W);
            for (int i2 = 0; i2 < iM353a; i2++) {
                Contact abstractC0041l = (Contact) vector.elementAt(i2);
                if (!abstractC0041l.canUnblock() && (abstractC0041l.hasMessages() || abstractC0041l.isOnline() || (!abstractC0041l.canUnblock() && (z || (((zM1056C = abstractC0041l.account.isConnected()) && abstractC0041l.highlighted) || (!zM1056C && abstractC0041l.isOffline())))))) {
                    c0013amM75b.addItem(abstractC0041l.createMenuItem().setLayout(iM586d, i / iM586d));
                }
            }
            ObjectPool.releaseVector(vector);
        } else if (AppState.getBool(StateKeys.SETTING_GROUP_BY_STATUS)) {
            int i3 = i / iM586d;
            boolean zM587e = AppState.getBool(StateKeys.SETTING_SHOW_GROUPS);
            boolean z2 = !AppState.getBool(StateKeys.SETTING_SORT_ORDER);
            Vector vectorM1213g = ObjectPool.newVector();
            Vector vectorM446d = AccountManager.getAccountConversations(TabBar.currentAccount);
            int size = vectorM446d.size();
            while (true) {
                size--;
                if (size < 0) {
                    break;
                }
                ContactGroup abstractC0046q = (ContactGroup) vectorM446d.elementAt(size);
                String str = abstractC0046q.name;
                int size2 = vectorM1213g.size();
                while (true) {
                    size2--;
                    if (size2 < 0) {
                        c0054y = null;
                        break;
                    }
                    MergedContactGroup c0054y2 = (MergedContactGroup) vectorM1213g.elementAt(size2);
                    if (c0054y2.name.equals(str)) {
                        c0054y = c0054y2;
                        break;
                    }
                }
                MergedContactGroup c0054y3 = c0054y;
                if (c0054y == null) {
                    MergedContactGroup c0054y4 = new MergedContactGroup(abstractC0046q, vectorM1213g.size());
                    c0054y3 = c0054y4;
                    vectorM1213g.addElement(c0054y4);
                }
                Vector vector2 = abstractC0046q.contacts;
                int size3 = vector2.size();
                while (true) {
                    size3--;
                    if (size3 < 0) {
                        break;
                    }
                    c0054y3.addContact(vector2.elementAt(size3));
                }
            }
            ObjectPool.releaseVector(vectorM446d);
            int iM353a2 = AppController.sortContacts(vectorM1213g);
            for (int i4 = 0; i4 < iM353a2; i4++) {
                ContactGroup abstractC0046q2 = (ContactGroup) vectorM1213g.elementAt(i4);
                boolean z3 = false;
                if (zM587e || !abstractC0046q2.isNotSpecial()) {
                    c0013amM75b.addItem(abstractC0046q2.createMenuItem(-1).setLayout(iM586d, i));
                    z3 = true;
                }
                if (abstractC0046q2.isNotSpecial()) {
                    Vector vector3 = abstractC0046q2.contacts;
                    int iM353a3 = AppController.sortContacts(vector3);
                    for (int i5 = 0; i5 < iM353a3; i5++) {
                        Contact abstractC0041l2 = (Contact) vector3.elementAt(i5);
                        if (shouldDisplayContact(z2, abstractC0041l2)) {
                            if (!z3) {
                                c0013amM75b.addItem(abstractC0046q2.createMenuItem(-1).setLayout(iM586d, i));
                                z3 = true;
                            }
                            c0013amM75b.addItem(abstractC0041l2.createMenuItem().setLayout(iM586d, i3));
                        }
                    }
                }
            }
            ObjectPool.releaseVector(vectorM1213g);
            MergedContactGroup c0054y5 = null;
            MergedContactGroup c0054y6 = null;
            MergedContactGroup c0054y7 = null;
            MergedContactGroup c0054y8 = null;
            int iM433Q = AccountManager.getActiveAccountCount();
            while (true) {
                iM433Q--;
                if (iM433Q < 0) {
                    break;
                }
                Account abstractC0037hM434I = AccountManager.getAccountByIndex(iM433Q);
                Account abstractC0037h2 = TabBar.currentAccount;
                if (abstractC0037h2 == null || abstractC0037h2 == abstractC0037hM434I) {
                    Vector vectorMo720O = abstractC0037hM434I.getPendingContacts();
                    int size4 = vectorMo720O.size();
                    int i6 = size4;
                    if (size4 > 0) {
                        if (c0054y8 == null) {
                            c0054y8 = new MergedContactGroup(abstractC0037hM434I.specialGroup, -4);
                        }
                        while (true) {
                            i6--;
                            if (i6 < 0) {
                                break;
                            }
                            c0054y8.addContact(vectorMo720O.elementAt(i6));
                        }
                    }
                    ObjectPool.releaseVector(vectorMo720O);
                    Vector vectorM1077N = abstractC0037hM434I.getOfflineContacts();
                    int size5 = vectorM1077N.size();
                    int i7 = size5;
                    if (size5 > 0) {
                        if (c0054y5 == null) {
                            c0054y5 = new MergedContactGroup(abstractC0037hM434I.offlineGroup, -1);
                        }
                        while (true) {
                            i7--;
                            if (i7 < 0) {
                                break;
                            }
                            c0054y5.addContact(vectorM1077N.elementAt(i7));
                        }
                    }
                    ObjectPool.releaseVector(vectorM1077N);
                    Vector vectorM1079Q = abstractC0037hM434I.getOnlineContacts();
                    int size6 = vectorM1079Q.size();
                    int i8 = size6;
                    if (size6 > 0) {
                        if (c0054y6 == null) {
                            c0054y6 = new MergedContactGroup(abstractC0037hM434I.defaultGroup, -2);
                        }
                        while (true) {
                            i8--;
                            if (i8 < 0) {
                                break;
                            }
                            c0054y6.addContact(vectorM1079Q.elementAt(i8));
                        }
                    }
                    ObjectPool.releaseVector(vectorM1079Q);
                    Vector vectorM1076M = abstractC0037hM434I.getUnreadContacts();
                    int size7 = vectorM1076M.size();
                    int i9 = size7;
                    if (size7 > 0) {
                        if (c0054y7 == null) {
                            c0054y7 = new MergedContactGroup(abstractC0037hM434I.onlineGroup, -3);
                        }
                        while (true) {
                            i9--;
                            if (i9 < 0) {
                                break;
                            }
                            c0054y7.addContact(vectorM1076M.elementAt(i9));
                        }
                    }
                    ObjectPool.releaseVector(vectorM1076M);
                }
            }
            if (c0054y8 != null) {
                Vector vector4 = c0054y8.contacts;
                int iM353a4 = AppController.sortContacts(vector4);
                c0013amM75b.addItem(c0054y8.createMenuItem(iM353a4).setLayout(iM586d, i));
                if (c0054y8.isNotSpecial()) {
                    for (int i10 = 0; i10 < iM353a4; i10++) {
                        c0013amM75b.addItem(((Contact) vector4.elementAt(i10)).createMenuItem().setLayout(iM586d, i3));
                    }
                    ObjectPool.releaseVector(vector4);
                }
            }
            if (c0054y5 != null) {
                Vector vector5 = c0054y5.contacts;
                int iM353a5 = AppController.sortContacts(vector5);
                c0013amM75b.addItem(c0054y5.createMenuItem(iM353a5).setLayout(iM586d, i));
                if (c0054y5.isNotSpecial()) {
                    for (int i11 = 0; i11 < iM353a5; i11++) {
                        c0013amM75b.addItem(((Contact) vector5.elementAt(i11)).createMenuItem().setLayout(iM586d, i3));
                    }
                    ObjectPool.releaseVector(vector5);
                }
            }
            if (c0054y7 != null) {
                Vector vector6 = c0054y7.contacts;
                int iM353a6 = AppController.sortContacts(vector6);
                c0013amM75b.addItem(c0054y7.createMenuItem(iM353a6).setLayout(iM586d, i));
                if (c0054y7.isNotSpecial()) {
                    for (int i12 = 0; i12 < iM353a6; i12++) {
                        c0013amM75b.addItem(((Contact) vector6.elementAt(i12)).createMenuItem().setLayout(iM586d, i3));
                    }
                    ObjectPool.releaseVector(vector6);
                }
            }
            if (c0054y6 != null) {
                Vector vector7 = c0054y6.contacts;
                int iM353a7 = AppController.sortContacts(vector7);
                c0013amM75b.addItem(c0054y6.createMenuItem(iM353a7).setLayout(iM586d, i));
                if (c0054y6.isNotSpecial()) {
                    for (int i13 = 0; i13 < iM353a7; i13++) {
                        c0013amM75b.addItem(((Contact) vector7.elementAt(i13)).createMenuItem().setLayout(iM586d, i3));
                    }
                    ObjectPool.releaseVector(vector7);
                }
            }
        } else {
            int i14 = i / iM586d;
            Vector vectorM446d2 = AccountManager.getAccountConversations(TabBar.currentAccount);
            int iM353a8 = AppController.sortContacts(vectorM446d2);
            boolean zM587e2 = AppState.getBool(StateKeys.SETTING_SHOW_GROUPS);
            boolean z4 = !AppState.getBool(StateKeys.SETTING_SORT_ORDER);
            for (int i15 = 0; i15 < iM353a8; i15++) {
                ContactGroup abstractC0046q3 = (ContactGroup) vectorM446d2.elementAt(i15);
                boolean z5 = false;
                if (zM587e2 || !abstractC0046q3.isNotSpecial()) {
                    c0013amM75b.addItem(abstractC0046q3.createMenuItem(-1).setLayout(iM586d, i));
                    z5 = true;
                }
                if (abstractC0046q3.isNotSpecial()) {
                    Vector vector8 = abstractC0046q3.contacts;
                    int iM353a9 = AppController.sortContacts(vector8);
                    for (int i16 = 0; i16 < iM353a9; i16++) {
                        Contact abstractC0041l3 = (Contact) vector8.elementAt(i16);
                        if (shouldDisplayContact(z4, abstractC0041l3)) {
                            if (!z5) {
                                c0013amM75b.addItem(abstractC0046q3.createMenuItem(-1).setLayout(iM586d, i));
                                z5 = true;
                            }
                            c0013amM75b.addItem(abstractC0041l3.createMenuItem().setLayout(iM586d, i14));
                        }
                    }
                }
            }
            ObjectPool.releaseVector(vectorM446d2);
            int iM433Q2 = AccountManager.getActiveAccountCount();
            int i17 = iM433Q2;
            while (true) {
                i17--;
                if (i17 < 0) {
                    break;
                }
                Account abstractC0037hM434I2 = AccountManager.getAccountByIndex(i17);
                Account abstractC0037h3 = TabBar.currentAccount;
                if (abstractC0037h3 == null || abstractC0037h3 == abstractC0037hM434I2) {
                    ContactGroup abstractC0046q4 = abstractC0037hM434I2.specialGroup;
                    Vector vectorMo720O2 = abstractC0037hM434I2.getPendingContacts();
                    int size8 = vectorMo720O2.size();
                    if (size8 > 0) {
                        c0013amM75b.addItem(abstractC0046q4.createMenuItem(size8).setLayout(iM586d, i));
                        if (abstractC0046q4.isNotSpecial()) {
                            AppController.sortContacts(vectorMo720O2);
                            for (int i18 = 0; i18 < size8; i18++) {
                                c0013amM75b.addItem(((Contact) vectorMo720O2.elementAt(i18)).createMenuItem().setLayout(iM586d, i14));
                            }
                        }
                    }
                    ObjectPool.releaseVector(vectorMo720O2);
                }
            }
            int i19 = iM433Q2;
            while (true) {
                i19--;
                if (i19 < 0) {
                    break;
                }
                Account abstractC0037hM434I3 = AccountManager.getAccountByIndex(i19);
                Account abstractC0037h4 = TabBar.currentAccount;
                if (abstractC0037h4 == null || abstractC0037h4 == abstractC0037hM434I3) {
                    ContactGroup abstractC0046q5 = abstractC0037hM434I3.offlineGroup;
                    Vector vectorM1077N2 = abstractC0037hM434I3.getOfflineContacts();
                    int size9 = vectorM1077N2.size();
                    if (size9 > 0) {
                        c0013amM75b.addItem(abstractC0046q5.createMenuItem(size9).setLayout(iM586d, i));
                        if (abstractC0046q5.isNotSpecial()) {
                            AppController.sortContacts(vectorM1077N2);
                            for (int i20 = 0; i20 < size9; i20++) {
                                c0013amM75b.addItem(((Contact) vectorM1077N2.elementAt(i20)).createMenuItem().setLayout(iM586d, i14));
                            }
                        }
                    }
                    ObjectPool.releaseVector(vectorM1077N2);
                }
            }
            int i21 = iM433Q2;
            while (true) {
                i21--;
                if (i21 < 0) {
                    break;
                }
                Account abstractC0037hM434I4 = AccountManager.getAccountByIndex(i21);
                Account abstractC0037h5 = TabBar.currentAccount;
                if (abstractC0037h5 == null || abstractC0037h5 == abstractC0037hM434I4) {
                    ContactGroup abstractC0046q6 = abstractC0037hM434I4.defaultGroup;
                    Vector vectorM1079Q2 = abstractC0037hM434I4.getOnlineContacts();
                    int size10 = vectorM1079Q2.size();
                    if (size10 > 0) {
                        c0013amM75b.addItem(abstractC0046q6.createMenuItem(size10).setLayout(iM586d, i));
                        if (abstractC0046q6.isNotSpecial()) {
                            AppController.sortContacts(vectorM1079Q2);
                            for (int i22 = 0; i22 < size10; i22++) {
                                c0013amM75b.addItem(((Contact) vectorM1079Q2.elementAt(i22)).createMenuItem().setLayout(iM586d, i14));
                            }
                        }
                    }
                    ObjectPool.releaseVector(vectorM1079Q2);
                }
            }
            int i23 = iM433Q2;
            while (true) {
                i23--;
                if (i23 < 0) {
                    break;
                }
                Account abstractC0037hM434I5 = AccountManager.getAccountByIndex(i23);
                Account abstractC0037h6 = TabBar.currentAccount;
                if (abstractC0037h6 == null || abstractC0037h6 == abstractC0037hM434I5) {
                    ContactGroup abstractC0046q7 = abstractC0037hM434I5.onlineGroup;
                    Vector vectorM1076M2 = abstractC0037hM434I5.getUnreadContacts();
                    int size11 = vectorM1076M2.size();
                    if (size11 > 0) {
                        c0013amM75b.addItem(abstractC0046q7.createMenuItem(size11).setLayout(iM586d, i));
                        if (abstractC0046q7.isNotSpecial()) {
                            AppController.sortContacts(vectorM1076M2);
                            for (int i24 = 0; i24 < size11; i24++) {
                                c0013amM75b.addItem(((Contact) vectorM1076M2.elementAt(i24)).createMenuItem().setLayout(iM586d, i14));
                            }
                        }
                    }
                    ObjectPool.releaseVector(vectorM1076M2);
                }
            }
        }
        TabBar.layout();
        return c0013amM75b.initTabs();
    }

    /* renamed from: a */
    private static final boolean shouldDisplayContact(boolean z, Contact abstractC0041l) {
        return ((!abstractC0041l.hasMessages() && !z && !abstractC0041l.highlighted) || abstractC0041l.canUnblock() || abstractC0041l.hasUnread() || abstractC0041l.isOnline() || abstractC0041l.isOffline() || abstractC0041l.isSystem()) ? false : true;
    }

    /* renamed from: a */
    public static final Screen addContactItems(Screen screen, Vector vector) {
        MenuItem menuItem;
        int count = Utils.vectorSize(vector);
        for (int i = 0; i < count; i++) {
            Object item = vector.elementAt(i);
            if (item instanceof Contact) {
                menuItem = ((Contact) item).createMenuItem();
            } else if (item instanceof ContactGroup) {
                menuItem = ((ContactGroup) item).createMenuItem(-1);
            } else if (item instanceof ContactInfo) {
                ContactInfo contactInfo = (ContactInfo) item;
                if (contactInfo.getAccount() instanceof MrimAccount) {
                    MenuItem entry = MenuItem.createDefault().setIcon(AppController.handleServerAction(Utils.parseIntBounded(contactInfo.getString(10), 0, 4, 0), contactInfo.getString(12))).addText(Utils.withComma(contactInfo.getDisplayName()), 1, 0).setLabel(contactInfo.getString(3));
                    entry.data = contactInfo;
                    menuItem = entry;
                } else {
                    MenuItem entry = MenuItem.createDefault();
                    int gender = Utils.parseInt((Object) contactInfo.getString(61));
                    MenuItem entry2 = entry.setIcon(gender == 0 ? 255 : gender == 1 ? 256 : 263).setLabel(Utils.appendSpace(contactInfo.getString(60))).addText(Utils.withComma(contactInfo.getDisplayName()), 1, 0).setLabel(StringUtils.concat(Utils.appendSpace(contactInfo.getFirstName()), contactInfo.getLastName()));
                    entry2.data = contactInfo;
                    menuItem = entry2;
                }
            } else {
                menuItem = ((Account) item).createMenuItem();
            }
            screen.addItem(menuItem);
        }
        return screen;
    }
}
