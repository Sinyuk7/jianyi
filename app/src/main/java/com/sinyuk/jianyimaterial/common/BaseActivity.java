package com.sinyuk.jianyimaterial.common;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.events.XEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Sinyuk on 16.2.3.
 * base activity
 * be caution to modify this if you have requests
 * try to make it simple
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;
    /**
     * Log tag
     */
    protected static String TAG = null;
    /**
     * context
     */
    protected Context mContext = null;
    protected CompositeSubscription mCompositeSubscription;
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        TAG = this.getClass().getSimpleName();

        mCompositeSubscription = new CompositeSubscription();

        beforeSetContentView(savedInstanceState);

        if (getContentViewID() != 0) {
            setContentView(getContentViewID());
        } else {
            throw new IllegalArgumentException("contentView has not been set yet");
        }

        ButterKnife.bind(this);

        if (isUsingEventBus()) {
            EventBus.getDefault().register(this);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            // TODO: Set back arrow as default navigation button
            if (isNavAsBack()) {
                setSupportActionBar(toolbar);
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
            }
        }

        initViews();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        initData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isUsingEventBus()) {
            EventBus.getDefault().unregister(this);
        }

        if (!mCompositeSubscription.isUnsubscribed()) { mCompositeSubscription.unsubscribe(); }

    }


    protected abstract void beforeSetContentView(Bundle savedInstanceState);

    protected abstract int getContentViewID();

    protected abstract boolean isNavAsBack();

    protected abstract boolean isUsingEventBus();

    @Subscribe
    public void onEvent(XEvent event) {
    }

    protected abstract void initViews();

    protected abstract void initData();


}
