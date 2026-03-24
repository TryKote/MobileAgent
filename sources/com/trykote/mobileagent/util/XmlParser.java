package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.model.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
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

    public static XmlElement parseFromBuffer(ByteBuffer buf) {
        return new XmlParser(buf.readUTFWithLen()).parse();
    }

    public static XmlElement parseFromString(ByteBuffer buf) {
        return new XmlParser(buf.getStringAndClear()).parse();
    }

    /* renamed from: b */
    private final int nextChar() {
        if (!(this.source instanceof String)) {
            if (!(this.source instanceof ByteBuffer)) {
                return ResourceManager.readUtf8Char((Object[]) this.source);
            }
            ByteBuffer buffer = (ByteBuffer) this.source;
            if (buffer.length == 0) {
                return -1;
            }
            return Utils.win1251ToChar(buffer.readUByte());
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
        StringBuffer sb = ObjectPool.newStringBuffer();
        boolean z2 = false;
        int ch = nextChar();
        int ch2 = ch;
        if (ch == -1) {
            throw new RuntimeException();
        }
        int i2 = 0;
        while (ch2 != -1) {
            if (!z2) {
                int i3 = ch2;
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
            if ((ch2 == 34 || ch2 == 39) && (i2 == 0 || i2 == ch2)) {
                z2 = !z2;
                i2 = ch2;
            } else {
                sb.append((char) ch2);
            }
            ch2 = nextChar();
        }
        if (ch2 != 60 && ch2 != 62) {
            sb.append((char) ch2);
        }
        return ObjectPool.toStringAndRelease(sb);
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
        boolean endTag;
        boolean z;
        XmlElement parentEl;
        String decoded;
        this.rootTagName = null;
        this.rootElement = null;
        while (true) {
            try {
                String text = parseTagOrContent(1);
                if (this.rootElement != null) {
                    XmlElement parentEl2 = this.rootElement;
                    if (text.indexOf(38) < 0) {
                        decoded = text;
                    } else {
                        StringBuffer sb = ObjectPool.newStringBuffer();
                        int length = text.length();
                        int i = 0;
                        while (i < length) {
                            char c = text.charAt(i);
                            if (c != '&') {
                                sb.append(c);
                            } else {
                                char c2 = i + 1 < length ? text.charAt(i + 1) : (char) 0;
                                char c3 = i + 2 < length ? text.charAt(i + 2) : (char) 0;
                                char c4 = i + 3 < length ? text.charAt(i + 3) : (char) 0;
                                if (c3 != 't' || c4 != ';') {
                                    char c5 = i + 4 < length ? text.charAt(i + 4) : (char) 0;
                                    if (c2 == 'a' && c3 == 'm' && c4 == 'p' && c5 == ';') {
                                        sb.append('&');
                                        i += 4;
                                    } else {
                                        if ((i + 5 < length ? text.charAt(i + 5) : (char) 0) == ';') {
                                            if (c4 != 'o') {
                                                if (c2 == '#' && c3 >= '0' && c3 <= '9' && c4 >= 0 && c4 <= '9' && c5 >= '0' && c5 <= '9') {
                                                    sb.append((char) ((c5 - '0') + ((c4 - '0') * 10) + ((c3 - '0') * 100)));
                                                    i += 5;
                                                }
                                            } else if (c2 == 'q' && c3 == 'u' && c5 == 't') {
                                                sb.append('\"');
                                                i += 5;
                                            } else if (c2 == 'a' && c3 == 'p' && c5 == 's') {
                                                sb.append('\'');
                                                i += 5;
                                            }
                                        }
                                    }
                                } else if (c2 == 'l') {
                                    sb.append('<');
                                    i += 3;
                                } else if (c2 == 'g') {
                                    sb.append('>');
                                    i += 3;
                                }
                            }
                            i++;
                        }
                        decoded = ObjectPool.toStringAndRelease(sb);
                    }
                    parentEl2.appendText((Object) decoded);
                }
                boolean z2 = true;
                boolean z3 = false;
                String tagName = null;
                Hashtable hashtable = null;
                while (true) {
                    String text2 = parseTagOrContent(2);
                    int i2 = 0;
                    int length2 = text2.length();
                    if (StringUtils.matchesKey(1046, text2)) {
                        z = true;
                    } else if (length2 <= 0) {
                        endTag = text2.endsWith(AppState.getString(StateKeys.STR_SPACE));
                        z = endTag;
                        if (endTag) {
                            length2--;
                        }
                        String token = StringUtils.substring(text2, i2, length2);
                        if (tagName != null) {
                            tagName = StringUtils.intern(token.toLowerCase());
                        } else {
                            if (hashtable == null) {
                                hashtable = new Hashtable();
                            }
                            int length3 = token.length();
                            int eqIdx = token.indexOf(61);
                            if (eqIdx >= 0) {
                                String attrName = StringUtils.prefix(token, eqIdx);
                                int i3 = eqIdx + 1;
                                if (i3 >= token.length()) {
                                    hashtable.put(attrName, AppState.emptyStr);
                                } else {
                                    int i4 = i3;
                                    char c6 = token.charAt(i4);
                                    if (c6 == '\"' || c6 == '\'') {
                                        i4++;
                                    }
                                    int i5 = length3;
                                    char c7 = token.charAt(i5 - 1);
                                    if ((i5 > i4 && c7 == '\"') || c7 == '\'') {
                                        i5--;
                                    }
                                    hashtable.put(attrName, StringUtils.substring(token, i4, i5));
                                }
                            } else if (!z) {
                                break;
                            }
                        }
                    } else {
                        if (text2.charAt(0) == '/') {
                            if (length2 == 1) {
                                z3 = true;
                                break;
                            }
                            z2 = false;
                            i2 = 0 + 1;
                        }
                        if (text2.charAt(length2 - 1) == '/') {
                            z3 = true;
                            length2--;
                        }
                        endTag = text2.endsWith(AppState.getString(StateKeys.STR_SPACE));
                        z = endTag;
                        if (endTag) {
                        }
                        String token2 = StringUtils.substring(text2, i2, length2);
                        if (tagName != null) {
                        }
                    }
                    if (!z) {
                        break;
                    }
                }
                if (tagName.charAt(0) != '?') {
                    String tagName2 = StringUtils.intern(tagName.toLowerCase());
                    if (z2) {
                        if (this.rootTagName == null) {
                            this.rootTagName = tagName2;
                        }
                        this.rootElement = new XmlElement(tagName2, this.rootElement, hashtable);
                        if (StringUtils.matchesKey(857301, tagName2)) {
                            throw new RuntimeException();
                        }
                    }
                    if (z3 || !z2) {
                        if (this.rootElement != null && (parentEl = this.rootElement.parent) != null) {
                            parentEl.addChild(this.rootElement);
                            this.rootElement = parentEl;
                        }
                        if (StringUtils.equals(tagName2, this.rootTagName)) {
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
