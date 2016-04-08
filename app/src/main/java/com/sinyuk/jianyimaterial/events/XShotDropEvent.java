package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.2.19.
 */
public class XShotDropEvent {
    public XShotDropEvent(int position) {
        this.mPosition = position;
    }

    private int mPosition;
    
    public int getPosition() {
        return mPosition;
    }
}
