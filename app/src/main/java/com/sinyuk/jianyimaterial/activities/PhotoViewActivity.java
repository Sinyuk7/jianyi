package com.sinyuk.jianyimaterial.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.base.BaseActivity;
import com.sinyuk.jianyimaterial.utils.FileUtils;
import com.sinyuk.jianyimaterial.utils.ToastUtils;
import com.sinyuk.jianyimaterial.widgets.HackyViewPager;
import com.sinyuk.jianyimaterial.widgets.InkPageIndicator;
import com.sinyuk.jianyimaterial.widgets.RoundCornerIndicator;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import cimi.com.easeinterpolator.EaseSineInOutInterpolator;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoViewActivity extends BaseActivity {

    private static final String ISLOCKED_ARG = "is_locked";
    @Bind(R.id.view_pager)
    HackyViewPager viewPager;
    @Bind(R.id.back_iv)
    ImageView backIv;
    @Bind(R.id.page_indicator)
    InkPageIndicator pageIndicator;
    @Bind(R.id.page_indicator_compat)
    RoundCornerIndicator cornerIndicator;
    @Bind(R.id.save_iv)
    ImageView saveIv;
    @Nullable
    @Bind(R.id.root_view)
    FrameLayout rootView;
    @Bind(R.id.progress_bar)
    ProgressBar progressBar;


    private int selectedPageIndex;
    //    private String productId;
    private ArrayList<String> shotUrls;
    private DrawableRequestBuilder<String> displayRequest;
    private BitmapRequestBuilder<String, Bitmap> downloadRequest;

    @Override
    protected void beforeSetContentView(Bundle savedInstanceState) {


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
        if (bundle == null)
            return;
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
    }

    private void setupViewPager() {
        // can't add this otherwise the photoView will not work
//        viewPager.setPageTransformer(false, new DepthPageTransformer());
        if (shotUrls.isEmpty())
            return;
        viewPager.setAdapter(new PhotoViewAdapter(this));

        if (shotUrls.size() == 1)
            return;

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
            outState.putBoolean(ISLOCKED_ARG, ((HackyViewPager) viewPager).isLocked());
        }
        super.onSaveInstanceState(outState);
    }

    @OnClick({R.id.back_iv, R.id.save_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_iv:
                onBackPressed();
                break;
            case R.id.save_iv:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            getString(R.string.permission_write_storage_rationale),
                            REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
                } else {
                    downloadPic();
                }
                break;
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
                    .setNegativeButton("取消", null).setPositiveButton("允许", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(PhotoViewActivity.this,
                            new String[]{permission}, requestCode);
                }
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
        this.setResult(ProductDetails.REQUEST_PAGE_INDEX, result);
        super.onBackPressed();
    }

    private boolean isViewPagerActive() {
        return (viewPager != null);
    }

    private void toggleViewPagerScrolling() {
        if (isViewPagerActive()) {
            viewPager.toggleLock();
        }
    }


    class PhotoViewAdapter extends PagerAdapter {

        private Context mContext;

        public PhotoViewAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return shotUrls.size();
        }

        @Override
        public View instantiateItem(final ViewGroup container, int position) {
            if (null != progressBar)
                progressBar.setVisibility(View.VISIBLE);

            final PhotoView photoView = new PhotoView(container.getContext());

            photoView.setOnMatrixChangeListener(new PhotoViewAttacher.OnMatrixChangedListener() {
                @Override
                public void onMatrixChanged(RectF rect) {
                    viewPager.setLocked(photoView.getScale() != 1);
                }
            });

            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);


            displayRequest.load(shotUrls.get(position)).listener(new RequestListener<String, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                    if (null != progressBar)
                        progressBar.setVisibility(View.GONE);
                    ToastUtils.toastSlow(PhotoViewActivity.this, "加载失败");
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (null != progressBar)
                        progressBar.setVisibility(View.GONE);
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
