package com.sinyuk.jianyimaterial.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.events.BaseEvent;

import butterknife.ButterKnife;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by Sinyuk on 16.2.3.
 * base activity
 * be caution to modify this if you have requests
 * try to make it simple
 */
public abstract class BaseActivity extends AppCompatActivity {
    /**
     * Log tag
     */
    protected static String TAG = null;

    /**
     * context
     */
    protected Context mContext = null;

    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;

    private AlertDialog mAlertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        TAG = this.getClass().getSimpleName();


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
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back_primary_24dp);
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
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
    }


    protected abstract void beforeSetContentView(Bundle savedInstanceState);

    protected abstract int getContentViewID();

    protected abstract boolean isNavAsBack();

    protected abstract boolean isUsingEventBus();

    @Subscribe
    public void onEvent(BaseEvent event) {
    }

    protected abstract void initViews();

    protected abstract void initData();




}
