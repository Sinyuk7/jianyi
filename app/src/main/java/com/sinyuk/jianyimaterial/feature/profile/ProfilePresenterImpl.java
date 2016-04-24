package com.sinyuk.jianyimaterial.feature.profile;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.entity.School;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.model.SchoolModel;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.List;

/**
 * Created by Sinyuk on 16.4.10.
 */
public class ProfilePresenterImpl extends BasePresenter<ProfileView> implements
        IProfilePresenter, UserModel.QueryCurrentUserCallback,
        SchoolModel.LoadSchoolsCallback,
        UserModel.UnShelfCallback,
        UserModel.OnShelfCallback {

    @Override
    public void queryCurrentUser() {
        UserModel.getInstance(mView).queryCurrentUser(this);
    }

    @Override
    public void fetchSchoolList() {
        SchoolModel.getInstance(mView).fetchSchools(this);
    }

    @Override
    public void unShelf(@NonNull String goodsId, @NonNull String reason) {
        UserModel.getInstance(mView).unShelf(goodsId, reason, this);
        mView.showProgressDialog("努力搬下货架ing");
    }

    @Override
    public void onShelf(@NonNull String goodsId) {
        UserModel.getInstance(mView).onShelf(goodsId, this);
        mView.showProgressDialog("努力搬上货架ing");
    }

    @Override
    public void onQuerySucceed(User currentUser) {
        mView.showToolbarTitle(currentUser.getName());
        mView.showUsername(currentUser.getName());
        mView.showLocation(currentUser.getSchool());
        mView.showAvatar(currentUser.getHeading());
        mView.showBackdrop(currentUser.getHeading());
        mView.initFragments(currentUser.getId());
    }

    @Override
    public void onQueryFailed(String message) {
        mView.onQueryFailed(message);
    }

    @Override
    public void onUserNotLogged() {
        mView.onUserNotLogged();
    }

    @Override
    public void onLoadSchoolSucceed(List<School> schoolList) {
        mView.onLoadSchoolSucceed(schoolList);
    }

    @Override
    public void onLoadSchoolParseError(String message) {
        mView.onLoadSchoolParseError(message);
    }

    @Override
    public void onLoadSchoolVolleyError(String message) {
        mView.onLoadSchoolVolleyError(message);
    }

    @Override
    public void onUnShelfSucceed() {
        mView.showSucceedDialog("");
    }

    @Override
    public void onUnShelfFailed(String message) {
        mView.showWarningDialog(message);
    }

    @Override
    public void onUnShelfVolleyError(String message) {
        mView.showErrorDialog(message);
    }

    @Override
    public void onUnShelfParseError(String message) {
        mView.showErrorDialog(message);
    }

    @Override
    public void onOnShelfSucceed() {

    }

    @Override
    public void onOnShelfFailed(String message) {

    }

    @Override
    public void onOnShelfVolleyError(String message) {

    }

    @Override
    public void onOnShelfParseError(String message) {

    }
}
