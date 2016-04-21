package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.4.21.
 */
public class XUnShelfOptionEvent {
    private int mOption;
    public XUnShelfOptionEvent(int option) {
        this.mOption = option;
    }

    public int getOption() {
        return mOption;
    }
}
