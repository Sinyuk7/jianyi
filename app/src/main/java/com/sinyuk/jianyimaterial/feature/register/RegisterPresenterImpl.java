package com.sinyuk.jianyimaterial.feature.register;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.entity.School;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.model.SchoolModel;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.List;

/**
 * Created by Sinyuk on 16.3.19.
 */
public class RegisterPresenterImpl extends BasePresenter<RegisterView>
        implements IRegisterPresenter,
        UserModel.RegisterCallback, UserModel.AuthenticateCallback, SchoolModel.LoadSchoolsCallback {
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
        mView.showProgressDialog();
    }

    @Override
    public void fetchSchools() {
        SchoolModel.getInstance(mView).fetchSchools(this);
    }

    @Override
    public void onRegisterSucceed(User user) {
        mView.showSucceedDialog(user);
    }

    @Override
    public void onRegisterFailed(String message) {
        mView.showWarningDialog(message);
    }

    @Override
    public void onRegisterVolleyError(String message) {
        mView.showErrorDialog(message);
    }

    @Override
    public void onRegisterParseError(String message) {
        mView.showErrorDialog(message);
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

    @Override
    public void onLoadSchoolSucceed(List<School> schoolList) {
        mView.onLoadSchoolSucceed(schoolList);
    }

    @Override
    public void onLoadSchoolParseError(String message) {

    }

    @Override
    public void onLoadSchoolVolleyError(String message) {

    }
}
