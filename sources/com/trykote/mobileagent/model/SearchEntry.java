package com.trykote.mobileagent.model;


public final class SearchEntry {

    public int id;

    public String query;

    public int type;

    public SearchEntry(String str, int i) {
        this.query = str;
        this.type = i;
    }
}
