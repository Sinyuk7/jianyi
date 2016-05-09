package com.sinyuk.jianyimaterial.feature.shelf;

import com.sinyuk.jianyimaterial.api.Index;
import com.sinyuk.jianyimaterial.model.YihuoModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

import java.util.HashMap;

/**
 * Created by Sinyuk on 16.4.2.
 */
public class ShelfPresenterImpl extends BasePresenter<ShelfView> implements IShelfPresenter, YihuoModel.RequestYihuoProfileCallback {
    @Override
    public void loadData(int pageIndex, HashMap<String, String> params) {
        YihuoModel.getInstance(mView.getContext()).getProfileByParams(pageIndex, params, this);
        mView.showLoadingProgress();
    }

    @Override
    public void loadData(int pageIndex, String uid) {
        YihuoModel.getInstance(mView.getContext()).getProfileByUid(pageIndex, uid, this);
        mView.showLoadingProgress();
    }

    @Override
    public void onCompleted(Index data, boolean isRefresh) {
        if (mView != null) {
            mView.showList(data, isRefresh);
            if (data.getData().getTotal_pages() == data.getData().getCurrent()) {
                mView.reachLastPage();
            }
            mView.dismissLoadingProgress();
        }
    }

    @Override
    public void onVolleyError(String message) {
        if (mView != null) {
            mView.onVolleyError(message);
            mView.dismissLoadingProgress();
        }
    }

    @Override
    public void onParseError(String message) {
        if (mView != null) {
            mView.onParseError(message);
            mView.dismissLoadingProgress();
        }
    }
}
