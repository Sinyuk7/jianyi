package com.sinyuk.jianyimaterial.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.Space;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.api.JianyiApi;
import com.sinyuk.jianyimaterial.api.Show;
import com.sinyuk.jianyimaterial.application.Jianyi;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.fragments.CommentsFragment;
import com.sinyuk.jianyimaterial.fragments.dialogs.CommentDialog;
import com.sinyuk.jianyimaterial.glide.CropCircleTransformation;
import com.sinyuk.jianyimaterial.greendao.dao.DaoUtils;
import com.sinyuk.jianyimaterial.greendao.dao.YihuoDetailsService;
import com.sinyuk.jianyimaterial.managers.SnackBarFactory;
import com.sinyuk.jianyimaterial.entity.YihuoDetails;
import com.sinyuk.jianyimaterial.entity.YihuoProfile;
import com.sinyuk.jianyimaterial.ui.trans.AccordionTransformer;
import com.sinyuk.jianyimaterial.utils.FormatUtils;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.PreferencesUtils;
import com.sinyuk.jianyimaterial.utils.StringUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.volley.JsonRequest;
import com.sinyuk.jianyimaterial.widgets.CheckableImageView;
import com.sinyuk.jianyimaterial.widgets.ExpandableTextView;
import com.sinyuk.jianyimaterial.widgets.InkPageIndicator;
import com.sinyuk.jianyimaterial.widgets.MyCircleImageView;
import com.sinyuk.jianyimaterial.widgets.RatioImageView;
import com.sinyuk.jianyimaterial.widgets.RoundCornerIndicator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cimi.com.easeinterpolator.EaseSineInInterpolator;

public class ProductDetails extends BaseActivity {

    public static final int REQUEST_PAGE_INDEX = 0;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.page_indicator)
    InkPageIndicator pageIndicator;
    @Bind(R.id.page_indicator_compat)
    RoundCornerIndicator roundCornerIndicator;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.title_tv)
    TextView titleTv;
    @Bind(R.id.like_checkable_iv)
    CheckableImageView likeButton;

    @Bind(R.id.description_tv)
    ExpandableTextView descriptionTv;

    @Bind(R.id.new_price_tv)
    TextView newPriceTv;
    @Bind(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.avatar)
    MyCircleImageView avatar;
    @Bind(R.id.user_name_tv)
    TextView userNameTv;

    @Bind(R.id.location_tv)
    TextView locationTv;

    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @Bind(R.id.fragment_container)
    FrameLayout fragmentContainer;
    @Bind(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.pub_date_tv)
    TextView pubDateTv;
    @Bind(R.id.phone_call_iv)
    ImageView phoneCallIv;

    @Bind(R.id.scrim)
    View scrim;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.placeholder)
    RatioImageView placeholder;
    @Bind(R.id.anchor)
    Space anchor;
    @Bind(R.id.shadow)
    View shadow;
    @Bind(R.id.expandable_text)
    TextView expandableText;
    @Bind(R.id.expand_collapse)
    ImageButton expandCollapse;
    @Bind(R.id.comment_btn)
    Button commentBtn;
    @Bind(R.id.contact_btn)
    Button contactBtn;

    // Viewpager touch event
    private int oldX;
    private int newX;

    private String avatarUrlStr;
    private String titleStr;
    private String descriptionStr;
    private String newPriceStr;
    private String telStr;
    private String locationStr;
    ;
    private List<YihuoDetails.Pics> shotsList = new ArrayList<>();
    private String userNameStr;
    private String pubDateStr;
    // 经度
    private String longitudeStr;
    // 纬度
    private String latitudeStr;
    private BitmapRequestBuilder<String, Bitmap> shotRequest;
    private DrawableRequestBuilder<String> avatarRequest;
    private ShotsAdapter viewPagerAdapter;
    private YihuoProfile profileData;
    private YihuoDetails detailsData;
    private String idStr;
    private boolean isEnterActivity = true;
    private String uId;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {
        uId = PreferencesUtils.getString(this, StringUtils.getRes(this, R.string.key_user_id));

        getExtras(getIntent().getExtras());

        avatarRequest = Glide.with(mContext).fromString()
                .dontAnimate()
                .placeholder(mContext.getResources().getDrawable(R.drawable.ic_avatar_placeholder))
                .error(mContext.getResources().getDrawable(R.drawable.ic_avatar_placeholder))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .priority(Priority.NORMAL)
                .bitmapTransform(new CropCircleTransformation(mContext));

        shotRequest = Glide.with(mContext).fromString()
                .asBitmap()
                .placeholder(mContext.getResources().getDrawable(R.drawable.image_placeholder_black))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.IMMEDIATE)
                .thumbnail(0.5f);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_product_details;
    }

    @Override
    protected boolean isUsingEventBus() {
        return false;
    }


    @Override
    protected void initViews() {
        initTextAreaWithoutNet(); // don't need request data on this page
        setupViewPager();
        setupAvatar();
    }


    @Override
    protected void initData() {
        if (!TextUtils.isEmpty(idStr))
            requestYihuoDetails(idStr);

        // pretend to
        loadComments();
    }

    private void loadComments() {
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, CommentsFragment.getInstance()).commit();
    }

    @Override
    protected boolean isNavAsBack() {
        return true;
    }


    private void getExtras(Bundle extras) {
        // TODO: 换成 url
        profileData = extras.getParcelable(YihuoProfile.TAG);

        if (profileData != null) {
            idStr = profileData.getId();
            userNameStr = profileData.getUsername();
            avatarUrlStr = profileData.getHeadImg();
            titleStr = profileData.getName();
            pubDateStr = profileData.getTime();
            newPriceStr = profileData.getPrice();
            telStr = profileData.getTel();
            locationStr = profileData.getSchoolname();
        } else {
            LogUtils.simpleLog(ProductDetails.class, "intent中的数据有空");
        }

    }

    private void requestYihuoDetails(final String id) {

        JsonRequest jsonRequest = new JsonRequest
                (Request.Method.GET, JianyiApi.yihuoDetails(id), null, response -> {
                    // the response is already constructed as a JSONObject!
                    final Gson gson = new Gson();
                    // 接受最原始的JSON数据
                    Show show = gson.fromJson(response.toString(), Show.class);
                    // 转换成我的Model
                    Show.Data jsonData = show.getData();

                    String trans = gson.toJson(jsonData);

                    detailsData = gson.fromJson(trans,
                            YihuoDetails.class);

                    if (!detailsData.getId().equals(id)) {
                        Toast.makeText(ProductDetails.this, "哦哦 简易出了点问题", Toast.LENGTH_LONG).show();
                    } else {
                        LogUtils.simpleLog(ProductDetails.class, detailsData.toString());
                        descriptionStr = detailsData.getDetail();
                        shotsList = detailsData.getPics();
                        longitudeStr = detailsData.getX();
                        latitudeStr = detailsData.getY();

                        resetViewPager();

                        resetTextArea();

                        setupLikeButton();

                    }
                }, error -> {
                    // TODO: SnackBar提示一下 不过分吧
                    LogUtils.simpleLog(ProductDetails.class, "加载错误啊啊啊啊啊");
                });
        Jianyi.getInstance().addRequest(jsonRequest, YihuoDetails.TAG);
    }

    private void setupLikeButton() {
        YihuoDetailsService yihuoService = DaoUtils.getYihuoDetailsService();
        YihuoDetails data = (YihuoDetails) yihuoService.query(detailsData.getId());
        if (null == data) {
            likeButton.setChecked(false);
        } else {
            likeButton.setChecked(true);
        }
    }

    private void initTextAreaWithoutNet() {
        // 初始化CollapseToolbar Title
        collapsingToolbarLayout.setTitle(StringUtils.check(this,
                titleStr, R.string.activity_product_details));
        // TODO: 初始化用户名
        userNameTv.setText(StringUtils.check(this,
                userNameStr, R.string.unknown_user_name));

        // TODO: 初始化标题
        titleTv.setText(StringUtils.check(this,
                titleStr, R.string.unknown_title));

        // TODO: 获得准确时间
        pubDateTv.setText(StringUtils.check(this,
                pubDateStr, R.string.unknown_date));

        // TODO: 初始化价格
        newPriceTv.setText(StringUtils.check(this,
                FormatUtils.formatPrice(newPriceStr), R.string.unknown_date));

        // TODO: 加载电话
        if (telStr != null)
            telStr = StringUtils.check(this,
                    FormatUtils.formatPhoneNum(telStr), R.string.unknown_tel);

        // TODO: 初始化地址
        locationTv.setText(StringUtils.check(this,
                locationStr, R.string.unknown_location));


    }

    private void setupViewPager() {
        // 让你在报错 我日
        pageIndicator.setVisibility(View.GONE);

        viewPagerAdapter = new ShotsAdapter(this);

        viewPager.setAdapter(viewPagerAdapter);


        viewPager.setPageTransformer(false, new AccordionTransformer());

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            // TODO: 图片点击事件 写在这里以解决viewPager的拦截
            @Override
            public boolean onTouch(View v, MotionEvent ev) {

                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        oldX = (int) ev.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        newX = (int) ev.getX();
                        if (Math.abs(oldX - newX) < ViewConfiguration.get(ProductDetails.this).getScaledTouchSlop()) {
                            oldX = 0;
                            newX = 0;

                            // TODO: 换成跳转到photoView之类的东西
                            onShotClick(viewPager.getCurrentItem());
                            break;
                        }
                }
                return false;
            }

        });
    }

    // 在网络请求之后在加载的内容
    private void resetTextArea() {
        // TODO: 初始化描述
        if (descriptionTv != null)
            descriptionTv.setText(StringUtils.check(this,
                    descriptionStr, R.string.unknown_yihuo_description));

//            descriptionTv.setText(StringUtils.getRes(this, R.string.lorem));
    }

    private void resetViewPager() {
        if (viewPager == null)
            return;
        viewPagerAdapter.notifyDataSetChanged();
        viewPager.setAdapter(viewPagerAdapter);

        if (shotsList.isEmpty() && shotsList.size() == 1)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pageIndicator.setViewPager(viewPager);
            pageIndicator.setVisibility(View.VISIBLE);
        } else {
            roundCornerIndicator.setViewPager(viewPager, shotsList.size());
            roundCornerIndicator.setVisibility(View.VISIBLE);
        }

    }


    private void setupAvatar() {
        // TODO: 加载头像
        avatarRequest.load(avatarUrlStr)
                .into(avatar);
    }


    // TODO: go to photoView activity
    private void onShotClick(int index) {
        if (detailsData == null || detailsData.getPics().isEmpty())
            return;
        Intent intent = new Intent(ProductDetails.this, PhotoViewActivity.class);
        Bundle bundle = new Bundle();

        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < detailsData.getPics().size(); i++) {
            list.add(JianyiApi.shotUrl(detailsData.getPics().get(i).getPic()));
        }

        bundle.putStringArrayList("shot_urls", list);
        bundle.putString("product_id", detailsData.getId());
        bundle.putInt("selected_page_index", index);

        intent.putExtras(bundle);
        startActivityForResult(intent, REQUEST_PAGE_INDEX);
    }


    // Dial Chat
    @OnClick({R.id.phone_call_iv, R.id.avatar, R.id.comment_btn, R.id.contact_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone_call_iv:
                if (TextUtils.isEmpty(telStr))
                    break;
                Uri uri = Uri.parse("tel:" + telStr);
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                startActivity(intent);
                break;
            case R.id.avatar:
                Intent startUserProfile = new Intent(ProductDetails.this, ProfileActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("user_name", userNameStr);
                bundle.putString("location", locationStr);
                bundle.putString("tel", telStr);
                bundle.putString("avatar", avatarUrlStr);
                startUserProfile.putExtras(bundle);
                startActivity(startUserProfile);
                break;
            case R.id.comment_btn:
                CommentDialog commentDialog = new CommentDialog();
                commentDialog.show(getSupportFragmentManager(), CommentDialog.TAG);
                break;
            case R.id.contact_btn:
                break;
        }
    }

    // 添加到收藏
    @OnClick(R.id.like_checkable_iv)
    public void addToLikes(CheckableImageView v) {

        if (TextUtils.isEmpty(uId)) {
            v.setChecked(false);
            requestForLogin();
            return;
        }
        YihuoDetailsService yihuoService = DaoUtils.getYihuoDetailsService();
        if (v.isChecked()) {
            // add to
            if (detailsData != null) {
                Date addedDate = new Date(System.currentTimeMillis());//获取当前时间
                detailsData.setDate(addedDate);
                yihuoService.saveOrUpdate(detailsData);

            }

        } else {
            // remove from
            yihuoService.deleteByKey(detailsData.getId());
        }
    }

    private void requestForLogin() {
        SnackBarFactory.requestLogin(this, coordinatorLayout).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PAGE_INDEX:
                viewPager.setCurrentItem(data.getIntExtra("selected_page_index", 0));
                break;
        }
    }


    /**
     * ViewPager Adapter
     */
    private class ShotsAdapter extends PagerAdapter {
        private Context context;


        ShotsAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            // TODO: 换成 url 的数量
            if (shotsList.isEmpty())
                return 1;
            return shotsList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View shot = getPage(position, container);
            container.addView(shot);
            return shot;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        private View getPage(final int position, final ViewGroup container) {

            if (null != progressBar)
                progressBar.setVisibility(View.VISIBLE);


            final RatioImageView imageView = new RatioImageView(context);
//            imageView.setClickable(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            // TODO: 当为空的时候 用error的图片代替
            if (shotsList.isEmpty()) {
                if (null != progressBar)
                    progressBar.setVisibility(View.GONE);
                imageView.setImageResource(R.drawable.image_placeholder_black);
                return imageView;
            }

            imageView.setTag(R.id.shot_tag, shotsList.get(position).getId());
            // TODO: 换成加载 Url
            shotRequest.load(JianyiApi.shotUrl(shotsList.get(position).getPic())).listener(new RequestListener<String, Bitmap>() {
                @Override
                public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                    if (null != progressBar)
                        progressBar.setVisibility(View.GONE);
                    ToastUtils.toastSlow(ProductDetails.this, "图片加载失败");
                    return false;
                }

                @Override
                public boolean onResourceReady(final Bitmap bitmap, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (null != progressBar)
                        progressBar.setVisibility(View.GONE);

                    if (isEnterActivity && null != scrim) {
                        scrim.animate().alpha(0).setDuration(100)
                                .setStartDelay(100)
                                .setInterpolator(new EaseSineInInterpolator())
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        if (scrim != null) {
                                            scrim.setAlpha(0);
                                            scrim.setVisibility(View.GONE);
                                        }
                                    }
                                })
                                .start();
                        isEnterActivity = false;
                    }
                    return false;
                }
            }).into(imageView);

            return imageView;
        }
    }
}
