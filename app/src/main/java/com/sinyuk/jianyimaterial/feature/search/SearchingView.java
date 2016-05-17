package com.sinyuk.jianyimaterial.feature.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.CommonGoodsListAdapter;
import com.sinyuk.jianyimaterial.api.Index;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.ui.GridItemSpaceDecoration;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.utils.ImeUtils;
import com.sinyuk.jianyimaterial.utils.NetWorkUtils;
import com.sinyuk.jianyimaterial.utils.SuggestionProvider;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.4.29.
 */
public class SearchingView extends BaseActivity<SearchPresenterImpl> implements ISearchView {


    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.empty_view)
    RelativeLayout mEmptyView;

    private boolean mIsRequestDataRefresh;
    private CommonGoodsListAdapter mAdapter;
    private int mPageIndex = 1;
    private String mQueryParam;
    private List<YihuoProfile> mYihuoProfileList = new ArrayList<>();
    private android.support.v7.widget.SearchView searchView;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected SearchPresenterImpl createPresenter() {
        return new SearchPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected void onFinishInflate() {
        setupToolbar();
        setupAppBarLayout();
        setupSwipeRefreshLayout();
        setupRecyclerView();
    }


    private void showSearch(boolean visible) {
        if (null == searchView) { return; }
        if (visible) {
            searchView.onActionViewExpanded();
        } else {
            searchView.onActionViewCollapsed();
        }
    }

    @Override
    protected void lazyLoad() {
//        refresh();
    }

    private String getQueryUrl() {
        return mQueryParam;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            showSearch(false);
            mQueryParam = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(mQueryParam, null);
            refresh();
        }
    }


    private void setupAppBarLayout() {
        mAppBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> mSwipeRefreshLayout.setEnabled(verticalOffset == 0));
    }

    private void setupRecyclerView() {
        mAdapter = new CommonGoodsListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRecyclerView.addItemDecoration(new GridItemSpaceDecoration(2, R.dimen.general_content_space, true, this));
        } else {
            mRecyclerView.addItemDecoration(new GridItemSpaceDecoration(2, R.dimen.tiny_content_space, true, this));
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
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ImeUtils.hideIme(recyclerView);
            }
        });
    }

    private void loadData(int pageIndex) {
        mPresenter.loadData(getQueryUrl(), pageIndex);
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
                        if (NetWorkUtils.isNetworkConnection(SearchingView.this)) {
                            refresh();
                        } else {
                            onVolleyError("你的网络好像出了点问题");
                            setRequestDataRefresh(false);
                        }
                    }
                });
    }

    private void refresh() {
        mPresenter.loadData(getQueryUrl(), 1);
    }

    public void setRequestDataRefresh(boolean requestDataRefresh) {
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


    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        final long[] mHits = new long[2];
        mToolbar.setOnClickListener(v -> {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    @Override
    protected int getContentViewID() {
        return R.layout.xsearch_view;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_search, menu);
        // Get the SearchingView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(true);
        searchView.onActionViewExpanded();
        if (!TextUtils.isEmpty(mQueryParam)) { searchView.setQueryHint(mQueryParam); }
        searchView.setQueryRefinementEnabled(true); //Query refinement for search suggestions
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (searchView != null) {
                    // 得到输入管理对象
                    ImeUtils.hideIme(searchView);
                    searchView.onActionViewCollapsed();
                    searchView.clearFocus(); // 不获取焦点
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView
                .findViewById(android.support.v7.appcompat.R.id.search_src_text);

        // Collapse the search menu when the user hits the back key
        searchAutoComplete.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) { showSearch(false); }
        });

        try {
            // This sets the cursor
            // resource ID to 0 or @null
            // which will make it visible
            // on white background
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");

            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchAutoComplete, 0);

        } catch (Exception e) {}

        mCompositeSubscription.add(RxSearchView.queryTextChanges(searchView).subscribe(input -> {
            mSwipeRefreshLayout.setEnabled(!TextUtils.isEmpty(input));
        }));


        return true;
    }

    @Override
    public void onBackPressed() {
        // todo 先退出搜索在退出整
        super.onBackPressed();
    }

    /**
     * Called when the hardware search button is pressed
     */
    @Override
    public boolean onSearchRequested() {
        showSearch(true);

        // dont show the built-in search dialog
        return false;
    }


    @Override
    public void showList(Index newPage, boolean isRefresh) {
        if (!mYihuoProfileList.isEmpty() && isRefresh) { mYihuoProfileList.clear(); }
        mYihuoProfileList.addAll(newPage.getData().getItems());
        mAdapter.setData(mYihuoProfileList);
        mAdapter.notifyDataSetChanged();
        invalidateEmptyView();
    }

    private void invalidateEmptyView() {
        if (mYihuoProfileList.isEmpty()) {
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void reachLastPage() {
        ToastUtils.toastFast(this, getString(R.string.common_hint_reach_list_bottom));
    }

    @Override
    public void onVolleyError(@NonNull String message) {
        ToastUtils.toastFast(this, message);
    }

    @Override
    public void onParseError(@NonNull String message) {
        ToastUtils.toastFast(this, message);
    }

}
