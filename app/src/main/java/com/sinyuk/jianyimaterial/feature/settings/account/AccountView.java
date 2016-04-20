package com.sinyuk.jianyimaterial.feature.settings.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.entity.School;
import com.sinyuk.jianyimaterial.events.XSchoolSelectedEvent;
import com.sinyuk.jianyimaterial.feature.dialog.SchoolDialog;
import com.sinyuk.jianyimaterial.mvp.BaseFragment;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.4.12.
 */
public class AccountView extends BaseFragment<AccountPresenterImpl> implements IAccountView {
    private static AccountView instance;
    @Bind(R.id.tel_tv)
    TextView mTelTv;
    @Bind(R.id.password_btn)
    TextView mPasswordBtn;
    @Bind(R.id.school_btn)
    TextView mSchoolBtn;
    @Bind(R.id.logout_btn)
    TextView mLogoutBtn;
    @Bind(R.id.settings_items)
    LinearLayout mSettingsItems;
    private List<School> mSchoolList;

    public static AccountView getInstance() {
        if (null == instance) { instance = new AccountView(); }
        return instance;
    }

    public static AccountView newInstance(Bundle args) {
        instance = new AccountView();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Override
    protected void beforeInflate() {
    }

    @Override
    protected void onFinishInflate() {
        if (null != getArguments()) {
            mTelTv.setText(String.format(getString(R.string.settings_tel_is), getArguments().getString("tel", "未知")));
            mPresenter.fetchSchoolList();
        }
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
                /*startActivity(new Intent(mContext, RegisterView.class));*/
                ToastUtils.toastFast(mContext, getString(R.string.common_hint_feature_not_finished));
                break;
            case R.id.school_btn:
                SchoolDialog dialog = SchoolDialog.getInstance();
                dialog.show(getChildFragmentManager(),SchoolDialog.TAG);
                break;
            case R.id.logout_btn:
                showLogoutAlert();
                break;
        }
    }

    private void showLogoutAlert() {
        SweetAlertDialog dialog = new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE);
        dialog.setCancelText(getString(R.string.settings_hint_cancel))
                .setConfirmText(getString(R.string.settings_hint_confirm))
                .setConfirmClickListener(sweetAlertDialog -> {
                    mPresenter.logout();
                    sweetAlertDialog.dismissWithAnimation();
                    getActivity().finish();
                })
                .setTitleText(getString(R.string.settings_hint_confirm_logout))
                .setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSchoolSelected(XSchoolSelectedEvent event) {
        updateSchoolTv(event.getSchoolIndex());
        final HashMap<String, String> schoolParam = new HashMap<>();
        schoolParam.put("school", event.getSchoolIndex());
        mPresenter.update(schoolParam);
    }

    private void updateSchoolTv(String schoolIndex) {
        final int index = Integer.valueOf(schoolIndex);
        try {
            mSchoolBtn.setText(String.format(getString(R.string.settings_school_is), mSchoolList.get(index - 1).getName()));
        } catch (Exception e) {
            e.printStackTrace();
            mSchoolBtn.setText(String.format(getString(R.string.settings_school_is), "加载失败"));
        }

    }

    @Override
    public void onUpdateSucceed(String message) {
        ToastUtils.toastFast(mContext, message);
    }

    @Override
    public void onUpdateFailed(String message) {
        ToastUtils.toastFast(mContext, message);
    }

    @Override
    public void onUpdateVolleyError(String message) {
        ToastUtils.toastFast(mContext, message);
    }

    @Override
    public void onUpdateParseError(String message) {
        ToastUtils.toastFast(mContext, message);
    }

    @Override
    public void onLoadSchoolSucceed(List<School> schoolList) {
        mSchoolList = schoolList;
        // 加载列表之后 根据index显示学校
        updateSchoolTv(getArguments().getString("school"));
    }

    @Override
    public void onLoadSchoolParseError(String message) {
        ToastUtils.toastFast(mContext, message);
    }

    @Override
    public void onLoadSchoolVolleyError(String message) {
        ToastUtils.toastFast(mContext, message);
    }
}
