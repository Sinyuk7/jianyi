package com.sinyuk.jianyimaterial.feature.info;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sinyuk on 16.4.16.
 */
public class InfoView extends BaseActivity<InfoPresenterImpl> implements IInfoView {
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.avatar_girl)
    ImageView mAvatarGirl;
    @Bind(R.id.avatar_boy)
    ImageView mAvatarBoy;
    @Bind(R.id.view_switcher)
    ViewSwitcher mViewSwitcher;
    @Bind(R.id.hint_set_avatar)
    TextView mHintSetAvatar;
    @Bind(R.id.female_flag)
    ImageView mFemaleFlag;
    @Bind(R.id.male_flag)
    ImageView mMaleFlag;
    @Bind(R.id.user_name_et)
    EditText mUserNameEt;
    @Bind(R.id.user_name_input_area)
    TextInputLayout mUserNameInputArea;
    @Bind(R.id.location_et)
    EditText mLocationEt;
    @Bind(R.id.school_input_area)
    TextInputLayout mSchoolInputArea;
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
    protected InfoPresenterImpl createPresenter() {
        return new InfoPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected void onFinishInflate() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.info_view;
    }

}
