package com.sinyuk.jianyimaterial.feature.register;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.entity.School;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.feature.info.InfoView;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.utils.AnimUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by Sinyuk on 16.3.19.
 */
public class RegisterView extends BaseActivity<RegisterPresenterImpl> implements IRegisterView {

    public static final String TYPE = "type";
    public static final String REGISTER = "register";
    public static final String UPDATE = "update";
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
    private boolean mIsRegister;
    private List<School> mSchoolList;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {
        configIntentType(getIntent().getExtras());
    }

    private void configIntentType(Bundle extras) {
        try {
            mIsRegister = extras.getString(TYPE, REGISTER).equals(REGISTER);
        } catch (Exception e) {
            mIsRegister = false;
            e.printStackTrace();
        }

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

        setupToolbar();
        setupHintView();

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
            if (authenticode.length() != 6) {
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

        // 还没有验证过
        canGetAuthenticode(false);

        mPresenter.fetchSchools();
    }

    private void setupHintView() {
        if (mIsRegister) {
            mHintContinueWithWechat.setVisibility(View.VISIBLE);
        } else {
            mHintContinueWithWechat.setVisibility(View.GONE);
        }
    }

    private void setupToolbar() {
        if (getSupportActionBar() == null) { return; }
        if (mIsRegister) {
            getSupportActionBar().setTitle(getString(R.string.register_hint_register));
        } else {
            getSupportActionBar().setTitle(getString(R.string.register_hint_update));
        }
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

    /**
     * WTF 验证成功之后要做好多事情啊
     * 显示 隐藏
     * 不能改手机号了
     * 注册按钮不能点的
     */
    public void hintAuthenticated() {
        mIsAuthenticated = true;
        passwordTextLayout.setVisibility(View.VISIBLE);
        authenticodeTextLayout.setVisibility(View.GONE);
        mPhoneNumberEt.setCursorVisible(false);
        mPhoneNumberEt.setFocusableInTouchMode(false);
        toggleButton(false);
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
    public void showProgressDialog() {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        mDialog.setTitleText(getString(R.string.register_hint_register_processing));
        mDialog.show();
    }

    @Override
    public void showErrorDialog(String message) {
        mDialog.setTitleText(message)
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
    }

    @Override
    public void showWarningDialog(String message) {
        mDialog.setTitleText(message)
                .setConfirmText(StringUtils.getRes(this, R.string.action_confirm))
                .setConfirmClickListener(null)
                .changeAlertType(SweetAlertDialog.WARNING_TYPE);
    }

    @Override
    public void showSucceedDialog(User user) {
        mDialog.setTitleText(getString(R.string.register_hint_register_succeed))
                .setConfirmText(getString(R.string.action_confirm))
                .setConfirmClickListener(sweetAlertDialog -> {
                    // TODO: 进入设置页面
                    Intent toInfoView = new Intent(RegisterView.this, InfoView.class);
                    Bundle extras = new Bundle();
                    extras.putString(InfoView.AVATAR_URL, user.getHeading());
                    extras.putString(InfoView.USERNAME, user.getName());
                    if (!TextUtils.isEmpty(user.getSchool()) && !mSchoolList.isEmpty()) {
                        final int index = Integer.valueOf(user.getSchool());
                        try {
                            extras.putString(InfoView.SCHOOL_NAME, mSchoolList.get(index).getName());
                        } catch (Exception e) {
                            e.printStackTrace();
                            extras.putString(InfoView.SCHOOL_NAME, "浙江传媒学院-下沙校区");
                        }
                    }
                    toInfoView.putExtras(extras);
                    startActivity(toInfoView);
                    myHandler.postDelayed(this::finish, AnimUtils.ANIMATION_TIME_MEDIUM);
                })
                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        mDialog.setCancelable(false);
    }

    @Override
    public void hintRegisterCompleted() {
        mDialog.dismissWithAnimation();
    }

    @Override
    public void onLoadSchoolSucceed(List<School> schoolList) {
        mSchoolList = schoolList;
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
