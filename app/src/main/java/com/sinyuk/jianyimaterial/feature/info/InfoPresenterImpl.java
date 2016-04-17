package com.sinyuk.jianyimaterial.feature.info;

import android.text.TextUtils;

import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.HashMap;

/**
 * Created by Sinyuk on 16.4.16.
 */
public class InfoPresenterImpl extends BasePresenter<InfoView> implements IInfoPresenter, UserModel.QueryCurrentUserCallback {
    @Override
    public void compressThenUpload(String uri) {

    }

    @Override
    public void loadUserInfo() {
        UserModel.getInstance(mView).queryCurrentUser(this);
    }

    @Override
    public void updateUser(HashMap<String, String> params) {

    }

    @Override
    public void onQuerySucceed(User currentUser) {
        if (!TextUtils.isEmpty(currentUser.getHeading())) {
            mView.showUserAvatar(currentUser.getHeading());
        }
        if (!TextUtils.isEmpty(currentUser.getName())) {
            mView.showUserNickname(currentUser.getName());
        }

        if (!TextUtils.isEmpty(currentUser.getSchool())) {
            mView.showUserSchool(currentUser.getSchool());
        }
        mView.dismissProgressDialog();
    }

    @Override
    public void onQueryFailed(String message) {
        mView.dismissProgressDialog();
        mView.onQueryFailed(message);
    }

    @Override
    public void onUserNotLogged() {
        mView.dismissProgressDialog();
        mView.onUserNotLogged();
    }
}
