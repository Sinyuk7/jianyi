package com.sinyuk.jianyimaterial.ui;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by Sinyuk on 15.12.25.
 */
public class BezierEvaluator implements TypeEvaluator<PointF> {

    private PointF points[];

    public BezierEvaluator(PointF... points) {
        if (points.length != 3) {
            throw new IllegalArgumentException("二次方贝赛尔曲线");
        }
        this.points = points;
    }

    @Override
    public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
        // B(t) = P0 * (1-t)^3 + 3 * P1 * t * (1-t)^2 + 3 * P2 * t^2 * (1-t) + P3 * t^3

        float one_t = 1.0f - fraction;

        PointF P0 = points[0];
        PointF P1 = points[1];
        PointF P2 = points[2];

        float x = (float) (P0.x * Math.pow(one_t, 2) + 2 * fraction * one_t * P1.x + Math.pow(fraction, 2) * P2.x);
        float y = (float) (P0.y * Math.pow(one_t, 2) + 2 * fraction * one_t * P1.y + Math.pow(fraction, 2) * P2.y);

        return new PointF(x, y);
    }

}
