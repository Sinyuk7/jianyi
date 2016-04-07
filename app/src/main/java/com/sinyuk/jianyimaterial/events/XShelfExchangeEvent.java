package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.4.7.
 */
public class XShelfExchangeEvent {
    private String mNewUrl;

    public XShelfExchangeEvent(String newUrl) {
        mNewUrl = newUrl;
    }

    public String getNewUrl() {
        return mNewUrl;
    }
}
