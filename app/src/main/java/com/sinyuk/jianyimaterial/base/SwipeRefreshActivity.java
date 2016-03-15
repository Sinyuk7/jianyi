package com.sinyuk.jianyimaterial.base;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.2.4.
 */
public abstract class SwipeRefreshActivity extends BaseActivity {


    private MultiSwipeRefreshLayout mSwipeRefreshLayout;

    private Toolbar mToolbar;

    private boolean mIsRequestDataRefresh = false;


    @CallSuper
    @Override
    protected void initViews() {
        mSwipeRefreshLayout = (MultiSwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbarEvent();
        setupSwipeRefreshLayout();
    }



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
    }

    protected void setupToolbarEvent() {
        if (mToolbar == null)
            return;

        final long[] mHits = new long[2];
        mToolbar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    // TODO: Toolbar双击事件 一般是快速滑动到顶部( 在有 scrollable-child 的布局中)
                    onToolbarDoubleClick();
                }
            }
        });

    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    protected abstract void onToolbarDoubleClick();

    @CallSuper
    protected void setupSwipeRefreshLayout() {
        if (mSwipeRefreshLayout == null)
            return;
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.themeYellow);
        // do not use lambda!!
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        requestDataRefresh();
                    }
                });

    }


    @CallSuper
    public void requestDataRefresh() {
        mIsRequestDataRefresh = true;
    }

    @CallSuper
    public void setRequestDataRefresh(boolean requestDataRefresh) {
        if (mSwipeRefreshLayout == null) {
            return;
        }
        if (!requestDataRefresh) {
            mIsRequestDataRefresh = false;
            // TODO: 防止刷新消失太快，让刷新有点存在感
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, 1000);
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }


    public void setProgressViewOffset(boolean scale, int start, int end) {
        mSwipeRefreshLayout.setProgressViewOffset(scale, start, end);
    }


    public void setSwipeableChildren(MultiSwipeRefreshLayout.CanChildScrollUpCallback canChildScrollUpCallback) {
        mSwipeRefreshLayout.setCanChildScrollUpCallback(canChildScrollUpCallback);
    }


    public boolean isRequestDataRefresh() {
        return mIsRequestDataRefresh;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
