package com.sinyuk.jianyimaterial.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.events.UserStateUpdateEvent;
import com.sinyuk.jianyimaterial.fragments.BlankFragment;
import com.sinyuk.jianyimaterial.fragments.UserInfoFragment;
import com.sinyuk.jianyimaterial.fragments.UserLikesFragment;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class PersonalPage extends BaseActivity {

    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;


    private List<Fragment> fragmentList = new ArrayList<>();

    public static String[] pageTitles = new String[]{
            "个人资料",
            "收藏",
            "易货",
            "需求",
    };

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {
//        avatarUrlStr = "http://ww1.sinaimg.cn/mw690/b29e155ajw8eb16z19yuxj20dc0dc3yt.jpg";
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_personal_page;
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected boolean isUsingEventBus() {
        return true;
    }

    @Override
    protected void initViews() {
        initFragments();
        setupViewPager();
        setupTabLayout();
    }

    @Override
    protected void initData() {

    }

    private void initFragments() {
        // 现在还没有数据源先用这个代替一下

        UserInfoFragment userInfoFragment = UserInfoFragment.getInstance();

        fragmentList.add(userInfoFragment);

        UserLikesFragment userLikesFragment = UserLikesFragment.getInstance();
        BlankFragment fragment2 = new BlankFragment();
        BlankFragment fragment3 = new BlankFragment();
        fragmentList.add(userLikesFragment);
        fragmentList.add(fragment2);
        fragmentList.add(fragment3);

    }


    private void setupViewPager() {
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return pageTitles.length;
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return pageTitles[position];
            }
        });
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onBackPressed() {
        EventBus.getDefault().post(new UserStateUpdateEvent(true, PreferencesUtils.getString(this, StringUtils.getRes(this, R.string.key_user_id))));
        finish();
    }
}
