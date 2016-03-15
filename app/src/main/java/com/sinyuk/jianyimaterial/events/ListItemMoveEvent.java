package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.2.19.
 */
public class ListItemMoveEvent extends BaseEvent{
    private int fromPosition;
    private int toPosition;
    public ListItemMoveEvent(int fromPosition, int toPosition) {
        this.fromPosition =fromPosition;
        this.toPosition = toPosition;
    }

    public int getFromPosition() {
        return fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }


    @Override
    public String getType() {
        return "ListItemMoveEvent";
    }
}
