package com.sinyuk.jianyimaterial.feature.home;

import android.content.Context;
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

import com.jakewharton.rxbinding.view.RxView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.CardListAdapter;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.mvp.BaseFragment;
import com.sinyuk.jianyimaterial.ui.HeaderItemSpaceDecoration;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.utils.NetWorkUtils;
import com.sinyuk.jianyimaterial.widgets.LabelView;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import java.util.List;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.3.27.
 */
public class HomeView extends com.sinyuk.jianyimaterial.mvp.BaseActivity<HomePresenterImpl> implements IHomeView {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout swipeRefreshLayout;

    public static HomeView instance;
    private boolean mIsRequestDataRefresh;
    private CardListAdapter adapter;

    private View headView;
    private int pageIndex = 1;
    private List<YihuoProfile> yihuoProfileList;
    private ImageView shotIv;
    private LabelView labelView;
    private TextView titleTv;
    private TextView descriptionTv;
    private TextView pubDataTv;
    private TextView readMore;


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
        recyclerView.smoothScrollToPosition(0);
    }

    private void setupListHeader() {
        shotIv = (ImageView) headView.findViewById(R.id.shot_iv);
        labelView = (LabelView) headView.findViewById(R.id.label_rect);
        titleTv = (TextView) headView.findViewById(R.id.title_tv);
        descriptionTv = (TextView) headView.findViewById(R.id.description_tv);
        pubDataTv = (TextView) headView.findViewById(R.id.pub_date_tv);
        readMore = (TextView) headView.findViewById(R.id.read_more);
        readMore.setOnClickListener(v -> toHeaderDetails());
    }

    private void toHeaderDetails() {
        // TODO:
    }

    private void setupRecyclerView() {
        adapter = new CardListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.addItemDecoration(new HeaderItemSpaceDecoration(2, R.dimen.general_content_space, true, this));
        } else {
            recyclerView.addItemDecoration(new HeaderItemSpaceDecoration(2, R.dimen.tiny_content_space, true, this));
        }

        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        headView = LayoutInflater.from(this).inflate(R.layout.include_home_daily_edition, recyclerView, false);
//
        adapter.setHeaderViewFullSpan(headView);

        recyclerView.addOnScrollListener(new OnLoadMoreListener(staggeredGridLayoutManager, swipeRefreshLayout) {
            @Override
            public void onLoadMore() {
                pageIndex++;
                loadData(pageIndex);
            }
        });
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.themeRed,
                R.color.themeGreen);
        // do not use lambda!!
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mIsRequestDataRefresh = true;
                        setRequestDataRefresh(true);
                        if (NetWorkUtils.isNetworkConnection(HomeView.this)) {
                            refresh();
                        } else {
                            hintVolleyError("你的网络好像出了点问题");
                            setRequestDataRefresh(false);
                        }
                    }
                });
    }

    private void setRequestDataRefresh(boolean requestDataRefresh) {
        if (swipeRefreshLayout == null) {
            return;
        }
        if (!requestDataRefresh) {
            mIsRequestDataRefresh = false;
            // TODO: 防止刷新消失太快，让刷新有点存在感
            swipeRefreshLayout.postDelayed(() -> {
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }, 600);
        } else {
            swipeRefreshLayout.setRefreshing(true);

        }
    }

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_list;
    }


    @Override
    protected HomePresenterImpl createPresenter() {
        return new HomePresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    public void refresh() {
        mPresenter.loadData(1, true);
    }

    @Override
    public void loadData(int pageIndex) {
        mPresenter.loadData(pageIndex, false);
    }

    @Override
    public void showList(List<YihuoProfile> newPage, boolean isRefresh) {
        if (isRefresh && !yihuoProfileList.isEmpty())
            yihuoProfileList.clear();
        yihuoProfileList.addAll(newPage);
        adapter.setData(yihuoProfileList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void toPostView() {
        // TODO : go to post view
    }

    @Override
    public void hintRequestLogin() {
        // TODO: snack bar fab shake it off
    }

    @Override
    public void loadListHeader() {
        mPresenter.loadListHeader();
    }

    @Override
    public void showListHeader(YihuoProfile data) {

    }

    @Override
    public void hintVolleyError(@NonNull String message) {

    }

    @Override
    public void hintParseError(@NonNull String message) {

    }
}
