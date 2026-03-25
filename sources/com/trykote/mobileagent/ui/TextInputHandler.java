package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.StateKeys;
import com.trykote.mobileagent.protocol.xmpp.XmppContactGroup;
import com.trykote.mobileagent.util.IOUtils;
import com.trykote.mobileagent.util.StringUtils;
import com.trykote.mobileagent.util.Utils;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

public final class TextInputHandler implements CommandListener {

    private Object[] context;

    public TextInputHandler() {
    }

    public TextInputHandler(Screen screen, MenuItem menuItem) {
        Object[] objArr = (Object[]) menuItem.data;
        this.context = new Object[]{screen, menuItem};
        String str = (String) objArr[0];
        int maxLen = ((Integer) objArr[1]).intValue();
        XmppContactGroup.showTextInputDialog(AppState.emptyStr, str.length() > maxLen ? StringUtils.prefix(str, maxLen) : str, maxLen, ((Integer) objArr[2]).intValue(), (String) objArr[3], 1053, 1055, this);
    }

    public final void commandAction(Command command, Displayable displayable) {
        if (this.context == null) {
            String text = StringUtils.getTextBoxString((TextBox) displayable);
            AppState.setObject(StateKeys.SLOT_STATUS_TEXT, (Object) text);
            AppState.setBool(StateKeys.FLAG_STATUS_TEXT_SET, !StringUtils.isEmpty(text));
            if (command.getPriority() == 0) {
                IOUtils.postOkEvent();
                return;
            } else {
                IOUtils.postCancelEvent();
                return;
            }
        }
        if (command.getPriority() == 0) {
            String inputText = StringUtils.intern(((TextBox) displayable).getString());
            Screen screen = (Screen) this.context[0];
            MenuItem menuItem = (MenuItem) this.context[1];
            Object[] objArr = (Object[]) menuItem.data;
            if (!StringUtils.equalsObj(inputText, objArr[0])) {
                objArr[0] = inputText;
                String displayText = ((Integer) objArr[2]).intValue() != 327680 ? inputText : Utils.maskPassword(inputText);
                String str = StringUtils.isEmpty(displayText) ? null : displayText;
                menuItem.clear();
                if (objArr[4] instanceof String) {
                    menuItem.setLabel(Utils.appendSpace((String) objArr[4]));
                } else {
                    menuItem.setIcon(((Integer) objArr[4]).intValue());
                }
                if (str != null) {
                    menuItem.addText(str, 1, 7);
                } else {
                    menuItem.setDefaultFont();
                }
                screen.rebuildItems();
            }
        }
        AppState.setScreen(AppState.getCanvas().updateCommands());
    }
}
