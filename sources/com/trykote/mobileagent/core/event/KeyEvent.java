package com.trykote.mobileagent.core.event;

public final class KeyEvent extends Event {
    public final int keyCode;
    public final int gameAction;
    public final int repeatFlag;

    public KeyEvent(int keyCode, int gameAction, int repeatFlag) {
        this.keyCode = keyCode;
        this.gameAction = gameAction;
        this.repeatFlag = repeatFlag;
    }
}
