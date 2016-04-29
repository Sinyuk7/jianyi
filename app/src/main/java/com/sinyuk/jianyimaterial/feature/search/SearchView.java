package com.sinyuk.jianyimaterial.feature.search;

import com.sinyuk.jianyimaterial.mvp.BaseActivity;

/**
 * Created by Sinyuk on 16.4.29.
 */
public class SearchView extends BaseActivity<SearchPresenterImpl> implements ISearchView{
    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected SearchPresenterImpl createPresenter() {
        return null;
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected void onFinishInflate() {

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentViewID() {
        return 0;
    }
}
