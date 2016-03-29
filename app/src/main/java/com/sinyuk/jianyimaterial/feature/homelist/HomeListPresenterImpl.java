package com.sinyuk.jianyimaterial.feature.homelist;

import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.model.YihuoModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.List;

/**
 * Created by Sinyuk on 16.3.29.
 */
public class HomeListPresenterImpl extends BasePresenter<HomeListView> implements IHomeListPresenter,
        YihuoModel.RequestYihuoProfileCallback {
    @Override
    public void loadListHeader() {

    }

    @Override
    public void toHeaderHistory() {

    }

    @Override
    public void loadData(int pageIndex, boolean isRefresh) {
        YihuoModel.getInstance(mView.getContext()).getProfile(pageIndex, this, isRefresh);
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
