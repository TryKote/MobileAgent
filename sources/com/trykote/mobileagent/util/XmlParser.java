package com.trykote.mobileagent.util;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.key.PackedStringKeys;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.protocol.xmpp.XmppProtocol;

import java.util.Hashtable;

public final class XmlParser {

    private String rootTagName;

    private XmlElement rootElement;

    private Object source;

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

    private final int nextChar() {
        if (!(this.source instanceof String)) {
            if (!(this.source instanceof ByteBuffer)) {
                return XmppProtocol.readUtf8Char((Object[]) this.source);
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
                        endTag = text2.endsWith(ResourceAccessor.str(StringResKeys.STR_SPACE));
                        z = endTag;
                        if (endTag) {
                            length2--;
                        }
                        String token = StringUtils.substring(text2, i2, length2);
                        if (tagName == null) {
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
                        endTag = text2.endsWith(ResourceAccessor.str(StringResKeys.STR_SPACE));
                        z = endTag;
                        if (endTag) {
                            length2--;
                        }
                        String token2 = StringUtils.substring(text2, i2, length2);
                        if (tagName == null) {
                            tagName = StringUtils.intern(token2.toLowerCase());
                        } else {
                            if (hashtable == null) {
                                hashtable = new Hashtable();
                            }
                            int length3 = token2.length();
                            int eqIdx = token2.indexOf(61);
                            if (eqIdx >= 0) {
                                String attrName = StringUtils.prefix(token2, eqIdx);
                                int i3 = eqIdx + 1;
                                if (i3 >= token2.length()) {
                                    hashtable.put(attrName, AppState.emptyStr);
                                } else {
                                    int i4 = i3;
                                    char c6 = token2.charAt(i4);
                                    if (c6 == '\"' || c6 == '\'') {
                                        i4++;
                                    }
                                    int i5 = length3;
                                    char c7 = token2.charAt(i5 - 1);
                                    if ((i5 > i4 && c7 == '\"') || c7 == '\'') {
                                        i5--;
                                    }
                                    hashtable.put(attrName, StringUtils.substring(token2, i4, i5));
                                }
                            } else if (!z) {
                                break;
                            }
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
                        if (StringUtils.matchesKey(PackedStringKeys.XMPP_STREAM_STREAM, tagName2)) {
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
