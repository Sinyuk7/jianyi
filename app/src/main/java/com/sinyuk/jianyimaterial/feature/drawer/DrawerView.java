package com.sinyuk.jianyimaterial.feature.drawer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mxn.soul.flowingdrawer_core.LeftDrawerLayout;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.entity.User;
import com.sinyuk.jianyimaterial.events.XLoginEvent;
import com.sinyuk.jianyimaterial.events.XLogoutEvent;
import com.sinyuk.jianyimaterial.feature.login.LoginView;
import com.sinyuk.jianyimaterial.glide.BlurTransformation;
import com.sinyuk.jianyimaterial.glide.ColorFilterTransformation;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by Sinyuk on 16.3.30.
 */
public class DrawerView extends MyMenuFragment<DrawerPresenterImpl> implements IDrawerView {
    private static final long DRAWER_CLOSE_DURATION = 400;
    private static DrawerView sInstance;
    @Bind(R.id.avatar)
    ImageView mAvatar;
    @Bind(R.id.user_name_tv)
    TextView mUserNameTv;
    @Bind(R.id.header_layout)
    RelativeLayout mHeaderLayout;
    LinearLayout mNavigationView;
    @Bind(R.id.drawer_menu_category)
    TextView mDrawerMenuCategory;
    @Bind(R.id.drawer_menu_explore)
    TextView mDrawerMenuExplore;
    @Bind(R.id.drawer_menu_want)
    TextView mDrawerMenuWant;
    @Bind(R.id.drawer_menu_message)
    TextView mDrawerMenuMessage;
    @Bind(R.id.drawer_menu_account)
    TextView mDrawerMenuAccount;
    @Bind(R.id.drawer_menu_settings)
    TextView mDrawerMenuSettings;
    @Bind(R.id.backdrop_iv)
    ImageView mBackdrop;

    private LeftDrawerLayout mLeftDrawerLayout;
    private int mSelected;

    public static DrawerView getInstance() {
        if (null == sInstance) { sInstance = new DrawerView(); }
        return sInstance;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mLeftDrawerLayout = (LeftDrawerLayout) getActivity().findViewById(R.id.left_drawer_layout);
    }

    @Override
    protected void beforeInflate() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected DrawerPresenterImpl createPresenter() {
        return new DrawerPresenterImpl();
    }


    @Override
    protected void onFinishInflate() {
        if (mPresenter.configLoginState()){
            showLoggedState(mPresenter.loadUserInfo());
        }else {
            showNotLoginState();
        }
    }

    @Override
    protected int getContentViewID() {
        return R.layout.drawer_view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    private void onLogin(XLogoutEvent event) {
        showLoggedState(mPresenter.loadUserInfo());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    private void onLogout(XLoginEvent event) {
        showNotLoginState();
    }

    private void showNotLoginState() {
        mDrawerMenuAccount.setVisibility(View.GONE);
        mDrawerMenuMessage.setVisibility(View.GONE);
        Glide.with(this).load(R.drawable.ic_avatar_placeholder)
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .into(mAvatar);
        /**
         * load custom picture in backdrop here
         */
        mUserNameTv.setText("点击登录");
    }

    private void showLoggedState(User user) {
        if (user != null) {
            mDrawerMenuAccount.setVisibility(View.VISIBLE);
            mDrawerMenuMessage.setVisibility(View.VISIBLE);
            DrawableRequestBuilder<String> requestBuilder;
            requestBuilder = Glide.with(mContext).fromString().diskCacheStrategy(DiskCacheStrategy.RESULT);
            requestBuilder.load(user.getHeading()).bitmapTransform(new CropCircleTransformation(mContext))
                    .thumbnail(0.2f).error(R.drawable.ic_avatar_placeholder).priority(Priority.IMMEDIATE).into(mAvatar);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                requestBuilder.load(user.getHeading()).bitmapTransform(new BlurTransformation(mContext))
                        .crossFade().priority(Priority.HIGH).error(R.drawable.backdrop_2).thumbnail(0.5f).into(mBackdrop);
            } else {
                requestBuilder.load(user.getHeading()).bitmapTransform(new ColorFilterTransformation(mContext, getResources().getColor(R.color.colorPrimary_50pct)))
                        .crossFade().priority(Priority.HIGH).error(R.drawable.backdrop_2).thumbnail(0.5f).into(mBackdrop);
            }
            mUserNameTv.setText(StringUtils.check(mContext, user.getName(), R.string.unknown_user_name));
        }
    }

    @Override
    public void showMessageBadge() {

    }

    @Override
    public void toPersonalView() {
        mLeftDrawerLayout.closeDrawer();
        mLeftDrawerLayout.postDelayed(() -> {
            startActivity(new Intent(getContext(), LoginView.class));
        }, DRAWER_CLOSE_DURATION);
    }

    @Override
    public void toLoginView() {
        mLeftDrawerLayout.closeDrawer();
        mLeftDrawerLayout.postDelayed(() -> {
            startActivity(new Intent(getContext(), LoginView.class));
        }, DRAWER_CLOSE_DURATION);
    }

    @OnClick({R.id.avatar, R.id.user_name_tv})
    public void onUserInfoItemClick() {
        mPresenter.onUserInfoClick();
    }

    @OnClick({R.id.drawer_menu_category, R.id.drawer_menu_explore, R.id.drawer_menu_want, R.id.drawer_menu_message, R.id.drawer_menu_account, R.id.drawer_menu_settings})
    public void onMenuItemSelected(View view) {
        mSelected = view.getId();
        toMenuItemIntent(mSelected);
    }

    private void toMenuItemIntent(int mSelected) {
        mLeftDrawerLayout.closeDrawer();
        final int sSelected = mSelected;
        mLeftDrawerLayout.postDelayed(() -> {
            switch (sSelected) {
                case R.id.drawer_menu_category:
                    break;
                case R.id.drawer_menu_explore:
                    break;
                case R.id.drawer_menu_want:
                    break;
                case R.id.drawer_menu_message:
                    break;
                case R.id.drawer_menu_account:
                    break;
                case R.id.drawer_menu_settings:
                    break;
            }
        }, DRAWER_CLOSE_DURATION);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
