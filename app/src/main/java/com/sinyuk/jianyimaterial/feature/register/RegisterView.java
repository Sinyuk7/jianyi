package com.sinyuk.jianyimaterial.feature.register;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;

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
    @Bind(R.id.hint_continue_with_wechat)
    TextView mHintContinueWithWechat;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

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
        mCompositeSubscription.add(RxTextView.editorActions(mPasswordEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {
                    if (done) { clickConfirmBtn(); }
                }));


        Observable<CharSequence> phoneNumObservable = RxTextView.textChanges(mPhoneNumberEt).skip(10);
        Observable<CharSequence> authenticodeObservable = RxTextView.textChanges(mAuthenticodeEt).skip(5);
        Observable<CharSequence> passwordObservable = RxTextView.textChanges(mPasswordEt).skip(5);

        mCompositeSubscription.add(passwordObservable.map(password -> {
            if (password.length() < 6) {
                mPasswordEt.setError("要大于6位");
                return false;
            }
            return true;
        }).subscribe(this::toggleConfirmButton));

        mCompositeSubscription.add(Observable.combineLatest(phoneNumObservable, authenticodeObservable, (phoneNum, authenticode) -> {
            if (phoneNum.length() != 11) {
                mPhoneNumberEt.setError("你确定?");
                return false;
            }
            if (authenticode.length() != 6) {
                mAuthenticodeEt.setError("你确定?");
                return false;
            }
            return true;
        }).subscribe(this::togglePasswordEt));

        mRegisterBtn.setEnabled(false);
        mRegisterBtn.setClickable(false);
    }

    private void togglePasswordEt(boolean valid) {
        if (valid) {
            passwordTextLayout.setVisibility(View.VISIBLE);
        } else {
            passwordTextLayout.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.register_btn)
    public void clickConfirmBtn() {
        mPresenter.attemptRegister(mPhoneNumberEt.getText().toString(), mPasswordEt.getText().toString());
    }

    private void toggleConfirmButton(boolean isReady) {
        mRegisterBtn.setEnabled(isReady);
        mRegisterBtn.setClickable(isReady);
        if (isReady) {
            mRegisterBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_accent));
        } else {
            mRegisterBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_grey));
        }
    }


    @Override
    public void hintRegisterProcessing() {
        
    }

    @Override
    public void hintRegisterError(String message) {

    }

    @Override
    public void hintRegisterFailed(String message) {

    }

    @Override
    public void hintRegisterSucceed() {

    }

    @Override
    public void hintRegisterCompleted() {

    }
}
