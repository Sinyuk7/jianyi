package com.sinyuk.jianyimaterial.feature.profile;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.events.XFragmentReadyEvent;
import com.sinyuk.jianyimaterial.glide.BlurTransformation;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.widgets.MyCircleImageView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.4.10.
 */
public class ProfileView extends BaseActivity<ProfilePresenterImpl> implements IProfileView {
    public static final String PROFILE_TYPE = "type";
    public static final float MINE = 1;
    public static final float OTHER = 2;
    private final static String[] sTabTitles = new String[]{"物品", "需求", "喜欢"};
    @Bind(R.id.reveal_view)
    ImageView mRevealView;
    @Bind(R.id.avatar)
    MyCircleImageView mAvatar;
    @Bind(R.id.user_name_et)
    EditText mUserNameEt;
    @Bind(R.id.location_tv)
    EditText mLocationTv;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.collapse_fab)
    ImageView mCollapseFab;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;

    private float mType;
    private String mUserNameStr;
    private String mLocationStr;
    private String mTelStr;
    private String mAvatarUrlStr;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {
        configType(getIntent().getExtras());
    }

    private void configType(Bundle extras) {
        mType = extras.getFloat(PROFILE_TYPE, OTHER);
        /**别人的
         * 按钮 -> chat
         * 标题 -> 名字
         */
        if (mType == OTHER) {
            mUserNameStr = extras.getString("user_name");
            mLocationStr = extras.getString("location");
            mTelStr = extras.getString("tel");
            mAvatarUrlStr = extras.getString("avatar");
        }
    }

    @Override
    protected ProfilePresenterImpl createPresenter() {
        return new ProfilePresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.profile_view;
    }

    @Override
    protected void onFinishInflate() {
        if (mType == OTHER) {
            showAvatar(mAvatarUrlStr);
            showBackdrop(mAvatarUrlStr);
            showLocation(mLocationStr);
            showUsername(mUserNameStr);
            showToolbarTitle(mUserNameStr);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFragmentsReady(XFragmentReadyEvent event) {
        // 当fragment加载好数据之后才显示tab
        showFragments();
    }

    private void showFragments() {

    }

    private void setupViewPager() {
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return sTabTitles[position];
            }
        });
    }

    private void setupTabLayout() {
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void showUsername(@NonNull String username) {
        mUserNameEt.setText(username);
    }

    @Override
    public void showAvatar(@NonNull String url) {
        Glide.with(this).load(url)
                .dontAnimate()
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(mAvatar);
    }

    @Override
    public void showBackdrop(@NonNull String url) {
        Glide.with(this).load(url)
                .crossFade(2000)
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new BlurTransformation(this, 20, 8))
                .into(mRevealView);
    }

    @Override
    public void showLocation(@NonNull String schoolName) {
        mLocationTv.setText(schoolName);
    }

    @Override
    public void showToolbarTitle(@NonNull String username) {
        mCollapsingToolbarLayout.setTitle(username);
    }

    @Override
    public void initFragments(@NonNull String url) {
        if (mType == OTHER) {
            // new fragment
            // add to list
        } else {

        }
    }
}
