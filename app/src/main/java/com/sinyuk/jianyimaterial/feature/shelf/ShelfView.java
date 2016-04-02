package com.sinyuk.jianyimaterial.feature.shelf;

import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.mvp.BaseFragment;

import java.util.List;

/**
 * Created by Sinyuk on 16.4.2.
 */
public class ShelfView extends BaseFragment<ShelfPresenterImpl> implements IShelfView,
        AppBarLayout.OnOffsetChangedListener {
    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected void onFinishInflate() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.shelf_view;
    }

    @Override
    protected ShelfPresenterImpl createPresenter() {
        return null;
    }

    @Override
    public void refresh() {

    }

    @Override
    public void loadData(int pageIndex) {

    }

    @Override
    public void onDataLoaded() {

    }

    @Override
    public void showList(List<YihuoProfile> newPage, boolean isRefresh) {

    }

    @Override
    public void onVolleyError(@NonNull String message) {

    }

    @Override
    public void onParseError(@NonNull String message) {

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

    }
}
