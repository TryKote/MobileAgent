package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: ar */
/* loaded from: MobileAgent_3.9.jar:ar.class */
public abstract class ContactListParser implements ListItem {

    /* renamed from: a */
    private static int addedCount;

    /* renamed from: b */
    private static int updateCounter;

    /* renamed from: a */
    public static final void parseContactsAsync(ByteBuffer buffer, Object obj, Object obj2) {
        EventDispatcher.postEvent(new ProtocolEvent(ProtocolEvent.PHONE_SEARCH_RESULT, new Object[]{obj, parseContactsInternal(buffer, 10, true), obj2}));
    }

    /* renamed from: a */
    public static final void parseContactsSync(ByteBuffer buffer, int i) {
        Vector contacts = parseContactsInternal(buffer, i, false);
        if (contacts != null && contacts.size() > 0) {
            AppState.pool[UIKeys.OBJ_HTTP_CALLBACK] = contacts;
        }
        MapRenderer.needsRedraw = true;
    }

    /* renamed from: a */
    private static final Vector parseContactsInternal(ByteBuffer buffer, int i, boolean z) {
        boolean z2;
        Hashtable hashtable = (Hashtable) JsonParser.parseUTF8(buffer, 2);
        Vector result = ObjectPool.newVector();
        Vector existing = AppState.getVector(UIKeys.OBJ_HTTP_CALLBACK);
        if (existing != null && !z) {
            int i2 = updateCounter;
            updateCounter = i2 + 1;
            for (int i3 = i2 <= 4 ? 0 : addedCount; i3 < existing.size(); i3++) {
                result.addElement(existing.elementAt(i3));
            }
        }
        int zoomLevel = AppState.getInt(MapKeys.MAP_ZOOM_LEVEL);
        addedCount = 0;
        Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            String str = (String) keys.nextElement();
            int size = result.size();
            while (true) {
                size--;
                if (size < 0) {
                    z2 = false;
                    break;
                }
                if (StringUtils.equals(str, ((Identifiable) result.elementAt(size)).getId()) && i == ((ListItem) result.elementAt(size)).getCommandCount()) {
                    z2 = true;
                    break;
                }
            }
            if (!z2) {
                if (str.startsWith("group_")) {
                    Hashtable hashtable2 = (Hashtable) hashtable.get(str);
                    Hashtable hashtable3 = (Hashtable) hashtable2.get("mass-center");
                    result.addElement(new PhoneContact(str, (int) MapUtils.longitudeToPixel((String) hashtable3.get("lon")), (int) MapUtils.latitudeToPixel((String) hashtable3.get("lat")), (String) hashtable2.get("lat1"), (String) hashtable2.get("lon1"), (String) hashtable2.get("lat2"), (String) hashtable2.get("lon2"), Integer.parseInt((String) hashtable2.get("users")), zoomLevel));
                } else {
                    Hashtable hashtable4 = (Hashtable) hashtable.get(str);
                    UserSearchResult searchResult = new UserSearchResult((int) MapUtils.longitudeToPixel((String) hashtable4.get("lon")), (int) MapUtils.latitudeToPixel((String) hashtable4.get("lat")), (String) hashtable4.get("object"), zoomLevel);
                    searchResult.userId = (String) hashtable4.get("email");
                    searchResult.nickname = (String) hashtable4.get("nick");
                    String str2 = (String) hashtable4.get("age");
                    if (Utils.nonEmpty(str2)) {
                        searchResult.age = Integer.parseInt(str2);
                    }
                    String str3 = (String) hashtable4.get("sex");
                    if (Utils.nonEmpty(str3)) {
                        searchResult.gender = str3.equals("male") ? 1 : 2;
                    }
                    result.addElement(searchResult);
                }
                addedCount++;
            }
        }
        return result;
    }

    @Override // p000.ListItem
    /* renamed from: b */
    public abstract int executeCommand(int i);

    @Override // p000.ListItem
    /* renamed from: a */
    public abstract int getCommandId(int i);

    @Override // p000.ListItem
    /* renamed from: z */
    public abstract boolean isHighlighted();

    @Override // p000.ListItem
    /* renamed from: y */
    public abstract int getCommandCount();

    @Override // p000.ListItem
    /* renamed from: x */
    public abstract String getText();

    @Override // p000.ListItem
    /* renamed from: w */
    public abstract int getBaseHeight();

    @Override // p000.ListItem
    /* renamed from: v */
    public abstract int getWidth();

    @Override // p000.ListItem
    /* renamed from: u */
    public abstract void deselect();

    @Override // p000.ListItem
    /* renamed from: t */
    public abstract void select();

    @Override // p000.ListItem
    /* renamed from: s */
    public abstract boolean isSelected();

    @Override // p000.ListItem
    /* renamed from: r */
    public abstract int getHeight();
}
