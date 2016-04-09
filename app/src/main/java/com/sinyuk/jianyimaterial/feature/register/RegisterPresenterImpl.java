package com.sinyuk.jianyimaterial.feature.register;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.feature.login.ILoginPresenter;
import com.sinyuk.jianyimaterial.feature.login.LoginView;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

/**
 * Created by Sinyuk on 16.3.19.
 */
public class RegisterPresenterImpl extends BasePresenter<RegisterView>
        implements IRegisterPresenter,
        UserModel.RegisterCallback{
    @Override
    public void askForAuthenticode(@NonNull String tel) {

    }

    @Override
    public void attemptRegister(@NonNull String tel, @NonNull String password) {

    }
}
