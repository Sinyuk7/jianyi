package com.sinyuk.jianyimaterial.widgets;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sinyuk.jianyimaterial.R;

/**
 * Created by Sinyuk on 16.1.1.
 */
public class RatioImageView extends ImageView {
    private float ratio = 1;
    public static final float VERTICAL_RATIO = 4 / 3.f;
    public static final float HORIZ_RATIO = 3 / 4.f;

    public RatioImageView(Context context) {
        this(context, null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRatio(context, attrs, defStyleAttr);

    }

    private void initRatio(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.RatioImageView, defStyleAttr, 0);
        try {
            if (a.hasValue(R.styleable.RatioImageView_ratio))
                ratio = a.getFloat(R.styleable.RatioImageView_ratio, 1f);
        } finally {
            a.recycle();
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initRatio(context, attrs, defStyleAttr);
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
        int fourThreeHeight = MeasureSpec.makeMeasureSpec((int) (MeasureSpec.getSize(widthSpec) * ratio),
                MeasureSpec.EXACTLY);
        super.onMeasure(widthSpec, fourThreeHeight);
    }


}
