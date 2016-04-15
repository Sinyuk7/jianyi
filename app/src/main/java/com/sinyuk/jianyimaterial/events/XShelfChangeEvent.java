package com.sinyuk.jianyimaterial.events;

import java.util.HashMap;

/**
 * Created by Sinyuk on 16.4.7.
 */
public class XShelfChangeEvent {
    private HashMap<String,String> mNewParams;

    public XShelfChangeEvent(HashMap<String,String> newParams) {
        mNewParams = newParams;
    }

    public HashMap<String,String> getNewParams() {
        return mNewParams;
    }
}
