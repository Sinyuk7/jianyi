package com.sinyuk.jianyimaterial.feature.profile;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;

/**
 * Created by Sinyuk on 16.4.10.
 */
public class ProfileView extends BaseActivity<ProfilePresenterImpl> implements IProfileView {
    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected ProfilePresenterImpl createPresenter() {
        return new ProfilePresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected void onFinishInflate() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.profile_view;
    }
}
