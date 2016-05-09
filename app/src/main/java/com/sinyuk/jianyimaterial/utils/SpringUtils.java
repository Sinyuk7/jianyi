package com.sinyuk.jianyimaterial.utils;

import android.os.Handler;
import android.view.View;

import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringListener;
import com.facebook.rebound.SpringSystem;

/**
 * Created by Sinyuk on 16.5.9.
 */
public class SpringUtils {
    public static void popOut(final View target, double tension, double fraction) {
        final int mOriginLayerType = target.getLayerType();
        final SpringSystem springSystem = SpringSystem.create();
        final Spring spring = springSystem.createSpring().setSpringConfig(new SpringConfig(tension, fraction));
        spring.addListener(new SpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                target.setScaleX((float) spring.getCurrentValue());
                target.setScaleY((float) spring.getCurrentValue());
            }

            @Override
            public void onSpringAtRest(Spring spring) {

            }

            @Override
            public void onSpringActivate(Spring spring) {
                target.setVisibility(View.VISIBLE);
                target.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }

            @Override
            public void onSpringEndStateChange(Spring spring) {
                target.setLayerType(mOriginLayerType, null);
            }
        });
        spring.setEndValue(1);
    }

    public static void popOut(final double tension, final double fraction, long delay, final View... views) {
        Handler handler = new Handler();
        for (int i = 0; i < views.length; i++) {
            final int finalI = i;
            handler.postDelayed(() -> popOut(views[finalI], tension, fraction), delay * i);

        }
    }
}
