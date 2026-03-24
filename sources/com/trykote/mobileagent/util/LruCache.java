package com.trykote.mobileagent.util;


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

/* renamed from: ab */
/* loaded from: MobileAgent_3.9.jar:ab.class */
public final class LruCache {

    /* renamed from: a */
    private int size;

    /* renamed from: b */
    private int maxSize;

    /* renamed from: c */
    private Hashtable map;

    /* renamed from: d */
    private Vector lruOrder;

    /* renamed from: e */
    private long hits;

    /* renamed from: f */
    private long misses;

    public LruCache() {
        this.map = new Hashtable();
        this.lruOrder = ObjectPool.newVector();
        this.maxSize = 27;
    }

    public LruCache(int i) {
        this.map = new Hashtable();
        this.lruOrder = ObjectPool.newVector();
        this.maxSize = i;
    }

    /* renamed from: c */
    private final void moveToFront(Object obj) {
        this.lruOrder.removeElement(obj);
        this.lruOrder.insertElementAt(obj, 0);
    }

    /* renamed from: a */
    public final synchronized void put(Object obj, Object obj2, int i) {
        if (this.size >= this.maxSize && !this.lruOrder.isEmpty()) {
            Object objLastElement = this.lruOrder.lastElement();
            this.lruOrder.removeElement(objLastElement);
            this.map.remove(objLastElement);
            this.size--;
        }
        if (this.map.containsKey(obj)) {
            this.map.put(obj, obj2);
            moveToFront(obj);
        } else {
            this.map.put(obj, obj2);
            this.lruOrder.insertElementAt(obj, 0);
            this.size++;
        }
    }

    /* renamed from: a */
    public final synchronized Object get(Object obj) {
        Object obj2 = this.map.get(obj);
        if (obj2 != null) {
            this.hits++;
            moveToFront(obj);
        } else {
            this.misses++;
        }
        return obj2;
    }

    /* renamed from: b */
    public final synchronized void remove(Object obj) {
        if (this.lruOrder.contains(obj)) {
            this.lruOrder.removeElement(obj);
            this.map.remove(obj);
            this.size--;
        }
    }

    /* renamed from: a */
    public final synchronized Enumeration keys() {
        return this.lruOrder.elements();
    }
}
