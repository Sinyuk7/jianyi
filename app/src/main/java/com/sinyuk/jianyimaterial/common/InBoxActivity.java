package com.sinyuk.jianyimaterial.common;

import android.os.Bundle;

/**
 * Created by Sinyuk on 16.5.18.
 */
public class InBoxActivity extends BaseActivity{
    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {

    }

    @Override
    protected int getContentViewID() {
        return 0;
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected boolean isUsingEventBus() {
        return false;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initData() {

    }
}
