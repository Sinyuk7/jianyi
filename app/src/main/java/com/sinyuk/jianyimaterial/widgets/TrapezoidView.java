package com.sinyuk.jianyimaterial.widgets;

/**
 * Created by Sinyuk on 16.3.19.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.RelativeLayout;

import com.sinyuk.jianyimaterial.R;

public class TrapezoidView extends RelativeLayout {

    private final static int NONE = -1;
    private final static int TOP = 0;
    private final static int LEFT = 1;
    private final static int BOTTOM = 2;
    private final static int RIGHT = 3;


    private int anchorOffset; // 顶点坐标

    private int anchorGravity; // 顶点方向

    private  int angleGravity; // 直角?

    private int triangleHeight; // 三角形部分的高度

    private int fillColor;

    private Path path;
    private Paint p;


    public TrapezoidView(Context context) {
        this(context, null);
    }

    public TrapezoidView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TrapezoidView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        path = new Path();
        p = new Paint();

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.TrapezoidView, defStyleAttr, 0);
        try {
            if (a.hasValue(R.styleable.TrapezoidView_anchor))
                anchorOffset = a.getDimensionPixelOffset(R.styleable.TrapezoidView_anchor, 0);
            if (a.hasValue(R.styleable.TrapezoidView_fill_color))
                fillColor = a.getColor(R.styleable.TrapezoidView_fill_color, getResources().getColor(android.R.color.transparent));
            if (a.hasValue(R.styleable.TrapezoidView_anchor_gravity))
                anchorGravity = a.getInt(R.styleable.TrapezoidView_anchor_gravity, TOP);
            if (a.hasValue(R.styleable.TrapezoidView_angle_gravity))
                angleGravity = a.getInt(R.styleable.TrapezoidView_angle_gravity,LEFT);
            if (a.hasValue(R.styleable.TrapezoidView_triangle_height))
                triangleHeight = a.getDimensionPixelOffset(R.styleable.TrapezoidView_triangle_height, 0);

        } finally {
            a.recycle();
        }


        p.setAntiAlias(true);
        p.setStyle(Paint.Style.FILL);
        p.setColor(fillColor);


    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private ViewOutlineProvider createOutLineProvider(final Path path) {
        return new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                // Or read size directly from the view's width/height
                outline.setConvexPath(path);
            }
        };
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

 /*       switch (anchorGravity){
            case TOP:

                break;

            case LEFT:

                break;
            case BOTTOM:

                break;
            case RIGHT:

                break;
        }*/

        int startAnchor = Math.min(getWidth(), anchorOffset);

        int secondAnchor = Math.min(getHeight(), triangleHeight);

        path.moveTo(startAnchor, 0);

        if (startAnchor < getWidth())
            path.lineTo(getWidth(), secondAnchor);

        path.lineTo(getWidth(), getHeight());

        path.lineTo(0, getHeight());

        if (secondAnchor < getHeight())
            path.lineTo(0, secondAnchor);

        path.close();

        canvas.drawPath(path, p);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (path.isConvex() && getOutlineProvider() == null)
                setOutlineProvider(createOutLineProvider(path));
        }
    }

}
