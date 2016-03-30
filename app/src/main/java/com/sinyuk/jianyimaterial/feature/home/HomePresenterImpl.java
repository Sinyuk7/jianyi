package com.sinyuk.jianyimaterial.feature.home;

import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.model.YihuoModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.List;

/**
 * Created by Sinyuk on 16.3.29.
 */
public class HomePresenterImpl extends BasePresenter<HomeView> implements IHomePresenter,
        YihuoModel.RequestYihuoProfileCallback {
    @Override
    public void loadBanner() {

    }

    @Override
    public void loadListHeader() {

    }

    @Override
    public void toHeaderHistory() {

    }

    @Override
    public void loadData(int pageIndex, boolean isRefresh) {
        YihuoModel.getInstance(mView).getProfile(pageIndex, this, isRefresh);
    }

    @Override
    public void toPostView() {
        if (UserModel.getInstance(mView).isLoggedIn()){
            mView.toPostView();
        }else {
            mView.hintRequestLogin();
        }
    }

    @Override
    public void onVolleyError(String message) {
        mView.hintVolleyError(message);
    }

    @Override
    public void onCompleted(List<YihuoProfile> data, boolean isRefresh) {
        mView.showList(data, isRefresh);
    }

    @Override
    public void onParseError(String message) {
        mView.hintParseError(message);
    }
}
