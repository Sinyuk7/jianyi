package com.sinyuk.jianyimaterial.feature.settings.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.events.XLocationSelectEvent;
import com.sinyuk.jianyimaterial.feature.register.RegisterView;
import com.sinyuk.jianyimaterial.fragments.dialogs.LocationSelectDialog;
import com.sinyuk.jianyimaterial.mvp.BaseFragment;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.utils.ToastUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

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
            if (!TextUtils.isEmpty(getArguments().getString("school"))) {
                final int index = Integer.parseInt(getArguments().getString("school"));
                setupSchool(index);
            }

        }
    }

    private void setupSchool(int index) {
        final String[] schools = getResources().getStringArray(R.array.schools_sort);
        if (index >= 0 && index < schools.length) {
            mSchoolBtn.setText(String.format(getString(R.string.settings_school_is), schools[index]));
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
                startActivity(new Intent(mContext, RegisterView.class));
                break;
            case R.id.school_btn:
                LocationSelectDialog dialog = new LocationSelectDialog();
                dialog.show(getChildFragmentManager(), LocationSelectDialog.TAG);
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
                .setConfirmClickListener(sweetAlertDialog -> mPresenter.logout())
                .setTitleText(getString(R.string.settings_hint_confirm_logout))
                .setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLocationSelected(XLocationSelectEvent event) {
        final HashMap<String, String> schoolParam = new HashMap<>();
        schoolParam.put("school", String.valueOf(event.getWhich() + 1));
        mPresenter.update(schoolParam);
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
}
