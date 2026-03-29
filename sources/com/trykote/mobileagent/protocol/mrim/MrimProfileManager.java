package com.trykote.mobileagent.protocol.mrim;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Vector;

public final class MrimProfileManager {

    private final MrimAccount account;
    public VCard profile;
    public SizeCache sizeCache;

    /* renamed from: d */
    public static Vector contactIdList;

    /* renamed from: e */
    public static String[] photoUrlList;

    /* renamed from: f */
    private static ListView selectionScreen;

    MrimProfileManager(MrimAccount account) {
        this.account = account;
        this.profile = new VCard();
        this.sizeCache = new SizeCache();
    }

    public void sync() {
        if (this.account.isConnected()) {
            int i = this.profile.gender;
            if (i == 1) {
                sendUpdate(1, new String[0], this.profile);
            } else if (i == 2) {
                sendUpdate(0, new String[0], this.profile);
            } else if (i == 3) {
                sendUpdate(0, this.profile.photoUrls, this.profile);
            }
        }
    }

    public void publishLocation() {
        int i = this.profile.gender;
        this.profile.gender = 1;
        if (this.account.isConnected()) {
            if (i == 3) {
                sendRename(this.profile.photoUrls, AppState.getString(PackedStringKeys.MRIM_GEO_POINT));
            }
            sendUpdate(1, new String[0], this.profile);
        }
    }

    public void hideLocation() {
        int i = this.profile.gender;
        this.profile.gender = 2;
        if (this.account.isConnected()) {
            if (i == 3) {
                sendRename(this.profile.photoUrls, AppState.getString(PackedStringKeys.MRIM_GEO_POINT));
            }
            sendUpdate(0, new String[0], this.profile);
        }
    }

    public void clearGroups() {
        int i = this.profile.gender;
        this.profile.gender = 4;
        if (this.account.isConnected()) {
            if (i == 3) {
                sendRename(this.profile.photoUrls, AppState.getString(PackedStringKeys.MRIM_GEO_POINT));
            }
            sendRename(new String[0], AppState.getString(PackedStringKeys.MRIM_GEO_POINT));
        }
    }

    public void setGroups() {
        int i = this.profile.gender;
        this.profile.gender = 3;
        if (this.account.isConnected()) {
            if (i == 3) {
                sendRename(this.profile.prevPhotoUrls, AppState.getString(PackedStringKeys.MRIM_GEO_POINT));
            } else if (i == 1 || i == 2) {
                sendRename(new String[0], AppState.getString(PackedStringKeys.MRIM_GEO_POINT));
            }
            sendUpdate(0, this.profile.photoUrls, this.profile);
        }
    }

    public void receiveContactProfile(String str, Vector vector) {
        for (int i = 0; i < vector.size(); i++) {
            ByteBuffer buffer = (ByteBuffer) vector.elementAt(i);
            buffer.readInt();
            if (StringUtils.matchesKey(PackedStringKeys.MRIM_GEO_POINT, buffer.readWideStr())) {
                String[] cardFields = VCard.parseCardFromBuffer(buffer);
                MrimContact contact = this.account.findContactByIdentifier(str);
                if (contact != null) {
                    if (cardFields == null) {
                        contact.clearVCard();
                    } else {
                        try {
                            contact.vCardInfo = new VCard();
                            contact.vCardInfo.setCardData(cardFields[0], cardFields[1], cardFields[2], cardFields[3], cardFields[4], cardFields[5], cardFields[6], cardFields[7]);
                            contact.isSelected = true;
                        } catch (Throwable unused) {
                            contact.clearVCard();
                        }
                        contact.sizeCache.lastScale = -1;
                    }
                }
            }
        }
        ObjectPool.releaseVector(vector);
    }

    public void setSimpleLocation(String str, String str2) {
        try {
            VCard vcard = this.profile;
            String typeStr = AppState.getString(PackedStringKeys.MRIM_MAPPOINT);
            String str3 = AppState.emptyStr;
            vcard.setCardData(str2, str, typeStr, str3, str3, str3, str3, str3);
        } catch (Throwable unused) {
            this.profile.clearCoordinates();
        }
        this.sizeCache.lastScale = -1;
    }

    public void setMapLocation(MapPoint mapPoint) {
        try {
            VCard vcard = this.profile;
            String latStr = MapUtils.pixelToLatitude(mapPoint.latitude);
            String lonStr = MapUtils.pixelToLongitude(mapPoint.longitude);
            String typeStr = AppState.getString(PackedStringKeys.MRIM_MAPOBJECT);
            String pointName = mapPoint.getDisplayName();
            String str = AppState.emptyStr;
            vcard.setCardData(latStr, lonStr, typeStr, pointName, str, str, StringUtils.intern(Integer.toString(mapPoint.objectCode)), StringUtils.intern(Integer.toString(mapPoint.typeCode)));
        } catch (Throwable unused) {
            this.profile.clearCoordinates();
        }
        this.sizeCache.lastScale = -1;
    }

    private int sendUpdate(int i, String[] strArr, VCard vcard) {
        if (!vcard.hasCoordinates() || vcard.dirty) {
            return 0;
        }
        String[] strArr2 = {vcard.latStr, vcard.lonStr, vcard.mapTypeStr, vcard.phone, vcard.email, vcard.nickname, vcard.address, vcard.zoomStr};
        this.account.trySendData(ProtocolFactory.createMrimPacket(this.account, MrimCommand.CS_ANKETA_UPDATE, new ByteBuffer().writeIntLE(i).writeStringArr(strArr).writeStringLatin1(AppState.getString(PackedStringKeys.MRIM_GEO_POINT)).writeBuffer(new ByteBuffer().writeBufferIntLen(new ByteBuffer().writeStringLatin1(strArr2[0]).writeStringLatin1(strArr2[1]).writeStringLatin1(strArr2[2]).writeStringUTF16(strArr2[3]).writeStringLatin1(strArr2[4]).writeStringLatin1(strArr2[5]).writeStringLatin1(strArr2[6]).writeStringLatin1(strArr2[7])))));
        return 0;
    }

    private int sendRename(String[] strArr, String str) {
        return this.account.trySendData(ProtocolFactory.createMrimPacket(this.account, MrimCommand.CS_ANKETA_UPDATE_PHOTOS, new ByteBuffer().writeStringArr(strArr).writeStringLatin1(str)));
    }

    /* renamed from: d */
    public static final void showPhotoSelector() {
        boolean z;
        MrimAccount account = (MrimAccount) AppState.getAccount();
        photoUrlList = account.profileManager.profile.photoUrls;
        Vector candidates = ObjectPool.newVector();
        Enumeration elements = account.contactMap.elements();
        while (elements.hasMoreElements()) {
            Contact contact = (Contact) elements.nextElement();
            if (!contact.isOffline() && !contact.isOnline()) {
                candidates.addElement(contact);
            }
        }
        int size = candidates.size();
        ListView screen = ScreenManager.createScreen(ScreenDef.CONTACT_INFO_EDITOR);
        contactIdList = ObjectPool.newVector();
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
        Vector selected = ObjectPool.newVector();
        int size = vector.size();
        for (int i = 0; i < size; i++) {
            if (((Boolean) ((MenuItem) vector.elementAt(i)).data).booleanValue()) {
                selected.addElement(contactIdList.elementAt(i));
            }
        }
        MrimAccount account = (MrimAccount) AppState.getAccount();
        VCard profile = account.profileManager.profile;
        profile.prevPhotoUrls = profile.photoUrls;
        int size2 = selected.size();
        profile.photoUrls = new String[size2];
        for (int i2 = 0; i2 < size2; i2++) {
            profile.photoUrls[i2] = (String) selected.elementAt(i2);
        }
        String[] strArr = account.profileManager.profile.photoUrls;
        XmlElement root = new XmlElement(114);
        XmlElement visibleEl = new XmlElement("visible", root, null);
        root.addChild(visibleEl);
        for (String str : strArr) {
            XmlElement userEl = new XmlElement("u", visibleEl, null);
            userEl.setAttrValue(328413, str);
            visibleEl.addChild(userEl);
        }
        account.trySendData(ProtocolFactory.createMrimPacket(account, 4181, new ByteBuffer().writeStringLatin1("geo-list").writeStringLatin1(root.toString())));
        if (account.profileManager.profile.gender != 3) {
            return 0;
        }
        account.profileManager.setGroups();
        return 0;
    }
}
