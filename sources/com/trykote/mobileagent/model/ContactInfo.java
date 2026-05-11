package com.trykote.mobileagent.model;

import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;

public final class ContactInfo extends Hashtable {

    // Age validation and formatting
    public static final int AGE_MIN_SPECIAL = 5;
    public static final int AGE_MAX_SPECIAL = 20;
    public static final int AGE_MAX_VALID = 100;
    public static final int AGE_SUFFIX_GENERAL = 320;
    public static final int AGE_SUFFIX_SINGULAR = 321;
    public static final int AGE_SUFFIX_FEW = 322;

    // Contact field indices
    public static final int FIELD_DISPLAY_NAME = 0;
    public static final int FIELD_FIRST_NAME = 1;
    public static final int FIELD_LAST_NAME = 2;
    public static final int FIELD_EMAIL = 3;
    public static final int FIELD_MARITAL_STATUS = 4;
    public static final int FIELD_AGE = 5;
    public static final int FIELD_BIRTHDAY = 6;
    public static final int FIELD_BIRTHDAY_MONTH = 7;
    public static final int FIELD_WORK_PHONE = 8;
    public static final int FIELD_JOB_TITLE = 9;
    public static final int FIELD_LOCATION = 10;
    public static final int FIELD_DESCRIPTION = 11;
    public static final int FIELD_ICON_ID = 12;
    public static final int FIELD_ICON_NAME = 13;
    public static final int FIELD_IMAGE_WIDTH = 24;
    public static final int FIELD_XMPP_IMAGE = 25;
    public static final int FIELD_XMPP_ID = 26;
    public static final int FIELD_CUSTOM_1 = 32;
    public static final int FIELD_CUSTOM_2 = 33;
    public static final int FIELD_CUSTOM_3 = 34;
    public static final int FIELD_CUSTOM_4 = 35;
    public static final int FIELD_CUSTOM_5 = 36;
    public static final int FIELD_CUSTOM_6 = 37;
    public static final int FIELD_PHONE_HOME = 50;
    public static final int FIELD_PHONE_MOBILE = 51;
    public static final int FIELD_WEBSITE = 52;
    public static final int FIELD_ALT_EMAIL = 53;
    public static final int FIELD_MMP_CONTACT_ID = 60;
    public static final int FIELD_MMP_TYPE_ID = 61;

    // Account key index
    private static final int KEY_ACCOUNT = -2;
    private static final int KEY_CONTACT_NAME = -1;

    // Marital status codes
    private static final int MARITAL_SINGLE = 1;
    private static final int MARITAL_MARRIED = 2;

    // Month range
    private static final int MONTH_MIN = 1;
    private static final int MONTH_MAX = 12;

    // MMP contact info field count
    public static final int MMP_FIELD_COUNT = 5;
    public ContactInfo(Contact contact) {
        put(ObjectPool.integerOf(KEY_ACCOUNT), contact.account);
        setContactField(FIELD_DISPLAY_NAME, contact.displayName);
        if (contact instanceof MrimContact) {
            setContactField(FIELD_EMAIL, ((MrimContact) contact).simpleIdentifier);
        } else if (contact instanceof MmpContact) {
            setMmpContactId(Utils.parseInt((Object) ((MmpContact) contact).identifier));
        } else if (contact instanceof XmppContact) {
            setContactField(FIELD_XMPP_ID, ((XmppContact) contact).jabberId);
        }
    }

    public ContactInfo() {
    }

    private ContactInfo(Account account) {
        put(ObjectPool.integerOf(KEY_ACCOUNT), account);
    }

    public static final ContactInfo createForAccount(Account account) {
        return new ContactInfo(account);
    }

    public final boolean isXmppContact() {
        if (getString(FIELD_XMPP_ID) == null) {
            return getAccount() != null && (getAccount() instanceof XmppProtocol);
        }
        return true;
    }

    public final boolean isMrimContact() {
        if (getString(FIELD_MMP_CONTACT_ID) != null) {
            return false;
        }
        if ((getAccount() == null || !(getAccount() instanceof MmpProtocol)) && getString(FIELD_XMPP_ID) == null) {
            return getAccount() == null || !(getAccount() instanceof XmppProtocol);
        }
        return false;
    }

    public static final ContactInfo createAccountInfo(Account account) {
        return new ContactInfo(account);
    }

    public final Account getAccount() {
        return (Account) get(ObjectPool.integerOf(KEY_ACCOUNT));
    }

    public final String getString(int i) {
        return (String) get(ObjectPool.integerOf(i));
    }

    public final ContactInfo setContactName(String str) {
        return setContactField(KEY_CONTACT_NAME, str);
    }

    private final ContactInfo setContactField(int i, String str) {
        if (Utils.nonEmpty(str)) {
            put(ObjectPool.integerOf(i), str);
        }
        return this;
    }

    public final ContactInfo setDisplayName(String str) {
        return setContactField(FIELD_DISPLAY_NAME, str);
    }

    public final ContactInfo setFirstName(String str) {
        return setContactField(FIELD_FIRST_NAME, str);
    }

    public final ContactInfo setLastName(String str) {
        return setContactField(FIELD_LAST_NAME, str);
    }

    public final ContactInfo setEmailAddress(String str) {
        return setContactField(FIELD_EMAIL, str);
    }

    public final ContactInfo setMaritalMarried() {
        return setContactField(FIELD_MARITAL_STATUS, ResourceAccessor.str(StringResKeys.STR_GENDER_MALE));
    }

    public final ContactInfo setMaritalSingle() {
        return setContactField(FIELD_MARITAL_STATUS, ResourceAccessor.str(StringResKeys.STR_GENDER_FEMALE));
    }

    public final ContactInfo setCompany(String str) {
        return setContactField(FIELD_BIRTHDAY, str);
    }

    public final ContactInfo setJobTitle(String str) {
        return setContactField(FIELD_JOB_TITLE, str);
    }

    public final ContactInfo setLocation(String str) {
        return setContactField(FIELD_LOCATION, str);
    }

    public final ContactInfo setCustomField1(String str) {
        return setContactField(FIELD_CUSTOM_1, str);
    }

    public final ContactInfo setCustomField2(String str) {
        return setContactField(FIELD_CUSTOM_2, str);
    }

    public final ContactInfo setCustomField3(String str) {
        return setContactField(FIELD_CUSTOM_3, str);
    }

    public final ContactInfo setCustomField4(String str) {
        return setContactField(FIELD_CUSTOM_4, str);
    }

    public final ContactInfo setCustomField5(String str) {
        return setContactField(FIELD_CUSTOM_5, str);
    }

    public final ContactInfo setCustomField6(String str) {
        return setContactField(FIELD_CUSTOM_6, str);
    }

    public final ContactInfo setWorkPhone(String str) {
        return setContactField(FIELD_WORK_PHONE, str);
    }
    public final ContactInfo setAge(int i) {
        String ageStr;
        int i2 = i % 10;
        if (i <= 0 || i >= AGE_MAX_VALID) {
            ageStr = ResourceAccessor.str(StringResKeys.STR_AGE_UNKNOWN);
        } else if (i < AGE_MIN_SPECIAL || i > AGE_MAX_SPECIAL) {
            ageStr = i2 == 1 ? formatAge(i, AGE_SUFFIX_SINGULAR) : (i2 < 2 || i2 > 4) ? formatAge(i, AGE_SUFFIX_GENERAL) : formatAge(i, AGE_SUFFIX_FEW);
        } else {
            ageStr = formatAge(i, AGE_SUFFIX_GENERAL);
        }
        return setContactField(FIELD_AGE, ageStr);
    }

    private static final String formatAge(int i, int i2) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(i).append(AppState.getString(i2)));
    }

    public final ContactInfo setMaritalStatus(int i) {
        return i == MARITAL_SINGLE ? setMaritalSingle() : i == MARITAL_MARRIED ? setMaritalMarried() : setContactField(FIELD_MARITAL_STATUS, ResourceAccessor.str(PackedStringKeys.PLACEHOLDER_UNKNOWN));
    }

    public final ContactInfo setBirthdayMonth(String str) {
        int month = Utils.parseIntBounded(str, MONTH_MIN, MONTH_MAX, 0);
        if (month != 0) {
            Vector labels = Utils.splitByNull(ResourceAccessor.str(StringResKeys.STR_MONTH_NAMES));
            setContactField(FIELD_BIRTHDAY_MONTH, (String) labels.elementAt(month));
            ObjectPool.releaseVector(labels);
        }
        return this;
    }

    public final ContactInfo setPhoneHome(String str) {
        return setContactField(FIELD_PHONE_HOME, str);
    }

    public final ContactInfo setPhoneMobile(String str) {
        return setContactField(FIELD_PHONE_MOBILE, str);
    }

    public final ContactInfo setWebsite(String str) {
        return setContactField(FIELD_WEBSITE, str);
    }

    public final ContactInfo setAltEmail(String str) {
        return setContactField(FIELD_ALT_EMAIL, str);
    }

    public final ContactInfo setMmpContactId(int i) {
        return setContactField(FIELD_MMP_CONTACT_ID, StringUtils.intern(Integer.toString(i)));
    }

    public final ContactInfo setMmpContactIdStr(String str) {
        return setContactField(FIELD_MMP_CONTACT_ID, str);
    }

    public final ContactInfo setMmpTypeId(int i) {
        return setContactField(FIELD_MMP_TYPE_ID, StringUtils.intern(Integer.toString(i)));
    }

    private final String getFieldDefault(int i) {
        return Utils.defaultStr(getString(i));
    }

    public final String getDisplayName() {
        return getFieldDefault(FIELD_DISPLAY_NAME);
    }

    public final String getFirstName() {
        return getFieldDefault(FIELD_FIRST_NAME);
    }

    public final String getLastName() {
        return getFieldDefault(FIELD_LAST_NAME);
    }

    public final String getEmailOrMmpId() {
        String fieldVal = getString(FIELD_EMAIL);
        return fieldVal != null ? fieldVal : getFieldDefault(FIELD_MMP_CONTACT_ID);
    }

    public final String getDisplayNameOrId() {
        String trimmed = Utils.trim(getFieldDefault(FIELD_DISPLAY_NAME));
        return StringUtils.isEmpty(trimmed) ? getString(FIELD_MMP_CONTACT_ID) : trimmed;
    }

    public final String getFullName() {
        String trimmed = Utils.trim(StringUtils.concat(Utils.appendSpace(getFieldDefault(FIELD_FIRST_NAME)), Utils.trim(getFieldDefault(FIELD_LAST_NAME))));
        String str = trimmed;
        if (StringUtils.isEmpty(trimmed)) {
            String fieldVal = getString(FIELD_DISPLAY_NAME);
            str = fieldVal;
            if (fieldVal == null) {
                return getEmailOrMmpId();
            }
        }
        return str;
    }
    public final ListView buildContactScreen(ListView screen) {
        int i2 = 0;
        int endIdx;
        int endIdx2;
        int endIdx3;
        Account acct = getAccount();
        Vector labels = Utils.splitByNull(ResourceAccessor.str(StringResKeys.STR_CONTACT_FIELD_LABELS));
        int size = labels.size();
        if (acct instanceof MrimAccount) {
            MrimContact mrimContact = (MrimContact) acct.getContact((Object) getString(FIELD_EMAIL));
            int i3 = 0;
            while (i3 < size) {
                try {
                    String label = ObjectPool.toStringAndRelease(Utils.appendColon(ObjectPool.newStringBuffer().append((String) labels.elementAt(i3))));
                    String fieldVal = getString(i3);
                    if (fieldVal != null) {
                        if (i3 == FIELD_BIRTHDAY) {
                            screen.addLabelValue(label, ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringUtils.substring(fieldVal, 8, 10)).append('/').append(StringUtils.substring(fieldVal, 5, 7)).append('/').append(StringUtils.prefix(fieldVal, 4))));
                        } else {
                            if (i3 == FIELD_LOCATION) {
                                screen.addItem(MenuItem.createSeparator().addText(label, 0, 6).setIcon(mrimContact == null ? AppController.resolveServerIcon(Utils.parseIntBounded(fieldVal, 0, 4, 0), Utils.defaultStr(getString(FIELD_ICON_ID))) : mrimContact.getIcon()).setLabel(Utils.defaultStr(getString(FIELD_ICON_NAME))));
                                break;
                            }
                            screen.addLabelValue(label, i3 == FIELD_JOB_TITLE ? Utils.formatPhone(Utils.extractDigits(fieldVal)) : fieldVal);
                        }
                    }
                } catch (Throwable unused) {
                }
                i3++;
            }
            if (mrimContact != null) {
                String statusMsg = Utils.defaultStr(mrimContact.statusMessage);
                int i4 = Conversation.hasKey(statusMsg, 927) ? 936 : Conversation.hasKey(statusMsg, 926) ? 935 : Conversation.hasKey(statusMsg, 929) ? 937 : Conversation.hasKey(statusMsg, 928) ? 938 : Conversation.hasKey(statusMsg, 930) ? 939 : Conversation.hasKey(statusMsg, 931) ? 940 : Conversation.hasKey(statusMsg, 932) ? 941 : Conversation.hasKey(statusMsg, 933) ? 942 : 934;
                StringBuffer sb = ObjectPool.newStringBuffer();
                if (i4 == 934) {
                    int startIdx = StringUtils.indexOfPackedLong(statusMsg, 2467256188365532259L);
                    if (startIdx >= 0 && (endIdx3 = statusMsg.indexOf(34, startIdx + 9)) >= 0) {
                        sb.append(StringUtils.substring(statusMsg, startIdx + 8, endIdx3));
                    }
                } else {
                    sb.append(AppState.getString(i4));
                }
                int titleIdx = StringUtils.indexOfPoolString(statusMsg, 943);
                if (titleIdx >= 0 && (endIdx = statusMsg.indexOf(34, titleIdx + 11)) >= 0) {
                    sb.append(ResourceAccessor.str(StringResKeys.STR_STATUS_TITLE_PREFIX)).append(StringUtils.substring(statusMsg, titleIdx + 10, endIdx));
                    int trackIdx = StringUtils.indexOfPoolString(statusMsg, 527990);
                    if (trackIdx >= 0 && (endIdx2 = statusMsg.indexOf(34, trackIdx + 9)) >= 0) {
                        sb.append('.').append(StringUtils.substring(statusMsg, trackIdx + 8, endIdx2));
                    }
                }
                String statusDesc = ObjectPool.toStringAndRelease(sb);
                if (Utils.nonEmpty(statusDesc)) {
                    MenuItem statusItem = MenuItem.createSeparator().addText(ResourceAccessor.str(StringResKeys.STR_LABEL_STATUS), 0, 6);
                    String str = mrimContact.statusMessage;
                    if (str == null) {
                        i2 = -1;
                        screen.addItem(statusItem.setIcon(i2).setLabel(statusDesc));
                    } else {
                        if (Conversation.hasKey(str, 927)) {
                            i2 = 357;
                        } else if (Conversation.hasKey(str, 926)) {
                            i2 = 317;
                        } else if (Conversation.hasKey(str, 929)) {
                            i2 = 355;
                        } else if (Conversation.hasKey(str, 928)) {
                            i2 = 356;
                        } else if (Conversation.hasKey(str, 930)) {
                            i2 = 358;
                        } else if (Conversation.hasKey(str, 931) || Conversation.hasKey(str, 932)) {
                            i2 = 359;
                        } else if (Conversation.hasKey(str, 933)) {
                            i2 = 307;
                        }
                        screen.addItem(statusItem.setIcon(i2).setLabel(statusDesc));
                    }
                }
                String str2 = mrimContact.customLink;
                if (Utils.nonEmpty(str2)) {
                    screen.addItem(MenuItem.createSeparator().addText(ResourceAccessor.str(StringResKeys.STR_SECTION_PHONE), 0, 6).setIcon(242).setLabel(str2));
                }
                String str3 = mrimContact.customNote;
                if (Utils.nonEmpty(str3)) {
                    screen.addItem(MenuItem.createSeparator().addText(ResourceAccessor.str(StringResKeys.STR_SECTION_EMAIL), 0, 6).setIcon(2).setLabel(str3));
                }
                String vCardDesc = mrimContact.getVCardDescription();
                if (Utils.nonEmpty(vCardDesc)) {
                    screen.addItem(MenuItem.createSeparator().addText(ResourceAccessor.str(StringResKeys.STR_SECTION_ABOUT), 0, 6).setIcon(365).setLabel(vCardDesc));
                }
            }
        } else if (acct instanceof MmpProtocol) {
            String mmpId = getString(FIELD_MMP_CONTACT_ID);
            if (mmpId != null) {
                screen.addLabelValue(Utils.appendSpace(ResourceAccessor.str(PackedStringKeys.PREFIX_UIN)), mmpId);
            }
            for (int i5 = 0; i5 < MMP_FIELD_COUNT; i5++) {
                try {
                    String mmpField = getString(i5);
                    if (mmpField != null) {
                        screen.addLabelValue(ObjectPool.toStringAndRelease(Utils.appendColon(ObjectPool.newStringBuffer().append(labels.elementAt(i5)))), mmpField);
                    }
                } catch (Throwable unused2) {
                }
            }
            String age = getString(FIELD_AGE);
            if (age != null) {
                screen.addLabelValue(ResourceAccessor.str(StringResKeys.STR_LABEL_AGE), age);
            }
            String company = getString(FIELD_CUSTOM_1);
            if (company != null) {
                screen.addLabelValue(ResourceAccessor.str(StringResKeys.STR_LABEL_COMPANY), company);
            }
            String loc = getString(FIELD_CUSTOM_6);
            if (loc != null) {
                screen.addLabelValue(ResourceAccessor.str(StringResKeys.STR_LABEL_LOCATION), loc);
            }
            String website = getString(FIELD_CUSTOM_5);
            if (website != null) {
                screen.addLabelValue(ResourceAccessor.str(StringResKeys.STR_LABEL_WEBSITE), website);
            }
        } else if (acct instanceof XmppProtocol) {
            Image image = (Image) get(ObjectPool.integerOf(FIELD_XMPP_IMAGE));
            if (image != null) {
                screen.addItem(MenuItem.createGraphics(new GraphicsContext(image)));
            }
            screen.addIconItem(Utils.parseInt((Object) getString(FIELD_IMAGE_WIDTH)), getFieldDefault(FIELD_DISPLAY_NAME), 0);
            screen.addTextPair(ResourceAccessor.str(StringResKeys.STR_LABEL_NOTES), getString(FIELD_XMPP_ID), 0);
            String xmppDesc = getString(FIELD_DESCRIPTION);
            if (xmppDesc != null) {
                screen.addTextItem(xmppDesc);
            }
        }
        ObjectPool.releaseVector(labels);
        return screen;
    }

    public final void setIconId(String str) {
        setContactField(FIELD_ICON_ID, str);
    }

    public final void setIconName(String str) {
        setContactField(FIELD_ICON_NAME, str);
    }

    public final ContactInfo setDescription(String str) {
        return setContactField(FIELD_DESCRIPTION, str);
    }

    public final ContactInfo setDescriptionBis(String str) {
        return setContactField(FIELD_DESCRIPTION, str);
    }

    public final ContactInfo setImageWidth(int i) {
        return setContactField(FIELD_IMAGE_WIDTH, StringUtils.intern(Integer.toString(i)));
    }
}
