package com.sinyuk.jianyimaterial.feature.settings;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.feature.settings.account.AccountView;
import com.sinyuk.jianyimaterial.managers.CacheManager;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;

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
        setupCacheOption();

    }

    private void createDialogs() {
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
        final FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag("account") == null) {
            fm.beginTransaction()
                    .add(R.id.settings_account_group, AccountView.getInstance(), "account").commit();
        } else {
            fm.beginTransaction().remove(AccountView.getInstance()).commit();
        }
    }

    @OnClick({R.id.settings_push, R.id.settings_cache, R.id.settings_feedback, R.id.settings_about})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.settings_push:
                break;
            case R.id.settings_cache:
                createDialogs();
                break;
            case R.id.settings_feedback:

                break;
            case R.id.settings_about:
                break;
        }
    }
}
