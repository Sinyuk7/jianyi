package com.sinyuk.jianyimaterial.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.events.AppBarEvent;
import com.sinyuk.jianyimaterial.events.UserStateUpdateEvent;
import com.sinyuk.jianyimaterial.feature.CategoryView;
import com.sinyuk.jianyimaterial.fragments.HomeListFragment;
import com.sinyuk.jianyimaterial.fragments.HomeTopAreaFragment;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.UserService;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.ImeUtils;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.widgets.MyCircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;
import rx.subscriptions.CompositeSubscription;


public class HomeActivity extends BaseActivity implements
        AppBarLayout.OnOffsetChangedListener {
    public static final boolean DEBUG = false;


    public static final String TAG = "HomeActivity";
    private static final long DRAWER_CLOSE_DELAY = 200;
    public static final int REQUEST_CODE_PERSONAL_PAGE = 1001;
    public static final int REQUEST_USER_DATA = 1002;
    @Nullable
    @Bind(R.id.top_fragment_container)
    FrameLayout topFragmentContainer;
    @Bind(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Nullable
    @Bind(R.id.list_fragment_container)
    FrameLayout listFragmentContainer;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.nav_view)
    NavigationView navView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private FragmentManager fragmentManager;
    private UserService userService;
    private long attemptExitTime = 0;
    private SearchView searchView;

    private CompositeSubscription mSubscription;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {

        mSubscription = new CompositeSubscription();
        userService = DaoUtils.getUserService();

    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_home;
    }

    @Override
    protected boolean isUsingEventBus() {
        return true;
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected void initViews() {

        initFragment();
        initAppBarLayout();
        setupToolbar();

        setupNavigationView();
        //        if (isUserLogin != )
        // TODO: 用户登录信息发生变化时 更新这个
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                updateDrawerHeader();
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    @Override
    protected void initData() {

    }

    private void initFragment() {
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.top_fragment_container, HomeTopAreaFragment.getInstance()).commit();
        fragmentManager.beginTransaction().replace(R.id.list_fragment_container, HomeListFragment.getInstance()).commit();

    }

    private void initAppBarLayout() {
        appBarLayout.addOnOffsetChangedListener(this);
    }


    private void updateDrawerHeader() {
        View headerLayout = navView.getHeaderView(0);
        if (headerLayout == null)
            return;

        final ImageView backdropIv = (ImageView) headerLayout.findViewById(R.id.backdrop_iv);
        final MyCircleImageView avatar = (MyCircleImageView) headerLayout.findViewById(R.id.avatar);
        final TextView userNameTv = (TextView) headerLayout.findViewById(R.id.user_name_tv);
        final TextView locationTv = (TextView) headerLayout.findViewById(R.id.location_tv);

       /* if (UserModel.getInstance(this).isLoggedIn()) {
            if (UserModel.getInstance(this).queryCurrentUser() == null) {
                ToastUtils.toastSlow(mContext, "登陆失败");

                Glide.with(mContext).load(R.drawable.backdrop_2).into(backdropIv);
                Glide.with(mContext).load(R.drawable.ic_avatar_placeholder).into(avatar);

                userNameTv.setText(StringUtils.getRes(mContext, R.string.hint_click_to_login));
                locationTv.setText(null);
            } else {
                final User user = UserModel.getInstance(this).queryCurrentUser();
                DrawableRequestBuilder<String> requestBuilder;
                requestBuilder = Glide.with(mContext).fromString().diskCacheStrategy(DiskCacheStrategy.RESULT);
                requestBuilder.load(user.getHeading()).bitmapTransform(new CropCircleTransformation(mContext)).crossFade()
                        .thumbnail(0.2f).error(R.drawable.ic_avatar_placeholder).priority(Priority.IMMEDIATE).into(avatar);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    requestBuilder.load(user.getHeading()).bitmapTransform(new BlurTransformation(mContext))
                            .crossFade().priority(Priority.HIGH).error(R.drawable.backdrop_2).thumbnail(0.5f).into(backdropIv);
                } else {
                    requestBuilder.load(user.getHeading()).bitmapTransform(new ColorFilterTransformation(mContext, getResources().getColor(R.color.colorPrimary_50pct)))
                            .crossFade().priority(Priority.HIGH).error(R.drawable.backdrop_2).thumbnail(0.5f).into(backdropIv);
                }
                userNameTv.setText(
                        StringUtils.check(mContext, user.getName(), R.string.unknown_user_name));
                final int index = Integer.parseInt(user.getSchool()) - 1;
                if (index >= 0 && index < getResources().getStringArray(R.array.schools_sort).length)
                    locationTv.setText(StringUtils.check(mContext, getResources().getStringArray(R.array.schools_sort)[index], R.string.untable));

            }
            *//**
             * TODO: 设置学校 这个要动态更新啊
             *//*

        } else {
            //  Logout State
            Glide.with(mContext).load(R.drawable.backdrop_2).into(backdropIv);
            Glide.with(mContext).load(R.drawable.ic_avatar_placeholder).into(avatar);
            userNameTv.setText(StringUtils.getRes(mContext, R.string.hint_click_to_login));
            locationTv.setText(null);
        }

        View.OnClickListener goToPersonalPage = v -> {
            if (UserModel.getInstance(mContext).isLoggedIn()) {
                startActivity(new Intent(HomeActivity.this, PersonalPage.class));
            } else {
                drawerLayout.closeDrawers();
                drawerLayout.postDelayed(() -> SnackBarFactory.requestLogin(HomeActivity.this, coordinatorLayout).show(), DRAWER_CLOSE_DELAY);
            }
        };
        avatar.setOnClickListener(goToPersonalPage);
        userNameTv.setOnClickListener(goToPersonalPage);*/
    }


    private void setupNavigationView() {
        navView.setNavigationItemSelectedListener(item -> {

            drawerLayout.closeDrawers();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    delayNavItemSelected(item.getItemId());
                }
            }, DRAWER_CLOSE_DELAY);
            // TODO: 取消对 item的选择 因为下次打开肯定是在主页 然而没有主页这个选项

            return false;
        });
    }

    private void delayNavItemSelected(int itemId) {
        switch (itemId) {
            case R.id.drawer_menu_category:
                startActivity(new Intent(HomeActivity.this, CategoryView.class));
                break;
            case R.id.drawer_menu_explore:
                startActivity(new Intent(HomeActivity.this, ExploreActivity.class));
                break;
            case R.id.drawer_menu_needs:
                startActivity(new Intent(HomeActivity.this, NeedsActivity.class));
                break;
            case R.id.drawer_menu_message:
                startActivity(new Intent(HomeActivity.this, WidgetDemo.class));
                break;
//                throw new RuntimeException("测试崩溃");
            case R.id.drawer_menu_account:
                if (PreferencesUtils.getBoolean(this, StringUtils.getRes(this, R.string.key_login_state))) {
                    startActivityForResult(new Intent(HomeActivity.this, PersonalPage.class), REQUEST_CODE_PERSONAL_PAGE);

                } else {
                    //判断登录了没有
                    SnackBarFactory.requestLogin(this, coordinatorLayout).show();
                }

                break;
            case R.id.drawer_menu_settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                break;
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_home, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
                break;
            case R.id.action_about:
                Intent intent = new Intent(HomeActivity.this, AboutPage.class);
                intent.putExtra("tap_location", new int[]{ScreenUtils.getScreenWidth(this) - 20, ScreenUtils.dpToPxInt(this, 30)});
                startActivity(intent);
                overridePendingTransition(0, 0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }

        if (confirmExit())
            finish();
    }

    private boolean confirmExit() {
        if ((System.currentTimeMillis() - attemptExitTime) > 1000) {
            SnackBarFactory.succeedNoAction(this, coordinatorLayout, "再按一次退出简易").show();
            attemptExitTime = System.currentTimeMillis();
            return false;
        }
        return true;
    }

    @OnClick(R.id.fab)
    void onFabClick(View v) {
// 如果没有登录 请求登录
        if (!PreferencesUtils.getBoolean(this, StringUtils.getRes(this, R.string.key_login_state))) {
            fab.setClickable(false);
            ObjectAnimator nopeFab = AnimUtils.nope(fab).setDuration(AnimUtils.ANIMATION_TIME_SHORT);
            final float finalFabX = fab.getX();
            final float finalFabY = fab.getY();
            nopeFab.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    SnackBarFactory.requestLogin(HomeActivity.this, coordinatorLayout)
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
            startActivity(new Intent(HomeActivity.this, PostActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubscription != null) mSubscription.unsubscribe();

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        EventBus.getDefault().post(new AppBarEvent(appBarLayout, verticalOffset));
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUserStateUpdate(UserStateUpdateEvent event) {
        LogUtils.simpleLog(RegisterSettings.class, "get UserStateUpdateEvent");
        updateDrawerHeader();
    }

}
