package com.sinyuk.jianyimaterial.feature.needs;

import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.NeedsListAdapter;
import com.sinyuk.jianyimaterial.entity.Needs;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.utils.NetWorkUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.4.19.
 */
public class NeedsView extends BaseActivity<NeedsPresenterImpl> implements INeedsView, AppBarLayout.OnOffsetChangedListener {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    private NeedsListAdapter mAdapter;
    private int mPageIndex = 1;
    private List<Needs> mNeedsList = new ArrayList<>();
    private boolean mIsRequestDataRefresh;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected NeedsPresenterImpl createPresenter() {
        return new NeedsPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected void onFinishInflate() {
        setupAppBarLayout();
        setupToolbar();
        setupSwipeRefreshLayout();
        setupRecyclerView();
        loadData(1);
    }

    private void setupAppBarLayout() {
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    private void setupToolbar() {
        final long[] mHits = new long[2];
        mToolbar.setOnClickListener(v -> {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void setupRecyclerView() {
        mAdapter = new NeedsListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addOnScrollListener(new OnLoadMoreListener(linearLayoutManager, mSwipeRefreshLayout) {
            @Override
            public void onLoadMore() {
                mPageIndex++;
                loadData(mPageIndex);
            }
        });
    }

    private void setupSwipeRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.themeRed,
                R.color.themeGreen);
        // do not use lambda!!
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mIsRequestDataRefresh = true;
                        setRequestDataRefresh(true);
                        if (NetWorkUtils.isNetworkConnection(NeedsView.this)) {
                            loadData(1);
                        } else {
                            onNeedsVolleyError("你的网络好像出了点问题");
                            setRequestDataRefresh(false);
                        }
                    }
                });
    }

    private void setRequestDataRefresh(boolean requestDataRefresh) {
        if (mSwipeRefreshLayout == null) {
            return;
        }
        if (!requestDataRefresh) {
            mIsRequestDataRefresh = false;
            // TODO: 防止刷新消失太快，让刷新有点存在感
            mSwipeRefreshLayout.postDelayed(() -> {
                if (mSwipeRefreshLayout != null) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }, 500);
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }

    private void loadData(int pageIndex) {
        mPresenter.loadData(pageIndex);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.needs_view;
    }

    @OnClick(R.id.fab)
    public void onClick() {}

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (null != mSwipeRefreshLayout) { mSwipeRefreshLayout.setEnabled(verticalOffset == 0); }
    }

    @Override
    public void showRefreshProgress() {
        setRequestDataRefresh(true);
    }

    @Override
    public void dismissRefreshProgress() {
        setRequestDataRefresh(false);
    }

    @Override
    public void showList(List<Needs> newPage, boolean isRefresh) {
        if (!mNeedsList.isEmpty() && isRefresh) { mNeedsList.clear(); }
        mNeedsList.addAll(newPage);
        mAdapter.setData(mNeedsList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onNeedsVolleyError(@NonNull String message) {
        ToastUtils.toastSlow(this,message);
    }

    @Override
    public void onNeedsParseError(@NonNull String message) {
        ToastUtils.toastSlow(this,message);
    }
}
