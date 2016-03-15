package com.sinyuk.jianyimaterial.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;

import com.sinyuk.jianyimaterial.R;

/**
 * Created by Sinyuk on 15.12.28.
 */
public class RectRevealView extends View {
    public static final int STATE_NOT_STARTED = 0;
    public static final int STATE_FILL_STARTED = 1;
    public static final int STATE_FINISHED = 2;

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private static final int FILL_TIME = 300;
    private static final int START_DELAY = 0; // 为了点击的ripple效果结束

    private int state = STATE_NOT_STARTED;

    private Paint fillPaint;

    ObjectAnimator scaleAnimator;

    private int startLocationX;
    private int startLocationY;

    private boolean isHorizontal;
    private boolean isVertical = true;


    private float currentScale;


    private OnStateChangeListener onStateChangeListener;
    private boolean lockedAnimations = false;

    public RectRevealView(Context context) {
        super(context);
        init(context);
    }

    public RectRevealView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RectRevealView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RectRevealView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        fillPaint = new Paint();
        fillPaint.setStyle(Paint.Style.FILL);
        fillPaint.setColor(getResources().getColor(R.color.white));
    }

    public void setFillPaintColor(int color) {
        fillPaint.setColor(color);
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.isHorizontal = horizontal;
    }

    public boolean isVertical() {
        return isVertical;
    }

    public void setVertical(boolean vertical) {
        this.isVertical = vertical;
    }


    public void startFromLocation(int[] tapLocationOnScreen) {
        if (lockedAnimations)
            return;
        changeState(STATE_FILL_STARTED);
        startLocationX = tapLocationOnScreen[0];
        startLocationY = tapLocationOnScreen[1];

        if (isHorizontal) {
            scaleAnimator = ObjectAnimator.
                    ofFloat(this, "currentScale", 0, 1);
            scaleAnimator.setDuration(FILL_TIME / 2);
        } else if (isVertical) {
            scaleAnimator = ObjectAnimator.ofFloat(this, "currentScale", 0, 1);
            scaleAnimator.setDuration(FILL_TIME);
        } else {
            return;
        }
        scaleAnimator.setInterpolator(INTERPOLATOR);
        scaleAnimator.setStartDelay(START_DELAY);
        scaleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                changeState(STATE_FINISHED);
            }
        });

        scaleAnimator.start();

    }

    public void setToFinishedFrame() {
        changeState(STATE_FINISHED);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (state == STATE_FINISHED) {
            canvas.drawRect(0, 0, getWidth(), getHeight(), fillPaint);
        } else {
            Log.w("RectRevealView", "fraction: " + currentScale);
            if (isVertical) {
                canvas.drawRect(0, startLocationY * (1 - currentScale),
                        getWidth(), startLocationY + (getHeight() - startLocationY) * currentScale, fillPaint);
            } else {
                canvas.drawRect(startLocationX - currentScale * getWidth() / 2, 0,
                        startLocationX + currentScale * getWidth() / 2, getHeight(), fillPaint);
            }

        }
    }

    private void changeState(int state) {
        if (this.state == state) {
            return;
        }

        this.state = state;
        if (onStateChangeListener != null) {
            onStateChangeListener.onStateChange(state);
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
    }


    public void setCurrentScale(float currentScale) {
        this.currentScale = currentScale;
        invalidate();
    }

    public void setLockedAnimations(boolean lockedAnimations) {
        this.lockedAnimations = lockedAnimations;
    }

    public static interface OnStateChangeListener {
        void onStateChange(int state);
    }
}