package com.sinyuk.jianyimaterial.feature.settings;

import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;
import com.sinyuk.jianyimaterial.utils.LogUtils;

/**
 * Created by Sinyuk on 16.4.12.
 */
public class SettingsPresenterImpl extends BasePresenter<SettingsView> implements ISettingsPresenter, UserModel.QueryCurrentUserCallback {
    public static final boolean DEBUG = true;

    @Override
    public void queryCurrentUser() {
        UserModel.getInstance(mView).queryCurrentUser(this);
        if (DEBUG) {LogUtils.simpleLog(SettingsPresenterImpl.class, "queryCurrentUser");}
    }

    @Override
    public void onQuerySucceed(User currentUser) {
        mView.onQuerySucceed(currentUser);
        if (DEBUG) {LogUtils.simpleLog(SettingsPresenterImpl.class, "onQuerySucceed");}
    }

    @Override
    public void onQueryFailed(String message) {
        mView.onQueryFailed(message);
        if (DEBUG) {LogUtils.simpleLog(SettingsPresenterImpl.class, "onQueryFailed");}
    }

    @Override
    public void onUserNotLogged() {
        mView.onUserNotLogged();
        if (DEBUG) {LogUtils.simpleLog(SettingsPresenterImpl.class, "onUserNotLogged");}
    }
}
