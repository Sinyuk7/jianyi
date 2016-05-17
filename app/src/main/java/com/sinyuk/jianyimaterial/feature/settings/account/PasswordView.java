package com.sinyuk.jianyimaterial.feature.settings.account;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.widgets.psdloadingview.EatAnimate;
import com.sinyuk.jianyimaterial.widgets.psdloadingview.IAnimate;
import com.sinyuk.jianyimaterial.widgets.psdloadingview.PsdLoadingView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.5.17.
 */
public class PasswordView extends BaseActivity<AccountPresenterImpl> implements IPasswordView {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.old_password_et)
    EditText mOldPasswordEt;
    @Bind(R.id.old_password_layout)
    TextInputLayout mOldPasswordLayout;
    @Bind(R.id.new_password_et)
    PsdLoadingView mNewPasswordEt;

    @Bind(R.id.confirm_btn)
    Button mConfirmBtn;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView mNestedScrollView;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected AccountPresenterImpl createPresenter() {
        return new AccountPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected void onFinishInflate() {
        setupPasswordView();
    }

    private void setupPasswordView() {
        IAnimate iAnimate = new EatAnimate();
        mNewPasswordEt.init(iAnimate);
        mNewPasswordEt.setDuration(5000);
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.password_view;
    }

    @OnClick(R.id.confirm_btn)
    public void confirm() {
        mNewPasswordEt.startLoading();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
