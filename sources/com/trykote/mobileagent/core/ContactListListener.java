package com.trykote.mobileagent.core;


import com.trykote.mobileagent.model.Contact;
import com.trykote.mobileagent.protocol.Account;

public interface ContactListListener {

    void onContactActivated(Contact contact);

    void onContactDeactivated(Contact contact);

    void onContactOnline(Contact contact);

    void onContactDeleted(Contact contact);

    void onContactListUpdated(Account account);
}
