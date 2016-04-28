package com.sinyuk.jianyimaterial.feature.drawer;

import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;
import com.sinyuk.jianyimaterial.utils.LogUtils;

/**
 * Created by Sinyuk on 16.3.30.
 */
public class DrawerPresenterImpl extends BasePresenter<DrawerView> implements IDrawerPresenter, UserModel.QueryCurrentUserCallback {
    private static final boolean DEBUG = true;

    @Override
    public void queryCurrentUser() {
        UserModel.getInstance(mView.getContext()).queryCurrentUser(this);
        if (DEBUG) { LogUtils.simpleLog(DrawerPresenterImpl.class, "queryCurrentUser"); }
    }

    @Override
    public void onUserInfoClick() {
        if (UserModel.getInstance(mView.getContext()).isLoggedIn()) {
            mView.toPersonalView();
        } else {
            mView.toLoginView();
        }
    }

    @Override
    public void onQuerySucceed(User currentUser) {
        if (null != mView) { mView.onQuerySucceed(currentUser); }
        LogUtils.simpleLog(DrawerPresenterImpl.class, "onQuerySucceed");
    }

    @Override
    public void onQueryFailed(String message) {
        if (null != mView) { mView.onQueryFailed(message); }
        LogUtils.simpleLog(DrawerPresenterImpl.class, "onQueryFailed");
    }

    @Override
    public void onUserNotLogged() {
        if (null != mView) { mView.onUserNotLogged(); }
        LogUtils.simpleLog(DrawerPresenterImpl.class, "onUserNotLogged");
    }
}
