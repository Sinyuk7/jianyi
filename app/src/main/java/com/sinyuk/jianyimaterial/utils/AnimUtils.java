package com.sinyuk.jianyimaterial.utils;

import android.animation.Animator;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.view.ViewPropertyAnimator;

import com.sinyuk.jianyimaterial.R;

import cimi.com.easeinterpolator.EaseBreathInterpolator;

/**
 * Created by Sinyuk on 15.12.19.
 */
public class AnimUtils {

    public final static long ACTIVITY_TRANSITION_DELAY = 150;

    public final static long ANIMATION_TIME_SHORT_EXTRA = 100;
    public final static long ANIMATION_TIME_SHORT = 200;
    public final static long ANIMATION_TIME_MEDIUM = 300;
    public final static long ANIMATION_TIME_LONG = 500;
    public final static long ANIMATION_TIME_LONG_EXTRA = 800;

    public static double mapValueFromRangeToRange(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return toLow + ((value - fromLow) / (fromHigh - fromLow) * (toHigh - toLow));
    }

    public static double clamp(double value, double low, double high) {
        return Math.min(Math.max(value, low), high);
    }

    public static ObjectAnimator tada(View view) {
        return tada(view, 1f);
    }

    public static ObjectAnimator tada(View view, float shakeFactor) {

        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofKeyframe(View.SCALE_X,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofKeyframe(View.SCALE_Y,
                Keyframe.ofFloat(0f, 1f),
                Keyframe.ofFloat(.1f, .9f),
                Keyframe.ofFloat(.2f, .9f),
                Keyframe.ofFloat(.3f, 1.1f),
                Keyframe.ofFloat(.4f, 1.1f),
                Keyframe.ofFloat(.5f, 1.1f),
                Keyframe.ofFloat(.6f, 1.1f),
                Keyframe.ofFloat(.7f, 1.1f),
                Keyframe.ofFloat(.8f, 1.1f),
                Keyframe.ofFloat(.9f, 1.1f),
                Keyframe.ofFloat(1f, 1f)
        );

        PropertyValuesHolder pvhRotate = PropertyValuesHolder.ofKeyframe(View.ROTATION,
                Keyframe.ofFloat(0f, 0f),
                Keyframe.ofFloat(.1f, -3f * shakeFactor),
                Keyframe.ofFloat(.2f, -3f * shakeFactor),
                Keyframe.ofFloat(.3f, 3f * shakeFactor),
                Keyframe.ofFloat(.4f, -3f * shakeFactor),
                Keyframe.ofFloat(.5f, 3f * shakeFactor),
                Keyframe.ofFloat(.6f, -3f * shakeFactor),
                Keyframe.ofFloat(.7f, 3f * shakeFactor),
                Keyframe.ofFloat(.8f, -3f * shakeFactor),
                Keyframe.ofFloat(.9f, 3f * shakeFactor),
                Keyframe.ofFloat(1f, 0)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhScaleX, pvhScaleY, pvhRotate).
                setDuration(1000);
    }

    public static ObjectAnimator nope(View view) {
        int delta = view.getResources().getDimensionPixelOffset(R.dimen.general_content_space);

        PropertyValuesHolder pvhTranslateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(.10f, -delta),
                Keyframe.ofFloat(.26f, delta),
                Keyframe.ofFloat(.42f, -delta),
                Keyframe.ofFloat(.58f, delta),
                Keyframe.ofFloat(.74f, -delta),
                Keyframe.ofFloat(.90f, delta),
                Keyframe.ofFloat(1f, 0f)
        );

        return ObjectAnimator.ofPropertyValuesHolder(view, pvhTranslateX).
                setDuration(500);
    }

}
