package com.sinyuk.jianyimaterial.common;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.ExtendedRecyclerViewAdapter;
import com.sinyuk.jianyimaterial.api.Index;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.feature.details.DetailsView;
import com.sinyuk.jianyimaterial.ui.GridItemSpaceDecoration;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.utils.FormatUtils;
import com.sinyuk.jianyimaterial.utils.FuzzyDateFormater;
import com.sinyuk.jianyimaterial.utils.HtmlUtils;
import com.sinyuk.jianyimaterial.utils.ImeUtils;
import com.sinyuk.jianyimaterial.utils.NetWorkUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.utils.SuggestionProvider;
import com.sinyuk.jianyimaterial.volley.FormDataRequest;
import com.sinyuk.jianyimaterial.widgets.LabelView;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;
import com.sinyuk.jianyimaterial.widgets.RatioImageView;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout mSwipeRefreshLayout;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    private boolean mIsRequestDataRefresh;
    private String queryStr;
    private QueryListAdapter adapter;

    private List<YihuoProfile> yihuoProfileList = new ArrayList<>();
    private int pageIndex = 1;
    private DrawableRequestBuilder<String> shotRequest;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            queryStr = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(queryStr, null);
            if (TextUtils.isEmpty(queryStr)) { queryEmpty(); }
        }


        shotRequest = Glide.with(mContext).fromString().diskCacheStrategy(DiskCacheStrategy.RESULT).priority(Priority.IMMEDIATE)
                .crossFade().placeholder(R.drawable.image_placeholder_grey300).error(R.drawable.image_placeholder_grey300);
    }

    private void queryEmpty() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_bar_search, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);

        final SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        if (!TextUtils.isEmpty(queryStr)) { searchView.setQueryHint(queryStr); }
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
        return true;
    }


    @Override
    protected int getContentViewID() {
        return R.layout.search_view;
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected boolean isUsingEventBus() {
        return false;
    }

    @Override
    protected void initViews() {
        setToolbarEvent();
        setupSwipeRefreshLayout();
        setupRecyclerView();
//        if (null != getSupportActionBar())
//            getSupportActionBar().setDisplayShowTitleEnabled(false);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mSwipeRefreshLayout.setEnabled(verticalOffset == 0);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            queryStr = intent.getStringExtra(SearchManager.QUERY);
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
            suggestions.saveRecentQuery(queryStr, null);
            try {
                refreshList(queryStr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void initData() {
        try {
            refreshList(queryStr);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void setupRecyclerView() {
        adapter = new QueryListAdapter(mContext);

        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.addItemDecoration(new GridItemSpaceDecoration(2, R.dimen.general_content_space, true, mContext));
        } else {
            recyclerView.addItemDecoration(new GridItemSpaceDecoration(2, R.dimen.tiny_content_space, true, mContext));
        }

        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);


        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        recyclerView.addOnScrollListener(new OnLoadMoreListener(staggeredGridLayoutManager, mSwipeRefreshLayout) {
            @Override
            public void onLoadMore() {
                pageIndex++;
                loadListData(pageIndex);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ImeUtils.hideIme(recyclerView);
            }
        });
    }

    private void setToolbarEvent() {
        final long[] mHits = new long[2];
        toolbar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    // TODO: Toolbar双击事件 一般是快速滑动到顶部( 在有 scrollable-child 的布局中)
                    onToolbarDoubleTap();
                }
            }


        });
    }

    private void onToolbarDoubleTap() {
        if (recyclerView == null) { return; }
        recyclerView.smoothScrollToPosition(0);
    }

    private void setupSwipeRefreshLayout() {
        if (mSwipeRefreshLayout == null) { return; }
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary,
                R.color.themeYellow);
        // do not use lambda!!
        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mIsRequestDataRefresh = true;
                        setRequestDataRefresh(true);
                        if (checkNetWork()) {
                            try {
                                refreshList(queryStr);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            netWorkError();
                            setRequestDataRefresh(false);
                        }
                    }


                });
    }

    private void netWorkError() {
    }


    private boolean checkNetWork() {
        return NetWorkUtils.isNetworkConnection(mContext);
    }


    public void setRequestDataRefresh(boolean requestDataRefresh) {
        if (mSwipeRefreshLayout == null) {
            return;
        }
        if (!requestDataRefresh) {
            mIsRequestDataRefresh = false;
            // TODO: 防止刷新消失太快，让刷新有点存在感
            mSwipeRefreshLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            }, 1000);
        } else {
            mSwipeRefreshLayout.setRefreshing(true);

        }
    }

    private void refreshList(final String query) throws UnsupportedEncodingException {
        setRequestDataRefresh(true);
        // reset pageIndex
        pageIndex = 1;

        FormDataRequest formDataRequest = new FormDataRequest
                (Request.Method.GET, JianyiApi.search(pageIndex, query), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String str) {
                        // the response is already constructed as a JSONObject!
                        final Gson gson = new Gson();
                        JsonParser parser = new JsonParser();
                        final JsonObject response = parser.parse(HtmlUtils.removeHtml(str)).getAsJsonObject();
//                        // 接受最原始的JSON数据
                        Index index = gson.fromJson(response, Index.class);
                        List<YihuoProfile> data = gson.fromJson(index.getData().getItems().toString(),
                                new TypeToken<List<YihuoProfile>>() {
                                }.getType());

                        // do clear
                        if (!yihuoProfileList.isEmpty()) { yihuoProfileList.clear(); }
//
                        yihuoProfileList.addAll(data);
//
                        adapter.setData(yihuoProfileList);
                        adapter.notifyDataSetChanged();

//                        // 刷新成功的回调
                        refreshSucceed();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: SnackBar提示一下 不过分吧

                        refreshFailed(error);
                    }
                });

        Jianyi.getInstance().addRequest(formDataRequest, "ss");
    }

    private void refreshFailed(VolleyError error) {
        setRequestDataRefresh(false);
    }


    private void refreshSucceed() {
        setRequestDataRefresh(false);
    }


    private void loadListData(int pageIndex) {

        FormDataRequest formDataRequest = new FormDataRequest
                (Request.Method.GET, JianyiApi.search(pageIndex, queryStr), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String str) {
                        final Gson gson = new Gson();
                        JsonParser parser = new JsonParser();

                        final JsonObject response = parser.parse(HtmlUtils.removeHtml(str)).getAsJsonObject();

                        Index index = gson.fromJson(response, Index.class);
                        List<YihuoProfile> data = gson.fromJson(index.getData().getItems().toString(),
                                new TypeToken<List<YihuoProfile>>() {
                                }.getType());


                        yihuoProfileList.addAll(data);

                        //
                        adapter.setData(yihuoProfileList);
                        adapter.notifyDataSetChanged();
                        // 刷新成功的回调
                        loadSucceed();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: SnackBar提示一下 不过分吧
                        loadFailed(error);
                    }
                });
        Jianyi.getInstance().addRequest(formDataRequest, "ss");
    }


    private void loadFailed(VolleyError error) {
        setRequestDataRefresh(false);
    }


    private void loadSucceed() {
        setRequestDataRefresh(false);
    }


    public class QueryListAdapter extends ExtendedRecyclerViewAdapter<YihuoProfile, QueryListAdapter.QueryItemViewHolder> {

        public QueryListAdapter(Context context) {
            super(context);
        }

        @Override
        public void footerOnVisibleItem() {

        }

        @Override
        public QueryItemViewHolder onCreateDataItemViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_query, parent, false);
            return new QueryItemViewHolder(v);
        }

        @Override
        public void onBindDataItemViewHolder(final QueryItemViewHolder holder, int position) {
            YihuoProfile itemData = null;
            if (!getData().isEmpty() && getData().get(position) != null) {
                itemData = getData().get(position);
            }

            if (itemData == null) { return; }


            // TODO: initialize cardView
            // 直接在adapter里面操作吧
            if (holder.cardView != null) {
                final YihuoProfile finalItemData = itemData;
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.cardView.setClickable(false); // prevent fast double tap
                        Intent intent = new Intent(mContext, DetailsView.class);
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(DetailsView.YihuoProfile, finalItemData);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                        holder.cardView.postDelayed(() -> holder.cardView.setClickable(true), 300);
                    }
                });
            }
            // TODO: initialize title;
            holder.detailsTv.setText(StringUtils.check(mContext, itemData.getName(), R.string.unknown_title));
            // TODO: initialize newPrice;

            holder.newPriceLabelView.setText(StringUtils.check(mContext, FormatUtils.formatPrice(itemData.getPrice()), R.string.untable));

            // TODO: initialize pubDate;
            try {
                holder.pubDateTv.setText(FuzzyDateFormater.getParsedDate(mContext, itemData.getTime()));
            } catch (ParseException e) {
                holder.pubDateTv.setText(StringUtils.getRes(mContext, R.string.unknown_date));
                e.printStackTrace();
            }
            // TODO: initialize location;
            holder.locationTv.setText(StringUtils.check(mContext, itemData.getSchoolname(), R.string.unknown_location));

            shotRequest.load(JianyiApi.JIANYI + itemData.getPic()).into(holder.shotIv);

        }

        public class QueryItemViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.shot_iv)
            RatioImageView shotIv;
            @Bind(R.id.new_price_label_view)
            LabelView newPriceLabelView;
            @Bind(R.id.details_tv)
            TextView detailsTv;
            @Bind(R.id.location_tv)
            TextView locationTv;
            @Bind(R.id.pub_date_tv)
            TextView pubDateTv;
            @Bind(R.id.card_view)
            CardView cardView;

            public QueryItemViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }


}
