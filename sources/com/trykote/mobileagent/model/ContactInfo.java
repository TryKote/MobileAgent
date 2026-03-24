package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
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

/* renamed from: m */
/* loaded from: MobileAgent_3.9.jar:m.class */
public final class ContactInfo extends Hashtable {
    public ContactInfo(Contact contact) {
        put(ResourceManager.integerOf(-2), contact.account);
        setContactField(0, contact.displayName);
        if (contact instanceof MrimContact) {
            setContactField(3, ((MrimContact) contact).simpleIdentifier);
        } else if (contact instanceof MmpContact) {
            setMmpContactId(Utils.parseInt((Object) ((MmpContact) contact).identifier));
        } else if (contact instanceof XmppContact) {
            setContactField(26, ((XmppContact) contact).jabberId);
        }
    }

    public ContactInfo() {
    }

    private ContactInfo(Account account) {
        put(ResourceManager.integerOf(-2), account);
    }

    /* renamed from: a */
    public static final ContactInfo createForAccount(Account account) {
        return new ContactInfo(account);
    }

    /* renamed from: a */
    public final boolean isXmppContact() {
        if (getString(26) == null) {
            return getAccount() != null && (getAccount() instanceof XmppProtocol);
        }
        return true;
    }

    /* renamed from: b */
    public final boolean isMrimContact() {
        if (getString(60) != null) {
            return false;
        }
        if ((getAccount() == null || !(getAccount() instanceof MmpProtocol)) && getString(26) == null) {
            return getAccount() == null || !(getAccount() instanceof XmppProtocol);
        }
        return false;
    }

    /* renamed from: b */
    public static final ContactInfo createAccountInfo(Account account) {
        return new ContactInfo(account);
    }

    /* renamed from: c */
    public final Account getAccount() {
        return (Account) get(ResourceManager.integerOf(-2));
    }

    /* renamed from: a */
    public final String getString(int i) {
        return (String) get(ResourceManager.integerOf(i));
    }

    /* renamed from: a */
    public final ContactInfo setContactName(String str) {
        return setContactField(-1, str);
    }

    /* renamed from: a */
    private final ContactInfo setContactField(int i, String str) {
        if (Utils.nonEmpty(str)) {
            put(ResourceManager.integerOf(i), str);
        }
        return this;
    }

    /* renamed from: b */
    public final ContactInfo setDisplayName(String str) {
        return setContactField(0, str);
    }

    /* renamed from: c */
    public final ContactInfo setFirstName(String str) {
        return setContactField(1, str);
    }

    /* renamed from: d */
    public final ContactInfo setLastName(String str) {
        return setContactField(2, str);
    }

    /* renamed from: e */
    public final ContactInfo setEmailAddress(String str) {
        return setContactField(3, str);
    }

    /* renamed from: d */
    public final ContactInfo setMaritalMarried() {
        return setContactField(4, AppState.getString(StateKeys.STR_GENDER_MALE));
    }

    /* renamed from: e */
    public final ContactInfo setMaritalSingle() {
        return setContactField(4, AppState.getString(StateKeys.STR_GENDER_FEMALE));
    }

    /* renamed from: f */
    public final ContactInfo setCompany(String str) {
        return setContactField(6, str);
    }

    /* renamed from: g */
    public final ContactInfo setJobTitle(String str) {
        return setContactField(9, str);
    }

    /* renamed from: h */
    public final ContactInfo setLocation(String str) {
        return setContactField(10, str);
    }

    /* renamed from: i */
    public final ContactInfo setCustomField1(String str) {
        return setContactField(32, str);
    }

    /* renamed from: j */
    public final ContactInfo setCustomField2(String str) {
        return setContactField(33, str);
    }

    /* renamed from: k */
    public final ContactInfo setCustomField3(String str) {
        return setContactField(34, str);
    }

    /* renamed from: l */
    public final ContactInfo setCustomField4(String str) {
        return setContactField(35, str);
    }

    /* renamed from: m */
    public final ContactInfo setCustomField5(String str) {
        return setContactField(36, str);
    }

    /* renamed from: n */
    public final ContactInfo setCustomField6(String str) {
        return setContactField(37, str);
    }

    /* renamed from: o */
    public final ContactInfo setWorkPhone(String str) {
        return setContactField(8, str);
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0048  */
    /* renamed from: b */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final ContactInfo setAge(int i) {
        String ageStr;
        int i2 = i % 10;
        if (i <= 0 || i >= 100) {
            ageStr = AppState.getString(StateKeys.STR_AGE_UNKNOWN);
        } else if (i < 5 || i > 20) {
            ageStr = i2 == 1 ? formatAge(i, 321) : (i2 < 2 || i2 > 4) ? formatAge(i, 320) : formatAge(i, 322);
        } else {
            ageStr = formatAge(i, 320);
        }
        return setContactField(5, ageStr);
    }

    /* renamed from: a */
    private static final String formatAge(int i, int i2) {
        return ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(i).append(AppState.getString(i2)));
    }

    /* renamed from: c */
    public final ContactInfo setMaritalStatus(int i) {
        return i == 1 ? setMaritalSingle() : i == 2 ? setMaritalMarried() : setContactField(4, AppState.getString(StateKeys.STR_RES_ARROW));
    }

    /* renamed from: p */
    public final ContactInfo setBirthdayMonth(String str) {
        int month = Utils.parseIntBounded(str, 1, 12, 0);
        if (month != 0) {
            Vector labels = Utils.splitByNull(AppState.getString(StateKeys.STR_MONTH_NAMES));
            setContactField(7, (String) labels.elementAt(month));
            ObjectPool.releaseVector(labels);
        }
        return this;
    }

    /* renamed from: q */
    public final ContactInfo setPhoneHome(String str) {
        return setContactField(50, str);
    }

    /* renamed from: r */
    public final ContactInfo setPhoneMobile(String str) {
        return setContactField(51, str);
    }

    /* renamed from: s */
    public final ContactInfo setWebsite(String str) {
        return setContactField(52, str);
    }

    /* renamed from: t */
    public final ContactInfo setAltEmail(String str) {
        return setContactField(53, str);
    }

    /* renamed from: d */
    public final ContactInfo setMmpContactId(int i) {
        return setContactField(60, StringUtils.intern(Integer.toString(i)));
    }

    /* renamed from: u */
    public final ContactInfo setMmpContactIdStr(String str) {
        return setContactField(60, str);
    }

    /* renamed from: e */
    public final ContactInfo setMmpTypeId(int i) {
        return setContactField(61, StringUtils.intern(Integer.toString(i)));
    }

    /* renamed from: h */
    private final String getFieldDefault(int i) {
        return Utils.defaultStr(getString(i));
    }

    /* renamed from: f */
    public final String getDisplayName() {
        return getFieldDefault(0);
    }

    /* renamed from: g */
    public final String getFirstName() {
        return getFieldDefault(1);
    }

    /* renamed from: h */
    public final String getLastName() {
        return getFieldDefault(2);
    }

    /* renamed from: i */
    public final String getEmailOrMmpId() {
        String fieldVal = getString(3);
        return fieldVal != null ? fieldVal : getFieldDefault(60);
    }

    /* renamed from: j */
    public final String getDisplayNameOrId() {
        String trimmed = Utils.trim(getFieldDefault(0));
        return StringUtils.isEmpty(trimmed) ? getString(60) : trimmed;
    }

    /* renamed from: k */
    public final String getFullName() {
        String trimmed = Utils.trim(StringUtils.concat(Utils.appendSpace(getFieldDefault(1)), Utils.trim(getFieldDefault(2))));
        String str = trimmed;
        if (StringUtils.isEmpty(trimmed)) {
            String fieldVal = getString(0);
            str = fieldVal;
            if (null == fieldVal) {
                return getEmailOrMmpId();
            }
        }
        return str;
    }

    /* JADX WARN: Removed duplicated region for block: B:99:0x0302  */
    /* renamed from: f */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final Screen buildContactScreen(int i) {
        int i2 = 0;
        int endIdx;
        int endIdx2;
        int endIdx3;
        Account acct = getAccount();
        Screen screen = ScreenManager.createScreen(i);
        Vector labels = Utils.splitByNull(AppState.getString(StateKeys.STR_CONTACT_FIELD_LABELS));
        int size = labels.size();
        if (acct instanceof MrimAccount) {
            MrimContact mrimContact = (MrimContact) acct.getContact((Object) getString(3));
            int i3 = 0;
            while (i3 < size) {
                try {
                    String label = ObjectPool.toStringAndRelease(Utils.appendColon(ObjectPool.newStringBuffer().append((String) labels.elementAt(i3))));
                    String fieldVal = getString(i3);
                    if (null != fieldVal) {
                        if (i3 == 6) {
                            screen.addLabelValue(label, ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(StringUtils.substring(fieldVal, 8, 10)).append('/').append(StringUtils.substring(fieldVal, 5, 7)).append('/').append(StringUtils.prefix(fieldVal, 4))));
                        } else {
                            if (i3 == 10) {
                                screen.addItem(MenuItem.createSeparator().addText(label, 0, 6).setIcon(mrimContact == null ? AppController.handleServerAction(Utils.parseIntBounded(fieldVal, 0, 4, 0), Utils.defaultStr(getString(12))) : mrimContact.getIcon()).setLabel(Utils.defaultStr(getString(13))));
                                break;
                            }
                            screen.addLabelValue(label, i3 == 9 ? Utils.formatPhone(Utils.extractDigits(fieldVal)) : fieldVal);
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
                    int startIdx = AppState.indexOfLong(statusMsg, 2467256188365532259L);
                    if (startIdx >= 0 && (endIdx3 = statusMsg.indexOf(34, startIdx + 9)) >= 0) {
                        sb.append(StringUtils.substring(statusMsg, startIdx + 8, endIdx3));
                    }
                } else {
                    sb.append(AppState.getString(i4));
                }
                int titleIdx = AppState.indexOfPool(statusMsg, 943);
                if (titleIdx >= 0 && (endIdx = statusMsg.indexOf(34, titleIdx + 11)) >= 0) {
                    sb.append(AppState.getString(StateKeys.STR_STATUS_TITLE_PREFIX)).append(StringUtils.substring(statusMsg, titleIdx + 10, endIdx));
                    int trackIdx = AppState.indexOfPool(statusMsg, 527990);
                    if (trackIdx >= 0 && (endIdx2 = statusMsg.indexOf(34, trackIdx + 9)) >= 0) {
                        sb.append('.').append(StringUtils.substring(statusMsg, trackIdx + 8, endIdx2));
                    }
                }
                String statusDesc = ObjectPool.toStringAndRelease(sb);
                if (Utils.nonEmpty(statusDesc)) {
                    MenuItem statusItem = MenuItem.createSeparator().addText(AppState.getString(StateKeys.STR_LABEL_STATUS), 0, 6);
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
                    screen.addItem(MenuItem.createSeparator().addText(AppState.getString(StateKeys.STR_SECTION_PHONE), 0, 6).setIcon(242).setLabel(str2));
                }
                String str3 = mrimContact.customNote;
                if (Utils.nonEmpty(str3)) {
                    screen.addItem(MenuItem.createSeparator().addText(AppState.getString(StateKeys.STR_SECTION_EMAIL), 0, 6).setIcon(2).setLabel(str3));
                }
                String vCardDesc = mrimContact.getVCardDescription();
                if (Utils.nonEmpty(vCardDesc)) {
                    screen.addItem(MenuItem.createSeparator().addText(AppState.getString(StateKeys.STR_SECTION_ABOUT), 0, 6).setIcon(365).setLabel(vCardDesc));
                }
            }
        } else if (acct instanceof MmpProtocol) {
            String mmpId = getString(60);
            if (null != mmpId) {
                screen.addLabelValue(Utils.appendSpace(AppState.getString(StateKeys.STR_RES_QUESTION_MARK)), mmpId);
            }
            for (int i5 = 0; i5 < 5; i5++) {
                try {
                    String mmpField = getString(i5);
                    if (null != mmpField) {
                        screen.addLabelValue(ObjectPool.toStringAndRelease(Utils.appendColon(ObjectPool.newStringBuffer().append(labels.elementAt(i5)))), mmpField);
                    }
                } catch (Throwable unused2) {
                }
            }
            String age = getString(5);
            if (null != age) {
                screen.addLabelValue(AppState.getString(StateKeys.STR_LABEL_AGE), age);
            }
            String company = getString(32);
            if (null != company) {
                screen.addLabelValue(AppState.getString(StateKeys.STR_LABEL_COMPANY), company);
            }
            String loc = getString(37);
            if (null != loc) {
                screen.addLabelValue(AppState.getString(StateKeys.STR_LABEL_LOCATION), loc);
            }
            String website = getString(36);
            if (null != website) {
                screen.addLabelValue(AppState.getString(StateKeys.STR_LABEL_WEBSITE), website);
            }
        } else if (acct instanceof XmppProtocol) {
            Image image = (Image) get(ResourceManager.integerOf(25));
            if (image != null) {
                screen.addItem(MenuItem.createGraphics(new GraphicsContext(image)));
            }
            screen.addIconItem(Utils.parseInt((Object) getString(24)), getFieldDefault(0), 0);
            screen.addTextPair(AppState.getString(StateKeys.STR_LABEL_NOTES), getString(26), 0);
            String xmppDesc = getString(11);
            if (null != xmppDesc) {
                screen.addTextItem(xmppDesc);
            }
        }
        ObjectPool.releaseVector(labels);
        return screen;
    }

    /* renamed from: v */
    public final void setIconId(String str) {
        setContactField(12, str);
    }

    /* renamed from: w */
    public final void setIconName(String str) {
        setContactField(13, str);
    }

    /* renamed from: x */
    public final ContactInfo setDescription(String str) {
        return setContactField(11, str);
    }

    /* renamed from: y */
    public final ContactInfo setDescriptionBis(String str) {
        return setContactField(11, str);
    }

    /* renamed from: g */
    public final ContactInfo setImageWidth(int i) {
        return setContactField(24, StringUtils.intern(Integer.toString(i)));
    }
}
