package com.sinyuk.jianyimaterial.feature.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.activities.PostActivity;
import com.sinyuk.jianyimaterial.adapters.CardListAdapter;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.entity.Banner;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.feature.login.LoginView;
import com.sinyuk.jianyimaterial.mvp.BaseFragment;
import com.sinyuk.jianyimaterial.ui.HeaderItemSpaceDecoration;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.ui.trans.AccordionTransformer;
import com.sinyuk.jianyimaterial.utils.NetWorkUtils;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;
import com.sinyuk.jianyimaterial.widgets.LabelView;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by Sinyuk on 16.3.27.
 */
public class HomeView extends BaseFragment<HomePresenterImpl> implements IHomeView,
        AppBarLayout.OnOffsetChangedListener {
    public static final float BANNER_ASPECT_RATIO = 243 / 720.f;
    private static final long BANNER_SWITCH_INTERVAL = 3000;
    private static HomeView sInstance;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.banner_view)
    ConvenientBanner mBannerView;
    @Bind(R.id.category_wears_tv)
    TextView mCategoryWearsTv;
    @Bind(R.id.category_personal_care_tv)
    TextView mCategoryPersonalCareTv;
    @Bind(R.id.category_devices_tv)
    TextView mCategoryDevicesTv;
    @Bind(R.id.category_all_items_tv)
    TextView mCategoryAllItemsTv;
    @Bind(R.id.home_category)
    CardView mHomeCategory;
    @Bind(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

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
    private LeftDrawerLayout mLeftDrawerLayout;
    private List<Banner> mBannerItemList;

    public static HomeView getInstance() {
        if (null == sInstance) { sInstance = new HomeView(); }
        return sInstance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLeftDrawerLayout = (LeftDrawerLayout) getActivity().findViewById(R.id.left_drawer_layout);
    }

    @Override
    protected void beforeInflate() {
    }

    @Override
    protected void onFinishInflate() {
        setupToolbar();
        mAppBarLayout.addOnOffsetChangedListener(this);
        setupBanner();
        setupSwipeRefreshLayout();
        setupRecyclerView();
        setupListHeader();
        mPresenter.loadBanner();
    }

    private void setupToolbar() {
        mToolbar.setNavigationOnClickListener(v -> toggleDrawerView());
        final long[] mHits = new long[2];
        mToolbar.setOnClickListener(v -> {
            System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
            mHits[mHits.length - 1] = SystemClock.uptimeMillis();
            if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                mRecyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void setupBanner() {
        final ViewTreeObserver observer = mBannerView.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mBannerView.getViewTreeObserver().removeOnPreDrawListener(this);
                final LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lps.height = (int) (ScreenUtils.getScreenWidth(mContext) * BANNER_ASPECT_RATIO);
                mBannerView.setLayoutParams(lps);
                return false;
            }
        });

        mBannerView.setPageTransformer(new AccordionTransformer());

        mBannerView.setOnItemClickListener(this::onBannerShotClick);

    }

    private void onBannerShotClick(int position) {
        Observable.just(position)
                .map(mBannerItemList::get)
                .map(Banner::getLink)
                .doOnError(throwable -> {})
                .map(Uri::parse)
                .doOnError(throwable -> {})
                .map(uri -> new Intent(Intent.ACTION_VIEW, uri))
                .subscribe(this::startActivity);
    }

    private void toggleDrawerView() {
        if (null != mLeftDrawerLayout) { mLeftDrawerLayout.toggle(); }
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
    public void showBanner(List<Banner> data) {
        mBannerItemList = data;
        mBannerView.setPages(BannerItemViewHolder::new,
                Observable.from(data)
                        .take(3)
                        .map(Banner::getSrc)
                        .map(src -> JianyiApi.JIANYI + src)
                        .toList().toBlocking().single());
        mBannerView.notifyDataSetChanged();
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
    public void onVolleyError(@NonNull String message) {mSwipeRefreshLayout.setRefreshing(false);}

    @Override
    public void onParseError(@NonNull String message) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        mPresenter.attemptToPostView();
    }

    @Override
    public void toPostView() {
        startActivity(new Intent(getContext(), PostActivity.class));
    }

    @Override
    public void toLoginView() {
        // 动画
        startActivity(new Intent(getContext(), LoginView.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        mBannerView.startTurning(BANNER_SWITCH_INTERVAL);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBannerView.stopTurning();
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
            mBannerView.startTurning(BANNER_SWITCH_INTERVAL);
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mBannerView.stopTurning();
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    public class BannerItemViewHolder implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lps);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, String url) {
            DrawableRequestBuilder<String> displayRequest = Glide.with(mContext).fromString()
                    .error(mContext.getResources().getDrawable(R.drawable.image_placeholder_grey300))
                    .placeholder(mContext.getResources().getDrawable(R.drawable.image_placeholder_grey300))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .priority(Priority.IMMEDIATE);

            displayRequest.load(url).into(imageView);
        }
    }
}
