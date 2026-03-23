package com.trykote.mobileagent.core;

public final class PointerEvent extends Event {
    public static final int PRESS = 0;
    public static final int DRAG = 1;
    public static final int RELEASE = 2;
    public static final int LONG_PRESS = 3;

    public final int action;
    public final int x;
    public final int y;
    public final int startX;
    public final int startY;
    public final boolean wasDragged;

    private PointerEvent(int action, int x, int y, int startX, int startY, boolean wasDragged) {
        this.action = action;
        this.x = x;
        this.y = y;
        this.startX = startX;
        this.startY = startY;
        this.wasDragged = wasDragged;
    }

    public static PointerEvent press(int x, int y) {
        return new PointerEvent(PRESS, x, y, 0, 0, false);
    }

    public static PointerEvent drag(int x, int y, int startX, int startY) {
        return new PointerEvent(DRAG, x, y, startX, startY, false);
    }

    public static PointerEvent release(int x, int y, int startX, int startY, boolean wasDragged) {
        return new PointerEvent(RELEASE, x, y, startX, startY, wasDragged);
    }

    public static PointerEvent longPress(int x, int y) {
        return new PointerEvent(LONG_PRESS, x, y, 0, 0, false);
    }
}
