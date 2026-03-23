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
/* renamed from: o */
/* loaded from: MobileAgent_3.9.jar:o.class */
public interface ListItem {
    /* renamed from: r */
    int getHeight();

    /* renamed from: s */
    boolean isSelected();

    /* renamed from: t */
    void select();

    /* renamed from: u */
    void deselect();

    /* renamed from: v */
    int getWidth();

    /* renamed from: w */
    int getBaseHeight();

    /* renamed from: x */
    String getText();

    /* renamed from: y */
    int getCommandCount();

    /* renamed from: z */
    boolean isHighlighted();

    /* renamed from: a */
    int getCommandId(int i);

    /* renamed from: b */
    int executeCommand(int i);
}
