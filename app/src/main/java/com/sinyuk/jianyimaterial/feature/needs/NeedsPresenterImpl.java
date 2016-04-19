package com.sinyuk.jianyimaterial.feature.needs;

import android.support.annotation.NonNull;

import com.sinyuk.jianyimaterial.api.JNeed;
import com.sinyuk.jianyimaterial.model.NeedsModel;
import com.sinyuk.jianyimaterial.mvp.BasePresenter;

/**
 * Created by Sinyuk on 16.4.19.
 */
public class NeedsPresenterImpl extends BasePresenter<NeedsView> implements INeedsPresenter, NeedsModel.NeedsLoadCallback {
    @Override
    public void loadData(int pageIndex) {
        NeedsModel.getInstance(mView).load(null,pageIndex,this);
        mView.showRefreshProgress();
    }


    @Override
    public void onNeedsLoadSucceed(@NonNull JNeed data, @NonNull boolean isRefresh) {
        mView.dismissRefreshProgress();
        mView.showList(data,isRefresh);
    }

    @Override
    public void onNeedsVolleyError(@NonNull String message) {
        mView.dismissRefreshProgress();
        mView.onNeedsVolleyError(message);
    }

    @Override
    public void onNeedsParseError(@NonNull String message) {
        mView.dismissRefreshProgress();
        mView.onNeedsParseError(message);
    }

    @Override
    public void onNeedsReachBottom() {
        mView.dismissRefreshProgress();
        mView.onNeedsReachBottom();
    }
}
