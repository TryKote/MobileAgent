package com.trykote.mobileagent.net;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.Storage;
import com.trykote.mobileagent.core.AsyncTask;
import com.trykote.mobileagent.core.AsyncTaskId;
import com.trykote.mobileagent.protocol.xmpp.XmppContactGroup;
import com.trykote.mobileagent.util.*;

import javax.microedition.lcdui.Image;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import com.trykote.mobileagent.core.PackedStringKeys;
import com.trykote.mobileagent.core.ContactKeys;
import com.trykote.mobileagent.core.MapKeys;
import com.trykote.mobileagent.core.UIKeys;

public final class ServiceRegistry {

    private static Hashtable photoRegistry;

    public static Hashtable photoCache;

    public static String pendingPhotoKey;

    public static Vector hiddenContacts;

    public static final void loadSavedData() {
        XmppContactGroup.sharedContactList = ObjectPool.newVector();
        hiddenContacts = Utils.split(Storage.state().getString(ContactKeys.HIDDEN_CONTACTS_LIST), (char) 0);
        try {
            ByteBuffer registryBuf = Base64.decode(Storage.state().getString(ContactKeys.CONTACT_REGISTRY_DATA));
            photoRegistry = new Hashtable();
            try {
                if (registryBuf.length > 0) {
                    for (int count = registryBuf.readIntBE() - 1; count >= 0; count--) {
                        NetworkUtils entry = new NetworkUtils(registryBuf);
                        photoRegistry.put(StringUtils.intern(Integer.toString(entry.port)), entry);
                    }
                }
            } catch (Throwable unused) {
            }
            clearPhotoCache();
            Storage.state().setInt(UIKeys.FLAG_PHOTO_REGISTRY_READY, 1);
        } catch (Throwable unused2) {
        }
    }

    public static final void parseServiceConfig(int configType, XmlElement rootElement, boolean persist) {
        photoRegistry = new Hashtable();
        Vector serviceNodes = rootElement.children;
        if (serviceNodes == null) {
            return;
        }
        for (int idx = 0; idx < Utils.vectorSize(serviceNodes); idx++) {
            XmlElement serviceNode = (XmlElement) serviceNodes.elementAt(idx);
            String serviceId = serviceNode.getLongKeyAttr(25705);
            NetworkUtils entry = new NetworkUtils(Integer.parseInt(serviceId), serviceNode.getIntAttribute(PackedStringKeys.ATTR_NAME), Integer.parseInt(serviceNode.getIntAttribute(PackedStringKeys.ATTR_DIR)), serviceNode.getIntAttribute(PackedStringKeys.TAG_LISTLINK));
            Vector childNodes = serviceNode.children;
            for (int childIdx = 0; childIdx < Utils.vectorSize(childNodes); childIdx++) {
                XmlElement childNode = (XmlElement) childNodes.elementAt(childIdx);
                if (StringUtils.matchesKey(PackedStringKeys.TAG_LINK, childNode.tagName)) {
                    entry.url = StringUtils.fromBuffer(childNode.textContent);
                }
            }
            photoRegistry.put(serviceId, entry);
        }
        photoCache = new Hashtable();
        Storage.state().setInt(UIKeys.FLAG_PHOTO_REGISTRY_READY, 1);
        try {
            Storage.state().setObject(ContactKeys.CONTACT_REGISTRY_DATA, (Object) Storage.emptyStr);
            Storage.state().setObject(ContactKeys.CONTACT_REGISTRY_DATA, (Object) serializeRegistry().toBase64());
        } catch (Throwable unused) {
            Storage.state().setObject(MapKeys.URL_GEO_CONFIG, (Object) Storage.emptyStr);
        }
    }

    public static final String getPhotoHost(Object key) {
        NetworkUtils entry;
        if (!Storage.state().getBool(UIKeys.FLAG_PHOTO_REGISTRY_READY) || (entry = (NetworkUtils) photoRegistry.get(key)) == null) {
            return null;
        }
        return entry.host;
    }

    public static final Image getProfileImage(String key) {
        NetworkUtils entry;
        Image result;
        if (!Storage.state().getBool(UIKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        synchronized (photoCache) {
            Image cached = (Image) photoCache.get(key);
            if (cached == null) {
                try {
                    Image loaded = ChunkedRecordStore.readChunkedRecord(StringUtils.concat("upi", key)).toImage();
                    cached = loaded;
                    photoCache.put(key, loaded);
                } catch (Throwable unused) {
                    if (pendingPhotoKey == null) {
                        pendingPhotoKey = key;
                        new AsyncTask(AsyncTaskId.DOWNLOAD_CACHED_PHOTO, (!Storage.state().getBool(UIKeys.FLAG_PHOTO_REGISTRY_READY) || (entry = (NetworkUtils) photoRegistry.get(key)) == null) ? null : entry.url);
                    }
                }
            }
            result = cached;
        }
        return result;
    }

    public static final Vector getServiceContactIds(int filterType) {
        if (!Storage.state().getBool(UIKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        Vector contactIds = ObjectPool.newVector();
        Enumeration keys = photoRegistry.keys();
        while (keys.hasMoreElements()) {
            Object contactId = keys.nextElement();
            if (!isContactOffline(contactId)) {
                NetworkUtils entry = (NetworkUtils) photoRegistry.get(contactId);
                if (entry != null && entry.type == 1) {
                    contactIds.addElement(contactId);
                }
            }
        }
        return contactIds;
    }

    public static final Vector getAllContactIds() {
        if (!Storage.state().getBool(UIKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        Vector contactIds = ObjectPool.newVector();
        Enumeration keys = photoRegistry.keys();
        while (keys.hasMoreElements()) {
            Object contactId = keys.nextElement();
            if (!isContactOffline(contactId)) {
                contactIds.addElement(contactId);
            }
        }
        return contactIds;
    }

    public static final Vector getActiveContactIds() {
        if (!Storage.state().getBool(UIKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return null;
        }
        Vector contactIds = ObjectPool.newVector();
        Enumeration keys = photoRegistry.keys();
        while (keys.hasMoreElements()) {
            Object contactId = keys.nextElement();
            if (!(getContactStatus(contactId) == 2)) {
                contactIds.addElement(contactId);
            }
        }
        return contactIds;
    }

    private static final int getContactStatus(Object key) {
        if (!Storage.state().getBool(UIKeys.FLAG_PHOTO_REGISTRY_READY)) {
            return 2;
        }
        try {
            return ((NetworkUtils) photoRegistry.get(key)).status;
        } catch (Throwable unused) {
            return 2;
        }
    }

    private static final boolean isContactOffline(Object key) {
        return getContactStatus(key) == 0;
    }

    private static ByteBuffer serializeRegistry() {
        ByteBuffer buf = new ByteBuffer();
        buf.writeIntBE(photoRegistry.size());
        Enumeration keys = photoRegistry.keys();
        while (keys.hasMoreElements()) {
            NetworkUtils entry = (NetworkUtils) photoRegistry.get(keys.nextElement());
            buf.writeIntBE(entry.type).writeIntBE(entry.port).writeStringUTF16(entry.host).writeStringLatin1(entry.url).writeIntBE(entry.status).writeStringLatin1(entry.protocol);
        }
        return buf;
    }

    public static final void clearPhotoCache() {
        photoCache = new Hashtable();
    }
}
