package com.sinyuk.jianyimaterial.feature.settings.account;

import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.HashMap;

/**
 * Created by Sinyuk on 16.4.12.
 */
public class AccountPresenterImpl extends BasePresenter<AccountView> implements IAccountPresenter, UserModel.UserUpdateCallback {

    @Override
    public void logout() {
        UserModel.getInstance(mView.getContext()).logout();
    }

    @Override
    public void update() {

    }

    @Override
    public void update(HashMap<String,String> params) {
        UserModel.getInstance(mView.getContext()).update(params,this);
    }

    @Override
    public void onUpdateSucceed(String message) {

    }

    @Override
    public void onUpdateFailed(String message) {

    }

    @Override
    public void onUpdateVolleyError(String message) {

    }

    @Override
    public void onUpdateParseError(String message) {

    }
}
