package com.sinyuk.jianyimaterial.feature.login;


import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.events.XLoginEvent;
import com.sinyuk.jianyimaterial.events.XLogoutEvent;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Sinyuk on 16.3.16.
 */
public class LoginPresenterImpl extends BasePresenter<LoginView>
        implements ILoginPresenter,
        UserModel.LoginCallback {

    @Override
    public void attemptLogin(@NonNull String userName, @NonNull String password) {
        UserModel.getInstance(mView).login(userName, password, this);
    }

    @Override
    public void onLoginSucceed() {
        mView.onLoginSucceed();
        EventBus.getDefault().post(new XLoginEvent());
    }

    @Override
    public void onLoginError(String message) {
        mView.onNetworkError(message);
    }

    @Override
    public void onLoginFailed(String message) {
        mView.onLoginFailed(message);
    }
}
