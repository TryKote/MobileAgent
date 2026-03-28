package com.trykote.mobileagent.protocol.mrim;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

public final class MrimProfileManager {

    private final MrimAccount account;
    public VCard profile;
    public SizeCache sizeCache;

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
                sendRename(this.profile.photoUrls, AppState.getString(StateKeys.STR_RES_USER_AGENT));
            }
            sendUpdate(1, new String[0], this.profile);
        }
    }

    public void hideLocation() {
        int i = this.profile.gender;
        this.profile.gender = 2;
        if (this.account.isConnected()) {
            if (i == 3) {
                sendRename(this.profile.photoUrls, AppState.getString(StateKeys.STR_RES_USER_AGENT));
            }
            sendUpdate(0, new String[0], this.profile);
        }
    }

    public void clearGroups() {
        int i = this.profile.gender;
        this.profile.gender = 4;
        if (this.account.isConnected()) {
            if (i == 3) {
                sendRename(this.profile.photoUrls, AppState.getString(StateKeys.STR_RES_USER_AGENT));
            }
            sendRename(new String[0], AppState.getString(StateKeys.STR_RES_USER_AGENT));
        }
    }

    public void setGroups() {
        int i = this.profile.gender;
        this.profile.gender = 3;
        if (this.account.isConnected()) {
            if (i == 3) {
                sendRename(this.profile.prevPhotoUrls, AppState.getString(StateKeys.STR_RES_USER_AGENT));
            } else if (i == 1 || i == 2) {
                sendRename(new String[0], AppState.getString(StateKeys.STR_RES_USER_AGENT));
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
            String typeStr = AppState.getString(StateKeys.STR_RES_CONTENT_TYPE);
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
            String latStr = IOUtils.pixelToLatitude(mapPoint.latitude);
            String lonStr = IOUtils.pixelToLongitude(mapPoint.longitude);
            String typeStr = AppState.getString(StateKeys.STR_RES_HTTP_METHOD);
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
        this.account.trySendData(ProtocolFactory.createMrimPacket(this.account, MrimCommand.CS_ANKETA_UPDATE, new ByteBuffer().writeIntLE(i).writeStringArr(strArr).writeStringLatin1(AppState.getString(StateKeys.STR_RES_USER_AGENT)).writeBuffer(new ByteBuffer().writeBufferIntLen(new ByteBuffer().writeStringLatin1(strArr2[0]).writeStringLatin1(strArr2[1]).writeStringLatin1(strArr2[2]).writeStringUTF16(strArr2[3]).writeStringLatin1(strArr2[4]).writeStringLatin1(strArr2[5]).writeStringLatin1(strArr2[6]).writeStringLatin1(strArr2[7])))));
        return 0;
    }

    private int sendRename(String[] strArr, String str) {
        return this.account.trySendData(ProtocolFactory.createMrimPacket(this.account, MrimCommand.CS_ANKETA_UPDATE_PHOTOS, new ByteBuffer().writeStringArr(strArr).writeStringLatin1(str)));
    }
}
