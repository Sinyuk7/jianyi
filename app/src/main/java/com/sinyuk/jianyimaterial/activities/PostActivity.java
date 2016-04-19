package com.sinyuk.jianyimaterial.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.fragments.PostFeedFragment;
import com.sinyuk.jianyimaterial.fragments.PostNeedFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class PostActivity extends BaseActivity {

    @Bind(R.id.tab_layout)
    TabLayout tabLayout;

    @Bind(R.id.view_pager)
    ViewPager viewPager;


    private List<Fragment> fragmentList = new ArrayList<>();
    private static String[] pageTitles = new String[]{
            "物品", "需求"
    };
    private int startPage = 0;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {
        startPage = getIntent().getIntExtra("page_index", 0);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_post;
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
        setupViewPager();
        setupTabLayout();
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state != ViewPager.SCROLL_STATE_IDLE)
                    hideSoftInput();
            }
        });
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return pageTitles[position];
            }
        });

        viewPager.setCurrentItem(startPage);
    }

    private void initFragments() {
        fragmentList.add(PostFeedFragment.getInstance());
        fragmentList.add(PostNeedFragment.getInstance());
    }

    @Override
    protected void initData() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds needList to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_post)
            return false;
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    private void hideSoftInput() {
        // TODO: 在登录的时候关闭软键盘
        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isActive()) {

            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
