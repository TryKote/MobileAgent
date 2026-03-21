package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/* renamed from: ar */
/* loaded from: MobileAgent_3.9.jar:ar.class */
public abstract class ContactListParser implements ListItem {

    /* renamed from: a */
    private static int f158a;

    /* renamed from: b */
    private static int f159b;

    /* renamed from: a */
    public static final void m487a(ByteBuffer c0043n, Object obj, Object obj2) {
        IOUtils.m778d(new IOUtils(4, new Object[]{obj, m489a(c0043n, 10, true), obj2}));
    }

    /* renamed from: a */
    public static final void m488a(ByteBuffer c0043n, int i) {
        Vector vectorM489a = m489a(c0043n, i, false);
        if (vectorM489a != null && vectorM489a.size() > 0) {
            AppState.pool[1404] = vectorM489a;
        }
        MapRenderer.f200h = true;
    }

    /* renamed from: a */
    private static final Vector m489a(ByteBuffer c0043n, int i, boolean z) {
        boolean z2;
        Hashtable hashtable = (Hashtable) JsonParser.parseUTF8(c0043n, 2);
        Vector vectorM1213g = NetworkUtils.m1213g();
        Vector vectorM614m = AppState.getVector(1404);
        if (vectorM614m != null && !z) {
            int i2 = f159b;
            f159b = i2 + 1;
            for (int i3 = i2 <= 4 ? 0 : f158a; i3 < vectorM614m.size(); i3++) {
                vectorM1213g.addElement(vectorM614m.elementAt(i3));
            }
        }
        int iM586d = AppState.getInt(39);
        f158a = 0;
        Enumeration enumerationKeys = hashtable.keys();
        while (enumerationKeys.hasMoreElements()) {
            String str = (String) enumerationKeys.nextElement();
            int size = vectorM1213g.size();
            while (true) {
                size--;
                if (size < 0) {
                    z2 = false;
                    break;
                }
                if (StringUtils.equals(str, ((Identifiable) vectorM1213g.elementAt(size)).getId()) && i == ((ListItem) vectorM1213g.elementAt(size)).getCommandCount()) {
                    z2 = true;
                    break;
                }
            }
            if (!z2) {
                if (str.startsWith("group_")) {
                    Hashtable hashtable2 = (Hashtable) hashtable.get(str);
                    Hashtable hashtable3 = (Hashtable) hashtable2.get("mass-center");
                    vectorM1213g.addElement(new PhoneContact(str, (int) IOUtils.m807b((String) hashtable3.get("lon")), (int) IOUtils.m808c((String) hashtable3.get("lat")), (String) hashtable2.get("lat1"), (String) hashtable2.get("lon1"), (String) hashtable2.get("lat2"), (String) hashtable2.get("lon2"), Integer.parseInt((String) hashtable2.get("users")), iM586d));
                } else {
                    Hashtable hashtable4 = (Hashtable) hashtable.get(str);
                    UserSearchResult c0045p = new UserSearchResult((int) IOUtils.m807b((String) hashtable4.get("lon")), (int) IOUtils.m808c((String) hashtable4.get("lat")), (String) hashtable4.get("object"), iM586d);
                    c0045p.f390a = (String) hashtable4.get("email");
                    c0045p.f391b = (String) hashtable4.get("nick");
                    String str2 = (String) hashtable4.get("age");
                    if (Utils.nonEmpty(str2)) {
                        c0045p.f392c = Integer.parseInt(str2);
                    }
                    String str3 = (String) hashtable4.get("sex");
                    if (Utils.nonEmpty(str3)) {
                        c0045p.f393d = str3.equals("male") ? 1 : 2;
                    }
                    vectorM1213g.addElement(c0045p);
                }
                f158a++;
            }
        }
        return vectorM1213g;
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
