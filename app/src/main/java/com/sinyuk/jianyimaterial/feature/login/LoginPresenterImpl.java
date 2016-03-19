package com.sinyuk.jianyimaterial.feature.login;

import android.support.annotation.NonNull;

import com.android.volley.VolleyError;
import com.sinyuk.jianyimaterial.api.JLoginError;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;

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
    public void onSucceed() {
        mView.onLoginSucceed();
    }

    @Override
    public void onError(VolleyError error) {
        mView.onNetworkError(VolleyErrorHelper.getMessage(error,mView));
    }

    @Override
    public void onFailed(JLoginError error) {
        mView.onLoginFailed(error.getError_msg());
    }
}
