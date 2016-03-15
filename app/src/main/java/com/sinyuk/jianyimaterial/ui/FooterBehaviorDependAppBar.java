package com.sinyuk.jianyimaterial.ui;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by Sinyuk on 15.12.7.
 */
public class FooterBehaviorDependAppBar extends CoordinatorLayout.Behavior<View> {


    public FooterBehaviorDependAppBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof AppBarLayout;
    }


    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        float translationY = Math.abs(dependency.getTranslationY());
        Log.d("FooterBehavior :", translationY+"");
        child.setTranslationY(translationY);
        return true;
    }
}
