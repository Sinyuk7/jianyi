package com.sinyuk.jianyimaterial.feature.register;

import com.sinyuk.jianyimaterial.mvp.BaseActivity;

/**
 * Created by Sinyuk on 16.3.19.
 */
public class RegisterView extends BaseActivity<RegisterPresenterImpl> implements IRegisterView{

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected RegisterPresenterImpl createPresenter() {
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
