package com.sinyuk.jianyimaterial.feature.home;

import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.CardListAdapter;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.ui.HeaderItemSpaceDecoration;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.utils.NetWorkUtils;
import com.sinyuk.jianyimaterial.widgets.LabelView;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.3.27.
 */
public class HomeView extends com.sinyuk.jianyimaterial.mvp.BaseFragment<HomePresenterImpl> implements IHomeView {
    private static HomeView sInstance;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private boolean mIsRequestDataRefresh;
    private CardListAdapter mAdapter;
    private View mListHeader;
    private int mPageIndex = 1;
    private List<YihuoProfile> mYihuoProfileList = new ArrayList<>();
    private ImageView mShotIv;
    private LabelView mLabelView;
    private TextView mTitleTv;
    private TextView mDescriptionTv;
    private TextView mPubDataTv;
    private TextView mReadMore;

    public static HomeView getInstance() {
        if (null == sInstance) { sInstance = new HomeView(); }
        return sInstance;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected void onFinishInflate() {
        setDoubleTapListener(mToolbar);
        setupSwipeRefreshLayout();
        setupRecyclerView();
        setupListHeader();
    }

    private void setDoubleTapListener(Toolbar mToolbar) {
        final long[] mHits = new long[2];
        mToolbar.setOnClickListener(v -> {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                fastToTop();
            }
        });
    }

    private void fastToTop() {
        mRecyclerView.smoothScrollToPosition(0);
    }

    private void setupListHeader() {
        mShotIv = (ImageView) mListHeader.findViewById(R.id.shot_iv);
        mLabelView = (LabelView) mListHeader.findViewById(R.id.label_rect);
        mTitleTv = (TextView) mListHeader.findViewById(R.id.title_tv);
        mDescriptionTv = (TextView) mListHeader.findViewById(R.id.description_tv);
        mPubDataTv = (TextView) mListHeader.findViewById(R.id.pub_date_tv);
        mReadMore = (TextView) mListHeader.findViewById(R.id.read_more);
        mReadMore.setOnClickListener(v -> toHeaderDetails());
    }

    private void toHeaderDetails() {
        // TODO:
    }

    private void setupRecyclerView() {
        mAdapter = new CardListAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRecyclerView.addItemDecoration(new HeaderItemSpaceDecoration(2, R.dimen.general_content_space, true, mContext));
        } else {
            mRecyclerView.addItemDecoration(new HeaderItemSpaceDecoration(2, R.dimen.tiny_content_space, true, mContext));
        }

        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);

        mRecyclerView.setLayoutManager(staggeredGridLayoutManager);

        mListHeader = LayoutInflater.from(mContext).inflate(R.layout.include_home_daily_edition, mRecyclerView, false);
//
        mAdapter.setHeaderViewFullSpan(mListHeader);

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
        return R.layout.home_view;
    }


    @Override
    protected HomePresenterImpl createPresenter() {
        return new HomePresenterImpl();
    }


    @Override
    public void refresh() {
        mPresenter.loadData(1);
    }

    @Override
    public void loadData(int pageIndex) {
        mPresenter.loadData(pageIndex);
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
    public void loadListHeader() {
        mPresenter.loadListHeader();
    }

    @Override
    public void showListHeader(YihuoProfile data) {

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
    public void toPostView() {

    }

    @Override
    public void toLoginView() {

    }
}
