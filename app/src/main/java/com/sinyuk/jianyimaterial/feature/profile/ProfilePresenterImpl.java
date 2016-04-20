package com.sinyuk.jianyimaterial.feature.profile;

import com.sinyuk.jianyimaterial.entity.School;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.model.SchoolModel;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.List;

/**
 * Created by Sinyuk on 16.4.10.
 */
public class ProfilePresenterImpl extends BasePresenter<ProfileView> implements IProfilePresenter, UserModel.QueryCurrentUserCallback, SchoolModel.LoadSchoolsCallback {

    @Override
    public void queryCurrentUser() {
        UserModel.getInstance(mView).queryCurrentUser(this);
    }

    @Override
    public void fetchSchoolList() {
        SchoolModel.getInstance(mView).fetchSchools(this);
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
}
