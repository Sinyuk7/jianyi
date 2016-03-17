package com.sinyuk.jianyimaterial.features.login;

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

import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.utils.ImeUtils;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.3.16.
 */
public class LoginView extends BaseActivity implements ILoginView {

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


    private LoginPresenterImpl loginPresenter;

    @Override
    protected int getContentViewID() {
        return R.layout.activity_sign_in;
    }

    @Override
    protected void onFinishInflate() {

        // 输了密码才给登录 不过分吧?
        mCompositeSubscription.add(RxTextView.textChanges(passwordEt).map(s -> !TextUtils.isEmpty(s)).subscribe(this::toggleLoginButton));

        mCompositeSubscription.add(RxTextView.editorActions(passwordEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {
                    if (done) clickLoginBtn();
                }));

        mCompositeSubscription.add(RxToolbar.navigationClicks(toolbar).subscribe(this::onClickBackArrow));
    }

    public void onClickBackArrow(Void v) {

    }

    @Override
    protected void attachPresenter() {
        loginPresenter = new LoginPresenterImpl();
        loginPresenter.attachView(this);
    }

    @Override
    protected void detachPresenter() {
        loginPresenter.detachView();
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void onLoginSucceed() {
        hideProgress();
    }

    @Override
    public void onLoginFailed(String message) {
        hideProgress();
    }

    @Override
    public void onNetworkError(String message) {
        hideProgress();
    }

    // 忘记密码
    @OnClick(R.id.forget_psw_tv)
    public void clickForgetPsw(View view) {

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
            userNameEt.setError("用户名不能为空");
            cancel = true;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEt.setError("密码不能为空");
            cancel = true;
        }
        if (!cancel) {
            ImeUtils.hideIme(coordinatorLayout);
            showProgress();
            loginPresenter.attemptLogin(userName, password);

        }
    }

    // 微信登录
    @OnClick(R.id.access_wechat)
    public void clickContinueWithWechat() {

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
