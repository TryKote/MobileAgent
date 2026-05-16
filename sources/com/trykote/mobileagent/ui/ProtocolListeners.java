package com.trykote.mobileagent.ui;


import com.trykote.mobileagent.core.AccountListener;
import com.trykote.mobileagent.core.AppController;
import com.trykote.mobileagent.core.ContactListListener;
import com.trykote.mobileagent.core.ContactState;
import com.trykote.mobileagent.core.MessageListener;
import com.trykote.mobileagent.core.UIState;
import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.protocol.Account;

import java.util.Vector;

public final class ProtocolListeners implements ContactListListener, AccountListener, MessageListener {

    public static final ProtocolListeners INSTANCE = new ProtocolListeners();

    private ProtocolListeners() {
    }

    // ContactListListener

    public void onContactActivated(Contact contact) {
        ContactListManager.markContactRead(contact);
    }

    public void onContactDeactivated(Contact contact) {
        ContactListManager.markContactUnread(contact);
    }

    public void onContactOnline(Contact contact) {
        NotificationHelper.playNotificationSound(NotificationHelper.SOUND_CONTACT_ONLINE);
    }

    public void onContactDeleted(Contact contact) {
        ContactListManager.deleteContact(contact);
    }

    public void onContactListUpdated(Account account) {
        AppController.needsLayoutUpdate = true;
    }

    // AccountListener

    public void onConnectionProgressChanged(Account account) {
        AppController.needsRepaint = true;
    }

    // MessageListener

    public void onMessageReceived(Contact contact, int soundType) {
        ContactState.setContactId(contact.getIdentifier());
        NotificationHelper.playNotificationSound(soundType);
        Account acct = contact.account;
        Vector tabs = UIState.getTabBars();
        for (int k = tabs.size() - 1; k >= 0; k--) {
            TabBar tabBar = (TabBar) tabs.elementAt(k);
            if (tabBar.account == acct) {
                tabBar.selectedTitle = contact.getIdentifier();
                tabBar.selectedIndex = 0;
                return;
            }
        }
    }

    public void onMessageSent(Contact contact) {
        NotificationHelper.playNotificationSound(NotificationHelper.SOUND_MESSAGE_SENT);
    }
}
