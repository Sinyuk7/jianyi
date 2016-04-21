package com.sinyuk.jianyimaterial.feature.entry;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.feature.drawer.DrawerView;
import com.sinyuk.jianyimaterial.feature.home.HomeView;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.3.30.
 */
public class EntryView extends BaseActivity<EntryPresenterImpl> implements IEntryView {
    @Bind(R.id.home_view)
    CoordinatorLayout mHomeView;
    private long attemptExitTime;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.container_menu, DrawerView.getInstance()).commit();
            fm.beginTransaction().replace(R.id.home_view, HomeView.getInstance()).commit();
        }
    }

    @Override
    protected void onFinishInflate() {
//        SchoolModel.getInstance(this).getSchools(this);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.entry_view;
    }

    @Override
    public void onBackPressed() {

        if (confirmExit()) { super.onBackPressed(); }

    }

    private boolean confirmExit() {
        if ((System.currentTimeMillis() - attemptExitTime) > 1000) {
            SnackBarFactory.succeedNoAction(this, mHomeView, "再按一次退出").show();
            attemptExitTime = System.currentTimeMillis();
            return false;
        }
        return true;
    }
}
