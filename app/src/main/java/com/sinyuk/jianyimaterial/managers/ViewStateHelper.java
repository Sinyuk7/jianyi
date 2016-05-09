package com.sinyuk.jianyimaterial.managers;

import android.content.Context;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.sinyuk.jianyimaterial.R;
import com.sinyuk.jianyimaterial.utils.SpringUtils;

/**
 * Created by Sinyuk on 16.5.9.
 */
public class ViewStateHelper {

    private static ViewStateHelper instance;
    private Context context;

    public ViewStateHelper(Context context) {
        this.context = context;
    }

    public static ViewStateHelper getInstance(Context context) {
        if (null == instance) { instance = new ViewStateHelper(context); }
        return instance;
    }

    public View showEmptyState(ViewStub viewStub) {
        return inflateStateView(viewStub, R.drawable.empty_list, context.getString(R.string.common_hint_empty_list));
    }

    public View inflateStateView(ViewStub viewStub, String url, String text) {
        final View view = viewStub.inflate();
        ImageView imageView = (ImageView) view.findViewById(R.id.state_image);
        TextView textView = (TextView) view.findViewById(R.id.state_hint);

        Glide.with(context.getApplicationContext()).load(url)
                .dontAnimate()
                .priority(Priority.IMMEDIATE)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        textView.setText(text);
                        SpringUtils.popOut(100, 10, 300, textView, imageView);
                        return false;
                    }
                }).into(imageView);
        return view;

    }


    public View inflateStateView(ViewStub viewStub, int resId, String text) {
        final View view = viewStub.inflate();
        ImageView imageView = (ImageView) view.findViewById(R.id.state_image);
        TextView textView = (TextView) view.findViewById(R.id.state_hint);

        Glide.with(context.getApplicationContext()).load(resId)
                .dontAnimate()
                .priority(Priority.IMMEDIATE)
                .listener(new RequestListener<Integer, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        textView.setText(text);
                        popOut(view);
                        return false;
                    }
                }).into(imageView);
        return view;
    }

    public void popOut(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.state_image);
        TextView textView = (TextView) view.findViewById(R.id.state_hint);
        SpringUtils.popOut(50, 8, 200, textView, imageView);
    }
}
