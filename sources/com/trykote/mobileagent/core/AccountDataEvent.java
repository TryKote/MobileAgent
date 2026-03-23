package com.trykote.mobileagent.core;

public final class AccountDataEvent extends Event {
    public final Object[] data;

    public AccountDataEvent(Object[] data) {
        this.data = data;
    }
}
