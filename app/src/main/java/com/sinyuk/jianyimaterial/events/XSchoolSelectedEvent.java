package com.sinyuk.jianyimaterial.events;

/**
 * Created by Sinyuk on 16.4.18.
 */
public class XSchoolSelectedEvent {
    private String mSchoolIndex;
    private String mSchoolName;
    public XSchoolSelectedEvent(String schoolIndex ,String schoolName) {
        this.mSchoolIndex = schoolIndex;
        this.mSchoolName = schoolName;
    }

    public String getSchoolIndex() {
        return mSchoolIndex;
    }

    public String getSchoolName() {
        return mSchoolName;
    }
}
