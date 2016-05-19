package com.sinyuk.jianyimaterial.feature.profile;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.design.widget.TextInputLayout;
import android.transition.ArcMotion;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.widgets.morphdialog.MorphDialogToFab;
import com.sinyuk.jianyimaterial.widgets.morphdialog.MorphFabToDialog;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * Created by Sinyuk on 16.5.19.
 */
public class MessageView extends BaseActivity<ProfilePresenterImpl> {
    @Bind(R.id.send_to_tv)
    TextView mSendToTv;
    @Bind(R.id.message_et)
    EditText mMessageEt;
    @Bind(R.id.message_input_layout)
    TextInputLayout mMessageInputLayout;
    @Bind(R.id.send_btn)
    Button mSendBtn;
    @Bind(R.id.container)
    RelativeLayout container;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {
        setupSharedEelementTransitions1();
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

    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected int getContentViewID() {
        return R.layout.message_view;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setupSharedEelementTransitions1() {
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
        dismiss();
    }

    @OnClick(R.id.send_btn)
    public void toSend() {
        dismiss();
        ToastUtils.toastSlow(this, "发送成功");
    }
}
