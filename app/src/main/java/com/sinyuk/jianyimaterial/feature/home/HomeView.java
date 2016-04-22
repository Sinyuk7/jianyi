package com.sinyuk.jianyimaterial.feature.home;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.jakewharton.rxbinding.support.design.widget.RxAppBarLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.jakewharton.rxbinding.view.RxView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.activities.WebViewActivity;
import com.sinyuk.jianyimaterial.adapters.CommonGoodsListAdapter;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.common.spanbuilder.AndroidSpan;
import com.sinyuk.jianyimaterial.common.spanbuilder.SpanOptions;
import com.sinyuk.jianyimaterial.entity.Banner;
import com.sinyuk.jianyimaterial.entity.YihuoDetails;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.events.XSchoolSelectedEvent;
import com.sinyuk.jianyimaterial.feature.CategoryView;
import com.sinyuk.jianyimaterial.feature.dialog.SchoolDialog;
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

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Sinyuk on 16.3.27.
 */
public class HomeView extends BaseFragment<HomePresenterImpl> implements IHomeView {
    public static final float BANNER_ASPECT_RATIO = 243 / 720.f;
    private static final long BANNER_SWITCH_INTERVAL = 3000;
    private static HomeView sInstance;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.banner_view)
    ConvenientBanner mBannerView;
    @Bind(R.id.entry_recommended)
    TextView mEntryRecommended;
    @Bind(R.id.entry_free)
    TextView mEntryFree;
    @Bind(R.id.entry_category)
    TextView mEntryCategory;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    private boolean mIsRequestDataRefresh;
    private CommonGoodsListAdapter mAdapter;
    private View mListHeader;
    private int mPageIndex = 1;
    private List<YihuoProfile> mYihuoProfileList = new ArrayList<>();
    private DrawerLayout mDrawerLayout;
    private List<Banner> mBannerItemList;
    private int mTouchThreshold;

    private Handler mScheduleHandler = new Handler();
    private TextView mSchoolAt;
    private String mCurrentSchool;

    public static HomeView getInstance() {
        if (null == sInstance) { sInstance = new HomeView(); }
        return sInstance;
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        mTouchThreshold = ScreenUtils.getScrollThreshold(mContext);
    }

    @Override
    protected void beforeInflate() {
    }

    @Override
    protected void onFinishInflate() {
        setupToolbar();
        setupAppBarLayout();
        setupSwipeRefreshLayout();
        setupRecyclerView();
        setupBanner();

        mPresenter.loadBanner();
        //加载完banner之后在...
        mScheduleHandler.postDelayed(() -> mPresenter.loadListHeader(), 200);
        //加载完这个之后在刷新
        mScheduleHandler.postDelayed(this::refresh, 500);
    }


    private void setupAppBarLayout() {
        mCompositeSubscription.add(RxAppBarLayout.offsetChanges(mAppBarLayout).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dy -> {
                    if (dy == 0) {
                        mBannerView.startTurning(BANNER_SWITCH_INTERVAL);
                        mSwipeRefreshLayout.setEnabled(true);
                    } else {
                        mBannerView.stopTurning();
                        mSwipeRefreshLayout.setEnabled(false);
                    }
                }));
    }

    private void setupToolbar() {
        mCompositeSubscription.add(RxToolbar.navigationClicks(mToolbar).subscribe(aVoid -> toggleDrawerView()));
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

        mCompositeSubscription.add(RxView.clicks(mEntryRecommended).subscribe(this::toRecommended));

        mCompositeSubscription.add(RxView.clicks(mEntryCategory).subscribe(this::toCategory));

        mCompositeSubscription.add(RxView.clicks(mEntryFree).subscribe(this::toFree));

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
        if (null != mDrawerLayout) {
            if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        }
    }

    private void setupRecyclerView() {
        mAdapter = new CommonGoodsListAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mRecyclerView.addItemDecoration(new HeaderItemSpaceDecoration(2, R.dimen.general_content_space, false, mContext));
        } else {
            mRecyclerView.addItemDecoration(new HeaderItemSpaceDecoration(2, R.dimen.tiny_content_space, false, mContext));
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
/*        if (Build.VERSION.SDK_INT > 21) {
            mCompositeSubscription.add(RxRecyclerView.scrollEvents(mRecyclerView)
                    .observeOn(AndroidSchedulers.mainThread())
                    .throttleFirst(1000, TimeUnit.MILLISECONDS)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(RecyclerViewScrollEvent::dy).subscribe(dy -> {
                        // toolbar 隐藏 -> 判断速度是否达到
                        if (mFab.getScaleY() == 0) {
                            ScreenUtils.hideSystemyBar(getActivity());
                        } else if (mFab.getScaleY() == 1) {
                            ScreenUtils.showSystemyBar(getActivity());
                        }
                    }));
        }*/
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
        mPresenter.loadData(mCurrentSchool, 1);
        mPageIndex = 1;
    }


    @Override
    public void loadData(int pageIndex) {
        mPresenter.loadData(mCurrentSchool, pageIndex);
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
        final TextView mSchoolSwitch = (TextView) mListHeader.findViewById(R.id.school_switch_tv);
        mSchoolAt = (TextView) mListHeader.findViewById(R.id.school_at_tv);

        Glide.with(mContext).fromString().load(data.getPic())
                .error(mContext.getResources().getDrawable(R.drawable.image_placeholder_grey300))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.IMMEDIATE)
                .thumbnail(0.2f).into(mShotIv);

        mTitleTv.setText(data.getName());
        mDescriptionTv.setText(data.getDetail());
        try {
            mPubDataTv.setText(String.format(getString(R.string.home_daily_edition_pubdate), FuzzyDateFormater.getParsedDate(mContext, data.getTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!TextUtils.isEmpty(data.getSort())) { mLabelView.setText(data.getSort()); }

        mCompositeSubscription.add(RxView.clicks(mShotIv).subscribe(aVoid -> {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("url", data.getReason());
            intent.putExtras(bundle);
            startActivity(intent);
        }));

        mSchoolSwitch.setOnClickListener(v -> {
            SchoolDialog schoolDialog = SchoolDialog.getInstance();
            schoolDialog.setCancelable(true);
            schoolDialog.show(getChildFragmentManager(), SchoolDialog.TAG);
        });

        EventBus.getDefault().post(new XSchoolSelectedEvent("1", "浙江传媒学院-下沙校区"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSchoolSelected(XSchoolSelectedEvent event) {
        mSchoolAt.setText(new AndroidSpan().drawRelativeSizeSpan("当前查看: ", 1f)
                .drawWithOptions(event.getSchoolName(), new SpanOptions().addTextAppearanceSpan(mContext, R.style.SchoolAtText)
                        .addRelativeSizeSpan(0.8f)).getSpanText());
        mCurrentSchool = event.getSchoolIndex();
        refresh();
    }


    @Override
    public void onVolleyError(@NonNull String message) {mSwipeRefreshLayout.setRefreshing(false);}

    @Override
    public void onParseError(@NonNull String message) {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        mPresenter.attemptToOfferView();
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
                mFab.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SnackBarFactory.requestLogin(getActivity(), mCoordinatorLayout).setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                mFab.setClickable(true);
                                mFab.setX(finalFabX);// for the scroll bug a little tricky
                                mFab.setY(finalFabY);
                            }
                        }).show();
                    }
                }, AnimUtils.ANIMATION_TIME_SHORT);

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
        mScheduleHandler.removeCallbacksAndMessages(null);
    }

    public void toRecommended(Void v) {
        Intent toRecommended = new Intent();
        toRecommended.setClass(getContext(), ExploreView.class);
        toRecommended.putExtra(ExploreView.TITLE, "hot");
        toRecommended.putExtra(ExploreView.ENABLE_FILTER, true);
        toRecommended.putExtra(ExploreView.ENABLE_SCHOOL, true);
        toRecommended.putExtra(ExploreView.ENABLE_ORDER, true);
        startActivity(toRecommended);
    }

    public void toFree(Void v) {
        Intent toFree = new Intent();
        toFree.setClass(getContext(), ExploreView.class);
        toFree.putExtra(ExploreView.TITLE, "free");
        toFree.putExtra(ExploreView.ENABLE_FILTER, true);
        toFree.putExtra(ExploreView.ENABLE_SCHOOL, true);
        toFree.putExtra(ExploreView.ENABLE_ORDER, true);
        startActivity(toFree);
    }

    public void toCategory(Void v) {
        Intent toCategory = new Intent(getContext(), CategoryView.class);
        startActivity(toCategory);
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
