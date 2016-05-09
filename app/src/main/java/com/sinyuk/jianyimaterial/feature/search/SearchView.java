package com.sinyuk.jianyimaterial.feature.search;

import android.view.Menu;

import com.sinyuk.jianyimaterial.R;
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
        return new SearchPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected void onFinishInflate() {

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.xsearch_view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_search, menu);
        return true;
    }
}
