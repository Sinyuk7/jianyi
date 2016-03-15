package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.2.4.
 */
public class RefreshDataEvent extends BaseEvent {
    @Override
    public String getType() {
        return "RefreshDataEvent";
    }

    boolean clean;

    public RefreshDataEvent(boolean clean) {
        this.clean = clean;
    }

    public boolean isClean() {
        return clean;
    }
}
