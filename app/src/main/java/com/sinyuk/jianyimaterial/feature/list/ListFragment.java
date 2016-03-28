package com.sinyuk.jianyimaterial.feature.list;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.feature.details.DetailsPresenterImpl;
import com.sinyuk.jianyimaterial.mvp.BaseFragment;
import com.sinyuk.jianyimaterial.widgets.MultiSwipeRefreshLayout;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.3.27.
 */
public class ListFragment extends BaseFragment implements IListView {
    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.swipe_refresh_layout)
    MultiSwipeRefreshLayout swipeRefreshLayout;

    public static ListFragment instance;

    @NonNull
    private Toolbar mToolbar;

    private IListPresenter mPresenter;

    public static ListFragment getInstance() {
        if (null == instance){
            instance = new ListFragment();
        }
        return instance;
    }

    public void setPresenter(IListPresenter presenter){
        this.mPresenter = presenter;
    };
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected void onFinishInflate() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.fragment_list;
    }

    @Override
    protected void attachPresenter() {
    }

    @Override
    protected void detachPresenter() {

    }


    @Override
    public void loadMore(int pageIndex) {

    }

    @Override
    public void refresh() {

    }

    @Override
    public void hintVolleyError(@NonNull String message) {

    }

    @Override
    public void hintParseError(@NonNull String message) {

    }
}
