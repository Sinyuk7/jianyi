package com.sinyuk.jianyimaterial.feature.home;

import com.sinyuk.jianyimaterial.entity.Banner;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.model.BannerModel;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.model.YihuoModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.List;

/**
 * Created by Sinyuk on 16.3.29.
 */
public class HomePresenterImpl extends BasePresenter<HomeView> implements
        IHomePresenter,
        YihuoModel.RequestYihuoProfileCallback,
        BannerModel.RequestBannerCallback {
    @Override
    public void loadBanner() {
        BannerModel.getInstance(mView.getContext()).getBanner(this);
    }

    @Override
    public void loadListHeader() {

    }

    @Override
    public void toHeaderHistory() {

    }

    @Override
    public void loadData(int pageIndex) {
        YihuoModel.getInstance(mView.getContext()).getProfile(pageIndex, this);
    }

    @Override
    public void attemptToPostView() {
        if (UserModel.getInstance(mView.getContext()).isLoggedIn()) {
            mView.toPostView();
        } else {
            mView.toLoginView();
        }
    }

    @Override
    public void onVolleyError(String message) {
        mView.onVolleyError(message);
    }

    @Override
    public void onCompleted(List<YihuoProfile> data, boolean isRefresh) {
        mView.showList(data, isRefresh);
        mView.onDataLoaded();
    }

    @Override
    public void onCompleted(List<Banner> data) {
        mView.showBanner(data);
    }

    @Override
    public void onParseError(String message) {
        mView.onParseError(message);
    }
}
