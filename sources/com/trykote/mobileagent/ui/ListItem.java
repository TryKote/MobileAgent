package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.*;
import com.trykote.mobileagent.key.*;
import com.trykote.mobileagent.ui.*;
import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.protocol.mmp.*;
import com.trykote.mobileagent.protocol.xmpp.*;
import com.trykote.mobileagent.map.*;
import com.trykote.mobileagent.net.*;
import com.trykote.mobileagent.util.*;

public interface ListItem {
    int getHeight();
    boolean isSelected();
    void select();
    void deselect();
    int getWidth();
    int getBaseHeight();
    String getText();
    int getCommandCount();
    boolean isHighlighted();
    int getCommandId(int index);
    int executeCommand(int index);
}
