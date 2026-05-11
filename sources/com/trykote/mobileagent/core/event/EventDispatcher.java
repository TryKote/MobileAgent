package com.trykote.mobileagent.core.event;

import com.trykote.mobileagent.core.AppState;
import com.trykote.mobileagent.core.ResourceAccessor;
import com.trykote.mobileagent.core.SessionState;
import com.trykote.mobileagent.key.*;
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

    public static final void postNavigationEvent(int keyCode, int gameAction, int repeatFlag) {
        postEvent(new KeyEvent(keyCode, gameAction, repeatFlag));
    }

    public static final void postNotification(String message) {
        postEvent(new NotificationEvent(message));
    }

    public static final void postEvent(Object event) {
        Vector eventQueue = SessionState.getEventQueue();
        synchronized (eventQueue) {
            eventQueue.addElement(event);
        }
    }

    public static final void postRenameError(Object[] requestData, int errorCode) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_REMOVED_FROM_LIST)).append(requestData[2]).append(ResourceAccessor.str(StringResKeys.STR_MESSAGE_SEPARATOR)).append(errorCode)));
    }

    public static final void postAddGroupError(Object[] requestData, int errorCode) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_TYPING_NOTIFICATION)).append(requestData[2]).append(ResourceAccessor.str(StringResKeys.STR_MESSAGE_SEPARATOR)).append(errorCode)));
    }

    public static final void postDeleteError(Object[] requestData, int errorCode) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_ADDED_TO_LIST)).append(requestData[2]).append(ResourceAccessor.str(StringResKeys.STR_MESSAGE_SEPARATOR)).append(errorCode)));
    }

    public static final void postOperationError(int errorCode) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_NETWORK_ERROR)).append(ResourceAccessor.str(StringResKeys.STR_MESSAGE_SEPARATOR)).append(errorCode)));
    }

    public static final void postAccountError(Account acct, int stringKey) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_ACCOUNT_CONNECTED)).append(acct).append(ResourceAccessor.str(StringResKeys.STR_ACCOUNT_SEPARATOR)).append(AppState.getString(stringKey))));
    }

    public static final void postAccountMessage(Account acct, String message) {
        postNotification(ObjectPool.toStringAndRelease(ObjectPool.newStringBuffer().append(ResourceAccessor.str(StringResKeys.STR_ACCOUNT_CONNECTED)).append(acct).append(ResourceAccessor.str(StringResKeys.STR_ACCOUNT_SEPARATOR)).append(message)));
    }

    public static void postAccountNotification(Account acct, String message) {
        postEvent(new AccountDataEvent(new Object[]{acct, message}));
    }

    public static final void postAccountEvent(MrimAccount account) {
        postEvent(new ProtocolEvent(ProtocolEvent.ACCOUNT_SYNC, account));
    }
}
