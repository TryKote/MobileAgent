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
/* renamed from: u */
/* loaded from: MobileAgent_3.9.jar:u.class */
public final class SearchEntry {

    /* renamed from: a */
    public int id;

    /* renamed from: b */
    public String query;

    /* renamed from: c */
    public int type;

    public SearchEntry(String str, int i) {
        this.query = str;
        this.type = i;
    }
}
