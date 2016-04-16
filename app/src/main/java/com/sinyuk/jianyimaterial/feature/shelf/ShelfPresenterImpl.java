package com.sinyuk.jianyimaterial.feature.shelf;

import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.model.YihuoModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Sinyuk on 16.4.2.
 */
public class ShelfPresenterImpl extends BasePresenter<ShelfView> implements IShelfPresenter, YihuoModel.RequestYihuoProfileCallback {
    @Override
    public void loadData(int pageIndex, HashMap<String,String> params) {
        YihuoModel.getInstance(mView.getContext()).getProfileByParams(pageIndex, params, this);
    }

    @Override
    public void loadData(int pageIndex, String uid) {
        YihuoModel.getInstance(mView.getContext()).getProfileByUid(pageIndex, uid, this);
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
    public void onParseError(String message) {
        mView.onParseError(message);
    }
}
