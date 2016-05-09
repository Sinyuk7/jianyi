package com.sinyuk.jianyimaterial.glide;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.request.target.ImageViewTarget;

/**
 * Created by Sinyuk on 16.5.8.
 */
public class SinyukTarget extends ImageViewTarget<Bitmap> {
    private final ImageView mImageView;

    private long mDuration = 2000;

    public SinyukTarget(ImageView view) {
        super(view);
        this.mImageView = view;
    }

    public SinyukTarget(ImageView view, long animateTime) {
        super(view);
        this.mImageView = view;
        this.mDuration = animateTime;
    }

    @Override
    protected void setResource(Bitmap resource) {
        mImageView.setImageBitmap(resource);
        AlphaSatColorMatrixEvaluator evaluator = new AlphaSatColorMatrixEvaluator();
        final AnimateColorMatrixColorFilter filter = new AnimateColorMatrixColorFilter(evaluator.getColorMatrix());

        final BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();

        drawable.setColorFilter(filter.getColorFilter());

        ObjectAnimator animator = ObjectAnimator.ofObject(filter, "colorMatrix", evaluator,
                evaluator.getColorMatrix());
        animator.addUpdateListener(animation -> drawable.setColorFilter(filter.getColorFilter()));
        animator.setInterpolator(new FastOutSlowInInterpolator());
        animator.setDuration(mDuration);
        animator.addListener(new AnimatorListenerAdapter() {});
        animator.start();
    }
}
