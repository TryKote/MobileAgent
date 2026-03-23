package com.trykote.mobileagent.core;

public final class CommandEvent extends Event {
    public static final int OK = 1;
    public static final int CANCEL = 2;
    public static final int SELECT = 3;
    public static final int BACK = 4;

    public static final CommandEvent OK_EVENT = new CommandEvent(OK);
    public static final CommandEvent CANCEL_EVENT = new CommandEvent(CANCEL);
    public static final CommandEvent SELECT_EVENT = new CommandEvent(SELECT);
    public static final CommandEvent BACK_EVENT = new CommandEvent(BACK);

    public final int command;

    private CommandEvent(int command) {
        this.command = command;
    }
}
