package com.sinyuk.jianyimaterial.events;

import com.sinyuk.jianyimaterial.model.School;

import java.util.List;

/**
 * Created by Sinyuk on 16.2.27.
 */
public class SchoolUpdateEvent extends BaseEvent{
    private final List<School> schools;

    public List<School> getSchools() {
        return schools;
    }

    public SchoolUpdateEvent(List<School> schools) {
        this.schools =schools;
    }

    @Override
    public String getType() {
        return "SchoolUpdateEvent";
    }
}
