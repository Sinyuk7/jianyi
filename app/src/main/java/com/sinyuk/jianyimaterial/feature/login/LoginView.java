package com.sinyuk.jianyimaterial.feature.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.feature.register.RegisterView;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.ImeUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by Sinyuk on 16.3.16.
 */
public class LoginView extends BaseActivity<LoginPresenterImpl> implements ILoginView {

    @Bind(R.id.user_name_et)
    EditText mUserNameEt;
    @Bind(R.id.password_et)
    EditText mPasswordEt;
    @Bind(R.id.login_btn)
    Button mLoginBtn;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    private SweetAlertDialog mDialog;

    @Override
    protected int getContentViewID() {
        return R.layout.login_view;
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
        mCompositeSubscription.add(RxTextView.editorActions(mPasswordEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {
                    if (done) { clickLoginBtn(); }
                }));

        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mPasswordEt).skip(5);
        Observable<CharSequence> phoneNumObservable = RxTextView.textChanges(mUserNameEt).skip(10);

        mCompositeSubscription.add(Observable.combineLatest(passwordObservable, phoneNumObservable, (password, phoneNum) -> {
            if (phoneNum.length() != 11) {
                mUserNameEt.setError("你确定?");
                return false;
            }
            if (password.length() < 6) {
                mPasswordEt.setError("你确定?");
                return false;
            }
            return true;
        }).subscribe(LoginView.this::toggleLoginButton));

        mLoginBtn.setEnabled(false);
        mLoginBtn.setClickable(false);

    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }


    @Override
    public void showProgress() {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        mDialog.setTitleText(StringUtils.getRes(this, R.string.login_hint_in_progress));
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    public void hideProgress() {
        mDialog.dismissWithAnimation();
    }

    /**
     * 錯誤信息的json轉換還是有很多問題的
     * 所以這裡先不用message
     */
    @Override
    public void onLoginSucceed() {
        mDialog.setTitleText(StringUtils.getRes(this, R.string.login_hint_succeed))
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(sweetAlertDialog -> {
                    sweetAlertDialog.dismiss();
                    myHandler.postDelayed(this::finish, AnimUtils.ANIMATION_TIME_MEDIUM);
                })
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }

    @Override
    public void onLoginFailed(String message) {
        mDialog.setTitleText(StringUtils.check(this, message, R.string.login_hint_failed))
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
    }

    @Override
    public void onNetworkError(String message) {
        mDialog.setTitleText(StringUtils.check(this, message, R.string.hint_network_error))
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    @OnClick(R.id.forget_psw_tv)
    public void clickForgetPsw(View view) {
        SweetAlertDialog dialog = new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        dialog.setTitleText(getString(R.string.login_hint_contact_with_admin))
                .setCustomImage(getResources().getDrawable(R.drawable.expression_9))
                .setConfirmText(getString(R.string.login_hint_confirm))
                .setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }

    @OnClick(R.id.login_btn)
    public void clickLoginBtn() {
        mPasswordEt.setError(null);
        mUserNameEt.setError(null);
        final String userName = mUserNameEt.getText().toString();
        final String password = mPasswordEt.getText().toString();
        ImeUtils.hideIme(mCoordinatorLayout);
        showProgress();
        mPresenter.attemptLogin(userName, password);
    }

    // 微信登录
    @OnClick(R.id.access_wechat)
    public void clickContinueWithWechat() {
        Intent toRegisterView = new Intent(LoginView.this, RegisterView.class);
        Bundle bundle = new Bundle();
        bundle.putString(RegisterView.TYPE, RegisterView.REGISTER);
        toRegisterView.putExtras(bundle);
        startActivity(toRegisterView);
        myHandler.postDelayed(this::finish, AnimUtils.ANIMATION_TIME_MEDIUM);
    }

    private void toggleLoginButton(boolean hasInput) {
        mLoginBtn.setEnabled(hasInput);
        mLoginBtn.setClickable(hasInput);
        if (hasInput) {
            mLoginBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_accent));
        } else {
            mLoginBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_grey));
        }
    }
}
