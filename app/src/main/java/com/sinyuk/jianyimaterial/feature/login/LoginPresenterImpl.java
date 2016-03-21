package com.sinyuk.jianyimaterial.feature.login;



import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

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
    public void onError(String message) {
        mView.onNetworkError(message);
    }

    @Override
    public void onFailed(String message) {
        mView.onLoginFailed(message);
    }
}
