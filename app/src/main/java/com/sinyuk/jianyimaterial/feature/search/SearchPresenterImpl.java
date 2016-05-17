package com.sinyuk.jianyimaterial.feature.search;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.api.Index;
import com.sinyuk.jianyimaterial.model.YihuoModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

/**
 * Created by Sinyuk on 16.4.29.
 */
public class SearchPresenterImpl extends BasePresenter<SearchingView> implements ISearchPresenter, YihuoModel.RequestYihuoProfileCallback {

    @Override
    public void loadData(@NonNull String param, @NonNull int pageIndex) {
        YihuoModel.getInstance(mView).getSearchResult(param, pageIndex, this);
        mView.setRequestDataRefresh(true);
    }

    @Override
    public void onVolleyError(String message) {
        if (null != mView) {
            mView.setRequestDataRefresh(false);
            mView.onVolleyError(message);
        }
    }

    @Override
    public void onCompleted(Index data, boolean isRefresh) {
        if (null != mView) {
            mView.showList(data, isRefresh);
            mView.setRequestDataRefresh(false);
        }
    }

    @Override
    public void onParseError(String message) {
        if (null != mView) {
            mView.setRequestDataRefresh(false);
            mView.onParseError(message);
        }
    }
}
