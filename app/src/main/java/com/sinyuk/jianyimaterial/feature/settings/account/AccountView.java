package com.sinyuk.jianyimaterial.feature.settings.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.feature.login.LoginView;
import com.sinyuk.jianyimaterial.feature.register.RegisterView;
import com.sinyuk.jianyimaterial.mvp.BaseFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.4.12.
 */
public class AccountView extends BaseFragment<AccountPresenterImpl> implements IAccountPresenter {
    private static AccountView instance;

    public static AccountView getInstance() {
        if (null == instance) { instance = new AccountView(); }
        return instance;
    }

    public static AccountView newInstance(Bundle args) {
        AccountView fragment = new AccountView();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected void onFinishInflate() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.settings_view_account;
    }

    @Override
    protected AccountPresenterImpl createPresenter() {
        return new AccountPresenterImpl();
    }

    @OnClick({R.id.password_btn, R.id.school_btn, R.id.logout_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.password_btn:
                startActivity(new Intent(mContext, RegisterView.class));
                break;
            case R.id.school_btn:
                break;
            case R.id.logout_btn:
                break;
        }
    }
}
