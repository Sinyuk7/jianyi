package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.4.5.
 */
public class XSelectionUpdateEvent {
    private int mNewSchoolPosition;
    private int mNewOrderPosition;
    private int mNewChildSortPosition;

    public XSelectionUpdateEvent(int newSchoolPosition, int newOrderPosition, int newChildSortPosition) {
        this.mNewChildSortPosition = newChildSortPosition;
        this.mNewOrderPosition = newOrderPosition;
        this.mNewSchoolPosition = newSchoolPosition;
    }

    public int getNewChildSortPosition() {
        return mNewChildSortPosition;
    }

    public int getNewOrderPosition() {
        return mNewOrderPosition;
    }

    public int getNewSchoolPosition() {
        return mNewSchoolPosition;
    }
}

