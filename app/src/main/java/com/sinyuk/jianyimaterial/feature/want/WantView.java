package com.sinyuk.jianyimaterial.feature.want;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;

/**
 * Created by Sinyuk on 16.4.20.
 */
public class WantView extends BaseActivity<WantPresenterImpl> implements IWantView {
    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected WantPresenterImpl createPresenter() {
        return new WantPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected void onFinishInflate() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.want_view;
    }
}
