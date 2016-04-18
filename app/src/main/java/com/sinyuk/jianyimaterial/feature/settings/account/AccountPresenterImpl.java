package com.sinyuk.jianyimaterial.feature.settings.account;

import com.sinyuk.jianyimaterial.entity.School;
import com.sinyuk.jianyimaterial.model.SchoolModel;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Sinyuk on 16.4.12.
 */
public class AccountPresenterImpl extends BasePresenter<AccountView> implements IAccountPresenter, UserModel.UserUpdateCallback, SchoolModel.LoadSchoolsCallback {

    @Override
    public void logout() {
        UserModel.getInstance(mView.getContext()).logout();
    }


    @Override
    public void update(HashMap<String, String> params) {
        UserModel.getInstance(mView.getContext()).update(params, this);
    }

    @Override
    public void fetchSchoolList() {
        SchoolModel.getInstance(mView.getContext()).getSchools(this);
    }

    @Override
    public void onUserUpdateSucceed(String message) {
        mView.onUpdateSucceed(message);
    }

    @Override
    public void onUserUpdateFailed(String message) {
        mView.onUpdateFailed(message);
    }

    @Override
    public void onUserUpdateVolleyError(String message) {
        mView.onUpdateVolleyError(message);
    }

    @Override
    public void onUserUpdateParseError(String message) {
        mView.onUpdateParseError(message);
    }

    @Override
    public void onLoadSchoolSucceed(List<School> schoolList) {
        if (!schoolList.isEmpty()) { mView.onLoadSchoolSucceed(schoolList); }
    }

    @Override
    public void onLoadSchoolParseError(String message) {
        mView.onLoadSchoolParseError(message);
    }

    @Override
    public void onLoadSchoolVolleyError(String message) {
        mView.onLoadSchoolVolleyError(message);
    }
}
