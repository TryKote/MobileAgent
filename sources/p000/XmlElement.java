package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: av */
/* loaded from: MobileAgent_3.9.jar:av.class */
public final class XmlElement {

    /* renamed from: a */
    public String tagName;

    /* renamed from: b */
    public Vector children;

    /* renamed from: c */
    public StringBuffer textContent;

    /* renamed from: d */
    public XmlElement parent;

    /* renamed from: e */
    private Hashtable attributes;

    public XmlElement() {
        this(null, null, null);
    }

    /* renamed from: a */
    public static final XmlElement createFromState(int i) {
        return new XmlElement(AppState.getString(i));
    }

    public XmlElement(int i) {
        this(NetworkUtils.longToHex(i));
    }

    private XmlElement(String str) {
        this.tagName = str;
    }

    public XmlElement(String str, XmlElement c0022av, Hashtable hashtable) {
        this.parent = c0022av;
        this.attributes = hashtable;
        this.tagName = str;
    }

    /* renamed from: a */
    public final XmlElement setIntAttribute(int i, int i2) {
        addTextChild(AppState.getString(i), AppState.getString(i2));
        return this;
    }

    /* renamed from: a */
    public final XmlElement addChild(XmlElement c0022av) {
        if (this.children == null) {
            this.children = NetworkUtils.newVector();
        }
        this.children.addElement(c0022av);
        return this;
    }

    /* renamed from: a */
    public final XmlElement appendText(Object obj) {
        if (this.textContent == null) {
            this.textContent = NetworkUtils.newStringBuffer();
        }
        this.textContent.append(obj);
        return this;
    }

    /* renamed from: b */
    public final String getIntAttribute(int i) {
        return getAttribute(AppState.getString(i));
    }

    /* renamed from: c */
    public final String getLongKeyAttr(int i) {
        return getAttribute(NetworkUtils.longToHex(i));
    }

    /* renamed from: d */
    public final long getAttrAsLong(int i) {
        return Long.parseLong(getLongKeyAttr(i));
    }

    /* renamed from: e */
    public final int getAttrAsInt(int i) {
        return Integer.parseInt(getLongKeyAttr(i));
    }

    /* renamed from: d */
    private String getAttribute(String str) {
        return (String) this.attributes.get(str);
    }

    /* renamed from: a */
    public final XmlElement setAttrValue(int i, String str) {
        return setAttrImpl(AppState.getString(i), str);
    }

    /* renamed from: b */
    public final XmlElement setLongKeyAttr(int i, String str) {
        return setAttrImpl(NetworkUtils.longToHex(i), str);
    }

    /* renamed from: b */
    private XmlElement setAttrImpl(String str, String str2) {
        if (str != null) {
            if (str2 != null) {
                if (this.attributes == null) {
                    this.attributes = new Hashtable();
                }
                this.attributes.put(str, str2);
            } else if (this.attributes != null) {
                this.attributes.remove(str);
            }
        }
        return this;
    }

    /* renamed from: f */
    public final XmlElement findChildByKey(int i) {
        return findChildByName(AppState.getString(i));
    }

    /* renamed from: e */
    private final XmlElement findChildByName(String str) {
        XmlElement c0022av;
        int iM541c = Utils.vectorSize(this.children);
        do {
            iM541c--;
            if (iM541c < 0) {
                return null;
            }
            c0022av = (XmlElement) this.children.elementAt(iM541c);
        } while (!StringUtils.equals(c0022av.tagName, str));
        return c0022av;
    }

    /* renamed from: g */
    public final XmlElement getChildAt(int i) {
        return (XmlElement) this.children.elementAt(i);
    }

    public final String toString() {
        StringBuffer stringBufferAppend = NetworkUtils.newStringBuffer().append('<').append(this.tagName);
        if (this.attributes != null) {
            Enumeration enumerationKeys = this.attributes.keys();
            while (enumerationKeys.hasMoreElements()) {
                StringBuffer stringBufferAppend2 = stringBufferAppend.append(' ');
                Object objNextElement = enumerationKeys.nextElement();
                StringBuffer stringBufferAppend3 = stringBufferAppend2.append(objNextElement).append('=').append('\"');
                StringBuffer stringBufferM565b = escapeXml(this.attributes.get(objNextElement));
                stringBufferAppend3.append((Object) stringBufferM565b).append('\"');
                NetworkUtils.bufToStringCached(stringBufferM565b);
            }
        }
        StringBuffer stringBufferAppend4 = NetworkUtils.newStringBuffer().append(NetworkUtils.bufToStringCached(isSelfClosing() ? stringBufferAppend.append('/').append('>') : stringBufferAppend.append('>')));
        if (this.textContent != null) {
            stringBufferAppend4.append((Object) escapeXml(this.textContent));
        }
        for (int i = 0; i < Utils.vectorSize(this.children); i++) {
            stringBufferAppend4.append(this.children.elementAt(i));
        }
        return NetworkUtils.bufToStringCached(isSelfClosing() ? stringBufferAppend4 : stringBufferAppend4.append(NetworkUtils.bufToStringCached(NetworkUtils.newStringBuffer().append('<').append('/').append(this.tagName).append('>'))));
    }

    /* renamed from: b */
    private final StringBuffer escapeXml(Object obj) {
        if (obj == null) {
            return null;
        }
        String string = obj.toString();
        StringBuffer stringBufferM1217h = NetworkUtils.newStringBuffer();
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = string.charAt(i);
            if (cCharAt == '&') {
                stringBufferM1217h.append(NetworkUtils.longToHex(255289286950L));
            } else if (cCharAt == '\"') {
                stringBufferM1217h.append(NetworkUtils.longToHex(65371272212774L));
            } else if (cCharAt == '<') {
                stringBufferM1217h.append(NetworkUtils.longToHex(997485606));
            } else if (cCharAt == '>') {
                stringBufferM1217h.append(NetworkUtils.longToHex(997484326));
            } else {
                stringBufferM1217h.append(cCharAt);
            }
        }
        return stringBufferM1217h;
    }

    /* renamed from: c */
    private final boolean isSelfClosing() {
        return this.textContent == null && this.children == null;
    }

    /* renamed from: a */
    public final XmlElement findChildByText(String str) {
        XmlElement c0022av;
        int iM541c = Utils.vectorSize(this.children);
        do {
            iM541c--;
            if (iM541c < 0) {
                return null;
            }
            c0022av = (XmlElement) this.children.elementAt(iM541c);
        } while (!StringUtils.equals(str, StringUtils.fromBuffer(c0022av.textContent)));
        return c0022av;
    }

    /* renamed from: b */
    public final XmlElement findByName(String str) {
        return findChildByName(str);
    }

    /* renamed from: h */
    public final XmlElement addIdAttr(int i) {
        return setAttrValue(333027, AppState.getString(i));
    }

    /* renamed from: i */
    public final XmlElement addNameAttr(int i) {
        return setAttrValue(262589, AppState.getString(i));
    }

    /* renamed from: a */
    public final XmlElement addTextChild(String str, String str2) {
        XmlElement c0022av = new XmlElement(str);
        if (str2 != null) {
            c0022av.appendText((Object) str2);
        }
        addChild(c0022av);
        return c0022av;
    }

    /* renamed from: b */
    public final XmlElement addChildWithId(int i, int i2) {
        return addTextChild(AppState.getString(i), (String) null).addIdAttr(i2);
    }

    /* renamed from: c */
    public final XmlElement addSimpleChild(int i, int i2) {
        return addChild(new XmlElement(AppState.getString(i)).addIdAttr(i2));
    }

    /* renamed from: a */
    public final String getNameAttr() {
        return getAttribute(AppState.getString(262589));
    }

    /* renamed from: c */
    public final String getChildText(String str) {
        try {
            return StringUtils.fromBuffer(findChildByName(str).textContent);
        } catch (Throwable unused) {
            return AppState.emptyStr;
        }
    }

    /* renamed from: d */
    public final XmlElement findByAttrs(int i, int i2) {
        int iM541c = Utils.vectorSize(this.children);
        while (true) {
            iM541c--;
            if (iM541c < 0) {
                return null;
            }
            XmlElement c0022avM564g = getChildAt(iM541c);
            if (StringUtils.matchesKey(i, c0022avM564g.tagName) && StringUtils.matchesKey(i2, c0022avM564g.getAttribute(AppState.getString(333027)))) {
                return c0022avM564g;
            }
        }
    }

    /* renamed from: b */
    public final XmlElement cloneElement() {
        return addNameAttr(398982).setAttrValue(131590, getAttribute(AppState.getString(262852))).setAttrValue(262852, (String) null);
    }
}
