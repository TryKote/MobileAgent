package com.trykote.mobileagent.protocol.mrim;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.util.*;
import java.util.Hashtable;
import java.util.Vector;

public abstract class MrimContactParser {

    /* renamed from: a */
    public static final void createSingleContact(MrimAccount account, int i, ByteBuffer buffer) {
        ContactInfo contactInfo = ContactInfo.createForAccount(account);
        switch (i) {
            case 0:
                contactInfo.setContactName(AppState.getString(StateKeys.STR_DEFAULT_CONTACT_NAME));
                break;
            case 1:
                contactInfo = (ContactInfo) parseMrimContacts(account, buffer).elementAt(0);
                break;
            default:
                contactInfo.setContactName(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(AppState.getString(StateKeys.STR_CONTACT_NAME_PREFIX)).append(i)));
                break;
        }
        AppState.pool[StateKeys.SLOT_REG_PARAM_1] = contactInfo;
    }

    /* renamed from: b */
    public static final void parseContactInfoResponse(MrimAccount account, int i, ByteBuffer buffer) {
        int nameIndex = 0;
        Vector contacts = null;
        switch (i) {
            case 0:
                nameIndex = 913;
                break;
            case 1:
                contacts = parseMrimContacts(account, buffer);
                break;
            default:
                nameIndex = 914;
                break;
        }
        AppState.setInt(StateKeys.INT_ERROR_MSG_INDEX, nameIndex);
        AppState.pool[StateKeys.SLOT_REG_PARAM_4] = contacts;
    }

    /* renamed from: c */
    public static final void updateContactName(MrimAccount account, int i, ByteBuffer buffer) {
        int nameIndex;
        switch (i) {
            case 0:
                nameIndex = 913;
                break;
            case 1:
                ContactInfo contactInfo = (ContactInfo) parseMrimContacts(account, buffer).elementAt(0);
                MrimContact contact = (MrimContact) account.contactMap.get(contactInfo.getEmailOrMmpId());
                if (null != contact) {
                    contact.setDisplayName(contactInfo.getFullName());
                    return;
                }
                return;
            default:
                nameIndex = 914;
                break;
        }
        IOUtils.postNotification(AppState.getString(nameIndex));
    }

    /* renamed from: d */
    public static final void addContactToGroup(MrimAccount account, int i, ByteBuffer buffer) {
        if (i == 1) {
            ContactInfo contactInfo = (ContactInfo) parseMrimContacts(account, buffer).elementAt(0);
            Hashtable contactMap = account.contactMap;
            String email = contactInfo.getEmailOrMmpId();
            MrimContact contact = (MrimContact) contactMap.get(email);
            if (null != contact) {
                String fullName = contactInfo.getFullName();
                contact.setDisplayName(fullName);
                account.validateGroupAdd(email, fullName, AppState.getString(StateKeys.STR_DEFAULT_GROUP_NAME), (ContactGroup) account.getFirstContactGroup(), true);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:53:0x0106 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0114 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x0123 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x0132 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0141 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:58:0x0173 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:59:0x0181 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:60:0x018f A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:61:0x019e A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:62:0x01ac A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:63:0x01b5 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:64:0x01be A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:65:0x01cc A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:66:0x01da A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:67:0x01e8 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x01f5 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0203 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:70:0x0212 A[SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x00f8 A[SYNTHETIC] */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private static final Vector parseMrimContacts(MrimAccount account, ByteBuffer buffer) {
        Vector result = ObjectPool.newVector();
        Vector fieldNames = Utils.splitByNull(AppState.getString(StateKeys.STR_REG_FIELD_NAMES));
        int fieldCount = buffer.readInt();
        int contactCount = buffer.readInt();
        buffer.readInt();
        Vector fieldTypes = ObjectPool.newVector();
        for (int i = 0; i < fieldCount; i++) {
            fieldTypes.addElement(buffer.readHexStr());
        }
        for (int i2 = 0; i2 < contactCount && buffer.length > 0; i2++) {
            ContactInfo contactInfo = ContactInfo.createForAccount(account);
            result.addElement(contactInfo);
            int i3 = 0;
            while (i3 < fieldCount) {
                int i4 = i3;
                i3++;
                String fieldType = (String) fieldTypes.elementAt(i4);
                int fieldIdx = Utils.vectorSize(fieldNames);
                do {
                    fieldIdx--;
                    if (fieldIdx < 0) {
                    }
                    switch (fieldIdx) {
                        case 0:
                            contactInfo.setPhoneHome(buffer.readWideStr());
                            break;
                        case 1:
                            contactInfo.setPhoneMobile(buffer.readWideStr());
                            break;
                        case 2:
                            contactInfo.setDisplayName(buffer.readUTF8Str((String) null));
                            break;
                        case 3:
                            contactInfo.setFirstName(buffer.readUTF8Str((String) null));
                            break;
                        case 4:
                            contactInfo.setLastName(buffer.readUTF8Str((String) null));
                            break;
                        case 5:
                            int maritalStatus = Utils.parseIntBounded(buffer.readWideStr(), 1, 2, 0);
                            if (1 == maritalStatus) {
                                contactInfo.setMaritalMarried();
                                break;
                            } else if (2 == maritalStatus) {
                                contactInfo.setMaritalSingle();
                                break;
                            } else {
                                break;
                            }
                        case 6:
                            contactInfo.setCompany(buffer.readWideStr());
                            break;
                        case 7:
                            contactInfo.setWebsite(buffer.readWideStr());
                            break;
                        case 8:
                            contactInfo.setWorkPhone(buffer.readUTF8Str((String) null));
                            break;
                        case 9:
                            contactInfo.setBirthdayMonth(buffer.readWideStr());
                            break;
                        case 10:
                            buffer.readWideStr();
                            break;
                        case 11:
                            buffer.readWideStr();
                            break;
                        case 12:
                            contactInfo.setAltEmail(buffer.readWideStr());
                            break;
                        case 13:
                            contactInfo.setJobTitle(buffer.readWideStr());
                            break;
                        case 14:
                            contactInfo.setLocation(buffer.readWideStr());
                            break;
                        case 15:
                            contactInfo.setIconId(buffer.readWideStr());
                            break;
                        case 16:
                            contactInfo.setIconName(buffer.readUTF8Str((String) null));
                            break;
                        case 17:
                            contactInfo.setDescription(buffer.readUTF8Str((String) null));
                            break;
                        default:
                            buffer.readWideStr();
                            break;
                    }
                } while (!StringUtils.equals(fieldType, (String) fieldNames.elementAt(fieldIdx)));
                switch (fieldIdx) {
                }
            }
            contactInfo.setEmailAddress(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(contactInfo.getString(50)).append('@').append(contactInfo.getString(51))));
        }
        ObjectPool.releaseVector(fieldNames);
        ObjectPool.releaseVector(fieldTypes);
        return result;
    }
}
