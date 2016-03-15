package com.sinyuk.jianyimaterial.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.fragments.NeedsListFragment;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.ImeUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;

public class NeedsActivity extends BaseActivity {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.list_fragment_container)
    FrameLayout listFragmentContainer;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_needs;
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
        initFragments();
    }

    private void initFragments() {
        getSupportFragmentManager().beginTransaction().add(R.id.list_fragment_container, NeedsListFragment.getInstance()).commit();
    }

    @Override
    protected void initData() {

    }

    @OnClick(R.id.fab)
    void onFabClick(View v) {
            // 如果没有登录 请求登录
        if (!PreferencesUtils.getBoolean(this, StringUtils.getResString(this, R.string.key_login_state))) {
            fab.setClickable(false);
            ObjectAnimator nopeFab = AnimUtils.nope(fab).setDuration(AnimUtils.ANIMATION_TIME_SHORT);
            final float finalFabX = fab.getX();
            final float finalFabY = fab.getY();
            nopeFab.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    SnackBarFactory.requestLogin(NeedsActivity.this, coordinatorLayout)
                            .setCallback(new Snackbar.Callback() {
                                @Override
                                public void onDismissed(Snackbar snackbar, int event) {
                                    super.onDismissed(snackbar, event);
                                    fab.setClickable(true);

                                    fab.setX(finalFabX);// for the scroll bug a little tricky

                                    fab.setY(finalFabY);
                                }
                            }).show();
                }
            });
            nopeFab.start();
        } else {
            // if login
            Intent intent = new Intent(NeedsActivity.this, PostActivity.class);
            intent.putExtra("page_index", 1);
            startActivity(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_search, menu);
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
