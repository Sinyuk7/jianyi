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
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding.support.design.widget.RxAppBarLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.feature.shelf.ShelfView;
import com.sinyuk.jianyimaterial.glide.BlurTransformation;
import com.sinyuk.jianyimaterial.glide.ColorFilterTransformation;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.widgets.MyCircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16.4.10.
 */
public class ProfileView extends BaseActivity<ProfilePresenterImpl> implements IProfileView {
    public static final String PROFILE_TYPE = "type";
    public static final float MINE = 1;
    public static final float OTHER = 2;
    private final static String[] sTabTitles = new String[]{"物品", "喜欢"};
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
    @Bind(R.id.action_iv)
    ImageView mAcionIv;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.profile_header)
    RelativeLayout mProfileHeader;
    @Bind(R.id.back_iv)
    ImageView mBackIv;

    private float mType;
    private String mUid;
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
            mUid = extras.getString("uid");
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
        return false;
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
//            showToolbarTitle(mUserNameStr);
        }
        setupLayerType();
        setupToolbar();
        setAppBarLayout();
        initFragments();
        setupViewPager();
        setupTabLayout();
    }

    private void setupLayerType() {
        mAvatar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mAcionIv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mBackIv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mUserNameEt.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mLocationTv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mCompositeSubscription.add(RxView.clicks(mBackIv).subscribe(aVoid -> onBackPressed()));
    }

    private void initFragments() {
        final Bundle sellArgs = new Bundle();
        sellArgs.putString(ShelfView.CONTENT, ShelfView.MY_GOODS);
        sellArgs.putString(ShelfView.USER_ID, mUid);
        LogUtils.simpleLog(ShelfView.class, "mUid" + mUid);
        fragmentList.add(ShelfView.newInstance(sellArgs));

        final Bundle likeArgs = new Bundle();
        likeArgs.putString(ShelfView.CONTENT, ShelfView.MY_GOODS);
        likeArgs.putString(ShelfView.USER_ID, mUid);
        fragmentList.add(ShelfView.newInstance(likeArgs));
    }

    private void setAppBarLayout() {
        mCompositeSubscription.add(RxAppBarLayout.offsetChanges(mAppBarLayout)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(dy -> 1 - (-dy / (mAppBarLayout.getTotalScrollRange() / 1.5f)))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fraction -> {
                    if (fraction < 0) { fraction = 0f; }
                    mUserNameEt.setAlpha(fraction);
                    mLocationTv.setAlpha(fraction);
                    mAvatar.setScaleY(fraction);
                    mAvatar.setAlpha(fraction);
                    mAvatar.setScaleX(fraction);
                    mAcionIv.setAlpha(fraction);
                    mBackIv.setAlpha(fraction);
                }));
    }


    private void setupViewPager() {
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return 2;
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
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Glide.with(this).load(url)
                    .crossFade(2000)
                    .priority(Priority.IMMEDIATE)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new BlurTransformation(this, 20, 8))
                    .into(mRevealView);
        } else {
            Glide.with(this).load(url)
                    .crossFade(2000)
                    .priority(Priority.IMMEDIATE)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .bitmapTransform(new ColorFilterTransformation(this, getResources().getColor(R.color.profile_view_backdrop_scrim)))
                    .into(mRevealView);
        }
    }

    @Override
    public void showLocation(@NonNull String schoolName) {
        mLocationTv.setText(schoolName);
    }

    @Override
    public void showToolbarTitle(@NonNull String username) {
//        mCollapsingToolbarLayout.setTitle(username);
    }

}
