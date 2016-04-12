package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.2.19.
 */
public class XLocationSelectEvent {
    private int which;

    public XLocationSelectEvent(int which) {
        this.which = which;
    }

    public int getWhich() {
        return which;
    }
}
