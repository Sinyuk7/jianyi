package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.4.7.
 */
public class XShelfChangeEvent {
    private String mNewUrl;

    public XShelfChangeEvent(String newUrl) {
        mNewUrl = newUrl;
    }

    public String getNewUrl() {
        return mNewUrl;
    }
}
