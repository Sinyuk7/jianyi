package com.sinyuk.jianyimaterial.mvp;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.sinyuk.jianyimaterial.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Sinyuk on 16.3.16.
 */
public abstract class BaseActivity<P extends BasePresenter>
        extends AppCompatActivity {

    protected static String TAG = "";
    protected P mPresenter;
    protected CompositeSubscription mCompositeSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TAG = this.getClass().getSimpleName();

        mCompositeSubscription = new CompositeSubscription();

        if (isUseEventBus()) {
            EventBus.getDefault().register(this);
        }

        beforeInflate();

        if (getContentViewID() != 0) {
            setContentView(getContentViewID());
        } else {
            throw new IllegalArgumentException(TAG + " -> contentView can not been set");
        }
        attachPresenter();

        ButterKnife.bind(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            // TODO: Set back arrow as default navigation button
            if (isNavAsBack()) {
                setSupportActionBar(toolbar);
                mCompositeSubscription.add(RxToolbar.navigationClicks(toolbar).subscribe(this::onNavigationClick));
            }
        }

        onFinishInflate();
    }

    protected abstract boolean isUseEventBus();

    protected abstract void beforeInflate();

    protected abstract P createPresenter();

    public void onNavigationClick(Void v) {
        onBackPressed();
    }

    protected abstract boolean isNavAsBack();

    protected abstract void onFinishInflate();

    protected abstract int getContentViewID();

    @CallSuper
    void attachPresenter() {
        mPresenter = createPresenter();
        mPresenter.attachView(this);
    }

    @Subscribe
    public void onEvent() {}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachPresenter();
        ButterKnife.unbind(this);
        if (!mCompositeSubscription.isUnsubscribed()) { mCompositeSubscription.unsubscribe(); }
        if (isUseEventBus()) { EventBus.getDefault().unregister(this); }
    }

    @CallSuper
    void detachPresenter() {
        mPresenter.detachView();
        mPresenter = null;
    }
}
