package com.sinyuk.jianyimaterial.feature.home;

import android.animation.Animator;
import android.animation.ObjectAnimator;
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
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.jakewharton.rxbinding.view.RxView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.activities.CategoryMenu;
import com.sinyuk.jianyimaterial.adapters.CardListAdapter;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.entity.Banner;
import com.sinyuk.jianyimaterial.entity.YihuoDetails;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.feature.explore.ExploreView;
import com.sinyuk.jianyimaterial.feature.offer.OfferView;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.mvp.BaseFragment;
import com.sinyuk.jianyimaterial.ui.HeaderItemSpaceDecoration;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.ui.trans.AccordionTransformer;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.AnimatorLayerListener;
import com.sinyuk.jianyimaterial.utils.FuzzyDateFormater;
import com.sinyuk.jianyimaterial.utils.NetWorkUtils;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;
import com.sinyuk.jianyimaterial.widgets.LabelView;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import java.text.ParseException;
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
    private LeftDrawerLayout mLeftDrawerLayout;
    private List<Banner> mBannerItemList;

    public static HomeView getInstance() {
        if (null == sInstance) { sInstance = new HomeView(); }
        return sInstance;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
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
        mPresenter.loadListHeader();
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
    public void showListHeader(YihuoDetails data) {
        final ImageView mShotIv = (ImageView) mListHeader.findViewById(R.id.shot_iv);
        final LabelView mLabelView = (LabelView) mListHeader.findViewById(R.id.label_rect);
        final TextView mTitleTv = (TextView) mListHeader.findViewById(R.id.title_tv);
        final TextView mDescriptionTv = (TextView) mListHeader.findViewById(R.id.description_tv);
        final TextView mPubDataTv = (TextView) mListHeader.findViewById(R.id.pub_date_tv);

        Glide.with(mContext).fromString().load(data.getPic())
                .error(mContext.getResources().getDrawable(R.drawable.image_placeholder_grey300))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.IMMEDIATE)
                .thumbnail(0.2f).into(mShotIv);

        mTitleTv.setText(data.getName());
        mDescriptionTv.setText(data.getDetail());
        try {
            mPubDataTv.setText(FuzzyDateFormater.getParsedDate(mContext, data.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        mLabelView.setText(data.getSort());

        mCompositeSubscription.add(RxView.clicks(mShotIv).subscribe(aVoid -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(data.getTitle())));
        }));
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
        startActivity(new Intent(getContext(), OfferView.class));
    }

    @Override
    public void toLoginView() {
        // shake the fab
        mFab.setClickable(false);
        ObjectAnimator nopeFab = AnimUtils.nope(mFab).setDuration(AnimUtils.ANIMATION_TIME_SHORT);
        final float finalFabX = mFab.getX();
        final float finalFabY = mFab.getY();
        nopeFab.addListener(new AnimatorLayerListener(mFab) {
            @Override
            public void onAnimationEnd(Animator animation) {
                SnackBarFactory.requestLogin(getActivity(), getView()).setCallback(new Snackbar.Callback() {
                    @Override
                    public void onDismissed(Snackbar snackbar, int event) {
                        super.onDismissed(snackbar, event);
                        mFab.setClickable(true);
                        mFab.setX(finalFabX);// for the scroll bug a little tricky
                        mFab.setY(finalFabY);
                    }
                }).show();
            }
        });
        nopeFab.start();
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

    @OnClick({R.id.entry_recommended, R.id.entry_free, R.id.entry_category})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.entry_recommended:
                Intent toRecommended = new Intent();
                toRecommended.setClass(getContext(), ExploreView.class);
                toRecommended.putExtra(ExploreView.TITLE, "hot");
                toRecommended.putExtra(ExploreView.ENABLE_FILTER, true);
                toRecommended.putExtra(ExploreView.ENABLE_SCHOOL, true);
                toRecommended.putExtra(ExploreView.ENABLE_ORDER, true);
                startActivity(toRecommended);
                getActivity().overridePendingTransition(0, 0);
                break;
            case R.id.entry_free:
                Intent toFree = new Intent();
                toFree.setClass(getContext(), ExploreView.class);
                toFree.putExtra(ExploreView.TITLE, "free");
                toFree.putExtra(ExploreView.ENABLE_FILTER, true);
                toFree.putExtra(ExploreView.ENABLE_SCHOOL, true);
                toFree.putExtra(ExploreView.ENABLE_ORDER, true);
                startActivity(toFree);
                getActivity().overridePendingTransition(0, 0);
                break;
            case R.id.entry_category:
                Intent toCategory = new Intent(getContext(), CategoryMenu.class);
                startActivity(toCategory);
                getActivity().overridePendingTransition(0, 0);
                break;
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
