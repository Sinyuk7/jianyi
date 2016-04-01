package com.sinyuk.jianyimaterial.feature.login;

import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.feature.register.RegisterView;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.utils.ImeUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.3.16.
 */
public class LoginView extends BaseActivity<LoginPresenterImpl> implements ILoginView {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.user_name_et)
    EditText userNameEt;
    @Bind(R.id.user_name_input_area)
    TextInputLayout userNameInputArea;
    @Bind(R.id.password_et)
    EditText passwordEt;
    @Bind(R.id.password_input_area)
    TextInputLayout passwordInputArea;
    @Bind(R.id.forget_psw_tv)
    TextView forgetPswTv;
    @Bind(R.id.login_btn)
    Button loginBtn;
    @Bind(R.id.access_wechat)
    TextView accessWechat;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView nestedScrollView;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    private SweetAlertDialog pDialog;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_sign_in;
    }

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected LoginPresenterImpl createPresenter() {
        return new LoginPresenterImpl();
    }

    @Override
    protected void onFinishInflate() {

        // 输了密码才给登录 不过分吧?
        mCompositeSubscription.add(RxTextView.textChanges(passwordEt).map(s -> !TextUtils.isEmpty(s)).subscribe(this::toggleLoginButton));
        // 监听软键盘
        mCompositeSubscription.add(RxTextView.editorActions(passwordEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {
                    if (done) { clickLoginBtn(); }
                }));


    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }


    @Override
    public void showProgress() {
        pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        pDialog.setTitleText(StringUtils.getRes(this, R.string.login_hint_in_progress));
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    public void hideProgress() {
        pDialog.dismissWithAnimation();
    }

    /**
     * 錯誤信息的json轉換還是有很多問題的
     * 所以這裡先不用message
     */
    @Override
    public void onLoginSucceed() {
        pDialog.setTitleText(StringUtils.getRes(this, R.string.login_hint_succeed))
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismiss();
                    finish();
                })
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }

    @Override
    public void onLoginFailed(String message) {
        pDialog.setTitleText(StringUtils.check(this, message, R.string.login_hint_failed))
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
    }

    @Override
    public void onNetworkError(String message) {
        pDialog.setTitleText(StringUtils.check(this, message, R.string.hint_network_error))
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    // 忘记密码
    @OnClick(R.id.forget_psw_tv)
    public void clickForgetPsw(View view) {
        Intent toRegisterView = new Intent(LoginView.this, RegisterView.class);
        toRegisterView.putExtra(RegisterView.USER_INTENT, RegisterView.FORGET_PASSWORD);
        startActivity(toRegisterView);
    }

    // 登录按钮
    @OnClick(R.id.login_btn)
    public void clickLoginBtn() {
        // Reset errors.
        passwordEt.setError(null);
        userNameEt.setError(null);
        // Store values at the time of the login attempt.
        String userName = userNameEt.getText().toString();
        String password = passwordEt.getText().toString();
        boolean cancel = false;
        if (TextUtils.isEmpty(userName)) {
            userNameEt.setError(StringUtils.getRes(this, R.string.alert_null_username));
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEt.setError(StringUtils.getRes(this, R.string.alert_null_password));
            cancel = true;
        }
        if (!cancel) {
            ImeUtils.hideIme(coordinatorLayout);
            showProgress();
            mPresenter.attemptLogin(userName, password);

        }
    }

    // 微信登录
    @OnClick(R.id.access_wechat)
    public void clickContinueWithWechat() {
        Intent toRegisterView = new Intent(LoginView.this, RegisterView.class);
        toRegisterView.putExtra(RegisterView.USER_INTENT, RegisterView.FORGET_PASSWORD);
        startActivity(toRegisterView);
    }

    private void toggleLoginButton(boolean hasInput) {
        loginBtn.setEnabled(hasInput);
        loginBtn.setClickable(hasInput);
        if (hasInput) {
            loginBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_accent));
        } else {
            loginBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_grey));
        }
    }
}
