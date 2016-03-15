package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.2.19.
 */
public class LocationSelectEvent extends BaseEvent {
    private int which;

    public LocationSelectEvent(int which) {
        this.which = which;
    }

    public int getWhich() {
        return which;
    }

    @Override
    public String getType() {
        return "LocationSelectEvent";
    }
}
