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
        this(ObjectPool.unpackChars(i));
    }

    private XmlElement(String str) {
        this.tagName = str;
    }

    public XmlElement(String str, XmlElement element, Hashtable hashtable) {
        this.parent = element;
        this.attributes = hashtable;
        this.tagName = str;
    }

    /* renamed from: a */
    public final XmlElement setIntAttribute(int i, int i2) {
        addTextChild(AppState.getString(i), AppState.getString(i2));
        return this;
    }

    /* renamed from: a */
    public final XmlElement addChild(XmlElement element) {
        if (this.children == null) {
            this.children = ObjectPool.newVector();
        }
        this.children.addElement(element);
        return this;
    }

    /* renamed from: a */
    public final XmlElement appendText(Object obj) {
        if (this.textContent == null) {
            this.textContent = ObjectPool.newStringBuffer();
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
        return getAttribute(ObjectPool.unpackChars(i));
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
        return setAttrImpl(ObjectPool.unpackChars(i), str);
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
        XmlElement element;
        int idx = Utils.vectorSize(this.children);
        do {
            idx--;
            if (idx < 0) {
                return null;
            }
            element = (XmlElement) this.children.elementAt(idx);
        } while (!StringUtils.equals(element.tagName, str));
        return element;
    }

    /* renamed from: g */
    public final XmlElement getChildAt(int i) {
        return (XmlElement) this.children.elementAt(i);
    }

    public final String toString() {
        StringBuffer sb = ObjectPool.newStringBuffer().append('<').append(this.tagName);
        if (this.attributes != null) {
            Enumeration keys = this.attributes.keys();
            while (keys.hasMoreElements()) {
                StringBuffer spaceBuf = sb.append(' ');
                Object key = keys.nextElement();
                StringBuffer attrBuf = spaceBuf.append(key).append('=').append('\"');
                StringBuffer escaped = escapeXml(this.attributes.get(key));
                attrBuf.append((Object) escaped).append('\"');
                ObjectPool.toStringAndRelease(escaped);
            }
        }
        StringBuffer result = ObjectPool.newStringBuffer().append(ObjectPool.toStringAndRelease(isSelfClosing() ? sb.append('/').append('>') : sb.append('>')));
        if (this.textContent != null) {
            result.append((Object) escapeXml(this.textContent));
        }
        for (int i = 0; i < Utils.vectorSize(this.children); i++) {
            result.append(this.children.elementAt(i));
        }
        return ObjectPool.toStringAndRelease(isSelfClosing() ? result : result.append(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append('<').append('/').append(this.tagName).append('>'))));
    }

    /* renamed from: b */
    private final StringBuffer escapeXml(Object obj) {
        if (obj == null) {
            return null;
        }
        String string = obj.toString();
        StringBuffer out = ObjectPool.newStringBuffer();
        int length = string.length();
        for (int i = 0; i < length; i++) {
            char ch = string.charAt(i);
            if (ch == '&') {
                out.append(ObjectPool.unpackChars(255289286950L));
            } else if (ch == '\"') {
                out.append(ObjectPool.unpackChars(65371272212774L));
            } else if (ch == '<') {
                out.append(ObjectPool.unpackChars(997485606));
            } else if (ch == '>') {
                out.append(ObjectPool.unpackChars(997484326));
            } else {
                out.append(ch);
            }
        }
        return out;
    }

    /* renamed from: c */
    private final boolean isSelfClosing() {
        return this.textContent == null && this.children == null;
    }

    /* renamed from: a */
    public final XmlElement findChildByText(String str) {
        XmlElement element;
        int idx = Utils.vectorSize(this.children);
        do {
            idx--;
            if (idx < 0) {
                return null;
            }
            element = (XmlElement) this.children.elementAt(idx);
        } while (!StringUtils.equals(str, StringUtils.fromBuffer(element.textContent)));
        return element;
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
        XmlElement element = new XmlElement(str);
        if (str2 != null) {
            element.appendText((Object) str2);
        }
        addChild(element);
        return element;
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
        return getAttribute(AppState.getString(StateKeys.STR_RES_EQUALS));
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
        int idx = Utils.vectorSize(this.children);
        while (true) {
            idx--;
            if (idx < 0) {
                return null;
            }
            XmlElement child = getChildAt(idx);
            if (StringUtils.matchesKey(i, child.tagName) && StringUtils.matchesKey(i2, child.getAttribute(AppState.getString(StateKeys.STR_RES_URL_PARAM_3)))) {
                return child;
            }
        }
    }

    /* renamed from: b */
    public final XmlElement cloneElement() {
        return addNameAttr(398982).setAttrValue(131590, getAttribute(AppState.getString(StateKeys.STR_RES_AMPERSAND))).setAttrValue(262852, (String) null);
    }
}
