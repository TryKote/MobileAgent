package p000;

import java.util.Hashtable;

/* renamed from: aa */
/* loaded from: MobileAgent_3.9.jar:aa.class */
public final class XmlParser {

    /* renamed from: a */
    private String rootTagName;

    /* renamed from: b */
    private XmlElement rootElement;

    /* renamed from: c */
    private Object source;

    /* renamed from: d */
    private int position;

    public XmlParser(Object obj) {
        this.source = obj;
    }

    /* renamed from: b */
    private final int nextChar() {
        if (!(this.source instanceof String)) {
            if (!(this.source instanceof ByteBuffer)) {
                return ResourceManager.readUtf8Char((Object[]) this.source);
            }
            ByteBuffer c0043n = (ByteBuffer) this.source;
            if (c0043n.length == 0) {
                return -1;
            }
            return Utils.win1251ToChar(c0043n.readUByte());
        }
        int i = this.position;
        String str = (String) this.source;
        if (i >= str.length()) {
            return -1;
        }
        int i2 = this.position;
        this.position = i2 + 1;
        return str.charAt(i2);
    }

    /* renamed from: a */
    private final String parseTagOrContent(int i) {
        boolean z;
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        boolean z2 = false;
        int iM45b = nextChar();
        int iM45b2 = iM45b;
        if (iM45b == -1) {
            throw new RuntimeException();
        }
        int i2 = 0;
        while (iM45b2 != -1) {
            if (!z2) {
                int i3 = iM45b2;
                if (i == 1) {
                    z = i3 == 60;
                } else {
                    if (i != 2) {
                        throw new RuntimeException();
                    }
                    z = i3 == 62 || i3 == 32;
                }
                if (z) {
                    break;
                }
            }
            if ((iM45b2 == 34 || iM45b2 == 39) && (i2 == 0 || i2 == iM45b2)) {
                z2 = !z2;
                i2 = iM45b2;
            } else {
                stringBufferM1217h.append((char) iM45b2);
            }
            iM45b2 = nextChar();
        }
        if (iM45b2 != 60 && iM45b2 != 62) {
            stringBufferM1217h.append((char) iM45b2);
        }
        return NetworkUtils.bufToStringCached(stringBufferM1217h);
    }

    /* JADX WARN: Removed duplicated region for block: B:106:0x0261 A[Catch: Throwable -> 0x03cb, TryCatch #0 {Throwable -> 0x03cb, blocks: (B:3:0x000a, B:5:0x001b, B:88:0x01e8, B:8:0x0030, B:9:0x0042, B:11:0x0051, B:13:0x005a, B:15:0x0067, B:17:0x0072, B:19:0x007f, B:21:0x008a, B:29:0x00ae, B:84:0x01d9, B:32:0x00c3, B:33:0x00d1, B:35:0x00da, B:45:0x0105, B:46:0x0113, B:48:0x011c, B:60:0x014d, B:67:0x0170, B:82:0x01ad, B:83:0x01d1, B:87:0x01e3, B:90:0x01fb, B:136:0x032b, B:138:0x0336, B:140:0x0344, B:142:0x034b, B:143:0x0351, B:145:0x037a, B:146:0x0381, B:151:0x038a, B:153:0x0394, B:155:0x03a2, B:156:0x03b4, B:158:0x03c0, B:159:0x03c7, B:95:0x0222, B:100:0x0238, B:101:0x023d, B:103:0x024b, B:104:0x0250, B:106:0x0261, B:107:0x0264, B:109:0x0274, B:112:0x0286, B:113:0x028f, B:118:0x02ab, B:120:0x02c3, B:121:0x02d1, B:126:0x02ee, B:132:0x0312, B:133:0x0315, B:125:0x02eb), top: B:163:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:109:0x0274 A[Catch: Throwable -> 0x03cb, TryCatch #0 {Throwable -> 0x03cb, blocks: (B:3:0x000a, B:5:0x001b, B:88:0x01e8, B:8:0x0030, B:9:0x0042, B:11:0x0051, B:13:0x005a, B:15:0x0067, B:17:0x0072, B:19:0x007f, B:21:0x008a, B:29:0x00ae, B:84:0x01d9, B:32:0x00c3, B:33:0x00d1, B:35:0x00da, B:45:0x0105, B:46:0x0113, B:48:0x011c, B:60:0x014d, B:67:0x0170, B:82:0x01ad, B:83:0x01d1, B:87:0x01e3, B:90:0x01fb, B:136:0x032b, B:138:0x0336, B:140:0x0344, B:142:0x034b, B:143:0x0351, B:145:0x037a, B:146:0x0381, B:151:0x038a, B:153:0x0394, B:155:0x03a2, B:156:0x03b4, B:158:0x03c0, B:159:0x03c7, B:95:0x0222, B:100:0x0238, B:101:0x023d, B:103:0x024b, B:104:0x0250, B:106:0x0261, B:107:0x0264, B:109:0x0274, B:112:0x0286, B:113:0x028f, B:118:0x02ab, B:120:0x02c3, B:121:0x02d1, B:126:0x02ee, B:132:0x0312, B:133:0x0315, B:125:0x02eb), top: B:163:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:110:0x0281  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00d1 A[Catch: Throwable -> 0x03cb, TryCatch #0 {Throwable -> 0x03cb, blocks: (B:3:0x000a, B:5:0x001b, B:88:0x01e8, B:8:0x0030, B:9:0x0042, B:11:0x0051, B:13:0x005a, B:15:0x0067, B:17:0x0072, B:19:0x007f, B:21:0x008a, B:29:0x00ae, B:84:0x01d9, B:32:0x00c3, B:33:0x00d1, B:35:0x00da, B:45:0x0105, B:46:0x0113, B:48:0x011c, B:60:0x014d, B:67:0x0170, B:82:0x01ad, B:83:0x01d1, B:87:0x01e3, B:90:0x01fb, B:136:0x032b, B:138:0x0336, B:140:0x0344, B:142:0x034b, B:143:0x0351, B:145:0x037a, B:146:0x0381, B:151:0x038a, B:153:0x0394, B:155:0x03a2, B:156:0x03b4, B:158:0x03c0, B:159:0x03c7, B:95:0x0222, B:100:0x0238, B:101:0x023d, B:103:0x024b, B:104:0x0250, B:106:0x0261, B:107:0x0264, B:109:0x0274, B:112:0x0286, B:113:0x028f, B:118:0x02ab, B:120:0x02c3, B:121:0x02d1, B:126:0x02ee, B:132:0x0312, B:133:0x0315, B:125:0x02eb), top: B:163:0x000a }] */
    /* JADX WARN: Removed duplicated region for block: B:68:0x017e  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x01d1 A[Catch: Throwable -> 0x03cb, TryCatch #0 {Throwable -> 0x03cb, blocks: (B:3:0x000a, B:5:0x001b, B:88:0x01e8, B:8:0x0030, B:9:0x0042, B:11:0x0051, B:13:0x005a, B:15:0x0067, B:17:0x0072, B:19:0x007f, B:21:0x008a, B:29:0x00ae, B:84:0x01d9, B:32:0x00c3, B:33:0x00d1, B:35:0x00da, B:45:0x0105, B:46:0x0113, B:48:0x011c, B:60:0x014d, B:67:0x0170, B:82:0x01ad, B:83:0x01d1, B:87:0x01e3, B:90:0x01fb, B:136:0x032b, B:138:0x0336, B:140:0x0344, B:142:0x034b, B:143:0x0351, B:145:0x037a, B:146:0x0381, B:151:0x038a, B:153:0x0394, B:155:0x03a2, B:156:0x03b4, B:158:0x03c0, B:159:0x03c7, B:95:0x0222, B:100:0x0238, B:101:0x023d, B:103:0x024b, B:104:0x0250, B:106:0x0261, B:107:0x0264, B:109:0x0274, B:112:0x0286, B:113:0x028f, B:118:0x02ab, B:120:0x02c3, B:121:0x02d1, B:126:0x02ee, B:132:0x0312, B:133:0x0315, B:125:0x02eb), top: B:163:0x000a }] */
    /* renamed from: a */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final XmlElement parse() {
        boolean zEndsWith;
        boolean z;
        XmlElement c0022av;
        String strM1215a;
        this.rootTagName = null;
        this.rootElement = null;
        while (true) {
            try {
                String strM46a = parseTagOrContent(1);
                if (this.rootElement != null) {
                    XmlElement c0022av2 = this.rootElement;
                    if (strM46a.indexOf(38) < 0) {
                        strM1215a = strM46a;
                    } else {
                        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
                        int length = strM46a.length();
                        int i = 0;
                        while (i < length) {
                            char cCharAt = strM46a.charAt(i);
                            if (cCharAt != '&') {
                                stringBufferM1217h.append(cCharAt);
                            } else {
                                char cCharAt2 = i + 1 < length ? strM46a.charAt(i + 1) : (char) 0;
                                char cCharAt3 = i + 2 < length ? strM46a.charAt(i + 2) : (char) 0;
                                char cCharAt4 = i + 3 < length ? strM46a.charAt(i + 3) : (char) 0;
                                if (cCharAt3 != 't' || cCharAt4 != ';') {
                                    char cCharAt5 = i + 4 < length ? strM46a.charAt(i + 4) : (char) 0;
                                    if (cCharAt2 == 'a' && cCharAt3 == 'm' && cCharAt4 == 'p' && cCharAt5 == ';') {
                                        stringBufferM1217h.append('&');
                                        i += 4;
                                    } else {
                                        if ((i + 5 < length ? strM46a.charAt(i + 5) : (char) 0) == ';') {
                                            if (cCharAt4 != 'o') {
                                                if (cCharAt2 == '#' && cCharAt3 >= '0' && cCharAt3 <= '9' && cCharAt4 >= 0 && cCharAt4 <= '9' && cCharAt5 >= '0' && cCharAt5 <= '9') {
                                                    stringBufferM1217h.append((char) ((cCharAt5 - '0') + ((cCharAt4 - '0') * 10) + ((cCharAt3 - '0') * 100)));
                                                    i += 5;
                                                }
                                            } else if (cCharAt2 == 'q' && cCharAt3 == 'u' && cCharAt5 == 't') {
                                                stringBufferM1217h.append('\"');
                                                i += 5;
                                            } else if (cCharAt2 == 'a' && cCharAt3 == 'p' && cCharAt5 == 's') {
                                                stringBufferM1217h.append('\'');
                                                i += 5;
                                            }
                                        }
                                    }
                                } else if (cCharAt2 == 'l') {
                                    stringBufferM1217h.append('<');
                                    i += 3;
                                } else if (cCharAt2 == 'g') {
                                    stringBufferM1217h.append('>');
                                    i += 3;
                                }
                            }
                            i++;
                        }
                        strM1215a = NetworkUtils.bufToStringCached(stringBufferM1217h);
                    }
                    c0022av2.appendText((Object) strM1215a);
                }
                boolean z2 = true;
                boolean z3 = false;
                String strM17c = null;
                Hashtable hashtable = null;
                while (true) {
                    String strM46a2 = parseTagOrContent(2);
                    int i2 = 0;
                    int length2 = strM46a2.length();
                    if (StringUtils.m3a(1046, strM46a2)) {
                        z = true;
                    } else if (length2 <= 0) {
                        zEndsWith = strM46a2.endsWith(AppState.getString(1046));
                        z = zEndsWith;
                        if (zEndsWith) {
                            length2--;
                        }
                        String strM12a = StringUtils.substring(strM46a2, i2, length2);
                        if (strM17c != null) {
                            strM17c = StringUtils.intern(strM12a.toLowerCase());
                        } else {
                            if (hashtable == null) {
                                hashtable = new Hashtable();
                            }
                            int length3 = strM12a.length();
                            int iIndexOf = strM12a.indexOf(61);
                            if (iIndexOf >= 0) {
                                String strM13b = StringUtils.prefix(strM12a, iIndexOf);
                                int i3 = iIndexOf + 1;
                                if (i3 >= strM12a.length()) {
                                    hashtable.put(strM13b, AppState.emptyStr);
                                } else {
                                    int i4 = i3;
                                    char cCharAt6 = strM12a.charAt(i4);
                                    if (cCharAt6 == '\"' || cCharAt6 == '\'') {
                                        i4++;
                                    }
                                    int i5 = length3;
                                    char cCharAt7 = strM12a.charAt(i5 - 1);
                                    if ((i5 > i4 && cCharAt7 == '\"') || cCharAt7 == '\'') {
                                        i5--;
                                    }
                                    hashtable.put(strM13b, StringUtils.substring(strM12a, i4, i5));
                                }
                            } else if (!z) {
                                break;
                            }
                        }
                    } else {
                        if (strM46a2.charAt(0) == '/') {
                            if (length2 == 1) {
                                z3 = true;
                                break;
                            }
                            z2 = false;
                            i2 = 0 + 1;
                        }
                        if (strM46a2.charAt(length2 - 1) == '/') {
                            z3 = true;
                            length2--;
                        }
                        zEndsWith = strM46a2.endsWith(AppState.getString(1046));
                        z = zEndsWith;
                        if (zEndsWith) {
                        }
                        String strM12a2 = StringUtils.substring(strM46a2, i2, length2);
                        if (strM17c != null) {
                        }
                    }
                    if (!z) {
                        break;
                    }
                }
                if (strM17c.charAt(0) != '?') {
                    String strM17c2 = StringUtils.intern(strM17c.toLowerCase());
                    if (z2) {
                        if (this.rootTagName == null) {
                            this.rootTagName = strM17c2;
                        }
                        this.rootElement = new XmlElement(strM17c2, this.rootElement, hashtable);
                        if (StringUtils.m3a(857301, strM17c2)) {
                            throw new RuntimeException();
                        }
                    }
                    if (z3 || !z2) {
                        if (this.rootElement != null && (c0022av = this.rootElement.parent) != null) {
                            c0022av.addChild(this.rootElement);
                            this.rootElement = c0022av;
                        }
                        if (StringUtils.equals(strM17c2, this.rootTagName)) {
                            throw new RuntimeException();
                        }
                    }
                }
            } catch (Throwable unused) {
                return this.rootElement;
            }
        }
    }
}
