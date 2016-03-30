package com.sinyuk.jianyimaterial.feature.drawer;

import com.sinyuk.jianyimaterial.mvp.BaseActivity;

/**
 * Created by Sinyuk on 16.3.30.
 */
public class DrawerView extends BaseActivity<DrawerPresenterImpl>{
    @Override
    protected void beforeInflate() {

    }

    @Override
    protected DrawerPresenterImpl createPresenter() {
        return null;
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected void onFinishInflate() {

    }

    @Override
    protected int getContentViewID() {
        return 0;
    }
}
