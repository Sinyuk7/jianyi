package com.sinyuk.jianyimaterial.feature;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.CategoryMenuAdapter;
import com.sinyuk.jianyimaterial.common.BaseActivity;
import com.sinyuk.jianyimaterial.feature.explore.ExploreView;
import com.sinyuk.jianyimaterial.feature.search.SearchingView;
import com.sinyuk.jianyimaterial.utils.LogUtils;

import butterknife.Bind;
import butterknife.OnClick;

public class CategoryView extends BaseActivity implements CategoryMenuAdapter.OnCategoryMenuItemClickListener {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.category_view;
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected boolean isUsingEventBus() {
        return false;
    }

    @Override
    protected void initViews() {
        setupRecyclerView();
    }

    @Override
    protected void initData() {

    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        CategoryMenuAdapter adapter = new CategoryMenuAdapter(this);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(gridLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.setOnCategoryMenuItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onCategoryMenuItemClick(View view, int position) {
        Intent intent = new Intent();
        intent.setClass(this, ExploreView.class);
        intent.putExtra(ExploreView.CATEGORY, position);
        LogUtils.simpleLog(CategoryView.class, "position->" + position);
        intent.putExtra(ExploreView.ENABLE_FILTER, true);
        intent.putExtra(ExploreView.ENABLE_SCHOOL, true);
        intent.putExtra(ExploreView.ENABLE_ORDER, true);
        intent.putExtra(ExploreView.ENABLE_CHILD_SORT, true);
        startActivity(intent);
    }

    @OnClick(R.id.search_button)
    public void toSearchView() {
        startActivity(new Intent(CategoryView.this, SearchingView.class));
    }

}
