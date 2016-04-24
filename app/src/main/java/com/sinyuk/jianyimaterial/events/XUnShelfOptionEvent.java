package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.4.21.
 */
public class XUnShelfOptionEvent {
    private int mOption;
    private String mId;

    public XUnShelfOptionEvent(int option, String id) {
        this.mOption = option;
        this.mId = id;
    }

    public int getOption() {
        return mOption;
    }

    public String getId() {
        return mId;
    }
}
