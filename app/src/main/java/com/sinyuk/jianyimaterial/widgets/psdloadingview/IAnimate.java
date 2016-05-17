package com.sinyuk.jianyimaterial.widgets.psdloadingview;

import android.graphics.Canvas;

/**
 * Created by Roger on 2016/1/8.
 */
public interface IAnimate {
    void init(PsdLoadingView mPsdLoadingView);

    void startLoading();

    void stopLoading();

    void setDuration(int duration);

    void onDraw(Canvas canvas);

    void onVisibilityChanged(boolean isVisibiable);

    boolean isLoading();
}
