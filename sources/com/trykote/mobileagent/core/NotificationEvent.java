package com.trykote.mobileagent.core;

public final class NotificationEvent extends Event {
    public final String message;

    public NotificationEvent(String message) {
        this.message = message;
    }
}
