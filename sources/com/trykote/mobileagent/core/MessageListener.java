package com.trykote.mobileagent.core;


import com.trykote.mobileagent.model.Contact;

public interface MessageListener {

    // Sound type constants (match NotificationHelper values)
    int SOUND_MESSAGE_RECEIVED = 2;
    int SOUND_CONVERSATION_MESSAGE = 3;
    int SOUND_MESSAGE_SENT = 4;

    void onMessageReceived(Contact contact, int soundType);

    void onMessageSent(Contact contact);
}
