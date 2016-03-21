package com.sinyuk.jianyimaterial.fragments;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.adapters.CommentsAdapter;
import com.sinyuk.jianyimaterial.base.BaseFragment;

import butterknife.Bind;

/**
 * Created by Sinyuk on 15.12.19.
 */
public class CommentsFragment extends BaseFragment {

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;

    private CommentsAdapter adapter;

    private static CommentsFragment instance;

    public static CommentsFragment getInstance() {
        if (null == instance)
            instance = new CommentsFragment();
        return instance;
    }


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_comments;
    }

    @Override
    protected boolean isUsingEventBus() {
        return false;
    }


    @Override
    protected void initViewsAndEvent() {
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        adapter = new CommentsAdapter(mContext);

        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);

        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        adapter.setData(new String[10]);

        final View commentListHeader = View.inflate(mContext,R.layout.include_product_details_socials,null);

        adapter.setHeaderView(commentListHeader);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    adapter.lockEnterAnimation(true);
                }
            }
        });

    }

}
