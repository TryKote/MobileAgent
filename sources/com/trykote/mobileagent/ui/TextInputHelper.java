package com.trykote.mobileagent.ui;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.StringPool;
import com.trykote.mobileagent.core.RuntimeState;
import com.trykote.mobileagent.core.SettingsState;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.key.StringResKeys;
import com.trykote.mobileagent.util.StringUtils;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.TextBox;

public final class TextInputHelper {

    public static void showTextInputDialog(String title, String initialText, int maxLength, int constraints, String inputMode, int okLabelKey, int cancelLabelKey, CommandListener commandListener) {
        if (initialText != null && initialText.length() > maxLength) {
            initialText = StringUtils.prefix(initialText, maxLength);
        }
        try {
            if (!StringUtils.isKnownDevice1) {
                throw new RuntimeException();
            }
            TextBox textBox = getTextInputBox();
            textBox.setTitle(AppState.emptyStr);
            textBox.setString(AppState.emptyStr);
            textBox.setCommandListener((CommandListener) null);
            textBox.setConstraints(constraints);
            textBox.setTitle(title);
            if (initialText != null) {
                textBox.setString(initialText);
            }
            textBox.setMaxSize(maxLength);
            textBox.setInitialInputMode((String) null);
        } catch (Throwable unused) {
            UIState.setTextBox(new TextBox(title, initialText, maxLength, constraints));
        }
        removePrimaryCommand();
        removeSecondaryCommand();
        try {
            TextBox textBox2 = getTextInputBox();
            if (StringUtils.matchesKey(424, inputMode)) {
                int fontSizeSetting = SettingsState.getFontSizeList();
                if (fontSizeSetting == 1) {
                    textBox2.setInitialInputMode(StringPool.get(StringResKeys.STR_INPUT_MODE_NUMERIC));
                } else if (fontSizeSetting == 2) {
                    textBox2.setInitialInputMode(StringPool.get(StringResKeys.STR_INPUT_MODE_LATIN));
                }
            } else {
                textBox2.setInitialInputMode(inputMode);
            }
        } catch (Throwable unused2) {
        }
        RuntimeState.setXmppCommandIndex(okLabelKey);
        Command command = new Command(AppState.getString(okLabelKey), SettingsState.isFullscreen() ? 2 : 4, 0);
        removePrimaryCommand();
        getTextInputBox().addCommand(command);
        RuntimeState.setXmppCommand1(command);
        setCommandLabel(cancelLabelKey);
        getTextInputBox().setCommandListener(commandListener);
        AppState.setScreen(getTextInputBox());
    }

    public static String getTextInputValue() {
        try {
            return com.trykote.mobileagent.util.Utils.defaultStr(StringUtils.intern(getTextInputBox().getString()));
        } catch (Throwable unused) {
            return AppState.emptyStr;
        }
    }

    public static void setTextInputScreen(int selectionIndex, int labelKey) {
        if (RuntimeState.getXmppSelectionIndex() == selectionIndex) {
            setCommandLabel(labelKey);
            AppState.setScreen(getTextInputBox());
        }
    }

    public static TextBox getTextInputBox() {
        return (TextBox) UIState.getTextBox();
    }

    private static void removePrimaryCommand() {
        Command command = (Command) RuntimeState.getXmppCommand1();
        if (command != null) {
            getTextInputBox().removeCommand(command);
        }
        RuntimeState.clearXmppCommand1();
    }

    private static void removeSecondaryCommand() {
        Command command = (Command) RuntimeState.getXmppCommand2();
        if (command != null) {
            getTextInputBox().removeCommand(command);
        }
        RuntimeState.clearXmppCommand2();
    }

    private static void setCommandLabel(int labelKey) {
        RuntimeState.setXmppSelectionIndex(labelKey);
        Command command = new Command(AppState.getString(labelKey), SettingsState.isFullscreen() ? 4 : 2, 1);
        removeSecondaryCommand();
        getTextInputBox().addCommand(command);
        RuntimeState.setXmppCommand2(command);
    }
}
