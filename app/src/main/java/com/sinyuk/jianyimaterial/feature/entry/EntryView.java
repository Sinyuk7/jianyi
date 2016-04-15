package com.sinyuk.jianyimaterial.feature.entry;

import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.entity.School;
import com.sinyuk.jianyimaterial.feature.drawer.DrawerView;
import com.sinyuk.jianyimaterial.feature.home.HomeView;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.model.SchoolModel;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.utils.LogUtils;

import java.util.List;

import butterknife.Bind;

/**
 * Created by Sinyuk on 16.3.30.
 */
public class EntryView extends BaseActivity<EntryPresenterImpl> implements IEntryView, SchoolModel.LoadSchoolsCallback {
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
    protected void onFinishInflate() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.container_menu, DrawerView.getInstance()).commit();
        fm.beginTransaction().replace(R.id.home_view, HomeView.getInstance()).commit();
        SchoolModel.getInstance(this).getSchools(this);
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

    @Override
    public void onLoadSchoolSucceed(List<School> schoolList) {
        for (int i = 0; i < schoolList.size(); i++) {
            LogUtils.simpleLog(EntryView.class, schoolList.get(i).getName());
        }
    }

    @Override
    public void onLoadSchoolParseError(String message) {
        LogUtils.simpleLog(EntryView.class, message);
    }

    @Override
    public void onLoadSchoolVolleyError(String message) {
        LogUtils.simpleLog(EntryView.class, message);
    }
}
