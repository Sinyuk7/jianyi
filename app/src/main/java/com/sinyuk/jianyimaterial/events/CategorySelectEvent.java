package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.2.19.
 */
public class CategorySelectEvent extends BaseEvent{
    private int which;
    public CategorySelectEvent(int which) {
    this.which = which;
    }

    public int getWhich() {
        return which;
    }

    @Override
    public String getType() {
        return "CategorySelectEvent";
    }
}
