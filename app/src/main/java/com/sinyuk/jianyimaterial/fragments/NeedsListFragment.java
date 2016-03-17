package com.sinyuk.jianyimaterial.fragments;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.NeedsListAdapter;
import com.sinyuk.jianyimaterial.api.JNeeds;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.base.SwipeRefreshFragment;
import com.sinyuk.jianyimaterial.entity.Needs;
import com.sinyuk.jianyimaterial.volley.JsonRequest;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sinyuk on 16.2.13.
 */
public class NeedsListFragment extends SwipeRefreshFragment {
    private static NeedsListFragment instance;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout swipeRefreshLayout;
    private NeedsListAdapter adapter;
    private int pageIndex = 1;
    private List<Needs> needsList = new ArrayList<>();

    synchronized public static NeedsListFragment getInstance() {
        if (null == instance)
            instance = new NeedsListFragment();
        return instance;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_needs_list;
    }

    @Override
    protected void initViewsAndEvent() {
        super.initViewsAndEvent();
        setupRecyclerView();

        // 在创建Fragment的时候 请求加载数据 加一个在onResume里面需不需要重新刷新的选项
        refreshList();
    }

    private void setupRecyclerView() {
        adapter = new NeedsListAdapter(mContext);

        recyclerView.setAdapter(adapter);


        recyclerView.setItemAnimator(new DefaultItemAnimator());

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(linearLayoutManager);


        recyclerView.addOnScrollListener(new OnLoadMoreListener(linearLayoutManager, swipeRefreshLayout) {
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

    }

    @Override
    protected void refreshList() {
        // reset pageIndex
        pageIndex = 1;

        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.needs(pageIndex), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        final Gson gson = new Gson();
                        // 接受最原始的JSON数据
                        JNeeds jNeeds = gson.fromJson(response.toString(), JNeeds.class);
                        // 转换成我的Model
                        List<JNeeds.Data.Items> items = jNeeds.getData().getItems();

                        String trans = gson.toJson(items);

                        List<Needs> firstPage = gson.fromJson(trans,
                                new TypeToken<List<Needs>>() {
                                }.getType());

                        // do clear
                        if (!needsList.isEmpty())
                            needsList.clear();

                        needsList.addAll(firstPage);

                        //
                        adapter.setData(needsList);
                        adapter.notifyDataSetChanged();
                        // 刷新成功的回调
                        refreshSucceed();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: SnackBar提示一下 不过分吧
//                            EventBus.getDefault().post(new RefreshCallback(false));
                        refreshFailed(error);
                    }
                });
        Jianyi.getInstance().addRequest(jsonRequest, Needs.REFRESH_REQUEST);
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
                (Request.Method.GET, JianyiApi.needs(pageIndex), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        final Gson gson = new Gson();
                        // 接受最原始的JSON数据
                        JNeeds jNeeds = gson.fromJson(response.toString(), JNeeds.class);
                        // 转换成我的Model
                        List<JNeeds.Data.Items> items = jNeeds.getData().getItems();

                        String trans = gson.toJson(items);

                        List<Needs> firstPage = gson.fromJson(trans,
                                new TypeToken<List<Needs>>() {
                                }.getType());

                        needsList.addAll(firstPage);
                        //
                        adapter.setData(needsList);
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
        Jianyi.getInstance().addRequest(jsonRequest, Needs.LOAD_REQUEST);
    }

    @Override
    protected void loadFailed(VolleyError error) {
        setRequestDataRefresh(false);
    }

    @Override
    protected void loadSucceed() {
        setRequestDataRefresh(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
