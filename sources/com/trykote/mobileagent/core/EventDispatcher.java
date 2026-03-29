package com.trykote.mobileagent.core;

import com.trykote.mobileagent.protocol.*;
import com.trykote.mobileagent.protocol.mrim.*;
import com.trykote.mobileagent.util.*;
import java.util.Vector;

public final class EventDispatcher {

    public static final void postOkEvent() {
        postEvent(CommandEvent.OK_EVENT);
    }

    public static final void postCancelEvent() {
        postEvent(CommandEvent.CANCEL_EVENT);
    }

    public static final void postSelectEvent() {
        postEvent(CommandEvent.SELECT_EVENT);
    }

    public static final void postBackEvent() {
        postEvent(CommandEvent.BACK_EVENT);
    }

    public static final void postNavigationEvent(int i, int i2, int i3) {
        postEvent(new KeyEvent(i, i2, i3));
    }

    public static final void postNotification(String str) {
        postEvent(new NotificationEvent(str));
    }

    public static final void postEvent(Object obj) {
        Vector vectorM614m = Storage.state().getVector(SessionKeys.VEC_EVENT_QUEUE);
        synchronized (vectorM614m) {
            vectorM614m.addElement(obj);
        }
    }

    public static final void postRenameError(Object[] objArr, int i) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_REMOVED_FROM_LIST)).append(objArr[2]).append(Storage.resources().getString(StringResKeys.STR_MESSAGE_SEPARATOR)).append(i)));
    }

    public static final void postAddGroupError(Object[] objArr, int i) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_TYPING_NOTIFICATION)).append(objArr[2]).append(Storage.resources().getString(StringResKeys.STR_MESSAGE_SEPARATOR)).append(i)));
    }

    public static final void postDeleteError(Object[] objArr, int i) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_ADDED_TO_LIST)).append(objArr[2]).append(Storage.resources().getString(StringResKeys.STR_MESSAGE_SEPARATOR)).append(i)));
    }

    public static final void postOperationError(int i) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_NETWORK_ERROR)).append(Storage.resources().getString(StringResKeys.STR_MESSAGE_SEPARATOR)).append(i)));
    }

    public static final void postAccountError(Account acct, int i) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_ACCOUNT_CONNECTED)).append(acct).append(Storage.resources().getString(StringResKeys.STR_ACCOUNT_SEPARATOR)).append(Storage.state().getString(i))));
    }

    public static final void postAccountMessage(Account acct, String str) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(Storage.resources().getString(StringResKeys.STR_ACCOUNT_CONNECTED)).append(acct).append(Storage.resources().getString(StringResKeys.STR_ACCOUNT_SEPARATOR)).append(str)));
    }

    public static void postAccountNotification(Account acct, String str) {
        postEvent(new AccountDataEvent(new Object[]{acct, str}));
    }

    public static final void postAccountEvent(MrimAccount account) {
        postEvent(new ProtocolEvent(ProtocolEvent.ACCOUNT_SYNC, account));
    }
}
