package com.sinyuk.jianyimaterial.feature.info;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.utils.FileUtils;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.yalantis.ucrop.UCrop;

import java.io.File;

import butterknife.Bind;
import butterknife.OnClick;
import rx.Observable;

/**
 * Created by Sinyuk on 16.4.16.
 */
public class InfoView extends BaseActivity<InfoPresenterImpl> implements IInfoView {
    private static final int REQUEST_PICK = 0x08;
    private static final int CHOOSE_BOY = 1;
    private static final int CHOOSE_GIRL = 0;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.avatar_girl)
    ImageView mAvatarGirl;
    @Bind(R.id.avatar_boy)
    ImageView mAvatarBoy;
    @Bind(R.id.view_switcher)
    ViewSwitcher mViewSwitcher;
    @Bind(R.id.female_flag)
    ImageView mFemaleFlag;
    @Bind(R.id.male_flag)
    ImageView mMaleFlag;
    @Bind(R.id.user_name_et)
    EditText mUserNameEt;
    @Bind(R.id.user_name_input_area)
    TextInputLayout mUserNameInputArea;
    @Bind(R.id.location_et)
    EditText mLocationEt;
    @Bind(R.id.school_input_area)
    TextInputLayout mSchoolInputArea;
    @Bind(R.id.confirm_btn)
    Button mConfirmBtn;
    @Bind(R.id.nested_scroll_view)
    NestedScrollView mNestedScrollView;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout mCoordinatorLayout;

    private int mSelectedGender = CHOOSE_GIRL;
    private String mUploadUrl;
    private DrawableRequestBuilder<Integer> mResRequest;
    private SweetAlertDialog mDialog;

    @Override
    protected boolean isUseEventBus() {
        return false;
    }

    @Override
    protected void beforeInflate() {

    }

    @Override
    protected InfoPresenterImpl createPresenter() {
        return new InfoPresenterImpl();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }

    @Override
    protected int getContentViewID() {
        return R.layout.info_view;
    }

    @Override
    protected void onFinishInflate() {
        // 加载用户信息
        setupViewSwitcher();
        setupGenderToggle();
        setObservers();
        mResRequest = Glide.with(this).fromResource().dontAnimate();
        mSelectedGender = CHOOSE_GIRL;
        mResRequest.load(R.drawable.girl).bitmapTransform(new CropCircleTransformation(this)).into(mAvatarGirl);
        mPresenter.loadUserInfo();
    }

    private void setObservers() {
        Observable<CharSequence> nicknameObservable = RxTextView.textChanges(mUserNameEt).skip(1);
        Observable<CharSequence> schoolObservable = RxTextView.textChanges(mLocationEt);

        mCompositeSubscription.add(Observable.combineLatest(nicknameObservable, schoolObservable, (nickname, school) -> {
            if (!TextUtils.isEmpty(nickname)) {
                mUserNameEt.setError("你确定?");
                return false;
            }
            if (!TextUtils.isEmpty(school)) {
                mLocationEt.setError("你确定?");
                return false;
            }
            return true;
        }).subscribe(InfoView.this::toggleConfirmButton));

        mCompositeSubscription.add(
                RxView.clicks(mAvatarBoy).compose(RxPermissions.getInstance(this)
                        .ensure(Manifest.permission.READ_EXTERNAL_STORAGE))
                        .subscribe(granted -> {
                            if (granted) {pickPhoto();} else {hintPermissionDenied();}
                        }));

        mCompositeSubscription.add(
                RxView.clicks(mAvatarGirl).compose(RxPermissions.getInstance(this)
                        .ensure(Manifest.permission.READ_EXTERNAL_STORAGE))
                        .subscribe(granted -> {
                            if (granted) {pickPhoto();} else {hintPermissionDenied();}
                        }));
    }

    private void hintPermissionDenied() {
        ToastUtils.toastSlow(this, getString(R.string.common_hint_permission_denied));
    }

    private void setupViewSwitcher() {
        final Animation slide_in_left = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        final Animation slide_out_right = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_out);
        mViewSwitcher.setInAnimation(slide_in_left);
        mViewSwitcher.setOutAnimation(slide_out_right);
    }

    private void setupGenderToggle() {

    }

    @OnClick({R.id.female_flag, R.id.male_flag})
    public void toggleGender(View v) {
        switch (v.getId()) {
            case R.id.female_flag:
                if (mSelectedGender == CHOOSE_GIRL) { break; }
                mResRequest.load(R.drawable.ic_gender_female_accent_24dp).into(mFemaleFlag);
                mResRequest.load(R.drawable.ic_gender_male_grey600_24dp).into(mMaleFlag);
                mSelectedGender = CHOOSE_GIRL;
                mViewSwitcher.showNext();
                mResRequest.load(R.drawable.girl).bitmapTransform(new CropCircleTransformation(this)).into(mAvatarGirl);
                mUploadUrl = null;
                break;
            case R.id.male_flag:
                if (mSelectedGender == CHOOSE_BOY) { break; }
                mResRequest.load(R.drawable.ic_gender_female_grey600_24dp).into(mFemaleFlag);
                mResRequest.load(R.drawable.ic_gender_male_accent_24dp).into(mMaleFlag);
                mSelectedGender = CHOOSE_BOY;
                mViewSwitcher.showPrevious();
                mResRequest.load(R.drawable.boy).bitmapTransform(new CropCircleTransformation(this)).into(mAvatarBoy);
                mUploadUrl = null;
                break;
        }

        LogUtils.simpleLog(InfoView.class, "mSelectedGender " + mSelectedGender);
    }

    private void pickPhoto() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.offer_hint_pick_from)), REQUEST_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK) {
                final Uri selectedUri = data.getData();
                if (selectedUri != null) {
                    handleUriFromMediaStore(selectedUri);
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                onCropSucceed(data);
            }
        }
    }

    private void onCropSucceed(@NonNull Intent result) {
        final Uri resultUri = UCrop.getOutput(result);
        if (resultUri != null) {
            LogUtils.simpleLog(InfoView.class, "resultUri " + resultUri);
            mPresenter.compressThenUpload(resultUri.toString());
            final DrawableRequestBuilder<Uri> request = Glide.with(this).load(resultUri).dontAnimate().bitmapTransform(new CropCircleTransformation(this)).priority(Priority.IMMEDIATE);
            if (mSelectedGender == 0) {
                request.into(mAvatarGirl);
            } else {
                request.into(mAvatarBoy);
            }
        } else {
            ToastUtils.toastSlow(this, getString(R.string.info_hint_crop_failed));
        }
    }


    private void handleUriFromMediaStore(Uri uri) {
        FileUtils.delete(new File(getCacheDir(), "avatar" + SystemClock.currentThreadTimeMillis()));
        Uri saveLocation = Uri.fromFile(new File(getCacheDir(), "avatar" + SystemClock.currentThreadTimeMillis()));
        startUCrop(uri, saveLocation);
    }

    private void startUCrop(Uri uri, Uri saveLocation) {
        UCrop uCrop = UCrop.of(uri, saveLocation);
        uCrop.withAspectRatio(1, 1);
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setMaxBitmapSize(500);
        options.setImageToCropBoundsAnimDuration(150);
        options.setMaxScaleMultiplier(5);
        options.setOvalDimmedLayer(true);
        uCrop.withOptions(options);
        uCrop.start(InfoView.this);
    }

    /**
     * toggle confirm button
     */
    private void toggleConfirmButton(boolean isReady) {
        isReady = isReady && !TextUtils.isEmpty(mUploadUrl);
        mConfirmBtn.setEnabled(isReady);
        mConfirmBtn.setClickable(isReady);
        if (isReady) {
            mConfirmBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_accent));
        } else {
            mConfirmBtn.setBackground(getResources().getDrawable(R.drawable.rounded_rect_fill_grey));
        }
    }


    @Override
    public void showProgressDialog(String message) {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.setTitleText(message)
                .setCancelable(false);
        mDialog.show();
    }

    @Override
    public void dismissProgressDialog() {
        if (null != mDialog) { mDialog.dismissWithAnimation(); }
    }

    @Override
    public void showUserAvatar(String url) {
        Glide.with(this).load(url).diskCacheStrategy(DiskCacheStrategy.RESULT)
                .dontAnimate()
                .error(R.drawable.girl)
                .priority(Priority.IMMEDIATE)
                .bitmapTransform(new CropCircleTransformation(this)).into(mAvatarBoy);
    }

    @Override
    public void showUserNickname(String nickname) {
        mUserNameEt.setText(nickname);
    }

    @Override
    public void showUserSchool(String schoolIndex) {
        int index = Integer.valueOf(schoolIndex);
        final String[] schoolArray = getResources().getStringArray(R.array.schools_sort);
        try {
            mLocationEt.setText(schoolArray[index-1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onQueryFailed(String message) {

    }

    @Override
    public void onUserNotLogged() {

    }

    @Override
    public void onShotUploadParseError(String message) {

    }

    @Override
    public void onShotUploadVolleyError(String message) {

    }

    @Override
    public void onShotUploadCompressError(String message) {

    }

    @Override
    public void onShotUploadSucceed(String url) {

    }

    @Override
    public void onUserUpdateSucceed(String message) {

    }

    @Override
    public void onUserUpdateFailed(String message) {

    }

    @Override
    public void onUserUpdateVolleyError(String message) {

    }

    @Override
    public void onUserUpdateParseError(String message) {

    }
}
