package com.sinyuk.jianyimaterial.feature.settings.account;

import com.sinyuk.jianyimaterial.mvp.BaseFragment;

/**
 * Created by Sinyuk on 16.4.12.
 */
public class AccountView extends BaseFragment<AccountPresenterImpl> implements IAccountPresenter{
    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected void onFinishInflate() {

    }

    @Override
    protected int getContentViewID() {
        return 0;
    }

    @Override
    protected AccountPresenterImpl createPresenter() {
        return null;
    }
}
