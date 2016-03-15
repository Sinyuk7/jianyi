package com.sinyuk.jianyimaterial.events;

import android.support.design.widget.AppBarLayout;

/**
 * Created by Sinyuk on 16.2.8.
 */
public class AppBarEvent extends BaseEvent{
    private  AppBarLayout appBarLayout;
    private  int verticalOffset;

    public AppBarEvent(AppBarLayout appBarLayout, int verticalOffset) {
        this.appBarLayout = appBarLayout;
        this.verticalOffset = verticalOffset;
    }

    public AppBarLayout getAppBarLayout() {
        return appBarLayout;
    }

    public int getVerticalOffset() {
        return verticalOffset;
    }

    @Override
    public String getType() {
        return "AppBarEvent";
    }
}
