package com.trykote.mobileagent.util;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
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

public final class LruCache {

    private static final int DEFAULT_MAX_SIZE = 27;

    private int size;

    private int maxSize;

    private Hashtable map;

    private Vector lruOrder;

    private long hits;

    private long misses;

    public LruCache() {
        this.map = new Hashtable();
        this.lruOrder = ObjectPool.newVector();
        this.maxSize = DEFAULT_MAX_SIZE;
    }

    public LruCache(int i) {
        this.map = new Hashtable();
        this.lruOrder = ObjectPool.newVector();
        this.maxSize = i;
    }

    private final void moveToFront(Object obj) {
        this.lruOrder.removeElement(obj);
        this.lruOrder.insertElementAt(obj, 0);
    }

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

    public final synchronized void remove(Object obj) {
        if (this.lruOrder.contains(obj)) {
            this.lruOrder.removeElement(obj);
            this.map.remove(obj);
            this.size--;
        }
    }

    public final synchronized Enumeration keys() {
        return this.lruOrder.elements();
    }
}
