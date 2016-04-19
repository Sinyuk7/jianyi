package com.sinyuk.jianyimaterial.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.glide.BlurTransformation;
import com.sinyuk.jianyimaterial.glide.ColorFilterTransformation;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.widgets.MyCircleImageView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;

public class ProfileActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.backdrop_iv)
    ImageView backdropIv;
    @Bind(R.id.user_name_tv)
    TextView userNameTv;

    @Bind(R.id.profile_header)
    LinearLayout profileHeader;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.location_tv)
    TextView locationTv;
    @Bind(R.id.tel_tv)
    TextView telTv;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.avatar)
    MyCircleImageView avatar;
    @Bind(R.id.overflow_iv)
    ImageView overflowIv;
    @Bind(R.id.fab)
    FloatingActionButton fab;


    private String userNameStr;
    private String locationStr;
    private String telStr;
    private String avatarUrlStr;


    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {
        userNameStr = getIntent().getExtras().getString("user_name");
        locationStr = getIntent().getExtras().getString("location");
        telStr = getIntent().getExtras().getString("tel");
        avatarUrlStr = getIntent().getExtras().getString("avatar");

    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_profile;
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

        setupAppbarLayout();
        setupTextArea();
        setupViewPager();
        setupTabLayout();
    }

    @Override
    protected void initData() {

    }

    private void setupTextArea() {
        userNameTv.setText(
                StringUtils.check(this, userNameStr, R.string.unknown_user_name));
        locationTv.setText(
                StringUtils.check(this, locationStr, R.string.unknown_location));
        telTv.setText(
                StringUtils.check(this, telStr, R.string.unknown_tel));

    }


    private void setupAppbarLayout() {
        // TODO 判断性别
        DrawableRequestBuilder backdropRequest = Glide.with(this).fromString()
                .crossFade(500)
                .priority(Priority.HIGH);
        DrawableRequestBuilder avatarRequest = Glide.with(this).fromString()
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .bitmapTransform(new CropCircleTransformation(this)).priority(Priority.IMMEDIATE);

        avatarRequest.diskCacheStrategy(DiskCacheStrategy.RESULT).load(avatarUrlStr)
                .error(R.drawable.ic_avatar_placeholder)
                .into(avatar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            backdropRequest.bitmapTransform(new BlurTransformation(this)).load(avatarUrlStr).into(backdropIv);
        }else {
            backdropRequest.bitmapTransform(new ColorFilterTransformation(this,getResources().getColor(R.color.colorPrimary_50pct)))
                    .load(avatarUrlStr).into(backdropIv);
        }

    }


    private void setupViewPager() {
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 0 ? "TA的易货" : "TA的需求";
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final View hintView = View.inflate(ProfileActivity.this, R.layout.hint_left_nothing_here, null);
                container.addView(hintView);
                return hintView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        });
    }

    private void setupTabLayout() {
        tabLayout.setupWithViewPager(viewPager);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds needList to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_profile, menu);
        return true;
    }

    @OnClick({R.id.avatar, R.id.overflow_iv, R.id.fab})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                startPhotoView();
                break;
            case R.id.overflow_iv:
                break;
            case R.id.fab:
                break;
        }
    }

    private void startPhotoView() {
        if (avatarUrlStr == null) return;

        final ArrayList<String> list = new ArrayList<>();
        list.add(avatarUrlStr);
        Intent intent = new Intent(ProfileActivity.this, PhotoViewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("shot_urls", list);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
