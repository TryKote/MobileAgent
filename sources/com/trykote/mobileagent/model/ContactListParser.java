package com.trykote.mobileagent.model;


import com.trykote.mobileagent.core.MapState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.core.event.EventDispatcher;
import com.trykote.mobileagent.map.MapRenderer;
import com.trykote.mobileagent.map.MapUtils;
import com.trykote.mobileagent.protocol.ProtocolEvent;
import com.trykote.mobileagent.ui.ListItem;
import com.trykote.mobileagent.util.ByteBuffer;
import com.trykote.mobileagent.util.JsonParser;
import com.trykote.mobileagent.util.ObjectPool;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public abstract class ContactListParser implements ListItem {

    // Gender codes from server
    public static final int GENDER_MALE = 1;
    public static final int GENDER_FEMALE = 2;

    // Default page size for contact list parsing
    public static final int DEFAULT_PAGE_SIZE = 10;

    private static int addedCount;

    private static int updateCounter;

    public static final void parseContactsAsync(ByteBuffer buffer, Object obj, Object obj2) {
        EventDispatcher.postEvent(new ProtocolEvent(ProtocolEvent.PHONE_SEARCH_RESULT, new Object[]{obj, parseContactsInternal(buffer, DEFAULT_PAGE_SIZE, true), obj2}));
    }

    public static final void parseContactsSync(ByteBuffer buffer, int i) {
        Vector contacts = parseContactsInternal(buffer, i, false);
        if (contacts != null && contacts.size() > 0) {
            UIState.setHttpCallback(contacts);
        }
        MapRenderer.needsRedraw = true;
    }

    private static final Vector parseContactsInternal(ByteBuffer buffer, int i, boolean z) {
        boolean z2;
        Hashtable hashtable = (Hashtable) JsonParser.parseUTF8(buffer, 2);
        Vector result = ObjectPool.newVector();
        Vector existing = (Vector) UIState.getHttpCallback();
        if (existing != null && !z) {
            int i2 = updateCounter;
            updateCounter = i2 + 1;
            for (int i3 = i2 <= 4 ? 0 : addedCount; i3 < existing.size(); i3++) {
                result.addElement(existing.elementAt(i3));
            }
        }
        int zoomLevel = MapState.getZoomLevel();
        addedCount = 0;
        Enumeration keys = hashtable.keys();
        while (keys.hasMoreElements()) {
            String str = (String) keys.nextElement();
            z2 = false;
            for (int j = result.size() - 1; j >= 0; j--) {
                if (StringUtils.equals(str, ((Identifiable) result.elementAt(j)).getId()) && i == ((ListItem) result.elementAt(j)).getCommandCount()) {
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
                        searchResult.gender = str3.equals("male") ? GENDER_MALE : GENDER_FEMALE;
                    }
                    result.addElement(searchResult);
                }
                addedCount++;
            }
        }
        return result;
    }

    @Override // p000.ListItem
    public abstract int executeCommand(int i);

    @Override // p000.ListItem
    public abstract int getCommandId(int i);

    @Override // p000.ListItem
    public abstract boolean isHighlighted();

    @Override // p000.ListItem
    public abstract int getCommandCount();

    @Override // p000.ListItem
    public abstract String getText();

    @Override // p000.ListItem
    public abstract int getBaseHeight();

    @Override // p000.ListItem
    public abstract int getWidth();

    @Override // p000.ListItem
    public abstract void deselect();

    @Override // p000.ListItem
    public abstract void select();

    @Override // p000.ListItem
    public abstract boolean isSelected();

    @Override // p000.ListItem
    public abstract int getHeight();
}
