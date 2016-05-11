package com.sinyuk.jianyimaterial.feature.details;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jakewharton.rxbinding.support.design.widget.RxAppBarLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.CommentsAdapter;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.common.Constants;
import com.sinyuk.jianyimaterial.common.PhotoViewActivity;
import com.sinyuk.jianyimaterial.entity.YihuoDetails;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.feature.profile.ProfileView;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.ui.smallbang.SmallBang;
import com.sinyuk.jianyimaterial.ui.trans.AccordionTransformer;
import com.sinyuk.jianyimaterial.utils.AnimatorLayerListener;
import com.sinyuk.jianyimaterial.utils.FormatUtils;
import com.sinyuk.jianyimaterial.utils.FuzzyDateFormater;
import com.sinyuk.jianyimaterial.utils.NetWorkUtils;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.widgets.CheckableImageView;
import com.sinyuk.jianyimaterial.widgets.InkPageIndicator;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;
import com.sinyuk.jianyimaterial.widgets.MyCircleImageView;
import com.sinyuk.jianyimaterial.widgets.RatioImageView;
import com.sinyuk.jianyimaterial.widgets.ReadMoreTextView;
import com.sinyuk.jianyimaterial.widgets.RoundCornerIndicator;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cimi.com.easeinterpolator.EaseSineInInterpolator;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16.3.19.
 */
public class DetailsView extends BaseActivity<DetailsPresenterImpl> implements IDetailsView {

    public static final String YihuoProfile = "YihuoProfile";
    private static final long LOAD_COMMENT_DELAY = 322;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.scrim)
    View scrim;
    @Bind(R.id.page_indicator)
    InkPageIndicator pageIndicator;
    @Bind(R.id.page_indicator_compat)
    RoundCornerIndicator roundCornerIndicator;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.new_price_tv)
    TextView newPriceTv;
    @Bind(R.id.title_tv)
    TextView titleTv;
    @Bind(R.id.description_tv)
    ReadMoreTextView descriptionTv;
    @Bind(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.avatar)
    MyCircleImageView avatar;
    @Bind(R.id.user_name_tv)
    TextView userNameTv;
    @Bind(R.id.pub_date_tv)
    TextView pubDateTv;
    @Bind(R.id.recycler_view)
    RecyclerView commentList;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout mSwipeRefreshLayout;
    private YihuoProfile profileData;

    private List<YihuoDetails.Pics> shotsList = new ArrayList<>();
    private int oldX;
    private int newX;
    // only show the scrim animation when first enter
    private boolean isEnterActivity = true;
    private DrawableRequestBuilder<String> avatarRequest;
    private BitmapRequestBuilder<String, Bitmap> shotRequest;
    private CommentsAdapter mCommentAdapter;
    private int mPageIndex = 1;
    private ArrayList<String> mCommentList = new ArrayList<>();
    private boolean mIsRequestDataRefresh;
    private TextView mViewCountTv;
    private CheckableImageView likeBtn;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    /**
     * Receive the yihuo profile data passed by the previous activity
     */
    @Override
    protected void beforeInflate() {
        profileData = getIntent().getExtras().getParcelable(YihuoProfile);

        avatarRequest = Glide.with(this).fromString()
                .dontAnimate()
                .placeholder(this.getResources().getDrawable(R.drawable.ic_avatar_placeholder))
                .error(this.getResources().getDrawable(R.drawable.ic_avatar_placeholder))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .priority(Priority.NORMAL)
                .bitmapTransform(new CropCircleTransformation(this)).thumbnail(0.2f);

        shotRequest = Glide.with(this).fromString()
                .asBitmap()
                .dontAnimate()
                .placeholder(this.getResources().getDrawable(R.drawable.image_placeholder_black))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.IMMEDIATE)
                .thumbnail(0.2f);
    }

    @Override
    protected DetailsPresenterImpl createPresenter() {
        return new DetailsPresenterImpl();
    }


    private void handleException() {

    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    /**
     * init some views that don't need to request data from server
     */
    @Override
    protected void onFinishInflate() {
        if (profileData == null) {
            handleException();
            return;
        }

        setupSwipeRefreshLayout();
        setupCommentList();
        setupViewPager();
        setLazyLoadDelay(LOAD_COMMENT_DELAY);
        // load yihuo details form server
        mPresenter.loadYihuoDetails(profileData.getId());
    }

    @Override
    protected void lazyLoad() {
        // glide load request
        setupAppBarLayout();
        setupUsername();
        setupAvatar();
        setupToolbarTitle();
        setupPubdate();
        setYihuoTitle();
        setupPrice();
        myHandler.postDelayed(this::refreshComment, 1000);
    }

    private void setupAppBarLayout() {
        newPriceTv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        titleTv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        descriptionTv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mCompositeSubscription.add(RxAppBarLayout.offsetChanges(appBarLayout)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(dy -> 1 - (-dy / (appBarLayout.getTotalScrollRange() * 1f)))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fraction -> {
                    if (fraction < 0) { fraction = 0f; }
                    newPriceTv.setAlpha(fraction);
                    titleTv.setAlpha(fraction);
                    descriptionTv.setAlpha(fraction);
                    if (fraction < 0.1f) {
                        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_primary_24dp);
                    } else if (fraction > 0.8f) {
                        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
                    }
                    mSwipeRefreshLayout.setEnabled(fraction == 1);
                }));
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
                        if (NetWorkUtils.isNetworkConnection(DetailsView.this)) {
                            refreshComment();
                        } else {
                            ToastUtils.toastFast(DetailsView.this, "你的网络好像出了点问题");
                            setRequestDataRefresh(false);
                        }
                    }
                });
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


    private void setYihuoTitle() {
        titleTv.setText(StringUtils.check(this, profileData.getName(), R.string.untable));
    }

    private void setupUsername() {
        userNameTv.setText(String.format(StringUtils.getRes(this, R.string.common_prefix_from),
                StringUtils.check(this, profileData.getUsername(), R.string.untable)));
    }

    private void setupAvatar() {
        avatarRequest.load(profileData.getHeadImg()).into(avatar);
    }

    private void setupToolbarTitle() {
        collapsingToolbarLayout.setTitle(
                StringUtils.check(this, profileData.getName(), R.string.activity_product_details));
    }

    private void setupPubdate() {
        try {
            pubDateTv.setText(
                    StringUtils.check(this, FuzzyDateFormater.getParsedDate(this, profileData.getTime()), R.string.empty));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setupPrice() {
        newPriceTv.setText(StringUtils.check(this, FormatUtils.formatPrice(profileData.getPrice()), R.string.untable));
    }


    private void setupViewPager() {
        // TODO:  use view-stub instead
        pageIndicator.setVisibility(View.GONE);
        viewPager.setPageTransformer(false, new AccordionTransformer());
        viewPager.setOnTouchListener((v, ev) -> {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    oldX = (int) ev.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    newX = (int) ev.getX();
                    if (Math.abs(oldX - newX) < ViewConfiguration.get(this).getScaledTouchSlop()) {
                        oldX = 0;
                        newX = 0;
                        onShotClick(viewPager.getCurrentItem());
                        break;
                    }
            }
            return false;
        });
    }

    private void onShotClick(int shotIndex) {
        if (shotsList == null) { return; }
        Intent intent = new Intent(DetailsView.this, PhotoViewActivity.class);
        Bundle bundle = new Bundle();
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < shotsList.size(); i++) {
            list.add(JianyiApi.shotUrl(shotsList.get(i).getPic()));
        }
        bundle.putStringArrayList("shot_urls", list);
        bundle.putString("product_id", profileData.getId());
        bundle.putInt("selected_page_index", shotIndex);
        intent.putExtras(bundle);
        startActivityForResult(intent, Constants.Request_Code_Page_Index);
    }

    // update the current viewPager item when return
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.Request_Code_Page_Index:
                viewPager.setCurrentItem(data.getIntExtra("selected_page_index", 0));
                break;
        }
    }

    @Override
    protected int getContentViewID() {
        return R.layout.details_view;
    }


    @Override
    public void setupLikeButton(boolean isAdded) {
        likeBtn.setChecked(isAdded);
    }


    @Override
    public void showDescription(String description) {
        descriptionTv.setText(StringUtils.check(this, description, R.string.untable));
    }

    public void addToLikes() {
        final SmallBang smallBang = SmallBang.attach2Window(this);
        if (!likeBtn.isChecked()) { // 取消的时候就不要那个动画了
            smallBang.bang(likeBtn, ScreenUtils.dpToPxInt(this, 36), null);
        }
        likeBtn.setChecked(!likeBtn.isChecked());
    }

    public void shareTo() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.details_share_prefix)
                + profileData.getName() + JianyiApi.shareById(profileData.getId()));
        //自定义选择框的标题
        startActivity(Intent.createChooser(shareIntent, getString(R.string.details_share_to_hint)));
    }


    @OnClick(R.id.avatar)
    public void toProfileView() {
        Intent intent = new Intent(this, ProfileView.class);
        Bundle bundle = new Bundle();
        bundle.putFloat(ProfileView.PROFILE_TYPE, ProfileView.OTHER);
        bundle.putString("uid", profileData.getUid());
        bundle.putString("user_name", profileData.getUsername());
        bundle.putString("location", profileData.getSchoolname());
        bundle.putString("tel", profileData.getTel());
        bundle.putString("avatar", profileData.getHeadImg());
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void showShots(List<YihuoDetails.Pics> shots) {
        shotsList = shots;
        // TODO: 这2个的位置
        ShotsAdapter mViewPagerAdapter = new ShotsAdapter(this);

        viewPager.setAdapter(mViewPagerAdapter);

        mViewPagerAdapter.notifyDataSetChanged();

        // TODO: use view-stub instead
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pageIndicator.setViewPager(viewPager);
            pageIndicator.setVisibility(View.VISIBLE);
        } else {
            roundCornerIndicator.setViewPager(viewPager, shotsList.size());
            roundCornerIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showViewCount(@NonNull String count) {
        if (TextUtils.isEmpty(count)) { count = getString(R.string.details_view_count_null); }
        mViewCountTv.setText(String.format(StringUtils.getRes(this, R.string.details_view_count), count));
    }

    private void setupCommentList() {
        mCommentAdapter = new CommentsAdapter(this);
        commentList.setAdapter(mCommentAdapter);
        commentList.setItemAnimator(new DefaultItemAnimator());
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        layoutManager.setAutoMeasureEnabled(true);
        commentList.setLayoutManager(layoutManager);
        commentList.addOnScrollListener(new OnLoadMoreListener(layoutManager, mSwipeRefreshLayout) {
            @Override
            public void onLoadMore() {
                mPageIndex++;
                loadComment(mPageIndex);
            }
        });

        final View listHeader = View.inflate(this, R.layout.include_product_details_socials, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        listHeader.setLayoutParams(layoutParams);
        mCommentAdapter.setHeaderView(listHeader);

        mViewCountTv = (TextView) listHeader.findViewById(R.id.view_count_tv);
        likeBtn = (CheckableImageView) listHeader.findViewById(R.id.like_btn);
        final TextView shareTv = (TextView) listHeader.findViewById(R.id.share_tv);
        final FrameLayout likeBtnWrapper = (FrameLayout) listHeader.findViewById(R.id.like_btn_wrapper);
        mCompositeSubscription.add(RxView.clicks(likeBtn).subscribe(aVoid -> addToLikes()));
        mCompositeSubscription.add(RxView.clicks(shareTv).subscribe(aVoid -> shareTo()));
        mCompositeSubscription.add(RxView.clicks(likeBtnWrapper).subscribe(aVoid -> addToLikes()));
    }

    private void refreshComment() {
        mPageIndex = 1;
        mPresenter.loadComments(mPageIndex);
    }

    private void loadComment(int pageIndex) {
        mPresenter.loadComments(pageIndex);
    }

    @Override
    public void showComments(List<String> data, boolean isRefresh) {
        if (!mCommentList.isEmpty() && isRefresh) { mCommentList.clear(); }
        mCommentList.addAll(data);
        mCommentAdapter.setData(mCommentList);
        mCommentAdapter.notifyDataSetChanged();
    }

    @Override
    public void hintNoComment() {

    }

    @Override
    public void sendComment(String comment) {

    }

    @Override
    public void hintAddToLikes() {

    }

    @Override
    public void hintRemoveFromLikes() {

    }

    @Override
    public void hintRequestLogin() {
        SnackBarFactory.requestLogin(this, coordinatorLayout).show();
    }

    public void showLoadingProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoadingProgress() {
        progressBar.setVisibility(View.GONE);
    }

    /**
     * ViewPager Adapter
     */
    private class ShotsAdapter extends PagerAdapter {
        private Context context;


        ShotsAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            // if the shotList is empty load a placeholder
            if (shotsList.isEmpty()) { return 1; }
            return shotsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View shot = getPage(position, container);
            container.addView(shot);
            return shot;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private View getPage(final int position, final ViewGroup container) {
            // in shot loading progress
            showLoadingProgress();

            final RatioImageView imageView = new RatioImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            if (shotsList.isEmpty()) {
                hideLoadingProgress();
                imageView.setImageResource(R.drawable.image_placeholder_black);
                return imageView;
            }

            /*imageView.setTag(R.id.shot_tag, shotsList.get(position).getId());*/

            shotRequest.load(JianyiApi.shotUrl(shotsList.get(position).getPic())).listener(new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                    hideLoadingProgress();
                    ToastUtils.toastSlow(DetailsView.this, R.string.hint_failed_to_loading_shot);
                    return false;
                }

                @Override
                public boolean onResourceReady(final Bitmap bitmap, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    hideLoadingProgress();
                    if (isEnterActivity) {
                        scrim.animate().alpha(0).setDuration(150).setStartDelay(50).setInterpolator(new EaseSineInInterpolator()).setListener(new AnimatorLayerListener(scrim) {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                scrim.setAlpha(0);
                                scrim.setVisibility(View.GONE);
                            }
                        }).start();
                        isEnterActivity = false;
                    }
                    return false;
                }
            }).into(imageView);

            return imageView;
        }
    }
}
