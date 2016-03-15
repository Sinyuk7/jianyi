package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.2.4.
 */
public class ScrollTopEvent extends BaseEvent{
    @Override
    public String getType() {
        return "ScrollTopEvent";
    }

    boolean smooth = true;
    public ScrollTopEvent(boolean smooth){
        this.smooth = smooth;
    }

    public ScrollTopEvent(){

    }

    public boolean isSmooth() {
        return smooth;
    }
}
