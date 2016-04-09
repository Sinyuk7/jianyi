package com.sinyuk.jianyimaterial.feature.register;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.utils.StringUtils;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by Sinyuk on 16.3.19.
 */
public class RegisterView extends BaseActivity<RegisterPresenterImpl> implements IRegisterView {

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.phone_number_et)
    EditText mPhoneNumberEt;
    @Bind(R.id.authenticode_et)
    EditText mAuthenticodeEt;
    @Bind(R.id.get_authenticode_btn)
    Button mGetAuthenticodeBtn;
    @Bind(R.id.password_et)
    EditText mPasswordEt;
    @Bind(R.id.register_btn)
    Button mRegisterBtn;
    @Bind(R.id.password_text_layout)
    TextInputLayout passwordTextLayout;
    @Bind(R.id.authenticode_text_layout)
    LinearLayout authenticodeTextLayout;
    @Bind(R.id.hint_continue_with_wechat)
    TextView mHintContinueWithWechat;
    private SweetAlertDialog mDialog;
    private boolean mIsAuthenticated = false;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {}

    @Override
    protected RegisterPresenterImpl createPresenter() {
        return new RegisterPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.register_view;
    }

    @Override
    protected void onFinishInflate() {

        Observable<CharSequence> phoneNumObservable = RxTextView.textChanges(mPhoneNumberEt).skip(10);
        Observable<CharSequence> authenticodeObservable = RxTextView.textChanges(mAuthenticodeEt).skip(5);
        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mPasswordEt).skip(5);

        // 输入了手机号才能获取验证码
        mCompositeSubscription.add(phoneNumObservable.map(phoneNum -> {
            if (phoneNum.length() != 11) {
                mPasswordEt.setError("你确定?");
                return false;
            }
            return true;
        }).subscribe(this::canGetAuthenticode));

        // 输入了验证码和手机号才给验证
        mCompositeSubscription.add(Observable.combineLatest(phoneNumObservable, authenticodeObservable, (phoneNum, authenticode) -> {
            if (phoneNum.length() != 11) {
                mPhoneNumberEt.setError("你确定?");
                return false;
            }
            // TODO: 这里应该是验证成功
            if (!authenticode.toString().equals("123456")) {
                mAuthenticodeEt.setError("你确定?");
                return false;
            }
            return true;
        }).subscribe(this::canAuthenticate));

        // 检查密码
        mCompositeSubscription.add(passwordObservable.map(password -> {
            if (password.length() < 6) {
                mPasswordEt.setError("要大于6位");
                return false;
            }
            return true;
        }).subscribe(this::canRegister));

        mHintContinueWithWechat.setVisibility(View.VISIBLE);
        // 还没有验证过
        canGetAuthenticode(false);
    }

    private void canGetAuthenticode(Boolean isPhoneNumValid) {
        mGetAuthenticodeBtn.setClickable(isPhoneNumValid);
        mGetAuthenticodeBtn.setEnabled(isPhoneNumValid);
        mAuthenticodeEt.setCursorVisible(isPhoneNumValid);
        mAuthenticodeEt.setFocusableInTouchMode(isPhoneNumValid);
    }

    private void canAuthenticate(boolean isReady) {
        toggleButton(isReady);
    }

    public void hintAuthenticated() {
        mIsAuthenticated = true;
        passwordTextLayout.setVisibility(View.VISIBLE);
        authenticodeTextLayout.setVisibility(View.GONE);
        mPhoneNumberEt.setCursorVisible(false);
        mPhoneNumberEt.setFocusableInTouchMode(false);
        mRegisterBtn.setText(R.string.hint_register_register);
    }

    private void canRegister(boolean isReady) {
        toggleButton(isReady);
    }

    private void toggleButton(boolean enable) {
        mRegisterBtn.setEnabled(enable);
        mRegisterBtn.setClickable(enable);
        if (enable) {
            mRegisterBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_accent));
        } else {
            mRegisterBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_grey));
        }
    }

    @Override
    public void hintRegisterProcessing() {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        mDialog.setTitleText(getString(R.string.register_hint_register_processing));
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    public void hintRegisterError(String message) {
        mDialog.setTitleText(message)
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    @Override
    public void hintRegisterFailed(String message) {
        mDialog.setTitleText(message)
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
    }

    @Override
    public void hintRegisterSucceed() {
        mDialog.setTitleText(getString(R.string.register_hint_register_succeed))
                .setConfirmText(getString(R.string.action_confirm))
                .setConfirmClickListener(sweetAlertDialog -> {
                    // TODO: 进入设置页面
                    finish();
                })
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
    }

    @Override
    public void hintRegisterCompleted() {
        mDialog.dismissWithAnimation();
    }

    @OnClick(R.id.register_btn)
    public void onConfirm() {
        if (mIsAuthenticated) {
            mPresenter.attemptRegister(mPhoneNumberEt.getText().toString(), mPasswordEt.getText().toString());
        } else {
            mPresenter.checkForAuthenticode(mPhoneNumberEt.getText().toString(), mAuthenticodeEt.getText().toString());
        }
    }

    @OnClick(R.id.get_authenticode_btn)
    public void onAskForAuthenticode() {
        mPresenter.askForAuthenticode(mPhoneNumberEt.getText().toString());
    }
}
