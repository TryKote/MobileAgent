package com.trykote.mobileagent.core;

public final class TransitionData {
    public int sourceScreenId;
    public Object entity;
    public String text;
    public int intParam;

    public void clear() {
        sourceScreenId = 0;
        entity = null;
        text = null;
        intParam = 0;
    }

    public static TransitionData get() {
        return UIState.getTransitionData();
    }
}
