package com.sinyuk.jianyimaterial.features.login;

import android.support.annotation.NonNull;

import com.android.volley.VolleyError;
import com.sinyuk.jianyimaterial.api.JResponse;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

/**
 * Created by Sinyuk on 16.3.16.
 */
public class LoginPresenterImpl extends BasePresenter<LoginView, UserModel>
        implements ILoginPresenter,
        UserModel.LoginCallback {

    private UserModel userModel;
    private LoginView loginView;


    @Override
    public void attemptLogin(@NonNull String userName, @NonNull String password) {

        UserModel.getInstance(loginView).login(userName, password, this);
    }

    @Override
    public void onSucceed() {
        loginView.onLoginSucceed();
    }

    @Override
    public void onError(VolleyError error) {
        loginView.onNetworkError(VolleyErrorHelper.getMessage(error,loginView));
    }

    @Override
    public void onFailed(JResponse error) {
        loginView.onLoginFailed(error.getData());
    }
}
