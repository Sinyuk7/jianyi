package com.sinyuk.jianyimaterial.common;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringSystem;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.utils.FileUtils;
import com.sinyuk.jianyimaterial.utils.LogUtils;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;
import com.sinyuk.jianyimaterial.utils.SpringUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.widgets.HackyViewPager;
import com.sinyuk.jianyimaterial.widgets.InkPageIndicator;
import com.sinyuk.jianyimaterial.widgets.RoundCornerIndicator;
import com.tumblr.backboard.imitator.ToggleImitator;
import com.tumblr.backboard.performer.MapPerformer;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import cimi.com.easeinterpolator.EaseSineInOutInterpolator;
import uk.co.senab.photoview.PhotoView;

public class PhotoViewActivity extends BaseActivity {

    private static final String ISLOCKED_ARG = "is_locked";
    @Bind(R.id.view_pager)
    HackyViewPager viewPager;
    @Bind(R.id.page_indicator)
    InkPageIndicator pageIndicator;
    @Bind(R.id.page_indicator_compat)
    RoundCornerIndicator cornerIndicator;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;
    @Bind(R.id.save_iv)
    ImageView saveButton;
    Handler myHandler = new Handler();
    private int selectedPageIndex;
    //    private String productId;
    private ArrayList<String> shotUrls;
    private DrawableRequestBuilder<String> displayRequest;
    private BitmapRequestBuilder<String, Bitmap> downloadRequest;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {

        ScreenUtils.hideSystemyBar(this);

        if (savedInstanceState != null) {
            boolean isLocked = savedInstanceState.getBoolean(ISLOCKED_ARG, false);
            viewPager.setLocked(isLocked);
        }

        displayRequest = Glide.with(mContext).fromString()
                .dontAnimate()
                .error(mContext.getResources().getDrawable(R.drawable.image_placeholder_black))
                .placeholder(mContext.getResources().getDrawable(R.drawable.image_placeholder_black))
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .priority(Priority.IMMEDIATE);


        downloadRequest = Glide.with(getApplicationContext()).fromString()
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .priority(Priority.HIGH);


        Bundle bundle = getIntent().getExtras();
        if (bundle == null) { return; }
        shotUrls = bundle.getStringArrayList("shot_urls");
        selectedPageIndex = bundle.getInt("selected_page_index", 0);
    }

    @Override
    protected int getContentViewID() {
        return R.layout.activity_photo_view;
    }

    @Override
    protected boolean isNavAsBack() {
        return false;
    }

    @Override
    protected boolean isUsingEventBus() {
        return false;
    }


    @Override
    protected void initViews() {
        progressBar.setIndeterminate(true);
        progressBar.setInterpolator(new EaseSineInOutInterpolator());
        setupViewPager();
        setupSaveButton();
    }

    private void setupSaveButton() {

        final SpringSystem springSystem = SpringSystem.create();

        final Spring spring = springSystem.createSpring();

        spring.addListener(new MapPerformer(saveButton, View.SCALE_X, 1, 1.2f));
        spring.addListener(new MapPerformer(saveButton, View.SCALE_Y, 1, 1.2f));

        saveButton.setOnTouchListener(new ToggleImitator(spring, 0, 1){
            @Override
            public boolean onTouch(View v, @NonNull MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    clickSaveButton();
                    return true;
                }
                return super.onTouch(v, event);
            }
        });


    }

    private void setupViewPager() {
        // can't add this otherwise the photoView will not work
//        viewPager.setPageTransformer(false, new DepthPageTransformer());
        if (shotUrls.isEmpty()) { return; }
        viewPager.setAdapter(new PhotoViewAdapter(this));

        if (shotUrls.size() == 1) { return; }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pageIndicator.setViewPager(viewPager);
            pageIndicator.setVisibility(View.VISIBLE);
        } else {
            cornerIndicator.setViewPager(viewPager, shotUrls.size());
            cornerIndicator.setVisibility(View.VISIBLE);
        }


    }

    @Override
    protected void initData() {
        if (viewPager.getAdapter() != null) {
            if (selectedPageIndex <= viewPager.getAdapter().getCount()) {
                viewPager.setCurrentItem(selectedPageIndex);
            } else {
                viewPager.setCurrentItem(0);
            }

        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (isViewPagerActive()) {
            outState.putBoolean(ISLOCKED_ARG, viewPager.isLocked());
        }
        super.onSaveInstanceState(outState);
    }


    private void clickSaveButton() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    getString(R.string.permission_write_storage_rationale),
                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        } else {
            downloadPic();
        }


    }

    private void downloadPic() {
        
        // use application context for a persistent download
        downloadRequest.load(shotUrls.get(viewPager.getCurrentItem()))
                .into(new SimpleTarget<Bitmap>() { // default means the original size
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        String savePath = FileUtils.savePhotoToSDCard(PhotoViewActivity.this, resource, FileUtils.getPhotoSavePath(), "img" + System.currentTimeMillis());

                        if (savePath == null) {
                            String errorMsg = FileUtils.checkSDCard() ? "保存失败" : "请插入SD卡";
                            Toast.makeText(PhotoViewActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(PhotoViewActivity.this, "图片已保存到" + savePath, Toast.LENGTH_LONG).show();

                        }

                    }
                });
    }

    /**
     * Requests given permission.
     * If the permission has been denied previously, a Dialog will prompt the user to grant the
     * permission, otherwise it is requested directly.
     */
    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(rationale)
                    .setCancelable(false)
                    .setNegativeButton("取消", null).setPositiveButton("允许", (dialog, which) -> {
                ActivityCompat.requestPermissions(PhotoViewActivity.this,
                        new String[]{permission}, requestCode);
            }).setTitle(getResources().getString(R.string.permission_title_rationale));

            AlertDialog alertDialog = builder.show();

        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_STORAGE_WRITE_ACCESS_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    downloadPic();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onBackPressed() {
        Intent result = new Intent();
        result.putExtra("selected_page_index", viewPager.getCurrentItem());
        this.setResult(Constants.Request_Code_Page_Index, result);
        super.onBackPressed();
    }

    private boolean isViewPagerActive() {
        return (viewPager != null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
    }

    class PhotoViewAdapter extends PagerAdapter {


        public PhotoViewAdapter(Context context) {

        }

        @Override
        public int getCount() {
            return shotUrls.size();
        }

        @Override
        public View instantiateItem(final ViewGroup container, int position) {
            if (null != progressBar) { progressBar.setVisibility(View.VISIBLE); }

            final PhotoView photoView = new PhotoView(container.getContext());

            photoView.setOnMatrixChangeListener(rect -> viewPager.setLocked(photoView.getScale() != 1));

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            displayRequest.load(shotUrls.get(position)).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    if (null != progressBar) { progressBar.setVisibility(View.GONE); }
                    ToastUtils.toastSlow(PhotoViewActivity.this, "加载失败");
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }

                    myHandler.postDelayed(() -> SpringUtils.popOut(saveButton, 100, 10), 1000);

                    return false;
                }
            }).into(photoView);
            return photoView;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}
