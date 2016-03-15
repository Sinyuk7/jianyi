package com.sinyuk.jianyimaterial.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import android.support.v7.widget.LinearLayoutManager;

import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

/**
 * Created by Sinyuk on 16.2.11.
 */
public abstract class OnLoadMoreListener extends RecyclerView.OnScrollListener {
    private int PRELOAD_SIZE = 4;
    private MultiSwipeRefreshLayout swipeRefreshLayout;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private LinearLayoutManager mLinearLayoutManager;
    private boolean mIsFirstTimeTouchBottom = true;


    public void setPreloadSize(int PreloadSize) {
        this.PRELOAD_SIZE = PreloadSize;
    }

    public int getPreloadSize() {
        return PRELOAD_SIZE;
    }

    public OnLoadMoreListener(@NonNull StaggeredGridLayoutManager staggeredGridLayoutManager, @NonNull MultiSwipeRefreshLayout swipeRefreshLayout) {
        this.mStaggeredGridLayoutManager = staggeredGridLayoutManager;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    public OnLoadMoreListener(@NonNull LinearLayoutManager linearLayout, @NonNull MultiSwipeRefreshLayout swipeRefreshLayout) {
        this.mLinearLayoutManager = linearLayout;
        this.swipeRefreshLayout = swipeRefreshLayout;
    }

    @Override
    public void onScrolled(RecyclerView rv, int dx, int dy) {
        if (mStaggeredGridLayoutManager != null) {
            boolean isBottom =
                    mStaggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(
                            new int[2])[1] >=
                            rv.getAdapter().getItemCount() -
                                    PRELOAD_SIZE;

            if (!swipeRefreshLayout.isRefreshing() && isBottom) {
                if (!mIsFirstTimeTouchBottom) {
                    swipeRefreshLayout.setRefreshing(true);
                    onLoadMore();
                } else {
                    mIsFirstTimeTouchBottom = false;
                }
            }
        }else if(mLinearLayoutManager != null){
            boolean isBottom =
                    mLinearLayoutManager.findLastCompletelyVisibleItemPosition() >= rv.getAdapter().getItemCount() - PRELOAD_SIZE;

            if (!swipeRefreshLayout.isRefreshing() && isBottom) {
                if (!mIsFirstTimeTouchBottom) {
                    swipeRefreshLayout.setRefreshing(true);
                    onLoadMore();
                } else {
                    mIsFirstTimeTouchBottom = false;
                }
            }
        }

    }

    public abstract void onLoadMore();
}
