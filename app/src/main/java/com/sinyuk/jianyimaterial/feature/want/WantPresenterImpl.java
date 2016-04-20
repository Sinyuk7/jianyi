package com.sinyuk.jianyimaterial.feature.want;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

/**
 * Created by Sinyuk on 16.4.20.
 */
public class WantPresenterImpl extends BasePresenter<WantView> implements IWantPresenter, UserModel.QueryCurrentUserCallback, UserModel.PostNeedCallback {
    @Override
    public void queryCurrentUser() {
        UserModel.getInstance(mView).queryCurrentUser(this);
    }

    @Override
    public void post(@NonNull String detail, @NonNull String tel, @NonNull String price) {
        UserModel.getInstance(mView).postNeed(detail,tel,price,this);
    }


    @Override
    public void onQuerySucceed(User currentUser) {
        if (!TextUtils.isEmpty(currentUser.getTel())) {
            mView.showContactInfo(currentUser.getTel());
        }
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
    public void onPostNeedSucceed() {
        mView.onPostNeedSucceed();
    }

    @Override
    public void onPostNeedFailed(String message) {
        mView.onPostNeedFailed(message);
    }

    @Override
    public void onPostNeedVolleyError(String message) {
        mView.onPostNeedVolleyError(message);
    }

    @Override
    public void onPostNeedParseError(String message) {
        mView.onPostNeedParseError(message);
    }
}
