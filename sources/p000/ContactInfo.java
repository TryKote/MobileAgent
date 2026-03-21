package p000;

import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;

/* renamed from: m */
/* loaded from: MobileAgent_3.9.jar:m.class */
public final class ContactInfo extends Hashtable {
    public ContactInfo(Contact abstractC0041l) {
        put(ResourceManager.integerOf(-2), abstractC0041l.account);
        setContactField(0, abstractC0041l.displayName);
        if (abstractC0041l instanceof MrimContact) {
            setContactField(3, ((MrimContact) abstractC0041l).simpleIdentifier);
        } else if (abstractC0041l instanceof MmpContact) {
            setMmpContactId(Utils.parseInt((Object) ((MmpContact) abstractC0041l).identifier));
        } else if (abstractC0041l instanceof XmppContact) {
            setContactField(26, ((XmppContact) abstractC0041l).jabberId);
        }
    }

    public ContactInfo() {
    }

    private ContactInfo(Account abstractC0037h) {
        put(ResourceManager.integerOf(-2), abstractC0037h);
    }

    /* renamed from: a */
    public static final ContactInfo createForAccount(Account abstractC0037h) {
        return new ContactInfo(abstractC0037h);
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
    public static final ContactInfo createAccountInfo(Account abstractC0037h) {
        return new ContactInfo(abstractC0037h);
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
        return setContactField(4, AppState.getString(318));
    }

    /* renamed from: e */
    public final ContactInfo setMaritalSingle() {
        return setContactField(4, AppState.getString(319));
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
        String strM584b;
        int i2 = i % 10;
        if (i <= 0 || i >= 100) {
            strM584b = AppState.getString(323);
        } else if (i < 5 || i > 20) {
            strM584b = i2 == 1 ? formatAge(i, 321) : (i2 < 2 || i2 > 4) ? formatAge(i, 320) : formatAge(i, 322);
        } else {
            strM584b = formatAge(i, 320);
        }
        return setContactField(5, strM584b);
    }

    /* renamed from: a */
    private static final String formatAge(int i, int i2) {
        return NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(i).append(AppState.getString(i2)));
    }

    /* renamed from: c */
    public final ContactInfo setMaritalStatus(int i) {
        return i == 1 ? setMaritalSingle() : i == 2 ? setMaritalMarried() : setContactField(4, AppState.getString(197069));
    }

    /* renamed from: p */
    public final ContactInfo setBirthdayMonth(String str) {
        int iM511a = Utils.m511a(str, 1, 12, 0);
        if (iM511a != 0) {
            Vector vectorM512e = Utils.splitByNull(AppState.getString(685));
            setContactField(7, (String) vectorM512e.elementAt(iM511a));
            NetworkUtils.releaseVector(vectorM512e);
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
        String strM1256a = getString(3);
        return strM1256a != null ? strM1256a : getFieldDefault(60);
    }

    /* renamed from: j */
    public final String getDisplayNameOrId() {
        String strM533j = Utils.trim(getFieldDefault(0));
        return StringUtils.isEmpty(strM533j) ? getString(60) : strM533j;
    }

    /* renamed from: k */
    public final String getFullName() {
        String strM533j = Utils.trim(StringUtils.concat(Utils.m527g(getFieldDefault(1)), Utils.trim(getFieldDefault(2))));
        String str = strM533j;
        if (StringUtils.isEmpty(strM533j)) {
            String strM1256a = getString(0);
            str = strM1256a;
            if (null == strM1256a) {
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
        int iIndexOf;
        int iIndexOf2;
        int iIndexOf3;
        Account abstractC0037hM1255c = getAccount();
        Screen c0013amM75b = ScreenManager.createScreen(i);
        Vector vectorM512e = Utils.splitByNull(AppState.getString(312));
        int size = vectorM512e.size();
        if (abstractC0037hM1255c instanceof MrimAccount) {
            MrimContact c0035f = (MrimContact) abstractC0037hM1255c.getContact((Object) getString(3));
            int i3 = 0;
            while (i3 < size) {
                try {
                    String strM1215a = NetworkUtils.bufToStringCached(Utils.m497a(NetworkUtils.newStringBuffer().append((String) vectorM512e.elementAt(i3))));
                    String strM1256a = getString(i3);
                    if (null != strM1256a) {
                        if (i3 == 6) {
                            c0013amM75b.m248a(strM1215a, NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append(StringUtils.substring(strM1256a, 8, 10)).append('/').append(StringUtils.substring(strM1256a, 5, 7)).append('/').append(StringUtils.prefix(strM1256a, 4))));
                        } else {
                            if (i3 == 10) {
                                c0013amM75b.m225a(MenuItem.createSeparator().addText(strM1215a, 0, 6).setIcon(c0035f == null ? AppController.m349a(Utils.m511a(strM1256a, 0, 4, 0), Utils.defaultStr(getString(12))) : c0035f.getIcon()).setLabel(Utils.defaultStr(getString(13))));
                                break;
                            }
                            c0013amM75b.m248a(strM1215a, i3 == 9 ? Utils.m530h(Utils.m532i(strM1256a)) : strM1256a);
                        }
                    }
                } catch (Throwable unused) {
                }
                i3++;
            }
            if (c0035f != null) {
                String strM522f = Utils.defaultStr(c0035f.statusMessage);
                int i4 = Conversation.hasKey(strM522f, 927) ? 936 : Conversation.hasKey(strM522f, 926) ? 935 : Conversation.hasKey(strM522f, 929) ? 937 : Conversation.hasKey(strM522f, 928) ? 938 : Conversation.hasKey(strM522f, 930) ? 939 : Conversation.hasKey(strM522f, 931) ? 940 : Conversation.hasKey(strM522f, 932) ? 941 : Conversation.hasKey(strM522f, 933) ? 942 : 934;
                StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
                if (i4 == 934) {
                    int iM627a = AppState.indexOfLong(strM522f, 2467256188365532259L);
                    if (iM627a >= 0 && (iIndexOf3 = strM522f.indexOf(34, iM627a + 9)) >= 0) {
                        stringBufferM1217h.append(StringUtils.substring(strM522f, iM627a + 8, iIndexOf3));
                    }
                } else {
                    stringBufferM1217h.append(AppState.getString(i4));
                }
                int iM628b = AppState.indexOfPool(strM522f, 943);
                if (iM628b >= 0 && (iIndexOf = strM522f.indexOf(34, iM628b + 11)) >= 0) {
                    stringBufferM1217h.append(AppState.getString(944)).append(StringUtils.substring(strM522f, iM628b + 10, iIndexOf));
                    int iM628b2 = AppState.indexOfPool(strM522f, 527990);
                    if (iM628b2 >= 0 && (iIndexOf2 = strM522f.indexOf(34, iM628b2 + 9)) >= 0) {
                        stringBufferM1217h.append('.').append(StringUtils.substring(strM522f, iM628b2 + 8, iIndexOf2));
                    }
                }
                String strM1215a2 = NetworkUtils.bufToStringCached(stringBufferM1217h);
                if (Utils.nonEmpty(strM1215a2)) {
                    MenuItem c0032cM901a = MenuItem.createSeparator().addText(AppState.getString(317), 0, 6);
                    String str = c0035f.statusMessage;
                    if (str == null) {
                        i2 = -1;
                        c0013amM75b.m225a(c0032cM901a.setIcon(i2).setLabel(strM1215a2));
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
                        c0013amM75b.m225a(c0032cM901a.setIcon(i2).setLabel(strM1215a2));
                    }
                }
                String str2 = c0035f.customLink;
                if (Utils.nonEmpty(str2)) {
                    c0013amM75b.m225a(MenuItem.createSeparator().addText(AppState.getString(324), 0, 6).setIcon(242).setLabel(str2));
                }
                String str3 = c0035f.customNote;
                if (Utils.nonEmpty(str3)) {
                    c0013amM75b.m225a(MenuItem.createSeparator().addText(AppState.getString(325), 0, 6).setIcon(2).setLabel(str3));
                }
                String strM998o = c0035f.getVCardDescription();
                if (Utils.nonEmpty(strM998o)) {
                    c0013amM75b.m225a(MenuItem.createSeparator().addText(AppState.getString(326), 0, 6).setIcon(365).setLabel(strM998o));
                }
            }
        } else if (abstractC0037hM1255c instanceof MmpProtocol) {
            String strM1256a2 = getString(60);
            if (null != strM1256a2) {
                c0013amM75b.m248a(Utils.m527g(AppState.getString(263250)), strM1256a2);
            }
            for (int i5 = 0; i5 < 5; i5++) {
                try {
                    String strM1256a3 = getString(i5);
                    if (null != strM1256a3) {
                        c0013amM75b.m248a(NetworkUtils.bufToStringCached(Utils.m497a(NetworkUtils.newStringBuffer().append(vectorM512e.elementAt(i5)))), strM1256a3);
                    }
                } catch (Throwable unused2) {
                }
            }
            String strM1256a4 = getString(5);
            if (null != strM1256a4) {
                c0013amM75b.m248a(AppState.getString(315), strM1256a4);
            }
            String strM1256a5 = getString(32);
            if (null != strM1256a5) {
                c0013amM75b.m248a(AppState.getString(313), strM1256a5);
            }
            String strM1256a6 = getString(37);
            if (null != strM1256a6) {
                c0013amM75b.m248a(AppState.getString(314), strM1256a6);
            }
            String strM1256a7 = getString(36);
            if (null != strM1256a7) {
                c0013amM75b.m248a(AppState.getString(316), strM1256a7);
            }
        } else if (abstractC0037hM1255c instanceof XmppProtocol) {
            Image image = (Image) get(ResourceManager.integerOf(25));
            if (image != null) {
                c0013amM75b.m225a(MenuItem.createGraphics(new GraphicsContext(image)));
            }
            c0013amM75b.m246a(Utils.parseInt((Object) getString(24)), getFieldDefault(0), 0);
            c0013amM75b.m245a(AppState.getString(744), getString(26), 0);
            String strM1256a8 = getString(11);
            if (null != strM1256a8) {
                c0013amM75b.m253a(strM1256a8);
            }
        }
        NetworkUtils.releaseVector(vectorM512e);
        return c0013amM75b;
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
