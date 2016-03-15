package com.sinyuk.jianyimaterial.events;

import com.sinyuk.jianyimaterial.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sinyuk on 16.2.13.
 */
public class SelectionsUpdateEvent extends BaseEvent {
    int schoolPos; String order; String childSortStr;

    public SelectionsUpdateEvent(int schoolPos, String order, String childSortStr) {
        this.schoolPos = schoolPos;
        this.order = order;
        this.childSortStr = childSortStr;
    }

    public int getSchoolPos() {
        return schoolPos;
    }

    public String getOrder() {
        return order;
    }

    public String getChildSortStr() {
        return childSortStr;
    }

    @Override
    public String getType() {
        return "SelectionsUpdateEvent";
    }

}
