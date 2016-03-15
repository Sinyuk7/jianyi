package com.sinyuk.jianyimaterial.fragments;


import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.activities.HomeActivity;
import com.sinyuk.jianyimaterial.adapters.CardListAdapter;
import com.sinyuk.jianyimaterial.api.Index;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.base.SwipeRefreshFragment;
import com.sinyuk.jianyimaterial.model.YihuoProfile;
import com.sinyuk.jianyimaterial.utils.NetWorkUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.volley.JsonRequest;
import com.sinyuk.jianyimaterial.ui.HeaderItemSpaceDecoration;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 *
 */
public class HomeListFragment extends SwipeRefreshFragment {

    private static HomeListFragment instance;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private CardListAdapter adapter;

    private List<YihuoProfile> yihuoProfileList = new ArrayList<>();
    private int pageIndex = 1;


    public static HomeListFragment getInstance() {
        if (null == instance)
            instance = new HomeListFragment();
        return instance;
    }


    public HomeListFragment() {
        // Required empty public constructor
    }

    // yao
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_home_list;
    }

    // yao
    @Override
    protected boolean isUsingEventBus() {
        return super.isUsingEventBus();
    }

    // yao
    @Override
    protected void initViewsAndEvent() {
        super.initViewsAndEvent();

        setupRecyclerView();

        refreshList();
    }

    @Override
    protected boolean needFastOnTop() {
        return true;
    }


    @Override
    protected void netWorkError() {

    }

    private void setupRecyclerView() {
        adapter = new CardListAdapter(mContext);

        recyclerView.setAdapter(adapter);


        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.addItemDecoration(new HeaderItemSpaceDecoration(2, R.dimen.general_content_space, true, mContext));
        }else {
            recyclerView.addItemDecoration(new HeaderItemSpaceDecoration(2, R.dimen.tiny_content_space, true, mContext));
        }

        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        final View headView = LayoutInflater.from(mContext).inflate(R.layout.include_home_daily_edition, recyclerView, false);
//
        adapter.setHeaderViewFullSpan(headView);


        recyclerView.addOnScrollListener(new OnLoadMoreListener(staggeredGridLayoutManager, swipeRefreshLayout) {
            @Override
            public void onLoadMore() {
                pageIndex++;
                loadListData(pageIndex);
            }
        });
    }


    protected void refreshList() {
        // reset pageIndex
        pageIndex = 1;

        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.yihuoAll(pageIndex), null, new Response.Listener<JSONObject>() {
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
//                            EventBus.getDefault().post(new RefreshCallback(true));
                        refreshSucceed();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        refreshFailed(error);
                    }
                });
        Jianyi.getInstance().addRequest(jsonRequest, YihuoProfile.REFRESH_REQUEST);
    }


    protected void refreshFailed(@Nullable VolleyError error) {
        ToastUtils.toastSlow(mContext, VolleyErrorHelper.getMessage(error,mContext));
        setRequestDataRefresh(false);
    }

    @Override
    protected void refreshSucceed() {
        setRequestDataRefresh(false);
    }


    protected void loadListData(int pageIndex) {

        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.yihuoAll(pageIndex), null, new Response.Listener<JSONObject>() {
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
                        // 刷新成功的回调
                        loadSucceed();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loadFailed(error);
                    }
                });
        Jianyi.getInstance().addRequest(jsonRequest, YihuoProfile.LOAD_REQUEST);
    }

    protected void loadFailed(VolleyError error) {
        swipeRefreshLayout.setRefreshing(false);
        ToastUtils.toastSlow(mContext, VolleyErrorHelper.getMessage(error,mContext));
    }

    protected void loadSucceed() {
        swipeRefreshLayout.setRefreshing(false);
    }


}
