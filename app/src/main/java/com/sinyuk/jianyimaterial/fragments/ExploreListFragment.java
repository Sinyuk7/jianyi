package com.sinyuk.jianyimaterial.fragments;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.CardListAdapter;
import com.sinyuk.jianyimaterial.api.Index;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.base.SwipeRefreshFragment;
import com.sinyuk.jianyimaterial.model.YihuoProfile;
import com.sinyuk.jianyimaterial.ui.GridItemSpaceDecoration;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.volley.JsonRequest;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.2.13.
 */
public class ExploreListFragment extends SwipeRefreshFragment {
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout swipeRefreshLayout;
    private String categoryTitle = "all";
    private CardListAdapter adapter;

    private List<YihuoProfile> yihuoProfileList = new ArrayList<>();
    private int pageIndex;

    public static ExploreListFragment newInstance(String title) {
        ExploreListFragment fragment = new ExploreListFragment();
        Bundle args = new Bundle();
        args.putString("category_title", title);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        categoryTitle = getArguments().getString("category_title");
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_explore_list;
    }

    @Override
    protected boolean isUsingEventBus() {
        return super.isUsingEventBus();
    }

    @Override
    protected void initViewsAndEvent() {
        super.initViewsAndEvent();

        setupRecyclerView();

        // refresh when create
        refreshList();
    }

    private void setupRecyclerView() {
        adapter = new CardListAdapter(mContext);

        recyclerView.setAdapter(adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.addItemDecoration(new GridItemSpaceDecoration(2, R.dimen.general_content_space, true, mContext));
        } else {
            recyclerView.addItemDecoration(new GridItemSpaceDecoration(2, R.dimen.tiny_content_space, true, mContext));
        }
        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);


        recyclerView.addOnScrollListener(new OnLoadMoreListener(staggeredGridLayoutManager, swipeRefreshLayout) {
            @Override
            public void onLoadMore() {
                pageIndex++;
                loadListData(pageIndex);
            }
        });
    }

    @Override
    protected boolean needFastOnTop() {
        return true;
    }

    @Override
    protected void netWorkError() {
        setRequestDataRefresh(false);
    }

    @Override
    protected void refreshList() {
        // reset pageIndex
        pageIndex = 1;

        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.yihuoByTabs(categoryTitle, pageIndex), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        final Gson gson = new Gson();
                        // 接受最原始的JSON数据
                        Index index = gson.fromJson(response.toString(), Index.class);
                        // 转换成我的Model
                        List<Index.Data.Items> items = index.getData().getItems();

                        String trans = gson.toJson(items);

                        List<YihuoProfile> firstPage = gson.fromJson(trans,
                                new TypeToken<List<YihuoProfile>>() {
                                }.getType());

                        // do clear
                        if (!yihuoProfileList.isEmpty())
                            yihuoProfileList.clear();

                        yihuoProfileList.addAll(firstPage);

                        //
                        adapter.setData(yihuoProfileList);
                        adapter.notifyDataSetChanged();
                        // 刷新成功的回调
                        refreshSucceed();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: SnackBar提示一下 不过分吧
                        refreshFailed(error);
                    }
                });
        Jianyi.getInstance().addRequest(jsonRequest, YihuoProfile.REFRESH_REQUEST);
    }

    @Override
    protected void refreshFailed(VolleyError error) {
        setRequestDataRefresh(false);
    }

    @Override
    protected void refreshSucceed() {
        setRequestDataRefresh(false);
    }

    @Override
    protected void loadListData(int pageIndex) {
        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.yihuoByTabs(categoryTitle, pageIndex), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        final Gson gson = new Gson();
                        // 接受最原始的JSON数据
                        Index index = gson.fromJson(response.toString(), Index.class);
                        // 转换成我的Model
                        List<Index.Data.Items> items = index.getData().getItems();

                        String trans = gson.toJson(items);

                        List<YihuoProfile> newPage = gson.fromJson(trans,
                                new TypeToken<List<YihuoProfile>>() {
                                }.getType());


                        yihuoProfileList.addAll(newPage);

                        //
                        adapter.setData(yihuoProfileList);
                        adapter.notifyDataSetChanged();

                        loadSucceed();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: SnackBar提示一下 不过分吧
                        loadFailed(error);
                    }
                });
        Jianyi.getInstance().addRequest(jsonRequest, YihuoProfile.LOAD_REQUEST);
    }

    @Override
    protected void loadFailed(VolleyError error) {
        setRequestDataRefresh(false);
    }

    @Override
    protected void loadSucceed() {
        setRequestDataRefresh(false);
    }
}
