package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: ab */
/* loaded from: MobileAgent_3.9.jar:ab.class */
public final class LruCache {

    /* renamed from: a */
    private int f7a;

    /* renamed from: b */
    private int f8b;

    /* renamed from: c */
    private Hashtable f9c;

    /* renamed from: d */
    private Vector f10d;

    /* renamed from: e */
    private long f11e;

    /* renamed from: f */
    private long f12f;

    public LruCache() {
        this.f9c = new Hashtable();
        this.f10d = C0040k.m1213g();
        this.f8b = 27;
    }

    public LruCache(int i) {
        this.f9c = new Hashtable();
        this.f10d = C0040k.m1213g();
        this.f8b = i;
    }

    /* renamed from: c */
    private final void m48c(Object obj) {
        this.f10d.removeElement(obj);
        this.f10d.insertElementAt(obj, 0);
    }

    /* renamed from: a */
    public final synchronized void m49a(Object obj, Object obj2, int i) {
        if (this.f7a >= this.f8b && !this.f10d.isEmpty()) {
            Object objLastElement = this.f10d.lastElement();
            this.f10d.removeElement(objLastElement);
            this.f9c.remove(objLastElement);
            this.f7a--;
        }
        if (this.f9c.containsKey(obj)) {
            this.f9c.put(obj, obj2);
            m48c(obj);
        } else {
            this.f9c.put(obj, obj2);
            this.f10d.insertElementAt(obj, 0);
            this.f7a++;
        }
    }

    /* renamed from: a */
    public final synchronized Object m50a(Object obj) {
        Object obj2 = this.f9c.get(obj);
        if (obj2 != null) {
            this.f11e++;
            m48c(obj);
        } else {
            this.f12f++;
        }
        return obj2;
    }

    /* renamed from: b */
    public final synchronized void m51b(Object obj) {
        if (this.f10d.contains(obj)) {
            this.f10d.removeElement(obj);
            this.f9c.remove(obj);
            this.f7a--;
        }
    }

    /* renamed from: a */
    public final synchronized Enumeration m52a() {
        return this.f10d.elements();
    }
}
