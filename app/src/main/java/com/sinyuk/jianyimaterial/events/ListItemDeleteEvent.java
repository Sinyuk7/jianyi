package com.sinyuk.jianyimaterial.events;

import com.sinyuk.jianyimaterial.events.BaseEvent;

/**
 * Created by Sinyuk on 16.2.19.
 */
public class ListItemDeleteEvent extends BaseEvent {
    public ListItemDeleteEvent(int position) {
        this.position = position;
    }

    private int position;


    public int getPosition() {
        return position;
    }


    @Override
    public String getType() {
        return "ListItemDeleteEvent";
    }
}
