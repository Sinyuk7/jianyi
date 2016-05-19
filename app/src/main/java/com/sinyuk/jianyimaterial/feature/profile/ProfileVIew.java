package com.sinyuk.jianyimaterial.feature.profile;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jakewharton.rxbinding.support.design.widget.RxAppBarLayout;
import com.jakewharton.rxbinding.view.RxView;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.common.Constants;
import com.sinyuk.jianyimaterial.entity.School;
import com.sinyuk.jianyimaterial.events.XLoginEvent;
import com.sinyuk.jianyimaterial.events.XOnShelfEvent;
import com.sinyuk.jianyimaterial.events.XUnShelfOptionEvent;
import com.sinyuk.jianyimaterial.feature.info.InfoView;
import com.sinyuk.jianyimaterial.feature.shelf.ShelfView;
import com.sinyuk.jianyimaterial.glide.BlurTransformation;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.mvp.BaseActivity;
import com.sinyuk.jianyimaterial.sweetalert.SweetAlertDialog;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.widgets.MyCircleImageView;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sinyuk on 16.4.10.
 */
public class ProfileView extends BaseActivity<ProfilePresenterImpl> implements IProfileView {
    public static final String PROFILE_TYPE = "type";
    public static final float MINE = 1;
    public static final float OTHER = 2;
    public static final int REQUEST_MESSAGE = 0X22;
    private final static String[] sTabTitles = new String[]{"物品", "喜欢"};
    @Bind(R.id.reveal_view)
    ImageView mRevealView;
    @Bind(R.id.avatar)
    MyCircleImageView mAvatar;
    @Bind(R.id.user_name_et)
    EditText mUserNameEt;
    @Bind(R.id.location_tv)
    EditText mLocationTv;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;
    @Bind(R.id.action_iv)
    ImageView mAcionIv;
    @Bind(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.back_iv)
    ImageView mBackIv;

    @Bind(R.id.fab)
    FloatingActionButton mFab;
    private float mType;

    private List<Fragment> fragmentList = new ArrayList<>();
    private Integer mSchoolIndex;
    private String mSchoolName;
    private String mUsername;
    private String mAvatarUrl;
    private SweetAlertDialog mDialog;
    private String mTel;


    @Override
    protected boolean isUseEventBus() {
        return true;
    }

    @Override
    protected void beforeInflate() {
        mType = getIntent().getExtras().getFloat(PROFILE_TYPE, OTHER);
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
    protected int getContentViewID() {
        return R.layout.profile_view;
    }

    @Override
    protected void onFinishInflate() {
        final Bundle extras = getIntent().getExtras();
        mType = extras.getFloat(PROFILE_TYPE, OTHER);
        setupLayerType();
        setupToolbar();
        setAppBarLayout();
        setupActionBtn();

        if (mType == OTHER) {
            showAvatar(extras.getString("avatar"));
            showBackdrop(extras.getString("avatar"));
            showLocation(extras.getString("location", getString(R.string.untable)));
            showUsername(extras.getString("user_name", getString(R.string.untable)));
            initFragments(extras.getString("uid"));
            mTel = extras.getString("tel");
        } else if (mType == MINE) {
            mPresenter.queryCurrentUser();
            mPresenter.fetchSchoolList();
        }

    }

    @Override
    protected void lazyLoad() {

    }

    private void setupActionBtn() {
        if (mType == OTHER) {
            mFab.setImageResource(R.drawable.ic_chat_white_48dp);
            mAcionIv.setImageResource(R.drawable.ic_chat_white_48dp);
            mCompositeSubscription.add(
                    RxView.clicks(mFab).compose(RxPermissions.getInstance(this)
                            .ensure(Manifest.permission.SEND_SMS))
                            .subscribe(granted -> {
                                if (granted) {startMessageDialog();} else {hintPermissionDenied();}
                            }));

            mCompositeSubscription.add(
                    RxView.clicks(mAcionIv).compose(RxPermissions.getInstance(this)
                            .ensure(Manifest.permission.SEND_SMS))
                            .subscribe(granted -> {
                                if (granted) {startMessageDialog();} else {hintPermissionDenied();}
                            }));
        } else if (mType == MINE) {
            mFab.setImageResource(R.drawable.ic_mode_edit_white_24dp);
            mAcionIv.setImageResource(R.drawable.ic_mode_edit_white_24dp);
        }
    }

    private void startMessageDialog() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileView.this, mFab, "transition_dialog");
            startActivityForResult(MessageView.newIntent(this, mUsername, mTel), REQUEST_MESSAGE, options.toBundle());
        }
    }

    private void hintPermissionDenied() {
        ToastUtils.toastSlow(this, getString(R.string.profile_permisstion_denied));
    }

    @OnClick({R.id.action_iv, R.id.fab})
    public void onClick() {
        if (mType == MINE) {
            Bundle bundle = new Bundle();
            bundle.putString(InfoView.USERNAME, mUsername);
            bundle.putString(InfoView.SCHOOL_NAME, mSchoolName);
            bundle.putString(InfoView.AVATAR_URL, mAvatarUrl);
            Intent toMyInfoView = new Intent(ProfileView.this, InfoView.class);
            toMyInfoView.putExtras(bundle);
            startActivity(toMyInfoView);
        }
    }

    private void setupLayerType() {
        mAvatar.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mAcionIv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mBackIv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mUserNameEt.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        mLocationTv.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    private void setupToolbar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        mCompositeSubscription.add(RxView.clicks(mBackIv).subscribe(aVoid -> onBackPressed()));
    }

    public void initFragments(String uid) {
        final Bundle sellArgs = new Bundle();
        final Bundle likeArgs = new Bundle();
        if (mType == OTHER) {
            sellArgs.putString(ShelfView.CONTENT, ShelfView.THEIR_GOODS);
            likeArgs.putString(ShelfView.CONTENT, ShelfView.THEIR_LIKES);
        } else {
            sellArgs.putString(ShelfView.CONTENT, ShelfView.MY_GOODS);
            likeArgs.putString(ShelfView.CONTENT, ShelfView.MY_LIKES);
        }
        sellArgs.putString(ShelfView.USER_ID, uid);
        likeArgs.putString(ShelfView.USER_ID, uid);
        fragmentList.add(ShelfView.newInstance(sellArgs));
        fragmentList.add(ShelfView.newInstance(likeArgs));

        // after init fragments
        setupViewPager();
        setupTabLayout();
    }

    private void setAppBarLayout() {
        mCompositeSubscription.add(RxAppBarLayout.offsetChanges(mAppBarLayout)
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(dy -> 1 - (-dy / (mAppBarLayout.getTotalScrollRange() / 1.5f)))
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(fraction -> {
                    if (fraction < 0) { fraction = 0f; }
                    mUserNameEt.setAlpha(fraction);
                    mLocationTv.setAlpha(fraction);
                    mAvatar.setScaleY(fraction);
                    mAvatar.setAlpha(fraction);
                    mAvatar.setScaleX(fraction);
                    mAcionIv.setAlpha(fraction);
                    mBackIv.setAlpha(fraction);
                    if (fraction < 0.28f) {
                        mFab.show();
                    } else {
                        mFab.hide();
                    }
                }));
    }


    private void setupViewPager() {
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return sTabTitles[position];
            }
        });
    }

    private void setupTabLayout() {
        mTabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void showUsername(@NonNull String username) {
        mUserNameEt.setText(username);
        mUsername = username;
    }

    @Override
    public void showAvatar(@NonNull String url) {
        mAvatarUrl = url;
        Glide.with(this).load(url)
                .dontAnimate()
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .bitmapTransform(new CropCircleTransformation(this))
                .into(mAvatar);
    }

    @Override
    public void showBackdrop(@NonNull String url) {
        Glide.with(this).load(url)
                .crossFade(2000)
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .bitmapTransform(new BlurTransformation(this, Constants.BLUR_RADIUS, Constants.BLUR_SAMPLING))
                .into(mRevealView);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(XLoginEvent event) {
        mPresenter.queryCurrentUser();
    }


    @Override
    public void showLocation(@NonNull String nameOrIndex) {
        if (mType == OTHER) {
            mLocationTv.setText(nameOrIndex);
            mSchoolName = nameOrIndex;
        } else {
            mPresenter.fetchSchoolList();
            mSchoolIndex = Integer.valueOf(nameOrIndex);
        }
    }

    @Override
    public void showToolbarTitle(@NonNull String username) {
//        mCollapsingToolbarLayout.setTitle(mUsername);
    }

    @Override
    public void onQueryFailed(String message) {
        ToastUtils.toastFast(this, message);
    }

    @Override
    public void onUserNotLogged() {

    }

    @Override
    public void onLoadSchoolSucceed(List<School> schoolList) {
        if (mSchoolIndex >= 1) {
            mSchoolName = schoolList.get(mSchoolIndex - 1).getName();
            mLocationTv.setText(mSchoolName);
        }
        LogUtils.simpleLog(ProfileView.class, schoolList.get(mSchoolIndex - 1).getName());
    }

    @Override
    public void onLoadSchoolParseError(String message) {
        ToastUtils.toastFast(this, message);
    }

    @Override
    public void onLoadSchoolVolleyError(String message) {
        ToastUtils.toastFast(this, message);
    }

    @Override
    public void showWarningDialog(@NonNull String message) {
        mDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
        mDialog.setTitleText(message)
                .setContentText("")
                .setConfirmText(getString(R.string.unshelf_confirm));
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void showErrorDialog(@NonNull String message) {
        mDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
        mDialog.setTitleText(message)
                .setContentText("")
                .setConfirmText(getString(R.string.unshelf_confirm));
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void showSucceedDialog(@NonNull String message) {
        mDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
        mDialog.setTitleText(message);

        if (message.equals("已下架")) {
            mDialog.setContentText(getString(R.string.unshelf_hint_rate_us))
                    .setCancelText(getString(R.string.unshelf_hint_just_soso))
                    .setConfirmText(getString(R.string.unshelf_hint_nice))
                    .setConfirmClickListener(sweetAlertDialog -> showBegDialog());
        } else {
            mDialog.setContentText("")
                    .setConfirmText(getString(R.string.unshelf_confirm))
                    .setConfirmClickListener(null);
        }
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
    }

    @Override
    public void showProgressDialog(@NonNull String message) {
        mDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        mDialog.setTitleText(message);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    @Override
    public void dismissDialog() {
        mDialog.dismissWithAnimation();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnShelfOptionSelect(XUnShelfOptionEvent event) {
        String reason = null;
        switch (event.getOption()) {
            case 0: // do something
                reason = getString(R.string.unshelf_hint_has_sold);
                break;
            case 1: // 不卖了
                reason = getString(R.string.unshelf_hint_dont_want);
            case 2: // 其他理由
                reason = getString(R.string.unshelf_hint_other_reason);
                break;
        }
        if (reason != null) {
            mPresenter.unShelf(event.getId(), reason);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onOnShelfEvent(XOnShelfEvent event) {
        LogUtils.simpleLog(ProfileView.class, event.getId() + "");
        mPresenter.onShelf(event.getId());
    }

    private void showBegDialog() {
        mDialog.changeAlertType(SweetAlertDialog.CUSTOM_IMAGE_TYPE);
        mDialog.setCustomImage(getResources().getDrawable(R.drawable.ex_hug))
                .setTitleText(getString(R.string.unshelf_hint_beg_title))
                .setContentText(getString(R.string.unshelf_hint_beg))
                .setCancelText(getString(R.string.unshelf_hint_next_time))
                .setConfirmText(getString(R.string.unshelf_hint_ok))
                .setConfirmClickListener(sweetAlertDialog -> {
                    gotoAlipay();
                    sweetAlertDialog.dismissWithAnimation();
                });

    }

    private void gotoAlipay() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://ds.alipay.com/?from=mobilecodec&scheme=alipayqr%3A%2F%2Fplatformapi%2Fstartapp%3FsaId%3D10000007%26clientVersion%3D3.7.0.0718%26qrcode%3Dhttps%253A%252F%252Fqr.alipay.com%252Faex02962ehdallsjjszohe5%253F_s%253Dweb-other"));//设置一个URI地址
        startActivity(intent);
    }


}
