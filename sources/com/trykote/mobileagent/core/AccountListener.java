package com.trykote.mobileagent.core;


import com.trykote.mobileagent.protocol.Account;

public interface AccountListener {

    void onConnectionProgressChanged(Account account);
}
