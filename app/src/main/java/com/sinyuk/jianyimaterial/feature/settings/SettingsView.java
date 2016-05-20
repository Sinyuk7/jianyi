package com.sinyuk.jianyimaterial.feature.settings;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.common.WebViewActivity;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.feature.login.LoginView;
import com.sinyuk.jianyimaterial.feature.profile.MessageView;
import com.sinyuk.jianyimaterial.feature.settings.account.AccountView;
import com.sinyuk.jianyimaterial.managers.CacheManager;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16.4.12.
 */
public class SettingsView extends BaseActivity<SettingsPresenterImpl> implements ISettingsView {

    private static final int REQUEST_FEEDBACK = 0x44;
    @Bind(R.id.icon_iv)
    TextView mIconIv;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.settings_account)
    TextView mSettingsAccount;
    @Bind(R.id.settings_account_group)
    LinearLayout mSettingsAccountGroup;
    @Bind(R.id.settings_push)
    TextView mSettingsPush;
    @Bind(R.id.settings_cache)
    TextView mSettingsCache;
    @Bind(R.id.cache_size_tv)
    TextView mCacheSizeTv;
    @Bind(R.id.settings_feedback)
    TextView mSettingsFeedback;
    @Bind(R.id.settings_about)
    TextView mSettingsAbout;
    @Bind(R.id.settings_items)
    LinearLayout mSettingsItems;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;
    private Observable<String> cacheObservable;
    private Observable<String> mCacheClearObservable;
    private User mCurrentUser;
    private boolean mIsLogged;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {
        cacheObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(CacheManager.getTotalCacheSize(SettingsView.this));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).doOnError(throwable -> mCacheSizeTv.setText(getString(R.string.settings_hint_read_cache_failed)));

        mCacheClearObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                CacheManager.clearAllCache(SettingsView.this);
                try {
                    subscriber.onNext(CacheManager.getTotalCacheSize(SettingsView.this));
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());

    }

    @Override
    protected SettingsPresenterImpl createPresenter() {
        return new SettingsPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected void onFinishInflate() {
        // whether the user is logged in or not
        setupCacheOption();
    }

    @Override
    protected void lazyLoad() {
        mCompositeSubscription.add(
                RxView.clicks(mSettingsFeedback).compose(RxPermissions.getInstance(this)
                        .ensure(Manifest.permission.SEND_SMS))
                        .subscribe(granted -> {
                            if (granted) {startFeedbackDialog();} else {hintPermissionDenied();}
                        }));
    }


    private void startFeedbackDialog() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SettingsView.this, mSettingsFeedback, "transition_dialog");
            startActivityForResult(MessageView.newIntent(this, "你有什么想说的？", "15757161279", true), REQUEST_FEEDBACK, options.toBundle());
        } else {
            startActivityForResult(MessageView.newIntent(this, "你有什么想说的？", "15757161279", true), REQUEST_FEEDBACK);
        }
    }

    private void hintPermissionDenied() {
        ToastUtils.toastSlow(this, getString(R.string.settings_feedback_permisstion_denied));
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.queryCurrentUser();
    }

    private void showAlertDialog() {
        SweetAlertDialog cacheDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        cacheDialog.setTitleText(getString(R.string.settings_hint_clear_cache_title))
                .setContentText(getString(R.string.settings_hint_clear_cache_content))
                .setConfirmText(getString(R.string.settings_hint_confirm))
                .setConfirmClickListener(sweetAlertDialog -> {
                    mCacheClearObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(cashSize -> {
                        mCacheSizeTv.setText(cashSize);
                    });
                    sweetAlertDialog.dismissWithAnimation();
                })
                .setCancelText(getString(R.string.settings_hint_cancel));
        cacheDialog.setCancelable(true);
        cacheDialog.show();
    }

    private void setupCacheOption() {
        mCompositeSubscription.add(cacheObservable.delay(500, TimeUnit.MILLISECONDS).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cacheSize -> {mCacheSizeTv.setText(cacheSize);}));
    }


    @Override
    protected int getContentViewID() {
        return R.layout.settings_view;
    }


    @OnClick(R.id.settings_account)
    public void onClickAccountOption() {
        if (mIsLogged) {
            final FragmentManager fm = getSupportFragmentManager();
            if (fm.findFragmentByTag("account") == null) {
                fm.beginTransaction()
                        .add(R.id.settings_account_group, AccountView.getInstance(), "account").commit();
            } else {
                fm.beginTransaction().remove(AccountView.getInstance()).commit();
            }
        } else {
            requireLogin();
        }
    }

    private void requireLogin() {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText(getString(R.string.settings_hint_has_not_logged))
                .setContentText(getString(R.string.settings_hint_require_login))
                .setConfirmText(getString(R.string.settings_hint_confirm))
                .setConfirmClickListener(sweetAlertDialog -> {
                    startActivity(new Intent(SettingsView.this, LoginView.class));
                    sweetAlertDialog.dismissWithAnimation();
                })
                .setCancelText(getString(R.string.settings_hint_cancel));
        dialog.setCancelable(true);
        dialog.show();
    }

    @OnClick({R.id.settings_push, R.id.settings_cache, R.id.settings_about})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.settings_push:
                break;
            case R.id.settings_cache:
                showAlertDialog();
                break;
            case R.id.settings_about:
                startActivity(WebViewActivity.newIntent(this, "https://github.com/80998062/jianyi"));
                break;
        }
    }

    @Override
    public void onQuerySucceed(User user) {
        mIsLogged = true;
        Bundle args = new Bundle();
        args.putString("school", user.getSchool());
        args.putString("tel", user.getTel());
        AccountView.newInstance(args);
    }

    @Override
    public void onQueryFailed(String message) {
        ToastUtils.toastSlow(this, message);
    }

    @Override
    public void onUserNotLogged() {
        mIsLogged = false;
    }
}
