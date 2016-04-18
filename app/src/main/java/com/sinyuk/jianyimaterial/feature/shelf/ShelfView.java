package com.sinyuk.jianyimaterial.feature.shelf;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.CardListAdapter;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.events.XShelfChangeEvent;
import com.sinyuk.jianyimaterial.mvp.BaseFragment;
import com.sinyuk.jianyimaterial.ui.GridItemSpaceDecoration;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.utils.NetWorkUtils;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16.4.2.
 */
public class ShelfView extends BaseFragment<ShelfPresenterImpl> implements IShelfView,
        AppBarLayout.OnOffsetChangedListener {
    public static final String CONTENT = "content";
    public static final String COMMON_GOODS = "common_goods";
    public static final String MY_GOODS = "my_goods";
    public static final String THEIR_GOODS = "their_goods";

    public static final String USER_ID = "user_id";
    public static final String PARAM_SCHOOL = "school";
    public static final String PARAM_SORT = "sort";
    public static final String PARAM_ORDER = "order";
    public static final String PARAM_CHILD_SORT = "child_sort";
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout mSwipeRefreshLayout;


    private boolean mIsRequestDataRefresh;
    private CardListAdapter mAdapter;
    private View mListHeader;
    private int mPageIndex = 1;
    private List<YihuoProfile> mYihuoProfileList = new ArrayList<>();
    private HashMap<String, String> mParams;
    private String mUid;
    private String mContentType;


    public static ShelfView newInstance(Bundle args) {
        ShelfView fragment = new ShelfView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        final AppBarLayout appbarLayout = (AppBarLayout) getActivity().findViewById(R.id.app_bar_layout);
        if (null != appbarLayout) { appbarLayout.addOnOffsetChangedListener(this); }
        final Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (null != toolbar) {
            final long[] mHits = new long[2];
            toolbar.setOnClickListener(v -> {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    mRecyclerView.smoothScrollToPosition(0);
                }
            });
        }
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected void onFinishInflate() {
        setupSwipeRefreshLayout();
        setupRecyclerView();
        buildParams(getArguments());
    }

    private void buildParams(Bundle bundle) {
        if (bundle == null || bundle.getString(CONTENT) == null) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        } else {
            mContentType = bundle.getString(CONTENT);
            switch (mContentType) {
                case COMMON_GOODS:
                    setupCommonContent(bundle);
                    break;
                case MY_GOODS:
                    break;
                case THEIR_GOODS:
                    mUid = bundle.getString(USER_ID);
                    mPresenter.loadData(1, mUid);
                    break;
            }
        }


    }

    private void setupCommonContent(Bundle bundle) {
        Observable.just(bundle)
                .map(args -> {
                    HashMap<String, String> params = new HashMap<>();
                    if (args.getString(PARAM_SCHOOL) != null) {
                        params.put("school", args.getString(PARAM_SCHOOL));
                    }
                    params.put("title", args.getString(PARAM_SORT, "all"));

                    params.put("sort", args.getString(PARAM_CHILD_SORT, "all"));

                    if (args.getString(PARAM_ORDER) != null) {
                        params.put("order", args.getString(PARAM_ORDER));
                    }
                    return params;
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(params -> {
                    mPresenter.loadData(1, params);
                    mParams = params;
                });
    }

    private void setupRecyclerView() {
        mAdapter = new CardListAdapter(mContext);
        // different adapter according to the specific content type
        switch (mContentType) {
            case COMMON_GOODS:
                break;
            case MY_GOODS:
                break;
            case THEIR_GOODS:
                break;
        }

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRecyclerView.addItemDecoration(new GridItemSpaceDecoration(2, R.dimen.general_content_space, true, mContext));
        } else {
            mRecyclerView.addItemDecoration(new GridItemSpaceDecoration(2, R.dimen.tiny_content_space, true, mContext));
        }

        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);

        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mRecyclerView.addOnScrollListener(new OnLoadMoreListener(staggeredGridLayoutManager, mSwipeRefreshLayout) {
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
                        if (NetWorkUtils.isNetworkConnection(mContext)) {
                            refresh();
                        } else {
                            onVolleyError("你的网络好像出了点问题");
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
            }, 600);
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }


    @Override
    protected int getContentViewID() {
        return R.layout.shelf_view;
    }

    @Override
    protected ShelfPresenterImpl createPresenter() {
        return new ShelfPresenterImpl();
    }

    @Override
    public void refresh() {
        switch (mContentType){
            case COMMON_GOODS:
                mPresenter.loadData(1, mParams);
                break;
            case MY_GOODS:
            case THEIR_GOODS:
                mPresenter.loadData(1, mUid);
                break;
        }
    }

    @Override
    public void loadData(int pageIndex) {

        switch (mContentType){
            case COMMON_GOODS:
                mPresenter.loadData(pageIndex, mParams);
                break;
            case MY_GOODS:
            case THEIR_GOODS:
                mPresenter.loadData(pageIndex, mUid);
                break;
        }
    }

    @Override
    public void onDataLoaded() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void showList(List<YihuoProfile> newPage, boolean isRefresh) {
        if (!mYihuoProfileList.isEmpty() && isRefresh) { mYihuoProfileList.clear(); }
        mYihuoProfileList.addAll(newPage);
        mAdapter.setData(mYihuoProfileList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onVolleyError(@NonNull String message) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onParseError(@NonNull String message) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (null != mSwipeRefreshLayout) { mSwipeRefreshLayout.setEnabled(verticalOffset == 0); }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onYihuoChange(XShelfChangeEvent event) {
        if (!event.getNewParams().equals(mParams)) {
            mParams = event.getNewParams();
            mPresenter.loadData(1, mParams);
        }
    }
}
