package com.sinyuk.jianyimaterial.feature.drawer;

import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

/**
 * Created by Sinyuk on 16.3.30.
 */
public class DrawerPresenterImpl extends BasePresenter<DrawerView> implements IDrawerPresenter {
    @Override
    public boolean configLoginState() {
        return false;
    }

    @Override
    public void loadUserInfo() {

    }

    @Override
    public void onUserInfoClick() {
        if (UserModel.getInstance(mView.getContext()).isLoggedIn()) {
            mView.toPersonalView();
        } else {
            mView.toLoginView();
        }
    }
}
