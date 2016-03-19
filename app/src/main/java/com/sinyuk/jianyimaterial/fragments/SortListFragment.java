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
import com.sinyuk.jianyimaterial.events.SelectionsUpdateEvent;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.ui.GridItemSpaceDecoration;
import com.sinyuk.jianyimaterial.ui.OnLoadMoreListener;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.volley.JsonRequest;
import com.sinyuk.jianyimaterial.volley.VolleyErrorHelper;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.1.6.
 */
public class SortListFragment extends SwipeRefreshFragment {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout swipeRefreshLayout;


    private int currentPageIndex = 1;
    private int parentSortIndex;
    private String parentSortSelection;


    String childSortSelection;  // 小类
    String optionSelection; // 价格 发布时间 等等
    String schoolSelection; // 筛选的学校

    private CardListAdapter adapter;

    private List<YihuoProfile> yihuoProfileList = new ArrayList<>();
    private int pageIndex = 1;
    private String order = "time_desc"; // defualt value
    private int schoolIndex = 1;
    private String childSort = "all";

    public SortListFragment() {

    }

    public static SortListFragment newInstance(int parentSortIndex) {
        SortListFragment fragment = new SortListFragment();
        Bundle args = new Bundle();
        args.putInt("parent_sort_index", parentSortIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentSortIndex = getArguments().getInt("parent_sort_index", 0);

        parentSortSelection = mContext.getResources().getStringArray(R.array.category_menu_items)[parentSortIndex];

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_sort_list;
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


    @Override
    protected boolean needFastOnTop() {
        return false;
    }


    private void setupRecyclerView() {
        adapter = new CardListAdapter(mContext);

        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            recyclerView.addItemDecoration(new GridItemSpaceDecoration(2, R.dimen.general_content_space, true, mContext));
        } else {
            recyclerView.addItemDecoration(new GridItemSpaceDecoration(2, R.dimen.tiny_content_space, true, mContext));
        }

        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);


        if (swipeRefreshLayout != null) {
            recyclerView.addOnScrollListener(new OnLoadMoreListener(staggeredGridLayoutManager, swipeRefreshLayout) {
                @Override
                public void onLoadMore() {
                    pageIndex++;
                    loadListData(pageIndex);
                }
            });
        }
    }


    @Override
    protected void netWorkError() {

    }

    @Override
    protected void refreshList() {
        // reset pageIndex
        pageIndex = 1;

        String url = null;
        try {
            url = JianyiApi.yihuoBySort(pageIndex, URLEncoder.encode(parentSortSelection,"UTF-8"))
                    + "&sort=" + URLEncoder.encode(childSort,"UTF-8")
                    + "&order=" + order
                    + "&school=" + schoolIndex;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, url, null,new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!

                        final Gson gson = new Gson();
//                        // 接受最原始的JSON数据
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
                        refreshFailed(error);
                        ToastUtils.toastSlow(mContext, VolleyErrorHelper.getMessage(error));
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
        String url = null;
        try {
            url = JianyiApi.yihuoBySort(pageIndex, URLEncoder.encode(parentSortSelection,"UTF-8"))
                    + "&sort=" + URLEncoder.encode(childSort,"UTF-8")
                    + "&order=" + order
                    + "&school=" + schoolIndex;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSelectionUpdate(SelectionsUpdateEvent event) {
        // TODO: 等后台有了学校和子类筛选的接口的时候再做
        if (event.getSchoolPos() != (schoolIndex - 1)) {
            schoolIndex = event.getSchoolPos() + 1;
            refreshList();
        }
        if (!event.getOrder().equals(order)) {
            order = event.getOrder();
            refreshList();
        }
//        if (event.getChildSortStr() != null && !event.getChildSortStr().equals(childSort)) {
//            childSort = event.getChildSortStr();
//            refreshList();
//        }
    }
}
