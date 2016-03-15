package com.sinyuk.jianyimaterial.activities;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.CategoryMenuAdapter;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.utils.ImeUtils;

import butterknife.Bind;

public class CategoryMenu extends BaseActivity implements CategoryMenuAdapter.OnCategoryMenuItemClickListener {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_category_menu;
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
        intent.setClass(this, CategoryPage.class);

        intent.putExtra("parent_sort_index", position);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_search, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, SearchActivity.class)));
        searchView.setQueryRefinementEnabled(true); //Query refinement for search suggestions
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchView != null) {
                    // 得到输入管理对象
                    ImeUtils.hideIme(searchView);
                    searchView.setIconified(true);
                    searchView.clearFocus();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

}
