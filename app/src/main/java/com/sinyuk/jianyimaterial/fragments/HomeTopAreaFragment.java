package com.sinyuk.jianyimaterial.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.feature.CategoryView;
import com.sinyuk.jianyimaterial.activities.HomeActivity;
import com.sinyuk.jianyimaterial.activities.WebViewActivity;
import com.sinyuk.jianyimaterial.base.BaseFragment;
import com.sinyuk.jianyimaterial.events.AppBarEvent;
import com.sinyuk.jianyimaterial.ui.trans.AccordionTransformer;
import com.sinyuk.jianyimaterial.utils.ScreenUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * HomeActivity Top Area in App bar layout
 */
public class HomeTopAreaFragment extends BaseFragment {

    private final static boolean DEBUG = HomeActivity.DEBUG;
    private static final long BILLBOARD_SWITCH_SPEED = 5000;

    private static HomeTopAreaFragment instance;
    @Bind(R.id.banner_view)
    ConvenientBanner billboard;




    private List<String> posterUrls = new ArrayList();


    public HomeTopAreaFragment() {
        // Required empty public constructor
    }

    public static HomeTopAreaFragment getInstance() {
        if (null == instance)
            instance = new HomeTopAreaFragment();
        return instance;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_home_top_area;
    }

    @Override
    protected boolean isUsingEventBus() {
        return true;
    }

    @Override
    protected void initViewsAndEvent() {
        final ViewTreeObserver observer = billboard.getViewTreeObserver();
        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

            @Override
            public boolean onPreDraw() {
                billboard.getViewTreeObserver().removeOnPreDrawListener(this);
                final LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lps.height = (int) (ScreenUtils.getScreenWidth(mContext) * 243 / 720.f);
                billboard.setLayoutParams(lps);
                return false;
            }
        });
        setupBillboard();

        // 要不要在onResume的时候刷新一下 不用 哈哈哈哈
        refreshData();
    }

    private void setupBillboard() {
        // 动态设置高度
        billboard.setPageTransformer(new AccordionTransformer());

        billboard.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                startActivity(new Intent(mContext, WebViewActivity.class));
            }
        });

        //noinspection unchecked

    }

    @OnClick({ R.id.entry_recommended, R.id.entry_free, R.id.entry_category})
    public void onClick(View view) {
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.entry_recommended:
                intent.putExtra("parent_sort_index", 3);
                break;
            case R.id.entry_free:
                intent.putExtra("parent_sort_index", 6);
                break;
            case R.id.entry_category:
                intent.setClass(mContext, CategoryView.class);
                break;
        }
        startActivity(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAppBarExpanded(AppBarEvent event) {
        if (event.getVerticalOffset() == 0) {
            billboard.startTurning(BILLBOARD_SWITCH_SPEED);
        } else {
            billboard.stopTurning();
        }

    }

    private void refreshData() {
        refreshData(false);
    }

    private void refreshData(boolean clean) {
        if (clean) {
            // 清除数据库里的缓存 再刷新
        } else {
            // TODO: 模拟获取图片的 url 不知道能不能在这里用 Glide 啊
            final String[] ads = new String[]{
                    "http://wx.i-jianyi.com/img/ios/1.jpg",
                    "http://wx.i-jianyi.com/img/ios/2.jpg",
                    "http://wx.i-jianyi.com/img/ios/3.jpg",
            };

            Collections.addAll(posterUrls, ads);
        }

        billboard.setPages(WebImageViewHolder::new, posterUrls);
        billboard.notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        billboard.startTurning(BILLBOARD_SWITCH_SPEED);
    }

    @Override
    public void onPause() {
        super.onPause();
        billboard.stopTurning();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public class WebImageViewHolder implements Holder<String> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            imageView.setLayoutParams(lps);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, int position, String data) {


            DrawableRequestBuilder<String> displayRequest = Glide.with(mContext).fromString()
                    .crossFade()
                    .error(mContext.getResources().getDrawable(R.drawable.image_placeholder_grey300))
                    .placeholder(mContext.getResources().getDrawable(R.drawable.image_placeholder_grey300))
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .priority(Priority.IMMEDIATE);

            displayRequest.load(posterUrls.get(position))
                    .into(imageView);
        }
    }
}
