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
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.events.AppBarEvent;
import com.sinyuk.jianyimaterial.fragments.ExploreListFragment;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.ImeUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class ExploreActivity extends BaseActivity implements
        ViewPager.OnPageChangeListener,
        AppBarLayout.OnOffsetChangedListener {


    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    private List<ExploreListFragment> fragmentList = new ArrayList<>();
    private ExploreListFragment newArrivals;
    private ExploreListFragment hotRecommend;
    private ExploreListFragment freeNow;

    private static String[] pageTitles;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {
        pageTitles = getResources().getStringArray(R.array.explore_tab_titles);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_explore;
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

        setupAppBarLayout();

        setupViewPager();

        setupTabLayout();
    }

    private void setupAppBarLayout() {
        appBarLayout.addOnOffsetChangedListener(this);
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return pageTitles[position];
            }
        });

        viewPager.addOnPageChangeListener(this);
    }

    private void initFragments() {
        newArrivals = ExploreListFragment.newInstance("all");
        hotRecommend = ExploreListFragment.newInstance("hot");
        freeNow = ExploreListFragment.newInstance("free");

        fragmentList.add(newArrivals);
        fragmentList.add(hotRecommend);
        fragmentList.add(freeNow);
    }

    @Override
    protected void initData() {

    }


    @OnClick(R.id.fab)
    public void onClick() {
        // 如果没有登录 请求登录
        if (!PreferencesUtils.getBoolean(this, StringUtils.getRes(this, R.string.key_login_state))) {
            fab.setClickable(false);
            ObjectAnimator nopeFab = AnimUtils.nope(fab).setDuration(AnimUtils.ANIMATION_TIME_SHORT);
            final float finalFabX = fab.getX();
            final float finalFabY = fab.getY();
            nopeFab.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    SnackBarFactory.requestLogin(ExploreActivity.this, coordinatorLayout)
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
            Intent intent = new Intent(ExploreActivity.this, PostActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == ViewPager.SCROLL_STATE_IDLE) {
            if (fab.getVisibility() != View.VISIBLE) fab.show();
        } else {
            if (fab.getVisibility() == View.VISIBLE) fab.hide();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        EventBus.getDefault().post(new AppBarEvent(appBarLayout, verticalOffset));

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
