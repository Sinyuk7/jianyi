package com.sinyuk.jianyimaterial.feature.entry;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;

import com.mxn.soul.flowingdrawer_core.FlowingView;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.feature.drawer.DrawerView;
import com.sinyuk.jianyimaterial.feature.home.HomeView;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.3.30.
 */
public class EntryView extends BaseActivity<EntryPresenterImpl> implements IEntryView {
    @Bind(R.id.home_view)
    CoordinatorLayout mHomeView;
    @Bind(R.id.flowing_view)
    FlowingView mFlowingView;
    @Bind(R.id.container_menu)
    FrameLayout mContainerMenu;
    @Bind(R.id.left_drawer_layout)
    LeftDrawerLayout mLeftDrawerLayout;

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected EntryPresenterImpl createPresenter() {
        return new EntryPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected void onFinishInflate() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.container_menu, DrawerView.getInstance()).commit();
        mLeftDrawerLayout.setFluidView(mFlowingView);
        mLeftDrawerLayout.setMenuFragment(DrawerView.getInstance());

        fm.beginTransaction().add(R.id.home_view, HomeView.getInstance()).commit();
    }

    @Override
    protected int getContentViewID() {
        return R.layout.entry_view;
    }

}
