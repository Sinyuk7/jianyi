package com.sinyuk.jianyimaterial.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Sinyuk on 16.2.4.
 */
public class SixteenNineImageView extends ImageView {
    private float ratio = 9.f / 16.f;

    public SixteenNineImageView(Context context) {
        super(context);
    }

    public SixteenNineImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SixteenNineImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SixteenNineImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
//        invalidate();
    }

    public float getRatio() {
        return ratio;
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
//        float ratio = (((float) Math.random() * 2f + 2f) / 2f);
        int fourThreeHeight = View.MeasureSpec.makeMeasureSpec((int) (View.MeasureSpec.getSize(widthSpec) * ratio),
                View.MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, fourThreeHeight);
    }


}
