package com.sinyuk.jianyimaterial.feature.register;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

/**
 * Created by Sinyuk on 16.3.19.
 */
public class RegisterPresenterImpl extends BasePresenter<RegisterView>
        implements IRegisterPresenter,
        UserModel.RegisterCallback, UserModel.AuthenticateCallback {
    @Override
    public void askForAuthenticode(@NonNull String tel) {

    }

    @Override
    public void checkForAuthenticode(@NonNull String tel, @NonNull String authenticode) {
        UserModel.getInstance(mView).checkAuthenticode(tel, authenticode, this);
    }

    @Override
    public void attemptRegister(@NonNull String tel, @NonNull String password) {
        UserModel.getInstance(mView).register(tel, password, this);
        mView.hintRegisterProcessing();
    }

    @Override
    public void onRegisterSucceed() {
        mView.hintRegisterSucceed();
    }

    @Override
    public void onRegisterFailed(String message) {
        mView.hintRegisterFailed(message);
    }

    @Override
    public void onRegisterVolleyError(String message) {
        mView.hintRegisterError(message);
    }

    @Override
    public void onRegisterParseError(String message) {
        mView.hintRegisterError(message);
    }

    @Override
    public void onAuthenticateSucceed() {
        mView.hintAuthenticated();
    }

    @Override
    public void onAuthenticateFailed(String message) {

    }

    @Override
    public void onAuthenticateVolleyError(String message) {

    }

    @Override
    public void onAuthenticateParseError(String message) {

    }
}
