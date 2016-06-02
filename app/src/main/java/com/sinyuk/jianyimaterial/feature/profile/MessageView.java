package com.sinyuk.jianyimaterial.feature.profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.transition.ArcMotion;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.model.UserModel;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.utils.ImeUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.widgets.morphdialog.MorphButtonToDialog;
import com.sinyuk.jianyimaterial.widgets.morphdialog.MorphDialogToButton;
import com.sinyuk.jianyimaterial.widgets.morphdialog.MorphDialogToFab;
import com.sinyuk.jianyimaterial.widgets.morphdialog.MorphFabToDialog;

import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.5.19.
 */
public class MessageView extends BaseActivity<ProfilePresenterImpl> implements UserModel.QueryCurrentUserCallback {
    private static final String USERNAME = "username";
    private static final String TEL = "tel";
    private static final String IS_MORPH_BUTTON = "is_morph_button";
    @Bind(R.id.send_to_tv)
    TextView mSendToTv;
    @Bind(R.id.message_et)
    EditText mMessageEt;
    @Bind(R.id.send_btn)
    Button mSendBtn;
    @Bind(R.id.container)
    RelativeLayout container;
    @Bind(R.id.send_to_hint)
    TextView mSendToHint;
    @Bind(R.id.message_input_layout)
    TextInputLayout mMessageInputLayout;
    @Bind(R.id.cancel_btn)
    Button mCancelBtn;
    private User mCurrentUser;

    public static Intent newIntent(Context activityFrom, String username, String tel) {
        Intent intent = new Intent(activityFrom, MessageView.class);
        Bundle bundle = new Bundle();
        bundle.putString(USERNAME, username);
        bundle.putString(TEL, tel);
        intent.putExtras(bundle);
        return intent;
    }

    public static Intent newIntent(Context activityFrom, String username, String tel, boolean isMorphButton) {
        Intent intent = new Intent(activityFrom, MessageView.class);
        Bundle bundle = new Bundle();
        bundle.putString(USERNAME, username);
        bundle.putString(TEL, tel);
        bundle.putBoolean(IS_MORPH_BUTTON, isMorphButton);
        intent.putExtras(bundle);
        return intent;
    }


    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {


    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupSharedElementTransitions2() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        MorphButtonToDialog sharedEnter = new MorphButtonToDialog();
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphDialogToButton sharedReturn = new MorphDialogToButton();
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (container != null) {
            sharedEnter.addTarget(container);
            sharedReturn.addTarget(container);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    protected ProfilePresenterImpl createPresenter() {
        return new ProfilePresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected void onFinishInflate() {
        setupUsername();
        mCompositeSubscription.add(RxTextView.textChanges(mMessageEt).map(TextUtils::isEmpty).subscribe(this::toggleSendButton));
        setLazyLoadDelay(1000);
        if (getIntent().getExtras().getBoolean(IS_MORPH_BUTTON, false)) {
            setupSharedElementTransitions2();
            setupTextArea();
        } else {
            setupSharedElementTransitions1();
        }
    }

    private void setupTextArea() {
        mSendToHint.setText("to 简易");
        mMessageInputLayout.setHint("反馈");
    }


    private void toggleSendButton(Boolean isEmpty) {
        int textColor = isEmpty ? getResources().getColor(R.color.grey_600) : getResources().getColor(R.color.colorAccent);
        mSendBtn.setTextColor(textColor);
    }

    private void setupUsername() {
        if (null == getIntent()) { return; }
        if (TextUtils.isEmpty(getIntent().getExtras().getString(USERNAME))) {
            mSendToTv.setText(getString(R.string.untable));
        } else {
            mSendToTv.setText(getIntent().getExtras().getString(USERNAME));
        }
    }

    @Override
    protected void lazyLoad() {
        UserModel.getInstance(this).queryCurrentUser(this);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.message_view;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setupSharedElementTransitions1() {
        ArcMotion arcMotion = new ArcMotion();
        arcMotion.setMinimumHorizontalAngle(50f);
        arcMotion.setMinimumVerticalAngle(50f);

        Interpolator easeInOut = AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in);

        MorphFabToDialog sharedEnter = new MorphFabToDialog();
        sharedEnter.setPathMotion(arcMotion);
        sharedEnter.setInterpolator(easeInOut);

        MorphDialogToFab sharedReturn = new MorphDialogToFab();
        sharedReturn.setPathMotion(arcMotion);
        sharedReturn.setInterpolator(easeInOut);

        if (container != null) {
            sharedEnter.addTarget(container);
            sharedReturn.addTarget(container);
        }
        getWindow().setSharedElementEnterTransition(sharedEnter);
        getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    public void dismiss() {
        setResult(Activity.RESULT_CANCELED);
        supportFinishAfterTransition();
    }

    @OnClick({R.id.container, R.id.cancel_btn})
    public void cancel() {
        delayDismiss();
    }

    @OnClick(R.id.send_btn)
    public void toSend() {
        if (TextUtils.isEmpty(mMessageEt.getText())) {
            mMessageEt.setError(getString(R.string.profile_send_content_null));
        } else {
            if (getIntent() == null) { return; }
            if (!TextUtils.isEmpty(getIntent().getExtras().getString(TEL))) {
                sendSMS(getIntent().getExtras().getString(TEL), mMessageEt.getText().toString());
            }
            delayDismiss();
            ToastUtils.toastSlow(MessageView.this, "发送成功");
        }

    }

    private void delayDismiss() {
        ImeUtils.hideIme(container);
        myHandler.postDelayed(this::dismiss, 400);
    }


    /**
     * 直接调用短信接口发短信，不含发送报告和接受报告
     *
     * @param phoneNumber
     * @param message
     */
    public void sendSMS(String phoneNumber, String message) {
        String username = "某个人";
        if (null != mCurrentUser) {
            if (!TextUtils.isEmpty(username)) { username = mCurrentUser.getName(); }
        }
        message = "来自简易上的 \"" + username + "\":" + message;        // 获取短信管理器

        SmsManager smsManager = SmsManager.getDefault();
        // 拆分短信内容（手机短信长度限制）
        List<String> divideContents = smsManager.divideMessage(message);
        for (String text : divideContents) {
            smsManager.sendTextMessage(phoneNumber, null, text, null, null);
        }
    }

    @Override
    public void onQuerySucceed(User currentUser) {
        mCurrentUser = currentUser;
    }

    @Override
    public void onQueryFailed(String message) {

    }

    @Override
    public void onUserNotLogged() {

    }
}