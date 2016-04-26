package com.sinyuk.jianyimaterial.common;

import android.os.Bundle;

import com.sinyuk.jianyimaterial.R;

/**
 * Created by Sinyuk on 16.4.26.
 */
public class WebViewActivity extends BaseActivity{
    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.web_view;
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
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
