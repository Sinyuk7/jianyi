package com.sinyuk.jianyimaterial.feature.explore;

import android.os.Bundle;
import android.text.TextUtils;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;

/**
 * Created by Sinyuk on 16.3.27.
 */
public class ExploreView extends BaseActivity<ExplorePresenterImpl> implements IExploreView{
    private static final java.lang.String PARENT_SORT = "title";
    private String mParentSort;
    private String mTitle;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {
        configParentSort(getIntent().getExtras());
    }

    private void configParentSort(Bundle extras) {
        mParentSort = extras.getString(PARENT_SORT);
        if (TextUtils.isEmpty(mParentSort)){
            mTitle = "易货分类";
            mParentSort = "最近上新";
        }else {
            mTitle = mParentSort;
        }
    }

    @Override
    protected ExplorePresenterImpl createPresenter() {
        return new ExplorePresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected void onFinishInflate() {
        setupToolbarTitle();
        initFragment();
        setupBottomSheet();
        setupFlowLayout();
    }

    @Override
    protected int getContentViewID() {
        return R.layout.explore_view;
    }


    private void setupToolbarTitle() {

    }

    private void initFragment() {

    }

    private void setupBottomSheet() {

    }

    private void setupFlowLayout() {

    }
}
