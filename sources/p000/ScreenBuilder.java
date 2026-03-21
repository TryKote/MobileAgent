package p000;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Image;

/* renamed from: au */
/* loaded from: MobileAgent_3.9.jar:au.class */
public final class ScreenBuilder {
    /* renamed from: a */
    public static final void m546a(int i) {
        boolean z;
        String str;
        String[] strArrM32g;
        Object[] objArrM1147a;
        int i2;
        AppController.m356y();
        AppController.f153g = true;
        while (true) {
            if (!ScreenManager.m72a(i)) {
                Vector vectorM614m = AppState.m614m(1272);
                int size = vectorM614m.size();
                do {
                    size--;
                    if (size < 0) {
                        z = false;
                        break;
                    } else {
                        i2 = ((Screen) vectorM614m.elementAt(size)).f97d;
                        if (i2 == 8) {
                            break;
                        }
                    }
                } while (i2 != 7);
                z = true;
                if (!z) {
                    break;
                }
            }
            m549c();
        }
        switch (i) {
            case 0:
                return;
            case 1:
                int size2 = AppState.m614m(1241).size();
                AppState.m599a(1463, size2 > 0);
                AppState.m599a(1462, size2 > 1);
                AppState.m599a(1464, AppState.m587e(1463));
                AppState.m599a(1465, size2 > 0);
                ScreenManager.m71b(ScreenManager.m75b(2361));
                return;
            case 2:
                AppController.m390G();
                return;
            case 3:
                Screen c0013amM76c = ScreenManager.m76c(3);
                Account abstractC0037hM616i = AppState.m616i();
                switch (abstractC0037hM616i.mo80a()) {
                    case 0:
                        c0013amM76c.m249a(156, 642, 0).m249a(159, 643, 1).m249a(157, 644, 2).m249a(160, 645, 3).m249a(158, 646, 4).m249a(155, 647, 5).m252b(-1, 718, 6);
                        break;
                    case 1:
                        MmpProtocol c0033d = (MmpProtocol) abstractC0037hM616i;
                        int iM915f = c0033d.m915f();
                        c0013amM76c.m249a(iM915f, 642, 0).m249a(iM915f | 16384000, 643, 1).m249a(iM915f | 16449536, 645, 3).m249a(iM915f | 16318464, 644, 4).m249a(iM915f | 16580608, 648, 5).m249a(iM915f | 16646144, 654, 6).m249a(iM915f | 17039360, 649, 7).m249a(iM915f | 16973824, 650, 8).m249a(iM915f | 16908288, 651, 9).m249a(iM915f | 16842752, 652, 10).m249a(iM915f | 17104896, 653, 11).m249a(iM915f | 16515072, 646, 2).m249a(255, 647, 12).m252b(c0033d.mo922n(), 655, 14).m252b(-1, 718, 13);
                        break;
                    default:
                        if (((XmppProtocol) abstractC0037hM616i).mo83f()) {
                            c0013amM76c.m249a(387, 642, 1).m249a(385, 647, 0);
                            break;
                        } else {
                            c0013amM76c.m249a(383, 642, 1).m249a(16384383, 643, 4).m249a(16318847, 644, 2).m249a(16449919, 645, 5).m249a(16580991, 648, 6).m249a(16515455, 646, 3).m249a(381, 647, 0);
                            break;
                        }
                }
                ScreenManager.m71b(c0013amM76c.m259a(AppState.m584b(1048), AppState.m584b(1050), 199, 12, 199));
                return;
            case 4:
                ContactListManager.m152a();
                return;
            case 5:
                AppState.m599a(1472, AppController.m439R().size() > 1);
                AppState.m599a(1473, AppController.m443V().size() > 1);
                ScreenManager.m71b(ScreenManager.m75b(2733));
                return;
            case 6:
                ConnectionThread.m1157f();
                return;
            case 7:
                ScreenManager.m71b(ScreenManager.m75b(2641));
                return;
            case 8:
                ScreenManager.m71b(ScreenManager.m75b(3959));
                return;
            case 9:
                AppState.m594c(1505, 1);
                AppState.m601a(1284, (Object) StringUtils.m17c(Long.toString(Runtime.getRuntime().totalMemory())));
                AppState.m601a(1285, (Object) AppController.m298f());
                AppState.m588a(1288, NetworkUtils.m1217h().append(AppState.m584b(1375)).append(AppState.m584b(511)));
                AppState.m601a(1287, (Object) new ByteBuffer().m1386c(7234309766870429269L).m1321f(44).m1314d(AppState.m603i(519)).m1317c());
                ScreenManager.m71b(ScreenManager.m75b(2448));
                return;
            case 10:
                NetworkUtils.m1198a(10, 710);
                return;
            case 11:
                NetworkUtils.m1197a(11, NetworkUtils.m1217h().append(AppState.m584b(768)).append(AppState.m611g().f376u).append(NetworkUtils.m1221a(16167)));
                return;
            case 12:
                return;
            case 13:
                NetworkUtils.m1200b(13, 505);
                return;
            case 14:
                StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
                int iM586d = AppState.m586d(113);
                AppState.m588a(1286, stringBufferM1217h.append(iM586d / 100).append('.').append(Utils.m501b(iM586d % 100)));
                ScreenManager.m71b(ScreenManager.m75b(3183));
                return;
            case 15:
                Screen c0013amM1193a = NetworkUtils.m1193a(ScreenManager.m75b(2719), AppState.m614m(1241));
                Account abstractC0037h = TabBar.f54k;
                if (abstractC0037h != null) {
                    c0013amM1193a.m257b(abstractC0037h.m1051A());
                }
                ScreenManager.m71b(c0013amM1193a);
                return;
            case 16:
                AppState.m594c(1475, 0);
                ScreenManager.m71b(ScreenManager.m75b(4467));
                return;
            case 17:
                Screen c0013amM76c2 = ScreenManager.m76c(17);
                if (AppState.m616i() instanceof MmpProtocol) {
                    for (int i3 = 0; i3 <= 36; i3++) {
                        c0013amM76c2.m249a(i3 + 268, i3 + 118, i3);
                    }
                } else {
                    for (int i4 = 0; i4 <= 49; i4++) {
                        if (i4 != 21 && i4 != 27) {
                            c0013amM76c2.m249a(i4 + 161, i4 + 155, i4 + 4);
                        }
                    }
                }
                ScreenManager.m71b(c0013amM76c2.m259a(AppState.m584b(1048), AppState.m584b(1050), 199, 12, 199));
                return;
            case 18:
                return;
            case 19:
                AppController.m378F();
                Contact abstractC0041lM611g = AppState.m611g();
                AppState.m601a(1302, (Object) abstractC0041lM611g.f376u);
                Vector vectorM516c = Utils.m516c(abstractC0041lM611g.mo995g(), ',');
                for (int i5 = 0; i5 < 3; i5++) {
                    if (i5 < vectorM516c.size()) {
                        AppState.f177b[i5 + 1303] = vectorM516c.elementAt(i5);
                    }
                }
                NetworkUtils.m1212a(vectorM516c);
                AppState.m594c(3510, (!(abstractC0041lM611g instanceof MrimContact) || abstractC0041lM611g.mo996n()) ? 1 : 4);
                ScreenManager.m71b(ScreenManager.m75b(3501));
                return;
            case 20:
                boolean zM587e = AppState.m587e(41);
                boolean zM587e2 = AppState.m587e(277);
                AppState.m599a(1422, !zM587e && zM587e2);
                AppState.m599a(1423, zM587e && zM587e2);
                ScreenManager.m71b(ScreenManager.m75b(1632));
                return;
            case 21:
                int iMo80a = AppState.m616i().mo80a();
                if (iMo80a == 0) {
                    StringUtils.m29e();
                    return;
                }
                if (iMo80a == 1) {
                    AppState.m594c(1491, -1);
                    ScreenManager.m71b(ScreenManager.m75b(3569));
                    return;
                }
                StringUtils.m30f();
                if (IOUtils.m757a(AppState.m616i()) == 0) {
                    IOUtils.m778d((Object) AppState.m584b(743));
                    return;
                } else {
                    ScreenManager.m71b(ScreenManager.m75b(3939));
                    return;
                }
            case 22:
                ScreenManager.m71b(ScreenManager.m75b(3535));
                return;
            case 23:
                return;
            case 24:
                return;
            case 25:
                Screen c0013amM75b = ScreenManager.m75b(2581);
                Vector vectorM614m2 = AppState.m614m(1241);
                int size3 = vectorM614m2.size();
                for (int i6 = 0; i6 < size3; i6++) {
                    c0013amM75b.m225a(((Account) vectorM614m2.elementAt(i6)).m1058E());
                }
                ScreenManager.m71b(c0013amM75b.m252b(-1, 531, 16).m249a(-1, 532, 1).m249a(-1, 533, 3).m249a(-1, 534, 2));
                return;
            case 26:
                AppState.m594c(4305, AppState.m586d(72));
                ScreenManager.m71b(ScreenManager.m75b(2917));
                return;
            case 27:
                ScreenManager.m71b(ScreenManager.m75b(3214));
                return;
            case 28:
                ScreenManager.m71b(ScreenManager.m75b(2978));
                return;
            case 29:
                AppState.m594c(4305, AppState.m586d(243));
                ScreenManager.m71b(ScreenManager.m75b(3102));
                return;
            case 30:
                AppState.m594c(3707, 1);
                Object obj = AppState.f177b[1365];
                if (obj instanceof ContactGroup) {
                    AppState.m594c(1494, 1);
                    ScreenManager.m71b(ScreenManager.m75b(3686));
                    return;
                }
                Contact abstractC0041l = (Contact) obj;
                if (abstractC0041l.mo996n()) {
                    AppState.m594c(3784, 30);
                    AppState.m594c(3785, 4);
                    ScreenManager.m71b(ScreenManager.m75b(3783));
                    return;
                }
                IOUtils.m822a(abstractC0041l);
                AppState.m594c(1494, 0);
                boolean z2 = abstractC0041l instanceof MrimContact;
                boolean z3 = z2;
                AppState.m599a(1495, z2);
                boolean z4 = z3 && abstractC0041l.mo990d();
                AppState.m599a(1496, z4);
                AppState.m599a(1497, z3 && !z4);
                AppState.m599a(1498, abstractC0041l.mo143m());
                AppState.m599a(1499, abstractC0041l.mo144l() && !abstractC0041l.mo143m());
                AppState.m599a(1501, z3 && !z4 && ((MrimContact) abstractC0041l).m1000q());
                AppState.m594c(3706, 4);
                AppState.m594c(3705, 30);
                ScreenManager.m71b(ScreenManager.m75b(3704));
                return;
            case 31:
            default:
                return;
            case 32:
                return;
            case 33:
                ScreenManager.m71b(ScreenManager.m75b(2817));
                return;
            case 34:
                ResourceManager.m935c();
                return;
            case 35:
                ScreenManager.m71b(ScreenManager.m75b(5157));
                return;
            case 36:
                ResourceManager.m954k();
                return;
            case 37:
                AppState.m591f(1271);
                MrimAccount c0028ba = (MrimAccount) AppState.m616i();
                if (c0028ba.m742V() != 0 && !c0028ba.f229e) {
                    AppController.m402M();
                    return;
                }
                NetworkUtils.m1200b(37, 833);
                Vector vectorM1213g = NetworkUtils.m1213g();
                JsonParser.m481d(vectorM1213g, 0);
                vectorM1213g.addElement(AppState.f181d);
                JsonParser.m481d(vectorM1213g, 1);
                IOUtils.m813b(ConnectionThread.m1147a(NetworkUtils.m1217h().append(AppState.m584b(1050207)).append('?').append(AppState.m584b(722608)).append(AppState.m584b(2098635)).append(AppState.m584b(1381)).append(AppState.m584b(395134)).append(Conversation.m1119a((Object) JsonParser.m484a(vectorM1213g)))));
                return;
            case 38:
                AppController.m402M();
                return;
            case 39:
                Vector vectorM614m3 = AppState.m614m(1283);
                Screen c0013amM75b2 = ScreenManager.m75b(2581);
                c0013amM75b2.f94a = 39;
                c0013amM75b2.f125x = true;
                int size4 = vectorM614m3.size();
                boolean zM587e3 = AppState.m587e(1467);
                for (int i7 = 0; i7 < size4; i7++) {
                    Object objElementAt = vectorM614m3.elementAt(i7);
                    if (!(objElementAt instanceof Account)) {
                        c0013amM75b2.m252b(11, 548, 0);
                    } else if (zM587e3) {
                        c0013amM75b2.m225a(((Account) objElementAt).m1058E());
                    } else {
                        c0013amM75b2.m225a(((Account) objElementAt).m1057D());
                    }
                }
                Account abstractC0037h2 = TabBar.f54k;
                if (abstractC0037h2 != null) {
                    c0013amM75b2.m257b(abstractC0037h2.m1051A());
                }
                ScreenManager.m71b(c0013amM75b2);
                NetworkUtils.m1212a(vectorM614m3);
                return;
            case 40:
                AppController.m300h();
                return;
            case 41:
                AppState.m591f(1271);
                ChatRoom c0052wM745h = ((MrimAccount) AppState.m616i()).m745h(AppState.m586d(1513));
                if (!c0052wM745h.f419k) {
                    IOUtils.m760c();
                    return;
                }
                NetworkUtils.m1200b(41, 836);
                MrimAccount c0028ba2 = (MrimAccount) AppState.m616i();
                Vector vectorM1213g2 = NetworkUtils.m1213g();
                if (c0052wM745h == c0028ba2.m746W()) {
                    vectorM1213g2.addElement(StringUtils.m17c(Integer.toString(0)));
                    vectorM1213g2.addElement(c0052wM745h.f418j);
                    objArrM1147a = ConnectionThread.m1148a(AppState.m584b(1050207), NetworkUtils.m1216b(722608).append(AppState.m584b(2950868)).append(AppState.m584b(1381)).append(AppState.m584b(395134)).append(Conversation.m1119a((Object) JsonParser.m484a(vectorM1213g2))));
                } else {
                    vectorM1213g2.addElement(StringUtils.m17c(Integer.toString(c0052wM745h.f409a)));
                    int iM586d2 = AppState.m586d(97);
                    vectorM1213g2.addElement(StringUtils.m17c(Integer.toString(Utils.m502a(iM586d2, c0052wM745h.f414f.size() + (c0052wM745h.f420l ? iM586d2 : 0)))));
                    vectorM1213g2.addElement(StringUtils.m17c(Integer.toString(1)));
                    vectorM1213g2.addElement(AppState.f181d);
                    Vector vectorM1213g3 = NetworkUtils.m1213g();
                    Enumeration enumerationElements = c0052wM745h.f414f.elements();
                    while (enumerationElements.hasMoreElements()) {
                        Hashtable hashtable = c0052wM745h.f416h;
                        Object objNextElement = enumerationElements.nextElement();
                        if (hashtable.containsKey(objNextElement)) {
                            vectorM1213g3.addElement(objNextElement);
                        }
                    }
                    vectorM1213g2.addElement(vectorM1213g3);
                    objArrM1147a = ConnectionThread.m1147a(NetworkUtils.m1216b(1050207).append('?').append(AppState.m584b(722608)).append(AppState.m584b(1640218)).append(AppState.m584b(1381)).append(AppState.m584b(395134)).append(Conversation.m1119a((Object) JsonParser.m484a(vectorM1213g2))));
                }
                IOUtils.m813b(objArrM1147a);
                return;
            case 42:
                NetworkUtils.m1200b(42, 862);
                Vector vectorM1213g4 = NetworkUtils.m1213g();
                JsonParser.m481d(vectorM1213g4, AppState.m586d(1527));
                vectorM1213g4.addElement(AppState.m614m(1356));
                IOUtils.m813b(ConnectionThread.m1148a(AppState.m584b(1050207), NetworkUtils.m1217h().append(AppState.m584b(722608)).append(AppState.m584b(1640193)).append(AppState.m584b(1381)).append(AppState.m584b(395134)).append(Conversation.m1119a((Object) JsonParser.m484a(vectorM1213g4)))));
                return;
            case 43:
                IOUtils.m760c();
                return;
            case 44:
                AppController.m392I();
                Account abstractC0037hM616i2 = AppState.m616i();
                if (AppState.m616i().mo80a() == 0) {
                    strArrM32g = StringUtils.m32g();
                } else {
                    strArrM32g = new String[8];
                    int iM586d3 = AppState.m586d(1491);
                    strArrM32g[0] = iM586d3 > 0 ? StringUtils.m17c(Integer.toString(iM586d3)) : AppState.f181d;
                    strArrM32g[1] = AppState.m584b(AppState.m587e(1492) ? 1046 : 1038);
                    strArrM32g[2] = Utils.m522f(AppState.m584b(1307));
                    strArrM32g[3] = Utils.m522f(AppState.m584b(1308));
                    strArrM32g[4] = Utils.m522f(AppState.m584b(1309));
                    strArrM32g[5] = Utils.m522f(AppState.m584b(1310));
                    strArrM32g[6] = Utils.m522f(AppState.m584b(1311));
                    strArrM32g[7] = Utils.m522f(AppState.m584b(1312));
                }
                NetworkUtils.m1199a(44, 729, abstractC0037hM616i2.mo115b(strArrM32g));
                return;
            case 45:
                return;
            case 46:
                return;
            case 47:
                ScreenManager.m71b(ScreenManager.m75b(2697));
                return;
            case 48:
                AppState.m591f(1271);
                String strM584b = AppState.m584b(1346);
                Message c0026az = (Message) ((MrimAccount) AppState.m616i()).m745h(AppState.m586d(1513)).f416h.get(strM584b);
                Message c0026az2 = c0026az.f223h != null ? c0026az : null;
                NetworkUtils.m1200b(48, 837);
                if (c0026az2 == null) {
                    Vector vectorM1213g5 = NetworkUtils.m1213g();
                    vectorM1213g5.addElement(strM584b);
                    vectorM1213g5.addElement(AppState.f181d);
                    vectorM1213g5.addElement(NetworkUtils.m1221a(6775156));
                    IOUtils.m813b(ConnectionThread.m1147a(NetworkUtils.m1217h().append(AppState.m584b(1377926)).append('?').append(AppState.m584b(722608)).append(AppState.m584b(1836851)).append(AppState.m584b(1381)).append(AppState.m584b(395134)).append(Conversation.m1119a((Object) JsonParser.m484a(vectorM1213g5)))));
                    return;
                }
                return;
            case 49:
                ScreenManager.m71b(ScreenManager.m75b(4302));
                return;
            case 50:
                ScreenManager.m71b(ScreenManager.m75b(3170));
                return;
            case 51:
                String strM584b2 = AppState.m584b(1346);
                int iM586d4 = AppState.m586d(1513);
                MrimAccount c0028ba3 = (MrimAccount) AppState.m616i();
                boolean z5 = strM584b2 != null;
                boolean z6 = z5;
                AppState.m599a(1521, z5);
                ChatRoom c0052wM745h2 = c0028ba3.m745h(iM586d4);
                boolean zM1414a = c0052wM745h2.m1414a(strM584b2);
                AppState.m599a(1518, z6 && zM1414a);
                AppState.m599a(1519, z6 && !zM1414a);
                Message c0026azM1415b = c0052wM745h2.m1415b(strM584b2);
                AppState.m599a(1522, z6 && !c0026azM1415b.m666a());
                AppState.m599a(1523, z6 && c0026azM1415b.m666a());
                int size5 = c0052wM745h2.f415g.size();
                AppState.m599a(1520, size5 != 0);
                AppState.m599a(1517, c0052wM745h2 != c0028ba3.m746W());
                AppState.m588a(1347, NetworkUtils.m1217h().append(AppState.m584b(849)).append(size5).append(')'));
                ScreenManager.m71b(ScreenManager.m75b(4589));
                return;
            case 52:
                String strM584b3 = AppState.m584b(1346);
                ChatRoom c0052wM745h3 = ((MrimAccount) AppState.m616i()).m745h(AppState.m586d(1513));
                int iM1418a = c0052wM745h3.m1418a();
                Message c0026azM1415b2 = c0052wM745h3.m1415b(strM584b3);
                if (iM1418a == 2) {
                    String[] strArrM869c = XmppMailRuProtocol.m869c(c0026azM1415b2.f219d);
                    str = strArrM869c != null ? strArrM869c[1] : AppState.f181d;
                } else {
                    String[] strArrM869c2 = XmppMailRuProtocol.m869c(c0026azM1415b2.f218c);
                    str = strArrM869c2 != null ? strArrM869c2[1] : AppState.f181d;
                }
                AppState.m601a(1284, (Object) str);
                AppState.m601a(1285, (Object) Utils.m539n(c0026azM1415b2.m673d()));
                AppState.m601a(1286, (Object) Utils.m539n(c0026azM1415b2.f223h));
                Screen c0013amM75b3 = ScreenManager.m75b(4537);
                Object[] objArr = c0026azM1415b2.f224i;
                if (objArr != null) {
                    for (Object obj2 : objArr) {
                        c0013amM75b3.m225a(MenuItem.m889d().m896a(221).m901a(((String[]) obj2)[1], 1, 0));
                    }
                }
                ScreenManager.m71b(c0013amM75b3);
                AppController.m299g();
                return;
            case 53:
                ScreenManager.m71b(ScreenManager.m75b(4551));
                return;
            case 54:
                ScreenManager.m71b(ScreenManager.m75b(4806));
                return;
            case 55:
                NetworkUtils.m1200b(55, 761);
                return;
            case 56:
                ScreenManager.m71b(ScreenManager.m75b(3052));
                return;
            case 57:
                NetworkUtils.m1200b(57, 730);
                AppState.m597a(219, System.currentTimeMillis());
                Object[] objArr2 = new Object[1];
                AppState.f177b[1271] = objArr2;
                new AsyncTask(2, objArr2);
                NetworkUtils.m1174a();
                return;
            case 58:
                ScreenManager.m71b(ScreenManager.m75b(4729));
                return;
            case 59:
                ResourceManager.m977r();
                return;
            case 60:
                ScreenManager.m71b(ScreenManager.m75b(4711));
                return;
            case 61:
                NetworkUtils.m1198a(61, 857);
                return;
            case 62:
                ScreenManager.m71b(ScreenManager.m75b(4667));
                return;
            case 63:
                XmppContactGroup.m1025a(AppState.m611g().f376u, AppState.m584b(1279), 1000, StringUtils.f2c ? 2097152 : 0, AppState.m584b(424), 1059, 1055, new AsyncTask());
                AppState.m594c(1457, 0);
                AppState.m594c(1458, 0);
                AppState.m594c(1459, 0);
                ScreenManager.m71b(new Screen());
                return;
            case 64:
                Contact abstractC0041lM611g2 = AppState.m611g();
                AppState.m594c(4113, abstractC0041lM611g2.mo140i() ? 25 : 24);
                AppState.m594c(4118, abstractC0041lM611g2.mo141j() ? 25 : 24);
                ScreenManager.m71b(ScreenManager.m75b(4100));
                return;
            case 65:
                Vector vectorM516c2 = Utils.m516c(AppState.m612h().f300g, ',');
                int size6 = vectorM516c2.size();
                if (size6 <= 0) {
                    AppController.m340m(713);
                    return;
                }
                StringBuffer stringBufferM1217h2 = NetworkUtils.m1217h();
                for (int i8 = 0; i8 < size6; i8++) {
                    stringBufferM1217h2.append(Utils.m530h((String) vectorM516c2.elementAt(i8))).append((char) 0);
                }
                AppState.m588a(1313, stringBufferM1217h2);
                AppState.m594c(1493, 0);
                ScreenManager.m71b(ScreenManager.m75b(3627));
                return;
            case 66:
                IOUtils.m758b();
                return;
            case 67:
                ScreenManager.m71b(ScreenManager.m75b(4633));
                return;
            case 68:
                AppController.m293c();
                ScreenManager.m71b(ScreenManager.m75b(4769));
                return;
            case 69:
                ScreenManager.m71b(ScreenManager.m75b(3340));
                return;
            case 70:
                AppState.m601a(1306, (Object) AppState.m610f().f398f);
                ScreenManager.m71b(ScreenManager.m75b(3553));
                return;
            case 71:
                StringBuffer stringBufferAppend = NetworkUtils.m1217h().append(AppState.m584b(672));
                Object obj3 = AppState.f177b[1365];
                NetworkUtils.m1197a(71, stringBufferAppend.append(obj3 instanceof ContactGroup ? ((ContactGroup) obj3).f398f : ((Contact) obj3).f376u).append(NetworkUtils.m1221a(16167)));
                return;
            case 72:
                NetworkUtils.m1200b(72, 866);
                Vector vectorM614m4 = AppState.m614m(1356);
                Vector vectorM1213g6 = NetworkUtils.m1213g();
                int size7 = vectorM614m4.size();
                while (true) {
                    size7--;
                    if (size7 < 0) {
                        Vector vectorM1213g7 = NetworkUtils.m1213g();
                        vectorM1213g7.addElement(vectorM1213g6);
                        IOUtils.m813b(ConnectionThread.m1148a(AppState.m584b(1377771), NetworkUtils.m1217h().append(AppState.m584b(722608)).append(AppState.m584b(1574400)).append(AppState.m584b(1381)).append(AppState.m584b(395134)).append(Conversation.m1119a((Object) JsonParser.m484a(vectorM1213g7)))));
                        return;
                    } else {
                        Hashtable hashtable2 = new Hashtable();
                        JsonParser.m473a(hashtable2, 329240, JsonParser.m482e(vectorM614m4, size7));
                        JsonParser.m473a(hashtable2, 263673, ResourceManager.m967e(AppState.m586d(1525)));
                        vectorM1213g6.addElement(hashtable2);
                    }
                }
            case 73:
                int iM586d5 = AppState.m586d(1506);
                if (0 != iM586d5) {
                    AppController.m391H();
                    AppController.m340m(iM586d5);
                    return;
                }
                Vector vectorM614m5 = AppState.m614m(1318);
                if (0 != vectorM614m5.size()) {
                    ScreenManager.m71b(NetworkUtils.m1193a(ScreenManager.m75b(3868), vectorM614m5));
                    return;
                } else {
                    AppController.m391H();
                    AppController.m340m(736);
                    return;
                }
            case 74:
                return;
            case 75:
                return;
            case 76:
                XmppMailRuProtocol.m838r();
                return;
            case 77:
                NetworkUtils.m1197a(77, NetworkUtils.m1217h().append(AppState.m584b(672)).append(AppState.m616i().f315k).append(NetworkUtils.m1221a(16167)));
                return;
            case 78:
                NetworkUtils.m1200b(78, 861);
                Vector vectorM1213g8 = NetworkUtils.m1213g();
                vectorM1213g8.addElement(AppState.m614m(1356));
                JsonParser.m481d(vectorM1213g8, AppState.m587e(1524) ? 1 : 0);
                IOUtils.m813b(ConnectionThread.m1148a(AppState.m584b(1508975), IOUtils.m817a(NetworkUtils.m1217h().append(AppState.m584b(722608)).append(AppState.m584b(1640123)), Conversation.m1119a((Object) JsonParser.m484a(vectorM1213g8)))));
                return;
            case 79:
                NetworkUtils.m1198a(79, 863);
                return;
            case 80:
                ScreenManager.m71b(ScreenManager.m75b(4747));
                return;
            case 81:
                NetworkUtils.m1200b(81, 872);
                Vector vectorM1213g9 = NetworkUtils.m1213g();
                vectorM1213g9.addElement(AppState.m584b(AppState.m587e(1526) ? 264068 : 1038));
                JsonParser.m481d(vectorM1213g9, AppState.m586d(1513));
                vectorM1213g9.addElement(Utils.m522f(AppState.m584b(1348)));
                vectorM1213g9.addElement(Utils.m522f(AppState.m584b(1349)));
                vectorM1213g9.addElement(Utils.m522f(AppState.m584b(1350)));
                vectorM1213g9.addElement(Utils.m522f(AppState.m584b(1351)));
                IOUtils.m813b(ConnectionThread.m1148a(AppState.m584b(1050207), NetworkUtils.m1217h().append(AppState.m584b(722608)).append(AppState.m584b(1509223)).append(AppState.m584b(1381)).append(AppState.m584b(395134)).append(Conversation.m1120b((Object) JsonParser.m484a(vectorM1213g9)))));
                return;
            case 82:
                NetworkUtils.m1200b(82, 877);
                Message c0026az3 = new Message(XmppMailRuProtocol.m871i(Utils.m522f(AppState.m584b(1352))), Utils.m522f(AppState.m584b(1353)), Utils.m522f(AppState.m584b(1354)));
                Vector vectorM1213g10 = NetworkUtils.m1213g();
                vectorM1213g10.addElement(c0026az3.m674e());
                IOUtils.m813b(ConnectionThread.m1148a(AppState.m584b(1377947), IOUtils.m817a(NetworkUtils.m1217h().append(AppState.m584b(722608)).append(AppState.m584b(1574735)), Conversation.m1120b((Object) JsonParser.m484a(vectorM1213g10)))));
                return;
            case 83:
                ResourceManager.m925a(4);
                AppState.m594c(3329, 83);
                AppState.m589a(1294, 879);
                AppController.m341r();
                return;
            case 84:
                String strM1026g = XmppContactGroup.m1026g();
                AppState.m601a(1279, (Object) strM1026g);
                AppState.m599a(1456, !StringUtils.m1a(strM1026g));
                ScreenManager.m71b(ScreenManager.m75b(2299));
                return;
            case 85:
                AppState.m591f(1315);
                if (AppState.m611g() == null) {
                    NetworkUtils.m1199a(85, 727, 0);
                    return;
                } else {
                    Contact abstractC0041lM611g3 = AppState.m611g();
                    NetworkUtils.m1199a(85, 727, abstractC0041lM611g3.f369o.mo114a(abstractC0041lM611g3));
                    return;
                }
            case 86:
                Screen c0013amM75b4 = ScreenManager.m75b(4238);
                Contact abstractC0041lM611g4 = AppState.m611g();
                Vector vector = abstractC0041lM611g4.f369o.f313i;
                int size8 = vector.size();
                for (int i9 = 0; i9 < size8; i9++) {
                    ContactGroup abstractC0046q = (ContactGroup) vector.elementAt(i9);
                    c0013amM75b4.m225a(abstractC0046q.m1395f(-1));
                    if (abstractC0046q.m1400b(abstractC0041lM611g4)) {
                        c0013amM75b4.f106k = i9;
                    }
                }
                ScreenManager.m71b(c0013amM75b4);
                return;
            case 87:
                AppState.m599a(1456, Utils.m535l(AppState.m584b(1279)));
                ScreenManager.m71b(ScreenManager.m75b(3647));
                return;
            case 88:
                ScreenManager.m71b(ScreenManager.m75b(4836));
                return;
            case 89:
                ResourceManager.m983t();
                return;
            case 90:
                AppController.m350w();
                return;
            case 91:
                boolean zM587e4 = AppState.m587e(277);
                boolean zM587e5 = AppState.m587e(230);
                AppState.m599a(1418, zM587e4 && !zM587e5);
                AppState.m599a(1419, zM587e4 && zM587e5);
                ScreenManager.m71b(ScreenManager.m75b(1600));
                return;
            case 92:
                AppState.m594c(3707, 0);
                Contact abstractC0041lM611g5 = AppState.m611g();
                if (abstractC0041lM611g5.mo996n()) {
                    AppState.m594c(3784, 92);
                    AppState.m594c(3785, 3);
                    ScreenManager.m71b(ScreenManager.m75b(3783));
                    return;
                }
                IOUtils.m822a(abstractC0041lM611g5);
                boolean z7 = abstractC0041lM611g5 instanceof MrimContact;
                boolean z8 = z7;
                AppState.m599a(1495, z7);
                boolean z9 = z8 && abstractC0041lM611g5.mo990d();
                AppState.m599a(1496, z9);
                AppState.m599a(1497, z8 && !z9);
                AppState.m599a(1498, abstractC0041lM611g5.mo143m());
                AppState.m599a(1499, abstractC0041lM611g5.mo144l() && !abstractC0041lM611g5.mo143m());
                AppState.m599a(1501, z8 && !z9 && ((MrimContact) abstractC0041lM611g5).m1000q());
                AppState.m594c(3706, 3);
                AppState.m594c(3705, 92);
                ScreenManager.m71b(ScreenManager.m75b(3704));
                return;
            case 93:
                Screen c0013amM75b5 = ScreenManager.m75b(2621);
                if (AppState.m611g() instanceof MmpContact) {
                    for (int i10 = 0; i10 < 43; i10++) {
                        if (AppState.m584b(i10 + 1141) != null) {
                            c0013amM75b5.m254b(i10 + 110, StringUtils.m17c(Integer.toString(i10)), i10);
                        }
                    }
                } else if (AppState.m611g() instanceof XmppContact) {
                    for (int i11 = 0; i11 < 37; i11++) {
                        if (AppState.m584b(i11 + 1184) != null) {
                            c0013amM75b5.m254b(i11 + 318, StringUtils.m17c(Integer.toString(i11)), i11);
                        }
                    }
                } else {
                    for (int i12 = 10; i12 < 74; i12++) {
                        c0013amM75b5.m254b(i12 + 36, StringUtils.m17c(Integer.toString(i12)), i12);
                    }
                    for (int i13 = 0; i13 < 10; i13++) {
                        c0013amM75b5.m254b(i13 + 36, StringUtils.m17c(Integer.toString(i13)), i13);
                    }
                    c0013amM75b5.m254b(142, StringUtils.m17c(Integer.toString(74)), 74);
                    c0013amM75b5.m254b(137, StringUtils.m17c(Integer.toString(75)), 75);
                    c0013amM75b5.m254b(210, StringUtils.m17c(Integer.toString(76)), 76);
                    c0013amM75b5.m254b(205, StringUtils.m17c(Integer.toString(77)), 77);
                }
                ScreenManager.m71b(c0013amM75b5);
                return;
            case 94:
                Screen c0013amM75b6 = ScreenManager.m75b(2611);
                for (int i14 = 0; i14 < 15; i14++) {
                    c0013amM75b6.m253a(AppState.m584b(i14 + 48));
                }
                ScreenManager.m71b(c0013amM75b6);
                return;
            case 95:
                Screen c0013amM75b7 = ScreenManager.m75b(2601);
                for (int i15 = 0; i15 < 15; i15++) {
                    c0013amM75b7.m253a(AppState.m584b(i15 + 48));
                }
                ScreenManager.m71b(c0013amM75b7);
                return;
            case 96:
                ContactInfo c0042m = (ContactInfo) AppState.f177b[1315];
                String str2 = (String) c0042m.get(ResourceManager.m967e(-1));
                if (null != str2) {
                    AppController.m339e(str2);
                } else {
                    AppState.m594c(3834, c0042m.m1252a() ? 0 : 503);
                    ScreenManager.m71b(c0042m.m1293f(3830));
                }
                AppState.m594c(3650, 102);
                return;
            case 97:
                Vector vectorM614m6 = AppState.m614m(1389);
                int size9 = vectorM614m6.size();
                if (size9 == 0) {
                    AppController.m340m(397);
                    return;
                }
                Screen c0013amM75b8 = ScreenManager.m75b(1691);
                for (int i16 = 0; i16 < size9; i16++) {
                    GeoRegion c0053x = (GeoRegion) vectorM614m6.elementAt(i16);
                    c0013amM75b8.m247a(-1, c0053x.f421a, 6, c0053x);
                }
                GeoRegion c0053xM40i = StringUtils.m40i();
                c0013amM75b8.m247a(-1, c0053xM40i.f421a, 6, c0053xM40i);
                ScreenManager.m71b(c0013amM75b8);
                return;
            case 98:
                Screen c0013amM75b9 = ScreenManager.m75b(4080);
                for (int i17 = 0; i17 < 15; i17++) {
                    c0013amM75b9.m253a(AppState.m584b(i17 + 48));
                }
                ScreenManager.m71b(c0013amM75b9);
                return;
            case 99:
                Screen c0013amM75b10 = ScreenManager.m75b(4090);
                for (int i18 = 0; i18 < 15; i18++) {
                    c0013amM75b10.m253a(AppState.m584b(i18 + 48));
                }
                ScreenManager.m71b(c0013amM75b10);
                return;
            case 100:
                Screen c0013amM75b11 = ScreenManager.m75b(1701);
                Vector vectorM614m7 = AppState.m614m(1400);
                for (int i19 = 0; i19 < vectorM614m7.size(); i19++) {
                    MapPoint c0014an = (MapPoint) vectorM614m7.elementAt(i19);
                    c0013amM75b11.m247a(-1, c0014an.f133a, 6, c0014an);
                }
                ScreenManager.m71b(c0013amM75b11);
                return;
            case 101:
                return;
            case 102:
                if (AppController.f149c == null && AppController.f148b == null) {
                    Contact abstractC0041lM611g6 = AppState.m611g();
                    String strM314m = AppController.m314m();
                    NetworkUtils.m1199a(102, 728, strM314m != null ? abstractC0041lM611g6.f369o.m1075d((Object) strM314m) : ResourceManager.m969a(abstractC0041lM611g6.mo135a(), abstractC0041lM611g6.f369o));
                    return;
                } else {
                    NetworkUtils.m1199a(102, 728, 0);
                    ResourceManager.m969a(AppController.f148b, AppController.f149c);
                    AppController.m394J();
                    return;
                }
            case 103:
                ScreenManager.m71b(((ContactInfo) AppState.f177b[1319]).m1293f(3878));
                AppState.m591f(1315);
                AppState.m594c(3650, 107);
                return;
            case 104:
                ScreenManager.m71b(ScreenManager.m75b(4204));
                return;
            case 105:
                XmppMailRuProtocol.m838r();
                ScreenManager.m66b().f94a = 105;
                return;
            case 106:
                Screen c0013amM75b12 = ScreenManager.m75b(3840);
                Object obj4 = ((Object[]) AppState.f177b[1271])[2];
                if (obj4 instanceof Image) {
                    c0013amM75b12.m225a(MenuItem.m893a(new GraphicsContext((Image) obj4)));
                } else {
                    c0013amM75b12.m255a(((Integer) obj4).intValue());
                }
                ScreenManager.m71b(c0013amM75b12);
                AppState.m591f(1271);
                return;
            case 107:
                ContactInfo c0042m2 = (ContactInfo) AppState.f177b[1319];
                NetworkUtils.m1199a(107, 728, c0042m2.m1255c().m1075d((Object) c0042m2.m1290i()));
                return;
            case 108:
                return;
            case 109:
                ScreenManager.m71b(ScreenManager.m75b(4862).m257b(AppState.m584b(879 + ((MmpProtocol) AppState.m616i()).m921m())));
                return;
            case 110:
                AppState.m601a(1249, (Object) AppState.f181d);
                String strM655c = MapRenderer.m655c();
                if (strM655c != null) {
                    AppState.m601a(1249, (Object) strM655c);
                }
                ScreenManager.m71b(ScreenManager.m75b(1727));
                return;
            case 111:
                Vector vectorM445W = AppController.m445W();
                int size10 = vectorM445W.size();
                while (true) {
                    size10--;
                    if (size10 < 0) {
                        if (vectorM445W.size() == 0) {
                            AppController.m340m(762);
                        } else {
                            AppController.m353a(vectorM445W);
                            ScreenManager.m71b(NetworkUtils.m1193a(ScreenManager.m75b(1743), vectorM445W));
                        }
                        NetworkUtils.m1212a(vectorM445W);
                        return;
                    }
                    if (((Contact) vectorM445W.elementAt(size10)).mo990d()) {
                        vectorM445W.removeElementAt(size10);
                    }
                }
            case 112:
                AppController.m341r();
                return;
            case 113:
                XmppMailRuProtocol.m858w();
                return;
            case 114:
                AppState.m601a(1250, (Object) AppState.f181d);
                String strM655c2 = MapRenderer.m655c();
                if (strM655c2 != null) {
                    AppState.m601a(1250, (Object) strM655c2);
                }
                ScreenManager.m71b(ScreenManager.m75b(1876));
                return;
            case 115:
                ScreenManager.m71b(ScreenManager.m75b(2501));
                AppController.m299g();
                return;
            case 116:
                Screen c0013amM75b13 = ScreenManager.m75b(1892);
                Enumeration enumerationM1167j = ConnectionThread.m1167j();
                while (enumerationM1167j.hasMoreElements()) {
                    MapPoint c0014an2 = (MapPoint) enumerationM1167j.nextElement();
                    c0013amM75b13.m247a(-1, c0014an2.f133a, 118, c0014an2);
                }
                ScreenManager.m71b(c0013amM75b13);
                return;
            case 117:
                Conversation.m1130c(375);
                ScreenManager.m71b(ScreenManager.m75b(1902));
                return;
            case 118:
                Vector vectorM445W2 = AppController.m445W();
                int size11 = vectorM445W2.size();
                while (true) {
                    size11--;
                    if (size11 < 0) {
                        if (vectorM445W2.size() == 0) {
                            AppController.m340m(762);
                        } else {
                            AppController.m353a(vectorM445W2);
                            ScreenManager.m71b(NetworkUtils.m1193a(ScreenManager.m75b(1930), vectorM445W2));
                        }
                        NetworkUtils.m1212a(vectorM445W2);
                        return;
                    }
                    if (((Contact) vectorM445W2.elementAt(size11)).mo990d()) {
                        vectorM445W2.removeElementAt(size11);
                    }
                }
            case 119:
                ScreenManager.m71b(ScreenManager.m75b(1940));
                return;
            case 120:
                Screen c0013amM75b14 = ScreenManager.m75b(1958);
                Enumeration enumerationM1167j2 = ConnectionThread.m1167j();
                while (enumerationM1167j2.hasMoreElements()) {
                    MapPoint c0014an3 = (MapPoint) enumerationM1167j2.nextElement();
                    c0013amM75b14.m247a(-1, c0014an3.f133a, 6, c0014an3);
                }
                ScreenManager.m71b(c0013amM75b14);
                return;
            case 121:
                ScreenManager.m71b(ScreenManager.m75b(1968));
                return;
            case 122:
                NetworkUtils.m1198a(122, 535);
                return;
            case 123:
                ScreenManager.m71b(AppState.m611g().m1246J());
                return;
            case 124:
                ScreenManager.m71b(ScreenManager.m75b(3479));
                return;
            case 125:
                Account abstractC0037hM616i3 = AppState.m616i();
                Vector vectorM1213g11 = NetworkUtils.m1213g();
                Enumeration enumerationElements2 = abstractC0037hM616i3.f321q.elements();
                while (enumerationElements2.hasMoreElements()) {
                    Contact abstractC0041l2 = (Contact) enumerationElements2.nextElement();
                    if (abstractC0041l2.mo140i()) {
                        vectorM1213g11.addElement(abstractC0041l2);
                    }
                }
                if (vectorM1213g11.size() > 0) {
                    ScreenManager.m71b(NetworkUtils.m1193a(ScreenManager.m75b(4070), vectorM1213g11));
                } else {
                    AppController.m340m(762);
                }
                NetworkUtils.m1212a(vectorM1213g11);
                return;
            case 126:
                Account abstractC0037hM616i4 = AppState.m616i();
                Vector vectorM1213g12 = NetworkUtils.m1213g();
                Enumeration enumerationElements3 = abstractC0037hM616i4.f321q.elements();
                while (enumerationElements3.hasMoreElements()) {
                    Contact abstractC0041l3 = (Contact) enumerationElements3.nextElement();
                    if (abstractC0041l3.mo141j()) {
                        vectorM1213g12.addElement(abstractC0041l3);
                    }
                }
                if (vectorM1213g12.size() > 0) {
                    ScreenManager.m71b(NetworkUtils.m1193a(ScreenManager.m75b(4060), vectorM1213g12));
                } else {
                    AppController.m340m(762);
                }
                NetworkUtils.m1212a(vectorM1213g12);
                return;
            case 127:
                Account abstractC0037hM616i5 = AppState.m616i();
                Vector vectorM1213g13 = NetworkUtils.m1213g();
                Enumeration enumerationElements4 = abstractC0037hM616i5.f321q.elements();
                while (enumerationElements4.hasMoreElements()) {
                    Contact abstractC0041l4 = (Contact) enumerationElements4.nextElement();
                    if (abstractC0041l4.mo142k()) {
                        vectorM1213g13.addElement(abstractC0041l4);
                    }
                }
                if (vectorM1213g13.size() > 0) {
                    ScreenManager.m71b(NetworkUtils.m1193a(ScreenManager.m75b(4050), vectorM1213g13));
                } else {
                    AppController.m340m(762);
                }
                NetworkUtils.m1212a(vectorM1213g13);
                return;
            case 128:
                NetworkUtils.m1197a(128, NetworkUtils.m1217h().append(AppState.m584b(760)).append(AppState.m611g().f376u).append(NetworkUtils.m1221a(16167)));
                return;
            case 129:
                StringBuffer stringBufferAppend2 = NetworkUtils.m1217h().append(AppState.m584b(396));
                Vector vectorM441T = AppController.m441T();
                int i20 = 0;
                int size12 = vectorM441T.size();
                int i21 = size12;
                while (true) {
                    i21--;
                    if (i21 < 0) {
                        AppState.m594c(1438, i20);
                        AppState.m601a(1252, (Object) NetworkUtils.m1215a(stringBufferAppend2));
                        ScreenManager.m71b(ScreenManager.m75b(1990));
                        return;
                    } else {
                        String str3 = ((MrimAccount) vectorM441T.elementAt(i21)).f315k;
                        stringBufferAppend2.append(str3);
                        if (i21 != 0) {
                            stringBufferAppend2.append((char) 0);
                        }
                        if (str3.equals(AppState.m584b(267))) {
                            i20 = size12 - i21;
                        }
                    }
                }
            case 130:
                ScreenManager.m71b(ScreenManager.m75b(4892));
                return;
            case 131:
                ScreenManager.m71b(ScreenManager.m75b(2043));
                return;
            case 132:
                ScreenManager.m71b(ScreenManager.m75b(2421));
                return;
            case 133:
                return;
            case 134:
                return;
            case 135:
                return;
            case 136:
                return;
            case 137:
                ScreenManager.m71b(ScreenManager.m75b(4369));
                if (AppState.m587e(269)) {
                    AppController.m355x();
                    return;
                }
                return;
            case 138:
                return;
            case 139:
                return;
            case 140:
                ScreenManager.m71b(ScreenManager.m75b(5090));
                return;
            case 141:
                return;
            case 142:
                Screen c0013amM75b15 = ScreenManager.m75b(4159);
                MrimContact c0035f = (MrimContact) AppState.m611g();
                MrimAccount c0028ba4 = (MrimAccount) c0035f.f369o;
                Vector vectorM614m8 = AppState.m614m(1318);
                c0035f.m989a(vectorM614m8);
                for (int i22 = 0; i22 < vectorM614m8.size(); i22++) {
                    String strM521a = Utils.m521a(vectorM614m8, i22);
                    if (!StringUtils.m6a(strM521a, c0028ba4.f315k)) {
                        MrimContact c0035f2 = (MrimContact) c0028ba4.m1069c((Object) strM521a);
                        if (c0035f2 != null) {
                            c0013amM75b15.m225a(c0035f2.mo138b());
                        } else {
                            c0013amM75b15.m247a(154, strM521a, 0, strM521a);
                        }
                    }
                }
                if (c0013amM75b15.f108m.size() == 0) {
                    c0013amM75b15.f103i = false;
                    Screen c0013amM255a = c0013amM75b15.m255a(772);
                    c0013amM255a.m259a(AppState.m584b(1038), AppState.m584b(1050), c0013amM255a.f120s, c0013amM255a.f121t, c0013amM255a.f122u);
                }
                ScreenManager.m71b(c0013amM75b15);
                AppState.m591f(1318);
                return;
            case 143:
                AppState.m594c(2722, 0);
                AppState.m588a(1292, NetworkUtils.m1217h().append(AppState.m584b(771)).append(1 + (AppState.m586d(63) % 1000)));
                ScreenManager.m71b(IOUtils.m793a(ScreenManager.m75b(4138), (MrimAccount) AppState.m616i(), (Contact) null));
                return;
            case 144:
                ScreenManager.m71b(IOUtils.m793a(ScreenManager.m75b(4169), (Account) null, AppState.m611g()));
                return;
            case 145:
                AppState.m591f(1315);
                NetworkUtils.m1199a(145, 727, ((MrimAccount) AppState.m611g().f369o).m732g(AppController.m314m()));
                return;
            case 146:
                AppState.m599a(1462, AppController.m439R().size() > 1);
                ScreenManager.m71b(ScreenManager.m75b(2523));
                return;
            case 147:
                String[] strArrM452Z = AppController.m452Z();
                if (strArrM452Z != null) {
                    AppState.m601a(1285, (Object) strArrM452Z[0]);
                    AppState.m588a(1284, NetworkUtils.m1217h().append(AppState.m584b(522)).append(strArrM452Z[1]));
                    if (AppState.m586d(282) != 0) {
                        AppState.m591f(524);
                        AppState.m591f(525);
                    }
                    AppState.m589a(1286, 524);
                    if (AppState.m584b(1289) != null) {
                        AppState.m589a(1286, 1289);
                        AppState.m591f(1289);
                    }
                    ScreenManager.m71b(ScreenManager.m75b(2482));
                    return;
                }
                break;
            case 148:
                return;
            case 149:
                return;
            case 150:
                AppController.m392I();
                MrimContact c0035f3 = (MrimContact) AppState.m611g();
                MrimAccount c0028ba5 = (MrimAccount) c0035f3.f369o;
                NetworkUtils.m1199a(150, 504, c0028ba5.m1052c(c0028ba5.m719a(new Object[]{AppController.m321a(c0028ba5, 4104, new ByteBuffer().m1360p(4194304).m1308a(c0035f3.f297d).m1360p(0).m1360p(0).m1360p(4).m1360p(1)), ResourceManager.m967e(10), c0035f3, new Long(1L)})));
                return;
            case 151:
                ScreenManager.m71b(ScreenManager.m75b(3272));
                return;
            case 152:
                ConnectionThread.m1173n();
                ScreenManager.m71b(ScreenManager.m75b(3302));
                return;
            case 153:
                ResourceManager.m958m();
                return;
            case 154:
                ScreenManager.m71b(ScreenManager.m75b(2085));
                return;
            case 155:
                Vector vectorM1141c = ConnectionThread.m1141c();
                int iM541c = Utils.m541c(vectorM1141c);
                if (iM541c == 0) {
                    AppController.m340m(404);
                    return;
                }
                Screen c0013amM75b16 = ScreenManager.m75b(2101);
                for (int i23 = 0; i23 < iM541c; i23++) {
                    Object objElementAt2 = vectorM1141c.elementAt(i23);
                    c0013amM75b16.m225a(MenuItem.m890a(ConnectionThread.m1138a(objElementAt2), !ConnectionThread.f355f.contains(objElementAt2)));
                }
                ScreenManager.m71b(c0013amM75b16);
                return;
            case 156:
                IOUtils.m768d();
                return;
            case 157:
                ScreenManager.m70a(ScreenManager.m75b(4381));
                return;
            case 158:
                AppState.m594c(2122, AppState.m587e(1442) ? 407 : 408);
                Conversation.m1130c(411);
                ScreenManager.m71b(ScreenManager.m75b(2111));
                return;
            case 159:
                if (!AppState.m587e(266)) {
                    Vector vectorM441T2 = AppController.m441T();
                    int size13 = vectorM441T2.size();
                    int i24 = size13;
                    if (size13 > 0) {
                        Screen c0013amM75b17 = ScreenManager.m75b(2140);
                        while (true) {
                            i24--;
                            if (i24 < 0) {
                                ScreenManager.m71b(c0013amM75b17);
                                return;
                            }
                            MrimAccount c0028ba6 = (MrimAccount) vectorM441T2.elementAt(i24);
                            int iMo108h = c0028ba6.mo108h();
                            String str4 = c0028ba6.f315k;
                            c0013amM75b17.m247a(iMo108h, str4, 153, str4);
                        }
                    }
                }
                ResourceManager.m958m();
                return;
            case 160:
                StringBuffer stringBuffer = new StringBuffer(AppState.m584b(780));
                stringBuffer.append(AppState.m584b(((MrimAccount) AppState.m616i()).f231g.f21i + 780));
                AppState.m588a(1337, stringBuffer);
                ResourceManager.f288g = System.currentTimeMillis();
                ScreenManager.m71b(ScreenManager.m75b(4258));
                return;
            case 161:
                NetworkUtils.m1200b(161, 872);
                return;
            case 162:
                Conversation.m1130c(411);
                ScreenManager.m71b(ScreenManager.m75b(4270));
                return;
            case 163:
                AppState.m594c(1577, 0);
                NetworkUtils.m1197a(163, NetworkUtils.m1217h().append(AppState.m584b(1028)));
                return;
            case 164:
                NetworkUtils.m1182b();
                return;
            case 165:
                NetworkUtils.m1200b(165, 505);
                AppState.f177b[1271] = NetworkUtils.m1222i();
                return;
            case 166:
                ScreenManager.m71b(ScreenManager.m75b(4179));
                return;
            case 167:
                ScreenManager.m71b(ScreenManager.m75b(2158));
                return;
            case 168:
                ResourceManager.m983t();
                return;
            case 169:
                ResourceManager.m951j();
                return;
            case 170:
                Screen c0013amM75b18 = ScreenManager.m75b(2176);
                Vector vector2 = ((Conversation) AppState.f177b[1255]).f343a;
                int size14 = vector2.size();
                while (true) {
                    size14--;
                    if (size14 < 0) {
                        ScreenManager.m71b(c0013amM75b18);
                        AppState.m591f(1255);
                        return;
                    } else {
                        ListItem interfaceC0044o = (ListItem) vector2.elementAt(size14);
                        c0013amM75b18.m247a(-1, interfaceC0044o.mo273x(), 0, interfaceC0044o);
                    }
                }
            case 171:
                NetworkUtils.m1198a(171, 787);
                AppState.m594c(286, 1);
                return;
            case 172:
                Screen c0013amM75b19 = ScreenManager.m75b(2186);
                Vector vectorM440S = AppController.m440S();
                int size15 = vectorM440S.size();
                while (true) {
                    size15--;
                    if (size15 < 0) {
                        ScreenManager.m71b(c0013amM75b19);
                        return;
                    } else {
                        MrimAccount c0028ba7 = (MrimAccount) vectorM440S.elementAt(size15);
                        c0013amM75b19.m247a(156, c0028ba7.f315k, 0, c0028ba7);
                    }
                }
            case 173:
                NetworkUtils.m1198a(173, 416);
                return;
            case 174:
                ScreenManager.m71b(ScreenManager.m75b(2198));
                return;
            case 175:
                ScreenManager.m71b(ScreenManager.m75b(2218));
                return;
            case 176:
                return;
            case 177:
                AppState.m613c((Object) null);
                ScreenManager.m71b(ScreenManager.m75b(2247));
                return;
            case 178:
                ScreenManager.m71b(ScreenManager.m75b(2279));
                return;
            case 179:
                NetworkUtils.m1200b(179, 504);
                return;
            case 180:
                break;
        }
        AppController.m324n();
    }

    /* renamed from: a */
    public static final void m547a() {
        int iM338l;
        int iM338l2;
        int iM338l3;
        int iM338l4;
        int iM338l5;
        int iM1234D;
        int iM1236F;
        int iM1235E;
        int iM1052c;
        int iM338l6;
        int iM338l7;
        int iM338l8;
        int iM721d;
        int iM1052c2;
        int iMo112a;
        AppController.f153g = true;
        AppController.f152f = true;
        Screen c0013amM66b = ScreenManager.m66b();
        String strM67c = ScreenManager.m67c();
        int iM68d = ScreenManager.m68d();
        MenuItem c0032cM69e = ScreenManager.m69e();
        Object obj = c0032cM69e == null ? null : c0032cM69e.f265d;
        int iM460J = 0;
        switch (ScreenManager.m66b().f94a) {
            case 1:
                iM460J = AppController.m408B(iM68d);
                break;
            case 2:
                iM460J = 0;
                break;
            case 3:
                iM460J = IOUtils.m824d(iM68d);
                break;
            case 4:
                iM460J = ContactListManager.m157e();
                break;
            case 5:
                iM460J = AppController.m389z(iM68d);
                break;
            case 6:
                if (!AppState.m587e(1547)) {
                    ConnectionThread.m1161a(c0013amM66b);
                }
                iM460J = 0;
                break;
            case 7:
                iM460J = 0;
                break;
            case 8:
                iM460J = AppController.m335j(iM68d);
                break;
            case 9:
                iM460J = 0;
                break;
            case 10:
                iM460J = 55;
                break;
            case 11:
                iM460J = AppController.m295d();
                break;
            case 13:
                iM460J = -1;
                break;
            case 14:
                iM460J = ResourceManager.m945f();
                break;
            case 15:
                iM460J = AppController.m287a(strM67c, obj);
                break;
            case 16:
                iM460J = 0;
                break;
            case 17:
                iM460J = AppController.m313c(iM68d);
                break;
            case 18:
                iM460J = 0;
                break;
            case 19:
                NetworkUtils.m1195d();
                String[] strArrM518a = Utils.m518a(false);
                Object[] objArr = new Object[strArrM518a.length + 1];
                objArr[0] = Utils.m522f(AppState.m584b(1302));
                for (int i = 0; i < strArrM518a.length; i++) {
                    objArr[i + 1] = strArrM518a[i];
                }
                Contact abstractC0041lM611g = AppState.m611g();
                if (abstractC0041lM611g.mo143m()) {
                    abstractC0041lM611g.m1249c((String) objArr[0]);
                    AppController.f152f = true;
                    iMo112a = 0;
                } else {
                    iMo112a = abstractC0041lM611g.f369o.mo112a(abstractC0041lM611g, objArr);
                }
                iM460J = 0 != iMo112a ? AppController.m338l(iMo112a) : 0;
                break;
            case 20:
                iM460J = AppController.m330i(iM68d);
                break;
            case 21:
                NetworkUtils.m1195d();
                iM460J = AppState.m616i() instanceof XmppProtocol ? ((XmppProtocol) AppState.m616i()).m116k() : 0;
                break;
            case 22:
                NetworkUtils.m1195d();
                MrimAccount c0028ba = (MrimAccount) AppState.m616i();
                String strM522f = Utils.m522f(AppState.m584b(1302));
                String[] strArrM518a2 = Utils.m518a(false);
                if (!c0028ba.m1056C()) {
                    iM1052c2 = 299;
                } else if (Utils.m535l(strM522f)) {
                    int length = strArrM518a2.length;
                    if (length == 0) {
                        iM1052c2 = 709;
                    } else {
                        Enumeration enumerationElements = c0028ba.f321q.elements();
                        while (true) {
                            if (enumerationElements.hasMoreElements()) {
                                MrimContact c0035f = (MrimContact) enumerationElements.nextElement();
                                int i2 = length;
                                do {
                                    i2--;
                                    if (i2 < 0) {
                                        break;
                                    }
                                } while (!c0035f.m994a(strArrM518a2[i2]));
                                iM1052c2 = 486;
                            } else {
                                MrimContactGroup c0010ajM718f = c0028ba.m718f();
                                ByteBuffer c0043nM1309b = new ByteBuffer().m1360p(1048576).m1360p(103).m1308a(AppState.m584b(1233)).m1309b(strM522f);
                                String strM519a = Utils.m519a(strArrM518a2);
                                iM1052c2 = c0028ba.m1052c(c0028ba.m719a(new Object[]{AppController.m321a(c0028ba, 4121, c0043nM1309b.m1308a(strM519a).m1306b(8)), ResourceManager.m967e(5), strM522f, strM519a, c0010ajM718f}));
                            }
                        }
                    }
                } else {
                    iM1052c2 = 708;
                }
                iM460J = 0 != iM1052c2 ? AppController.m338l(iM1052c2) : 0;
                break;
            case 23:
                iM460J = 0;
                break;
            case 24:
                iM460J = 0;
                break;
            case 25:
                iM460J = AppController.m373a(iM68d, obj);
                break;
            case 26:
                NetworkUtils.m1195d();
                AppState.m594c(4305, AppState.m586d(72));
                ScreenManager.m65a();
                AppState.m582c().m201a();
                TabBar.m163a();
                ResourceManager.m927a();
                iM460J = 0;
                break;
            case 27:
                iM460J = NetworkUtils.m1195d();
                break;
            case 28:
                iM460J = NetworkUtils.m1195d();
                break;
            case 29:
                NetworkUtils.m1195d();
                if (AppState.m586d(4305) != AppState.m586d(243)) {
                    TabBar.m163a();
                }
                iM460J = 0;
                break;
            case 30:
                iM460J = IOUtils.m823c(strM67c, iM68d);
                break;
            case 32:
                iM460J = ResourceManager.m933a(strM67c, c0032cM69e);
                break;
            case 33:
                iM460J = NetworkUtils.m1195d();
                break;
            case 34:
                int iM586d = AppState.m586d(1510);
                Account abstractC0037hM616i = AppState.m616i();
                if (abstractC0037hM616i != null) {
                    abstractC0037hM616i.f317m[iM586d + iM586d + 1] = 0;
                    abstractC0037hM616i.f317m[iM586d + iM586d] = 0;
                } else {
                    for (int i3 = 0; i3 < 4; i3++) {
                        AppController.m430b(i3, iM586d, 0);
                        AppController.m430b(i3, iM586d, 1);
                    }
                }
                iM460J = 0;
                break;
            case 35:
                iM460J = AppController.m346n(iM68d);
                break;
            case 36:
                iM460J = 0;
                break;
            case 37:
                iM460J = -1;
                break;
            case 38:
                iM460J = 0;
                break;
            case 39:
                iM460J = AppController.m286a(obj);
                break;
            case 40:
                iM460J = 0;
                break;
            case 41:
                iM460J = -1;
                break;
            case 42:
                iM460J = -1;
                break;
            case 43:
                AppState.m594c(1514, c0013amM66b.f105j);
                AppState.m601a(1345, (Object) strM67c);
                Message c0026az = (Message) obj;
                AppState.m601a(1346, (Object) (c0026az != null ? c0026az.f216a : null));
                iM460J = 0;
                break;
            case 44:
                iM460J = -1;
                break;
            case 45:
                iM460J = -1;
                break;
            case 46:
                iM460J = 0;
                break;
            case 47:
                iM460J = 0;
                break;
            case 48:
                iM460J = -1;
                break;
            case 49:
                NetworkUtils.m1195d();
                iM460J = (AppState.m586d(4308) != 4 || 0 == (iM721d = ((MrimAccount) AppState.m616i()).m721d(((AppState.m586d(4305) - 157) << 8) + 4))) ? 0 : AppController.m338l(iM721d);
                break;
            case 50:
                NetworkUtils.m1195d();
                iM460J = 0;
                break;
            case 51:
                iM460J = ResourceManager.m974c(strM67c);
                break;
            case 52:
                iM460J = 0;
                break;
            case 53:
                iM460J = IOUtils.m787a(strM67c);
                break;
            case 54:
                NetworkUtils.m1195d();
                Vector vectorM1213g = NetworkUtils.m1213g();
                StringBuffer stringBufferM1217h = NetworkUtils.m1217h();
                String strM522f2 = Utils.m522f(AppState.m584b(1352));
                int length2 = strM522f2.length();
                int i4 = 0;
                while (i4 <= length2) {
                    char cCharAt = i4 == length2 ? ';' : strM522f2.charAt(i4);
                    char c = cCharAt;
                    if (cCharAt == ';' || c == ',' || c == ' ') {
                        String strM14b = StringUtils.m14b(stringBufferM1217h);
                        if (!StringUtils.m1a(strM14b)) {
                            vectorM1213g.addElement(strM14b);
                        }
                    } else {
                        stringBufferM1217h.append(c);
                    }
                    i4++;
                }
                if (Utils.m541c(vectorM1213g) == 0) {
                    iM338l8 = AppController.m338l(873);
                } else {
                    boolean z = false;
                    int iM541c = Utils.m541c(vectorM1213g);
                    while (true) {
                        iM541c--;
                        if (iM541c < 0) {
                            iM338l8 = z ? AppController.m338l(876) : 0;
                        } else {
                            String str = (String) vectorM1213g.elementAt(iM541c);
                            int iIndexOf = str.indexOf(64);
                            if (iIndexOf <= 0 || str.indexOf(46) <= 0 || str.indexOf(32) >= 0 || iIndexOf != str.lastIndexOf(64) || str.indexOf(44) >= 0) {
                                z = true;
                            }
                        }
                    }
                }
                iM460J = iM338l8;
                break;
            case 55:
                iM460J = -1;
                break;
            case 56:
                NetworkUtils.m1195d();
                if (AppState.m587e(90)) {
                    AppController.m412O();
                }
                iM460J = 0;
                break;
            case 57:
                iM460J = -1;
                break;
            case 58:
                iM460J = AppController.m322f(iM68d);
                break;
            case 59:
                iM460J = ResourceManager.m978s();
                break;
            case 60:
                iM460J = AppController.m372g(strM67c);
                break;
            case 61:
                iM460J = 42;
                break;
            case 62:
                iM460J = IOUtils.m752a(strM67c, iM68d);
                break;
            case 63:
                AppController.f152f = true;
                AppState.m604b(AppState.m582c().m204b());
                m549c();
                iM460J = 84;
                break;
            case 64:
                iM460J = AppController.m401A(iM68d);
                break;
            case 65:
                NetworkUtils.m1195d();
                AppState.f177b[1314] = Utils.m516c(AppState.m612h().f300g, ',').elementAt(AppState.m586d(1493));
                iM460J = 0;
                break;
            case 66:
                NetworkUtils.m1195d();
                int iMo734a = ((ContactInfo) AppState.f177b[1319]).m1255c().mo734a(Utils.m522f(AppState.m584b(1320)), Utils.m522f(AppState.m584b(1321)), Utils.m522f(AppState.m584b(1322)), (ContactGroup) AppState.m614m(1324).elementAt(AppState.m586d(1507)), AppState.m587e(1509));
                iM460J = 0 != iMo734a ? AppController.m338l(iMo734a) : 0;
                break;
            case 67:
                iM460J = AppController.m289a(strM67c);
                break;
            case 68:
                iM460J = AppController.m291a();
                break;
            case 69:
                NetworkUtils.m1195d();
                int iMo122a = AppState.m616i().mo122a(Utils.m522f(AppState.m584b(1295)));
                iM460J = 0 != iMo122a ? AppController.m338l(iMo122a) : 0;
                break;
            case 70:
                NetworkUtils.m1195d();
                int iMo1399b = AppState.m610f().mo1399b(Utils.m522f(AppState.m584b(1306)));
                iM460J = 0 != iMo1399b ? AppController.m338l(iMo1399b) : 0;
                break;
            case 71:
                iM460J = ResourceManager.m976q();
                break;
            case 72:
                iM460J = -1;
                break;
            case 73:
                AppState.f177b[1319] = obj;
                iM460J = 0;
                break;
            case 74:
                iM460J = -1;
                break;
            case 75:
                iM460J = -1;
                break;
            case 76:
                iM460J = XmppMailRuProtocol.m839s();
                break;
            case 77:
                iM460J = AppController.m400L();
                break;
            case 78:
                iM460J = -1;
                break;
            case 79:
                m549c();
                m549c();
                iM460J = 0;
                break;
            case 80:
                iM460J = AppController.m352o(iM68d);
                break;
            case 81:
                iM460J = -1;
                break;
            case 82:
                iM460J = -1;
                break;
            case 83:
                iM460J = AppController.m308i();
                break;
            case 84:
                iM460J = ResourceManager.m960a(strM67c, iM68d);
                break;
            case 85:
                iM460J = -1;
                break;
            case 86:
                iM460J = AppController.m397h(obj);
                break;
            case 87:
                iM460J = ResourceManager.m938a(strM67c);
                break;
            case 88:
                iM460J = AppController.m371s(iM68d);
                break;
            case 89:
                iM460J = -1;
                break;
            case 90:
                iM460J = AppController.m351e(obj);
                break;
            case 91:
                iM460J = AppController.m364q(iM68d);
                break;
            case 92:
                iM460J = IOUtils.m795b(strM67c, iM68d);
                break;
            case 93:
                iM460J = AppController.m374t(iM68d);
                break;
            case 94:
                iM460J = AppController.m396h(strM67c);
                break;
            case 95:
                iM460J = AppController.m347f(strM67c);
                break;
            case 96:
                iM460J = ((ContactInfo) AppState.f177b[1315]).m1253b() ? 130 : ((ContactInfo) AppState.f177b[1315]).m1252a() ? -1 : AppState.m586d(3650);
                break;
            case 97:
                iM460J = AppController.m398i(obj);
                break;
            case 98:
                iM460J = AppController.m396h(strM67c);
                break;
            case 99:
                iM460J = AppController.m333d(strM67c);
                break;
            case 100:
                iM460J = IOUtils.m770a(iM68d, obj);
                break;
            case 101:
                iM460J = AppController.m382g(obj);
                break;
            case 102:
                iM460J = -1;
                break;
            case 103:
                iM460J = ((ContactInfo) AppState.f177b[1319]).m1253b() ? 130 : AppState.m586d(3650);
                break;
            case 104:
                iM460J = AppController.m363p(iM68d);
                break;
            case 105:
                int iM839s = XmppMailRuProtocol.m839s();
                iM460J = 0 == iM839s ? 4 : iM839s;
                break;
            case 106:
                iM460J = 0;
                break;
            case 107:
                iM460J = -1;
                break;
            case 108:
                iM460J = AppController.m327o();
                break;
            case 109:
                iM460J = ((MmpProtocol) AppState.m616i()).m923d(iM68d);
                break;
            case 110:
                NetworkUtils.m1195d();
                iM460J = StringUtils.m1a(Utils.m522f(AppState.m584b(1249))) ? AppController.m338l(352) : 0;
                break;
            case 111:
                iM460J = AppController.m404j(obj);
                break;
            case 112:
                iM460J = -1;
                break;
            case 113:
                iM460J = XmppMailRuProtocol.m859h(iM68d);
                break;
            case 114:
                NetworkUtils.m1195d();
                String strM522f3 = Utils.m522f(AppState.m584b(1250));
                if (StringUtils.m1a(strM522f3)) {
                    iM338l7 = AppController.m338l(372);
                } else {
                    long jMo274v = MapRenderer.f196d;
                    long jMo275w = MapRenderer.f195c;
                    ListItem interfaceC0044o = MapRenderer.f203k;
                    if (interfaceC0044o != null && interfaceC0044o.mo277s()) {
                        jMo274v = interfaceC0044o.mo274v();
                        jMo275w = interfaceC0044o.mo275w();
                        interfaceC0044o.mo278t();
                    }
                    MapPoint c0014an = new MapPoint(strM522f3, 0L, 0L, 0L, 0L, jMo274v, jMo275w, AppState.m586d(39));
                    c0014an.f143k = 4;
                    Vector vectorM614m = AppState.m614m(1401);
                    XmppContactGroup.m1043a(vectorM614m, c0014an, 0, 50);
                    XmppContactGroup.m1046a(vectorM614m, 226);
                    MapRenderer.m654b(c0014an);
                    iM338l7 = 0;
                }
                iM460J = iM338l7;
                break;
            case 115:
                NetworkUtils.m1195d();
                String strM522f4 = Utils.m522f(AppState.m584b(1286));
                if (StringUtils.m1a(strM522f4)) {
                    iM338l6 = AppController.m338l(523);
                } else {
                    MrimAccount c0028ba2 = (MrimAccount) AppState.m611g().f369o;
                    boolean zM587e = AppState.m587e(1507);
                    long jM598g = AppState.m598g(1469);
                    if (c0028ba2.m1056C()) {
                        IOUtils.m778d((Object) AppState.m584b(494));
                        iM1052c = c0028ba2.m1052c(AppController.m321a(c0028ba2, 4196, new ByteBuffer().m1360p(zM587e ? 5 : 20).m1309b(strM522f4).m1323a(jM598g)));
                    } else {
                        iM1052c = 299;
                    }
                    iM338l6 = 0 != iM1052c ? AppController.m338l(iM1052c) : 0;
                }
                iM460J = iM338l6;
                break;
            case 116:
                iM460J = AppController.m405k(obj);
                break;
            case 117:
                iM460J = AppController.m320b(strM67c);
                break;
            case 118:
                iM460J = AppController.m409m(obj);
                break;
            case 119:
                iM460J = AppController.m384v(iM68d);
                break;
            case 120:
                iM460J = AppController.m463n(obj);
                break;
            case 121:
                iM460J = AppController.m387x(iM68d);
                break;
            case 122:
                iM460J = AppController.m329p();
                break;
            case 123:
                iM460J = AppController.m407l(obj);
                break;
            case 124:
                iM460J = 0;
                break;
            case 125:
                Contact abstractC0041l = (Contact) obj;
                iM460J = (null == abstractC0041l || 0 == (iM1235E = abstractC0041l.m1235E())) ? 0 : AppController.m338l(iM1235E);
                break;
            case 126:
                Contact abstractC0041l2 = (Contact) obj;
                iM460J = (null == abstractC0041l2 || 0 == (iM1236F = abstractC0041l2.m1236F())) ? 0 : AppController.m338l(iM1236F);
                break;
            case 127:
                Contact abstractC0041l3 = (Contact) obj;
                iM460J = (null == abstractC0041l3 || 0 == (iM1234D = abstractC0041l3.m1234D())) ? 0 : AppController.m338l(iM1234D);
                break;
            case 128:
                AppState.m611g().m1229B();
                iM460J = 4;
                break;
            case 129:
                NetworkUtils.m1195d();
                AppState.m594c(44, 1);
                int iM586d2 = AppState.m586d(1438);
                if (iM586d2 > 0) {
                    AppState.m594c(266, 1);
                    AppState.m601a(267, (Object) Utils.m542c(1252, iM586d2));
                } else {
                    AppState.m601a(267, (Object) AppState.f181d);
                }
                iM460J = 0;
                break;
            case 130:
                iM460J = AppController.m288a(iM68d);
                break;
            case 131:
                iM460J = AppController.m406i(strM67c);
                break;
            case 132:
                iM460J = AppController.m301b(iM68d);
                break;
            case 133:
                iM460J = 0;
                break;
            case 134:
                iM460J = 0;
                break;
            case 135:
                iM460J = 0;
                break;
            case 136:
                iM460J = 0;
                break;
            case 137:
                iM460J = -1;
                break;
            case 138:
                iM460J = 0;
                break;
            case 139:
                iM460J = 129;
                break;
            case 140:
                iM460J = NetworkUtils.m1195d();
                break;
            case 141:
                iM460J = -1;
                break;
            case 142:
                if (obj == null) {
                    iM338l5 = -1;
                } else if (obj instanceof String) {
                    AppState.f177b[1319] = ContactInfo.m1251a(AppState.m611g().f369o).m1262e((String) obj).m1259b((String) obj);
                    iM338l5 = 66;
                } else {
                    iM338l5 = AppController.m338l(773);
                }
                iM460J = iM338l5;
                break;
            case 143:
                NetworkUtils.m1195d();
                String strM522f5 = Utils.m522f(AppState.m584b(1292));
                if (StringUtils.m1a(strM522f5)) {
                    iM338l4 = AppController.m338l(301);
                } else {
                    Vector vectorM794a = IOUtils.m794a(c0013amM66b, 3);
                    if (vectorM794a.size() == 0) {
                        iM338l4 = AppController.m338l(775);
                    } else {
                        MrimAccount c0028ba3 = (MrimAccount) AppState.m616i();
                        boolean zM587e2 = AppState.m587e(2722);
                        ByteBuffer c0043n = new ByteBuffer();
                        int size = vectorM794a.size();
                        int i5 = size;
                        ByteBuffer c0043nM1360p = c0043n.m1360p(size);
                        while (true) {
                            i5--;
                            if (i5 < 0) {
                                ByteBuffer c0043nM1327c = new ByteBuffer().m1327c(c0043nM1360p);
                                Object[] objArr2 = new Object[3];
                                objArr2[0] = AppController.m321a(c0028ba3, 4121, new ByteBuffer().m1360p(128).m1306b(8).m1309b(strM522f5).m1306b(12).m1327c(zM587e2 ? c0043nM1327c.m1308a(c0028ba3.f315k) : c0043nM1327c));
                                objArr2[1] = ResourceManager.m967e(15);
                                objArr2[2] = strM522f5;
                                int iM1052c3 = c0028ba3.m1052c(c0028ba3.m719a(objArr2));
                                if (0 != iM1052c3) {
                                    iM338l4 = AppController.m338l(iM1052c3);
                                } else {
                                    AppState.m595d(63, 1);
                                    iM338l4 = 0;
                                }
                            } else {
                                c0043nM1360p.m1308a((String) vectorM794a.elementAt(i5));
                            }
                        }
                    }
                }
                iM460J = iM338l4;
                break;
            case 144:
                Vector vectorM794a2 = IOUtils.m794a(c0013amM66b, 0);
                if (vectorM794a2.size() == 0) {
                    iM338l3 = AppController.m338l(775);
                } else {
                    MrimContact c0035f2 = (MrimContact) AppState.m611g();
                    MrimAccount c0028ba4 = (MrimAccount) c0035f2.f369o;
                    ByteBuffer c0043n2 = new ByteBuffer();
                    int size2 = vectorM794a2.size();
                    int i6 = size2;
                    ByteBuffer c0043nM1360p2 = c0043n2.m1360p(size2);
                    while (true) {
                        i6--;
                        if (i6 < 0) {
                            int iM1052c4 = c0028ba4.m1052c(c0028ba4.m719a(new Object[]{AppController.m321a(c0028ba4, 4104, new ByteBuffer().m1360p(4194304).m1308a(c0035f2.f297d).m1360p(0).m1360p(0).m1327c(new ByteBuffer().m1360p(3).m1327c(c0043nM1360p2))), ResourceManager.m967e(10), c0035f2, new Long(2L)}));
                            iM338l3 = 0 != iM1052c4 ? AppController.m338l(iM1052c4) : 0;
                        } else {
                            c0043nM1360p2.m1308a((String) vectorM794a2.elementAt(i6));
                        }
                    }
                }
                iM460J = iM338l3;
                break;
            case 145:
                iM460J = -1;
                break;
            case 146:
                iM460J = AppController.m323g(iM68d);
                break;
            case 147:
                NetworkUtils.m1195d();
                String strM522f6 = Utils.m522f(AppState.m584b(1286));
                if (StringUtils.m1a(strM522f6)) {
                    iM338l2 = AppController.m338l(523);
                } else {
                    MrimAccount c0028ba5 = (MrimAccount) AppState.m616i();
                    new AsyncTask(17, new ByteBuffer().m1310c(1442705).m1310c(1049531).m1385u(4022591).m1314d(c0028ba5.f315k).m1385u(4022822).m1314d(c0028ba5.f316l).m1310c(459757).m1310c(459750).m1314d(Conversation.m1120b((Object) strM522f6)).m1314d(Utils.m522f(AppState.m587e(1468) ? AppState.m584b(1285) : null)).m1317c());
                    AppState.m595d(282, 1);
                    iM338l2 = 0;
                }
                iM460J = iM338l2;
                break;
            case 148:
                iM460J = 0;
                break;
            case 149:
                iM460J = 0;
                break;
            case 150:
                iM460J = -1;
                break;
            case 151:
                iM460J = AppController.m336k(iM68d);
                break;
            case 152:
                iM460J = AppController.m325h(iM68d);
                break;
            case 153:
                iM460J = ResourceManager.m959d(obj);
                break;
            case 154:
                NetworkUtils.m1195d();
                String strM584b = AppState.m584b(1253);
                long jMo274v2 = MapRenderer.f196d;
                long jMo275w2 = MapRenderer.f195c;
                ListItem interfaceC0044o2 = MapRenderer.f203k;
                if (interfaceC0044o2 != null && interfaceC0044o2.mo277s()) {
                    jMo274v2 = interfaceC0044o2.mo274v();
                    jMo275w2 = interfaceC0044o2.mo275w();
                    interfaceC0044o2.mo278t();
                }
                String strM584b2 = AppState.m584b(1254);
                long j = jMo274v2;
                long j2 = jMo275w2;
                if (strM584b != null) {
                    XmppContactGroup.f310a.addElement(new Object[]{strM584b, new long[]{j, j2}, strM584b2});
                }
                long j3 = jMo274v2;
                long j4 = jMo275w2;
                if (strM584b != null) {
                    String strM522f7 = Utils.m522f(AppState.m584b(223));
                    ByteBuffer c0043nM1314d = new ByteBuffer().m1310c(3150648).m1385u(15713).m1314d(strM584b).m1385u(4022822).m1383b(j3).m1385u(4023078).m1383b(j4).m1385u(4023334).m1314d(strM522f7).m1385u(4023590).m1314d(new ByteBuffer().m1314d(strM522f7).m1310c(396139).m1383b(j3).m1365B().m1387H());
                    if (strM584b2 != null) {
                        c0043nM1314d.m1385u(4023846).m1313c(strM584b2);
                    }
                    if (AppState.m587e(266)) {
                        String strM584b3 = AppState.m584b(267);
                        if (Utils.m535l(strM584b3)) {
                            c0043nM1314d.m1385u(4024102).m1313c(strM584b3);
                        }
                    }
                    new AsyncTask(16, c0043nM1314d.m1317c());
                }
                MapRenderer.f200h = true;
                iM460J = 0;
                break;
            case 155:
                Vector vectorM1141c = ConnectionThread.m1141c();
                StringBuffer stringBufferM1217h2 = NetworkUtils.m1217h();
                Vector vector = c0013amM66b.f108m;
                int size3 = vector.size();
                for (int i7 = 0; i7 < size3; i7++) {
                    if (!((Boolean) ((MenuItem) vector.elementAt(i7)).f265d).booleanValue()) {
                        stringBufferM1217h2.append(vectorM1141c.elementAt(i7)).append((char) 0);
                    }
                }
                String strM1215a = NetworkUtils.m1215a(stringBufferM1217h2);
                ConnectionThread.f355f = Utils.m515b(strM1215a, (char) 0);
                AppState.m601a(264, (Object) strM1215a);
                iM460J = 0;
                break;
            case 156:
                iM460J = IOUtils.m769e();
                break;
            case 157:
                iM460J = 0;
                break;
            case 158:
                iM460J = AppController.m370r(iM68d);
                break;
            case 159:
                iM460J = AppController.m296b(obj);
                break;
            case 160:
                iM460J = ResourceManager.m947h();
                break;
            case 161:
                iM460J = -1;
                break;
            case 162:
                iM460J = AppController.m388y(iM68d);
                break;
            case 163:
                iM460J = AppController.m348v();
                break;
            case 164:
                NetworkUtils.m1195d();
                String strM843u = XmppMailRuProtocol.m843u();
                String strM9b = strM843u;
                if (!XmppMailRuProtocol.m841f(strM843u)) {
                    strM9b = StringUtils.m9b(strM9b, Utils.m542c(694, AppState.m586d(1474)));
                }
                if (XmppMailRuProtocol.m844g(strM9b)) {
                    String str2 = strM9b;
                    String strM522f8 = Utils.m522f(AppState.m584b(1293));
                    String strM522f9 = Utils.m522f(AppState.m584b(1284));
                    int iM586d3 = AppState.m586d(4305);
                    AppState.f177b[1271] = NetworkUtils.m1223a(str2, 0, strM522f8, strM522f9, 0 == iM586d3 ? Utils.m522f(AppState.m584b(1287)) : (String) Utils.m516c(AppState.m584b(810), (char) 0).elementAt(iM586d3), Utils.m522f(AppState.m584b(1288)), Utils.m522f(AppState.m584b(1298)), Utils.m522f(AppState.m584b(1299)), AppState.m586d(1489), AppState.m586d(1488), AppState.m586d(1491), AppState.m586d(1481), AppState.m586d(1480), AppState.m584b(1342), AppState.m584b(1343));
                    iM338l = 13;
                } else {
                    iM338l = AppController.m338l(559);
                }
                iM460J = iM338l;
                break;
            case 165:
                iM460J = -1;
                break;
            case 166:
                iM460J = AppController.m381u(iM68d);
                break;
            case 167:
                iM460J = AppController.m385w(iM68d);
                break;
            case 168:
                iM460J = ResourceManager.m984a(c0013amM66b);
                break;
            case 169:
                iM460J = ResourceManager.m952b(obj);
                break;
            case 170:
                iM460J = AppController.m379f(obj);
                break;
            case 171:
                iM460J = AppController.m297e();
                break;
            case 172:
                iM460J = AppController.m337d(obj);
                break;
            case 173:
                iM460J = AppController.m399K();
                break;
            case 174:
                iM460J = 0;
                break;
            case 175:
                NetworkUtils.m1195d();
                if (AppState.m587e(280)) {
                    IOUtils.m772f();
                }
                iM460J = 0;
                break;
            case 176:
                iM460J = AppController.m309c(obj);
                break;
            case 177:
                iM460J = ResourceManager.m956d(iM68d);
                break;
            case 178:
                iM460J = AppController.m460J(iM68d);
                break;
            case 179:
                iM460J = -1;
                break;
            case 180:
                iM460J = -1;
                break;
        }
        if (iM460J != -1) {
            if (iM460J == 12) {
                m549c();
                return;
            }
            if (iM460J != 0) {
                m546a(iM460J);
                return;
            }
            int i8 = c0013amM66b.f120s;
            if (i8 != 200) {
                int i9 = i8 == 199 ? iM68d : i8;
                int i10 = i9;
                if (i9 == 12) {
                    m549c();
                } else if (i10 != 0) {
                    m546a(i10);
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* renamed from: b */
    public static final void m548b() {
        int iM1233b;
        AppController.f153g = true;
        AppController.f152f = true;
        Screen c0013amM66b = ScreenManager.m66b();
        int i = ScreenManager.m66b().f94a;
        ScreenManager.m67c();
        MenuItem c0032cM69e = ScreenManager.m69e();
        Object obj = c0032cM69e == null ? null : c0032cM69e.f265d;
        int iM308i = 0;
        switch (i) {
            case 1:
                iM308i = 0;
                break;
            case 2:
                iM308i = 0;
                break;
            case 3:
                iM308i = 0;
                break;
            case 4:
                iM308i = ContactListManager.m159a(obj);
                break;
            case 5:
                iM308i = 0;
                break;
            case 6:
                iM308i = ConnectionThread.m1162b(c0013amM66b);
                break;
            case 7:
                iM308i = 0;
                break;
            case 8:
                iM308i = 0;
                break;
            case 9:
                iM308i = 0;
                break;
            case 10:
                iM308i = 0;
                break;
            case 11:
                iM308i = 0;
                break;
            case 13:
                iM308i = 0;
                break;
            case 14:
                iM308i = 0;
                break;
            case 15:
                iM308i = 0;
                break;
            case 16:
                iM308i = 0;
                break;
            case 17:
                iM308i = 0;
                break;
            case 18:
                iM308i = 0;
                break;
            case 19:
                iM308i = 0;
                break;
            case 20:
                iM308i = 0;
                break;
            case 21:
                iM308i = 0;
                break;
            case 22:
                iM308i = 0;
                break;
            case 23:
                iM308i = 0;
                break;
            case 24:
                iM308i = 0;
                break;
            case 25:
                iM308i = 0;
                break;
            case 26:
                AppController.f152f = true;
                iM308i = 0;
                break;
            case 27:
                iM308i = 0;
                break;
            case 28:
                iM308i = 0;
                break;
            case 29:
                iM308i = 0;
                break;
            case 30:
                iM308i = 0;
                break;
            case 32:
                iM308i = 0;
                break;
            case 33:
                iM308i = 0;
                break;
            case 34:
                iM308i = 0;
                break;
            case 35:
                iM308i = 0;
                break;
            case 36:
                iM308i = ResourceManager.m955c(obj);
                break;
            case 37:
                iM308i = 0;
                break;
            case 38:
                iM308i = 0;
                break;
            case 39:
                iM308i = 0;
                break;
            case 40:
                iM308i = 0;
                break;
            case 41:
                AppState.m591f(1271);
                iM308i = 0;
                break;
            case 42:
                iM308i = 0;
                break;
            case 43:
                iM308i = 0;
                break;
            case 44:
                iM308i = 0;
                break;
            case 45:
                iM308i = 0;
                break;
            case 46:
                iM308i = 0;
                break;
            case 47:
                iM308i = 0;
                break;
            case 48:
                AppState.m591f(1271);
                iM308i = 0;
                break;
            case 49:
                iM308i = 0;
                break;
            case 50:
                iM308i = 0;
                break;
            case 51:
                iM308i = 0;
                break;
            case 52:
                iM308i = 0;
                break;
            case 53:
                iM308i = 0;
                break;
            case 54:
                iM308i = 0;
                break;
            case 55:
                iM308i = -1;
                break;
            case 56:
                iM308i = 0;
                break;
            case 57:
                iM308i = 0;
                break;
            case 58:
                iM308i = 0;
                break;
            case 59:
                iM308i = 0;
                break;
            case 60:
                iM308i = 0;
                break;
            case 61:
                iM308i = 12;
                break;
            case 62:
                iM308i = 0;
                break;
            case 63:
                String strM1026g = XmppContactGroup.m1026g();
                if (!StringUtils.m1a(strM1026g) && 0 != (iM1233b = AppState.m611g().m1233b(strM1026g))) {
                    m549c();
                    IOUtils.m778d((Object) AppState.m584b(iM1233b));
                }
                AppState.m594c(1456, 0);
                AppState.m591f(1279);
                AppController.f152f = true;
                AppState.m604b(AppState.m582c());
                m549c();
                iM308i = 40;
                break;
            case 64:
                iM308i = 0;
                break;
            case 65:
                iM308i = 0;
                break;
            case 66:
                iM308i = 0;
                break;
            case 67:
                iM308i = 0;
                break;
            case 68:
                iM308i = AppController.m292b();
                break;
            case 69:
                iM308i = 0;
                break;
            case 70:
                iM308i = 0;
                break;
            case 71:
                iM308i = 0;
                break;
            case 72:
                iM308i = 0;
                break;
            case 73:
                iM308i = 0;
                break;
            case 74:
                iM308i = 0;
                break;
            case 75:
                iM308i = 0;
                break;
            case 76:
                iM308i = 0;
                break;
            case 77:
                iM308i = 0;
                break;
            case 78:
                iM308i = 0;
                break;
            case 79:
                iM308i = 0;
                break;
            case 80:
                iM308i = 0;
                break;
            case 81:
                iM308i = 0;
                break;
            case 82:
                iM308i = 0;
                break;
            case 83:
                iM308i = AppController.m308i();
                break;
            case 84:
                m549c();
                iM308i = 0;
                break;
            case 85:
                AppController.m394J();
                iM308i = 0;
                break;
            case 86:
                iM308i = 0;
                break;
            case 87:
                iM308i = 0;
                break;
            case 88:
                iM308i = 0;
                break;
            case 89:
                iM308i = 0;
                break;
            case 90:
                iM308i = 0;
                break;
            case 91:
                iM308i = 0;
                break;
            case 92:
                iM308i = 0;
                break;
            case 93:
                iM308i = 0;
                break;
            case 94:
                iM308i = 0;
                break;
            case 95:
                iM308i = 0;
                break;
            case 96:
                AppController.m394J();
                iM308i = 0;
                break;
            case 97:
                iM308i = 0;
                break;
            case 98:
                iM308i = 0;
                break;
            case 99:
                iM308i = 0;
                break;
            case 100:
                AppState.m594c(1443, 0);
                AppState.m594c(1477, 0);
                iM308i = 0;
                break;
            case 101:
                AppState.m594c(1478, 0);
                AppState.m594c(1443, 0);
                iM308i = 0;
                break;
            case 102:
                AppState.m591f(1271);
                iM308i = 0;
                break;
            case 103:
                iM308i = 0;
                break;
            case 104:
                iM308i = 0;
                break;
            case 105:
                iM308i = 12;
                break;
            case 106:
                iM308i = 0;
                break;
            case 107:
                AppState.m591f(1271);
                iM308i = 0;
                break;
            case 108:
                iM308i = 0;
                break;
            case 109:
                iM308i = 0;
                break;
            case 110:
                iM308i = 0;
                break;
            case 111:
                iM308i = 0;
                break;
            case 112:
                iM308i = 0;
                break;
            case 113:
                iM308i = 0;
                break;
            case 114:
                iM308i = 0;
                break;
            case 115:
                iM308i = 0;
                break;
            case 116:
                AppState.m594c(1443, 0);
                iM308i = 0;
                break;
            case 117:
                iM308i = 0;
                break;
            case 118:
                iM308i = 0;
                break;
            case 119:
                iM308i = 0;
                break;
            case 120:
                AppState.m594c(1478, 0);
                iM308i = 0;
                break;
            case 121:
                iM308i = 0;
                break;
            case 122:
                iM308i = 0;
                break;
            case 123:
                iM308i = 0;
                break;
            case 124:
                iM308i = 0;
                break;
            case 125:
                iM308i = 0;
                break;
            case 126:
                iM308i = 0;
                break;
            case 127:
                iM308i = 0;
                break;
            case 128:
                iM308i = 0;
                break;
            case 129:
                iM308i = 0;
                break;
            case 130:
                iM308i = 0;
                break;
            case 131:
                iM308i = 0;
                break;
            case 132:
                iM308i = 0;
                break;
            case 133:
                iM308i = 0;
                break;
            case 134:
                iM308i = 0;
                break;
            case 135:
                iM308i = 0;
                break;
            case 136:
                iM308i = 0;
                break;
            case 137:
                iM308i = 12;
                break;
            case 138:
                iM308i = 0;
                break;
            case 139:
                iM308i = 6;
                break;
            case 140:
                iM308i = 0;
                break;
            case 141:
                iM308i = 0;
                break;
            case 142:
                iM308i = 0;
                break;
            case 143:
                iM308i = 0;
                break;
            case 144:
                iM308i = 0;
                break;
            case 145:
                iM308i = 0;
                break;
            case 146:
                iM308i = 0;
                break;
            case 147:
                iM308i = 0;
                break;
            case 148:
                iM308i = 0;
                break;
            case 149:
                iM308i = 0;
                break;
            case 150:
                iM308i = 12;
                break;
            case 151:
                iM308i = 0;
                break;
            case 152:
                iM308i = 0;
                break;
            case 153:
                iM308i = 0;
                break;
            case 154:
                iM308i = 0;
                break;
            case 155:
                iM308i = 0;
                break;
            case 156:
                iM308i = 0;
                break;
            case 157:
                iM308i = 0;
                break;
            case 158:
                iM308i = 0;
                break;
            case 159:
                iM308i = 0;
                break;
            case 160:
                m549c();
                iM308i = 0;
                break;
            case 161:
                iM308i = 4;
                break;
            case 162:
                iM308i = 0;
                break;
            case 163:
                iM308i = 0;
                break;
            case 164:
                iM308i = 0;
                break;
            case 165:
                iM308i = 0;
                break;
            case 166:
                iM308i = 0;
                break;
            case 167:
                iM308i = 0;
                break;
            case 168:
                iM308i = 0;
                break;
            case 169:
                ((MrimAccount) AppState.m616i()).f232h = true;
                iM308i = 0;
                break;
            case 170:
                iM308i = 0;
                break;
            case 171:
                AppState.m594c(285, 0);
                ConnectionThread.m1164h();
                iM308i = 6;
                break;
            case 172:
                iM308i = 0;
                break;
            case 173:
                AppState.m617d((Object) null);
                iM308i = 12;
                break;
            case 174:
                iM308i = 0;
                break;
            case 175:
                iM308i = 0;
                break;
            case 176:
                iM308i = 12;
                break;
            case 177:
                iM308i = 0;
                break;
            case 178:
                iM308i = 0;
                break;
            case 179:
                iM308i = 0;
                break;
            case 180:
                iM308i = 0;
                break;
        }
        if (iM308i != -1) {
            if (iM308i == 12) {
                m549c();
                return;
            }
            if (iM308i != 0) {
                m546a(iM308i);
                return;
            }
            int i2 = c0013amM66b.f121t;
            if (i2 != 200) {
                if (i2 == 12) {
                    m549c();
                } else if (i2 != 0) {
                    m546a(i2);
                }
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* renamed from: c */
    public static final void m549c() {
        AppController.f153g = true;
        switch (ScreenManager.m66b().f94a) {
            case 2:
                AppState.m599a(71, AppState.m587e(218));
                AppState.m582c().m201a();
                AppState.m594c(1511, 0);
                break;
            case 6:
                TabBar.f44a = false;
                TabBar.m172h();
                break;
            case 9:
                AppController.m299g();
                break;
            case 14:
                AppState.m591f(1286);
                break;
            case 19:
                AppController.m378F();
                break;
            case 21:
                int iMo80a = AppState.m616i().mo80a();
                if (iMo80a != 0 && iMo80a == 1) {
                    AppState.m591f(1307);
                    AppState.m591f(1308);
                    AppState.m591f(1309);
                    AppState.m591f(1310);
                    AppState.m591f(1311);
                    AppState.m591f(1312);
                    AppState.m594c(1492, 0);
                    break;
                } else {
                    StringUtils.m30f();
                    break;
                }
            case 22:
                AppController.m378F();
                break;
            case 25:
                AppState.m591f(1281);
                break;
            case 26:
                AppState.m594c(72, AppState.m586d(4305));
                break;
            case 36:
                TabBar.m170f();
                break;
            case 37:
                AppState.m591f(1271);
                break;
            case 39:
                AppState.m591f(1283);
                break;
            case 40:
                AppState.m591f(1290);
                AppState.m591f(1279);
                break;
            case 41:
                AppState.m591f(1271);
                break;
            case 42:
                AppState.m591f(1271);
                break;
            case 48:
                AppState.m591f(1271);
                break;
            case 51:
                AppState.m591f(1347);
                break;
            case 54:
                AppState.m590b(1352, 1354);
                break;
            case 59:
                AppState.m591f(1284);
                AppState.m591f(1285);
                AppState.m591f(1271);
                break;
            case 66:
                AppState.m591f(1319);
                AppState.m590b(1320, 1324);
                break;
            case 67:
                IOUtils.m814e((Object) null);
                break;
            case 68:
                AppController.m293c();
                break;
            case 69:
                AppState.m591f(1295);
                break;
            case 70:
                AppState.m591f(1306);
                break;
            case 72:
                AppState.m591f(1271);
                break;
            case 73:
                AppController.m391H();
                break;
            case 76:
                XmppMailRuProtocol.m840t();
                break;
            case 78:
                AppState.m591f(1271);
                break;
            case 81:
                AppState.m591f(1271);
                break;
            case 82:
                AppState.m591f(1271);
                break;
            case 85:
                if (AppState.m611g() != null) {
                    AppState.m611g().mo148L();
                    break;
                }
                break;
            case 96:
                AppState.m591f(1315);
                break;
            case 100:
                AppState.m591f(1248);
                break;
            case 101:
                AppState.m594c(1443, 0);
                AppState.m594c(1477, 0);
                break;
            case 105:
                XmppMailRuProtocol.m840t();
                break;
            case 108:
                AppController.m412O();
                AppState.m591f(1282);
                break;
            case 113:
                XmppMailRuProtocol.f257f = null;
                break;
            case 120:
                AppState.m594c(1443, 0);
                break;
            case 122:
                NetworkUtils.m1212a(AppState.m614m(1291));
                AppState.m591f(1291);
                break;
            case 138:
                AppController.m334q();
                break;
            case 142:
                AppState.m591f(1336);
                break;
            case 143:
                AppState.m591f(1292);
                break;
            case 147:
                AppController.m299g();
                break;
            case 154:
                AppState.m591f(1253);
                AppState.m591f(1254);
                break;
            case 156:
                IOUtils.f239e = null;
                IOUtils.f238d = null;
                break;
            case 164:
                AppState.m591f(1292);
                AppState.m591f(1293);
                AppState.m591f(1284);
                AppState.m594c(1474, 0);
                AppState.m594c(4305, 0);
                AppState.m590b(1341, 1343);
                AppController.m299g();
                StringUtils.m30f();
                break;
            case 168:
                AppController.m378F();
                break;
            case 176:
                AppState.m590b(1256, 1258);
                break;
            case 179:
                AppState.m591f(1284);
                break;
            case 180:
                AppState.m591f(1271);
                break;
        }
        Vector vectorM614m = AppState.m614m(1272);
        int size = vectorM614m.size() - 1;
        Screen c0013am = (Screen) vectorM614m.elementAt(size);
        NetworkUtils.m1212a(c0013am.f110o);
        NetworkUtils.m1212a(c0013am.f108m);
        vectorM614m.removeElementAt(size);
        Utils.m526b(vectorM614m);
    }
}
