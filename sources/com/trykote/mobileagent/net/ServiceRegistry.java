package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.protocol.xmpp.XmppContactGroup;
import com.trykote.mobileagent.protocol.xmpp.XmppMailRuProtocol;
import com.trykote.mobileagent.util.*;

import javax.microedition.lcdui.Image;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public final class ServiceRegistry {

    private static Hashtable photoRegistry;

    public static Hashtable photoCache;

    public static String pendingPhotoKey;

    public static Vector hiddenContacts;

    public static final void loadSavedData() {
        XmppContactGroup.sharedContactList = ObjectPool.newVector();
        hiddenContacts = Utils.split(AppState.getString(StateKeys.HIDDEN_CONTACTS_LIST), (char) 0);
        try {
            ByteBuffer c0043nM986d = Base64.decode(AppState.getString(StateKeys.CONTACT_REGISTRY_DATA));
            photoRegistry = new Hashtable();
            try {
                if (c0043nM986d.length > 0) {
                    int iM1355w = c0043nM986d.readIntBE();
                    while (true) {
                        iM1355w--;
                        if (iM1355w < 0) {
                            break;
                        }
                        NetworkUtils c0040k = new NetworkUtils(c0043nM986d);
                        photoRegistry.put(StringUtils.intern(Integer.toString(c0040k.port)), c0040k);
                    }
                }
            } catch (Throwable unused) {
            }
            clearPhotoCache();
            AppState.setInt(StateKeys.FLAG_PHOTO_REGISTRY_READY, 1);
        } catch (Throwable unused2) {
        }
    }

    public static final void parseServiceConfig(int i, XmlElement c0022av, boolean z) {
        photoRegistry = new Hashtable();
        Vector vector = c0022av.children;
        if (vector == null) {
            return;
        }
        for (int i2 = 0; i2 < Utils.vectorSize(vector); i2++) {
            XmlElement c0022av2 = (XmlElement) vector.elementAt(i2);
            String strM555c = c0022av2.getLongKeyAttr(25705);
            NetworkUtils c0040k = new NetworkUtils(Integer.parseInt(strM555c), c0022av2.getIntAttribute(262601), Integer.parseInt(c0022av2.getIntAttribute(201594)), c0022av2.getIntAttribute(529266));
            Vector vector2 = c0022av2.children;
            int i3 = 0;
            while (i3 < Utils.vectorSize(vector2)) {
                int i4 = i3;
                i3++;
                XmlElement c0022av3 = (XmlElement) vector2.elementAt(i4);
                if (StringUtils.matchesKey(263156, c0022av3.tagName)) {
                    c0040k.url = StringUtils.fromBuffer(c0022av3.textContent);
                }
            }
            photoRegistry.put(strM555c, c0040k);
        }
        photoCache = new Hashtable();
        AppState.setInt(StateKeys.FLAG_PHOTO_REGISTRY_READY, 1);
        try {
            AppState.setObject(StateKeys.CONTACT_REGISTRY_DATA, (Object) AppState.emptyStr);
            AppState.setObject(StateKeys.CONTACT_REGISTRY_DATA, (Object) serializeRegistry().toBase64());
        } catch (Throwable unused) {
            AppState.setObject(StateKeys.URL_GEO_CONFIG, (Object) AppState.emptyStr);
        }
    }

    public static final String getPhotoHost(Object obj) {
        NetworkUtils c0040k;
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY) || (c0040k = (NetworkUtils) photoRegistry.get(obj)) == null) {
            return null;
        }
        return c0040k.host;
    }

    public static final Image getProfileImage(String str) {
        NetworkUtils c0040k;
        Image image;
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        synchronized (photoCache) {
            Image image2 = (Image) photoCache.get(str);
            Image image3 = image2;
            if (image2 == null) {
                try {
                    Hashtable hashtable = photoCache;
                    Image imageM1348r = XmppMailRuProtocol.readChunkedRecord(StringUtils.concat("upi", str)).toImage();
                    image3 = imageM1348r;
                    hashtable.put(str, imageM1348r);
                } catch (Throwable unused) {
                    if (pendingPhotoKey == null) {
                        pendingPhotoKey = str;
                        new AsyncTask(14, (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY) || (c0040k = (NetworkUtils) photoRegistry.get(str)) == null) ? null : c0040k.url);
                    }
                }
                image = image3;
            } else {
                image = image3;
            }
        }
        return image;
    }

    public static final Vector getServiceContactIds(int i) {
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        Vector vectorM1213g = ObjectPool.newVector();
        Enumeration enumerationKeys = photoRegistry.keys();
        while (enumerationKeys.hasMoreElements()) {
            Object objNextElement = enumerationKeys.nextElement();
            if (!isContactOffline(objNextElement)) {
                NetworkUtils c0040k = (NetworkUtils) photoRegistry.get(objNextElement);
                if (c0040k != null && c0040k.type == 1) {
                    vectorM1213g.addElement(objNextElement);
                }
            }
        }
        return vectorM1213g;
    }

    public static final Vector getAllContactIds() {
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        Vector vectorM1213g = ObjectPool.newVector();
        Enumeration enumerationKeys = photoRegistry.keys();
        while (enumerationKeys.hasMoreElements()) {
            Object objNextElement = enumerationKeys.nextElement();
            if (!isContactOffline(objNextElement)) {
                vectorM1213g.addElement(objNextElement);
            }
        }
        return vectorM1213g;
    }

    public static final Vector getActiveContactIds() {
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        Vector vectorM1213g = ObjectPool.newVector();
        Enumeration enumerationKeys = photoRegistry.keys();
        while (enumerationKeys.hasMoreElements()) {
            Object objNextElement = enumerationKeys.nextElement();
            if (!(getContactStatus(objNextElement) == 2)) {
                vectorM1213g.addElement(objNextElement);
            }
        }
        return vectorM1213g;
    }

    private static final int getContactStatus(Object obj) {
        if (!AppState.getBool(StateKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return 2;
        }
        try {
            return ((NetworkUtils) photoRegistry.get(obj)).status;
        } catch (Throwable unused) {
            return 2;
        }
    }

    private static final boolean isContactOffline(Object obj) {
        return getContactStatus(obj) == 0;
    }

    private static ByteBuffer serializeRegistry() {
        ByteBuffer c0043n = new ByteBuffer();
        c0043n.writeIntBE(photoRegistry.size());
        Enumeration enumerationKeys = photoRegistry.keys();
        while (enumerationKeys.hasMoreElements()) {
            NetworkUtils c0040k = (NetworkUtils) photoRegistry.get(enumerationKeys.nextElement());
            c0043n.writeIntBE(c0040k.type).writeIntBE(c0040k.port).writeStringUTF16(c0040k.host).writeStringLatin1(c0040k.url).writeIntBE(c0040k.status).writeStringLatin1(c0040k.protocol);
        }
        return c0043n;
    }

    public static final void clearPhotoCache() {
        photoCache = new Hashtable();
    }
}
