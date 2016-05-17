package com.sinyuk.jianyimaterial.feature.settings.account;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.utils.ImeUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.widgets.psdloadingview.EatAnimate;
import com.sinyuk.jianyimaterial.widgets.psdloadingview.IAnimate;
import com.sinyuk.jianyimaterial.widgets.psdloadingview.PsdLoadingView;
import com.sinyuk.jianyimaterial.widgets.psdloadingview.TranslationX2Animate;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by Sinyuk on 16.5.17.
 */
public class PasswordView extends BaseActivity<AccountPresenterImpl> implements IPasswordView {


    @Bind(R.id.confirm_btn)
    Button mConfirmBtn;
    @Bind(R.id.old_password_et)
    PsdLoadingView mOldPasswordEt;
    @Bind(R.id.new_password_et)
    PsdLoadingView mNewPasswordEt;
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
    protected AccountPresenterImpl createPresenter() {
        return new AccountPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected void onFinishInflate() {
        setupPasswordView();
        setupObservers();
    }

    private void setupObservers() {
        mCompositeSubscription.add(RxTextView.editorActions(mNewPasswordEt)
                .map(actionId -> actionId == EditorInfo.IME_ACTION_DONE)
                .subscribe(done -> {
                    if (done) { confirm(); }
                }));

        Observable<CharSequence> oldPswObservable = RxTextView.textChanges(mOldPasswordEt).skip(5);
        Observable<CharSequence> newPswObservable = RxTextView.textChanges(mNewPasswordEt).skip(5);

        mCompositeSubscription.add(oldPswObservable.subscribe(charSequence -> {
            toggleNewPasswordEt(TextUtils.isEmpty(mOldPasswordEt.getTextDuringLoading()));
        }));

        mCompositeSubscription.add(newPswObservable.subscribe(charSequence -> {
            toggleConfirmButton(mNewPasswordEt.getTextDuringLoading().length() >= 6);
        }));


        mNewPasswordEt.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                mOldPasswordEt.startLoading();
            }
        });

        init();
    }

    private void init() {
        toggleConfirmButton(false);
        toggleNewPasswordEt(true);
    }

    private void toggleNewPasswordEt(boolean isEmpty) {
        if (isEmpty) {
            mNewPasswordEt.setFocusableInTouchMode(false);
            mNewPasswordEt.setCursorVisible(false);
        } else {
            mNewPasswordEt.setError(null);
            mNewPasswordEt.setFocusableInTouchMode(true);
            mNewPasswordEt.setCursorVisible(true);
        }
    }

    private void setupPasswordView() {
        IAnimate eatAnimate = new EatAnimate();
        IAnimate translationX2Animate = new TranslationX2Animate();
        mNewPasswordEt.init(translationX2Animate);
        mOldPasswordEt.init(eatAnimate);
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.password_view;
    }

    @OnClick(R.id.confirm_btn)
    public void confirm() {
        toggleNewPasswordEt(true);
        mNewPasswordEt.startLoading();
        myHandler.postDelayed(() -> {
            mNewPasswordEt.stopLoading();
            mOldPasswordEt.stopLoading();
            ImeUtils.hideIme(mCoordinatorLayout);
            ToastUtils.toastSlow(PasswordView.this, "修改成功");
        }, 5000);
    }

    private void toggleConfirmButton(boolean hasInput) {
        mConfirmBtn.setEnabled(hasInput);
        mConfirmBtn.setClickable(hasInput);
        if (hasInput) {
            mConfirmBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_accent));
        } else {
            mConfirmBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_grey));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
