package com.sinyuk.jianyimaterial.feature.register;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

/**
 * Created by Sinyuk on 16.3.19.
 */
public class RegisterPresenterImpl extends BasePresenter<RegisterView>
        implements IRegisterPresenter,
        UserModel.RegisterCallback {
    @Override
    public void askForAuthenticode(@NonNull String tel) {

    }

    @Override
    public void checkForAuthenticode(@NonNull String tel, @NonNull String authenticode) {

    }

    @Override
    public void attemptRegister(@NonNull String tel, @NonNull String password) {
        UserModel.getInstance(mView).register(tel, password, this);
        mView.hintRegisterProcessing();
    }

    @Override
    public void onSucceed() {
        mView.hintRegisterCompleted();
        mView.hintRegisterSucceed();
    }

    @Override
    public void onFailed(String message) {
        mView.hintRegisterCompleted();
        mView.hintRegisterFailed(message);
    }

    @Override
    public void onVolleyError(String message) {
        mView.hintRegisterCompleted();
        mView.hintRegisterError(message);
    }

    @Override
    public void onParseError(String message) {
        mView.hintRegisterCompleted();
        mView.hintRegisterError(message);
    }
}
