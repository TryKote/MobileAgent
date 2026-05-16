package com.trykote.mobileagent.protocol.mrim;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.RegistrationState;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.model.ContactGroup;
import com.trykote.mobileagent.model.ContactInfo;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import java.util.Hashtable;
import java.util.Vector;

public abstract class MrimContactParser {

    // Error message string resource IDs
    private static final int MSG_CONTACT_NOT_FOUND = 913;
    private static final int MSG_CONTACT_PARSE_ERROR = 914;

    // ContactInfo field indices for email construction
    private static final int FIELD_EMAIL_USER = 50;
    private static final int FIELD_EMAIL_DOMAIN = 51;

    // Marital status values
    private static final int MARITAL_MARRIED = 1;
    private static final int MARITAL_SINGLE = 2;

    public static final void createSingleContact(MrimAccount account, int i, ByteBuffer buffer) {
        ContactInfo contactInfo = ContactInfo.createForAccount(account);
        switch (i) {
            case 0:
                contactInfo.setContactName(ResourceAccessor.str(StringResKeys.STR_DEFAULT_CONTACT_NAME));
                break;
            case 1:
                contactInfo = (ContactInfo) parseMrimContacts(account, buffer).elementAt(0);
                break;
            default:
                contactInfo.setContactName(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_CONTACT_NAME_PREFIX)).append(i)));
                break;
        }
        RegistrationState.setParam1(contactInfo);
    }

    public static final void parseContactInfoResponse(MrimAccount account, int i, ByteBuffer buffer) {
        int nameIndex = 0;
        Vector contacts = null;
        switch (i) {
            case 0:
                nameIndex = MSG_CONTACT_NOT_FOUND;
                break;
            case 1:
                contacts = parseMrimContacts(account, buffer);
                break;
            default:
                nameIndex = MSG_CONTACT_PARSE_ERROR;
                break;
        }
        RuntimeState.setErrorMsgIndex(nameIndex);
        RegistrationState.setParam4(contacts);
    }

    public static final void updateContactName(MrimAccount account, int i, ByteBuffer buffer) {
        int nameIndex;
        switch (i) {
            case 0:
                nameIndex = MSG_CONTACT_NOT_FOUND;
                break;
            case 1:
                ContactInfo contactInfo = (ContactInfo) parseMrimContacts(account, buffer).elementAt(0);
                MrimContact contact = (MrimContact) account.contactMap.get(contactInfo.getEmailOrMmpId());
                if (contact != null) {
                    contact.setDisplayName(contactInfo.getFullName());
                    return;
                }
                return;
            default:
                nameIndex = MSG_CONTACT_PARSE_ERROR;
                break;
        }
        EventDispatcher.postNotification(AppState.getString(nameIndex));
    }

    public static final void addContactToGroup(MrimAccount account, int i, ByteBuffer buffer) {
        if (i == 1) {
            ContactInfo contactInfo = (ContactInfo) parseMrimContacts(account, buffer).elementAt(0);
            Hashtable contactMap = account.contactMap;
            String email = contactInfo.getEmailOrMmpId();
            MrimContact contact = (MrimContact) contactMap.get(email);
            if (contact != null) {
                String fullName = contactInfo.getFullName();
                contact.setDisplayName(fullName);
                account.validateGroupAdd(email, fullName, ResourceAccessor.str(StringResKeys.STR_DEFAULT_GROUP_NAME), (ContactGroup) account.getFirstContactGroup(), true);
            }
        }
    }
    private static final Vector parseMrimContacts(MrimAccount account, ByteBuffer buffer) {
        Vector result = ObjectPool.newVector();
        Vector fieldNames = Utils.splitByNull(ResourceAccessor.str(StringResKeys.STR_REG_FIELD_NAMES));
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
                            int maritalStatus = Utils.parseIntBounded(buffer.readWideStr(), MARITAL_MARRIED, MARITAL_SINGLE, 0);
                            if (MARITAL_MARRIED == maritalStatus) {
                                contactInfo.setMaritalMarried();
                                break;
                            } else if (MARITAL_SINGLE == maritalStatus) {
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
            contactInfo.setEmailAddress(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(contactInfo.getString(FIELD_EMAIL_USER)).append('@').append(contactInfo.getString(FIELD_EMAIL_DOMAIN))));
        }
        ObjectPool.releaseVector(fieldNames);
        ObjectPool.releaseVector(fieldTypes);
        return result;
    }
}
