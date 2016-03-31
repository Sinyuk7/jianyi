package com.sinyuk.jianyimaterial.feature.drawer;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.entity.User;


/**
 * Created by Sinyuk on 16.3.30.
 */
public class DrawerView extends MyMenuFragment<DrawerPresenterImpl> implements IDrawerView {
    private static DrawerView sInstance;

    public static DrawerView getInstance() {
        if (null == sInstance)
            sInstance = new DrawerView();
        return sInstance;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected DrawerPresenterImpl createPresenter() {
        return new DrawerPresenterImpl();
    }


    @Override
    protected void onFinishInflate() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.drawer_view;
    }

    @Override
    public void onLoginStateUpdate(boolean hasLogged) {

    }

    @Override
    public void showLoginHeader() {

    }

    @Override
    public void showLoggedHeader(User data) {

    }

    @Override
    public void showMessageBadge() {

    }

    @Override
    public void toPersonalView() {

    }

    @Override
    public void toLoginView() {

    }
}
