package com.sinyuk.jianyimaterial.fragments;

/**
 * Created by Sinyuk on 16.2.13.
 */
public class NeedsListFragment {
    /*private static NeedsListFragment instance;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout swipeRefreshLayout;
    private NeedsListAdapter adapter;
    private int pageIndex = 1;
    private List<Need> needsList = new ArrayList<>();

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
                        JNeed jNeeds = gson.fromJson(response.toString(), JNeed.class);
                        // 转换成我的Model
                        List<JNeed.Data.Items> needList = jNeeds.getData().getItems();

                        String trans = gson.toJson(needList);

                        List<Need> firstPage = gson.fromJson(trans,
                                new TypeToken<List<Need>>() {
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
        Jianyi.getInstance().addRequest(jsonRequest, Need.REFRESH_REQUEST);
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
                        JNeed jNeeds = gson.fromJson(response.toString(), JNeed.class);
                        // 转换成我的Model
                        List<JNeed.Data.Items> needList = jNeeds.getData().getItems();

                        String trans = gson.toJson(needList);

                        List<Need> firstPage = gson.fromJson(trans,
                                new TypeToken<List<Need>>() {
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
        Jianyi.getInstance().addRequest(jsonRequest, Need.LOAD_REQUEST);
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
    }*/
}
