package com.sinyuk.jianyimaterial.base;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.VolleyError;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.events.AppBarEvent;
import com.sinyuk.jianyimaterial.utils.NetWorkUtils;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sinyuk on 16.2.12.
 */
public abstract class SwipeRefreshFragment extends BaseFragment {
    @Nullable
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Nullable
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @NonNull
    private Toolbar mToolbar;
    private boolean mIsRequestDataRefresh;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
    }

    @Override
    protected int getContentViewId() {
        return 0;
    }

    @CallSuper
    @Override
    protected boolean isUsingEventBus() {
        return true;
    }

    @CallSuper
    @Override
    protected void initViewsAndEvent() {
        if (needFastOnTop())
            setToolbarEvent();
        setupSwipeRefreshLayout();
    }

    protected abstract boolean needFastOnTop();

    private void setToolbarEvent() {

        final long[] mHits = new long[2];
        mToolbar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    // TODO: Toolbar双击事件 一般是快速滑动到顶部( 在有 scrollable-child 的布局中)
                    onToolbarDoubleTap();
                }
            }
        });
    }

    private void onToolbarDoubleTap() {
        if (mRecyclerView == null)
            return;
        mRecyclerView.smoothScrollToPosition(0);
    }


    private void setupSwipeRefreshLayout() {
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
                        mIsRequestDataRefresh = true;
                        setRequestDataRefresh(true);
                        if (checkNetWork()) {
                            refreshList();
                        } else {
                            netWorkError();
                            setRequestDataRefresh(false);
                        }
                    }
                });

    }

    protected abstract void netWorkError();


    private boolean checkNetWork() {
        return NetWorkUtils.isNetworkConnection(mContext);
    }


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


    protected abstract void refreshList();

    protected abstract void refreshFailed(VolleyError error);

    protected abstract void refreshSucceed();

    protected abstract void loadListData(int pageIndex);

    protected abstract void loadFailed(VolleyError error);

    protected abstract void loadSucceed();

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppBarExpanded(AppBarEvent event) {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setEnabled(event.getVerticalOffset() == 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
