package com.sinyuk.jianyimaterial.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.events.UserStateUpdateEvent;
import com.sinyuk.jianyimaterial.managers.CacheManager;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    @Bind(R.id.backdrop_iv)
    ImageView backdropIv;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.caption)
    TextView caption;
    @Bind(R.id.hint)
    TextView hint;
    @Bind(R.id.push_switch)
    SwitchCompat pushSwitch;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.about_fl)
    FrameLayout aboutBtn;
    @Bind(R.id.push_fl)
    FrameLayout pushBtn;
//    @Bind(R.id.account_fl)
//    FrameLayout accountBtn;
    @Bind(R.id.cache_fl)
    FrameLayout cacheBtn;
    @Bind(R.id.logout_fl)
    FrameLayout logoutBtn;
    @Bind(R.id.cache_size_tv)
    TextView cacheSizeTv;
    private String cacheSizeStr = "";
    private int tapX = 0;
    private int tapY = 0;


    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_settings;
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected boolean isUsingEventBus() {
        return true;
    }


    @Override
    protected void initViews() {
        if (null != getSupportActionBar())
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        updateCacheSize();

        setupTouchEvent();

    }

    private void setupTouchEvent() {
        aboutBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    tapX = (int) event.getRawX();
                    tapY = (int) event.getRawY();
                }
                return false;
            }
        });
        aboutBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    tapX = (int) event.getRawX();
                    tapY = (int) event.getRawY();
                }
                return false;
            }
        });
    }

    private void updateCacheSize() {
        try {
            cacheSizeStr = CacheManager.getTotalCacheSize(this);
        } catch (Exception e) {
            e.printStackTrace();
            cacheSizeStr = "读取失败";
        }

        if (cacheSizeStr != null) {
            cacheSizeTv.setText(cacheSizeStr);
        }
    }

    @Override
    protected void initData() {
    }


    @OnClick({R.id.about_fl, R.id.push_fl, R.id.cache_fl, R.id.logout_fl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.push_fl:
                break;
            case R.id.cache_fl:
                attemptClearCache();
                break;
            case R.id.about_fl:
                view.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SettingsActivity.this, AboutPage.class);
                        intent.putExtra("tap_location", new int[]{tapX, tapY});
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }, 200);

                break;
            case R.id.logout_fl:
                attemptLogout();
                break;
        }
    }

    private void attemptLogout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
        builder.setCancelable(true)
                .setMessage("你确定要退出当前的账号吗")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                });
        AlertDialog alertDialog = builder.show();
    }

    private void logout() {
        PreferencesUtils.removeByKey(this, StringUtils.getResString(this, R.string.key_login_state));
        PreferencesUtils.removeByKey(this, StringUtils.getResString(this, R.string.key_user_id));
        PreferencesUtils.removeByKey(this, StringUtils.getResString(this, R.string.key_psw));
        PreferencesUtils.removeByKey(this, StringUtils.getResString(this, R.string.key_login_times));

        EventBus.getDefault().post(new UserStateUpdateEvent(false, null));
    }

    private void attemptClearCache() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
        builder.setCancelable(true)
                .setMessage("你真的要清除缓存吗,这会耗费流量来重新加载内容哦(∩_∩)")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CacheManager.clearAllCache(SettingsActivity.this);
                        cacheSizeTv.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                updateCacheSize();
                            }
                        }, 200);
                    }
                });
        AlertDialog alertDialog = builder.show();
    }

}
